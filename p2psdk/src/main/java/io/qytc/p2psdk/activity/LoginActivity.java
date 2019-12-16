package io.qytc.p2psdk.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import io.qytc.p2psdk.R;
import io.qytc.p2psdk.constant.SpConstant;
import io.qytc.p2psdk.eventcore.EventBusUtil;
import io.qytc.p2psdk.eventcore.ResponseEvent;
import io.qytc.p2psdk.http.DoHttpManager;
import io.qytc.p2psdk.http.ResponseEventStatus;
import io.qytc.p2psdk.http.response.LoginResponse;
import io.qytc.p2psdk.service.SocketConnectService;
import io.qytc.p2psdk.utils.CmdUtil;
import io.qytc.p2psdk.utils.SpUtil;
import io.qytc.p2psdk.utils.ToastUtils;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class LoginActivity extends Activity {

    private TextView mTvLoginTip;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        mTvLoginTip = findViewById(R.id.tv_login_tip);
        EventBusUtil.register(this);
        login();
        getCameraInfo();
    }

    private void login() {
        String cardNo = CmdUtil.getCardNo();

        if (TextUtils.isEmpty(cardNo)) {
            ToastUtils.toast(this, R.string.no_IC_card_num);
            mTvLoginTip.setText(R.string.no_IC_card_num);
            return;
        }
        String password = "qytc@123";
        String deviceId = SpUtil.getString(this, SpConstant.JPUSH_DEVICE_ID);
        DoHttpManager.getInstance().auth(this, cardNo, password, deviceId);
    }

    public void onEventMainThread(ResponseEvent event) {
        if (event == null) return;

        if (event.getId() != ResponseEventStatus.LOGIN_ID) return;

        switch (event.getStatus()) {
            case ResponseEventStatus.OK:
                LoginResponse loginResponse = event.getData();
                LoginResponse.DataBean data = loginResponse.getData();

                String userSig = data.getUserSig();
                String accessToken = data.getAccessToken();

                LoginResponse.DataBean.TerminalBean terminalBean = data.getTerminal();
                int deptId = terminalBean.getDeptId();
                String pmi = terminalBean.getPmi();
                int tenantId = terminalBean.getTenantId();
                String name = terminalBean.getName();
                int id = terminalBean.getId();

                SpUtil.saveString(this, SpConstant.USERSIG, userSig);
                SpUtil.saveString(this, SpConstant.ACCESS_TOKEN, accessToken);
                SpUtil.saveString(this, SpConstant.PMI, pmi);
                SpUtil.saveString(this, SpConstant.NAME, name);
                SpUtil.saveString(this, SpConstant.DEPTID, deptId + "");
                SpUtil.saveString(this, SpConstant.TENANTID, tenantId + "");
                SpUtil.saveString(this, SpConstant.ID, id + "");

                startService(new Intent(this, SocketConnectService.class));

                ToastUtils.toast(this, R.string.login_success);

                startActivity(new Intent(this, HomeActivity.class));
                finish();
                break;
            case ResponseEventStatus.UNREGISTERED:
                ToastUtils.toast(this, R.string.unregister);
                mTvLoginTip.setText(R.string.unregister);
                break;
            case ResponseEventStatus.ERROR:
                ToastUtils.toast(this, event.getMessage());
                mTvLoginTip.setText(event.getMessage());
                break;
        }
    }

    private void getCameraInfo() {
        OkHttpClient mOkHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://ums.whqunyu.com/upload/camera.txt")
                .get()
                .build();
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody body = response.body();
                if (body == null) {
                    return;
                }
                String result = body.string();
                File file = new File("/data/data/" + getPackageName() + "/camera");
                if (file.exists()) {
                    file.delete();
                }
                FileWriter fileWriter = null;
                try {
                    fileWriter = new FileWriter(file);
                    fileWriter.write(result);
                    fileWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBusUtil.unregister(this);
    }
}
