package io.qytc.vc.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.ml.android.eventcore.EventBusUtil;
import com.ml.android.eventcore.ResponseEvent;

import io.qytc.vc.R;
import io.qytc.vc.constant.SpConstant;
import io.qytc.vc.http.DoHttpManager;
import io.qytc.vc.http.ResponseEventStatus;
import io.qytc.vc.http.response.LoginResponse;
import io.qytc.vc.service.SocketConnectService;
import io.qytc.vc.utils.CmdUtil;
import io.qytc.vc.utils.SpUtil;
import io.qytc.vc.utils.ToastUtils;

public class LoginActivity extends Activity {

    private TextView mTvLoginTip;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        mTvLoginTip = findViewById(R.id.tv_login_tip);

        EventBusUtil.register(this);

        login();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBusUtil.unregister(this);
    }
}
