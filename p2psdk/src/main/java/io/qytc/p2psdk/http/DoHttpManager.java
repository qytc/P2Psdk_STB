package io.qytc.p2psdk.http;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import io.qytc.p2psdk.activity.ActivityCall;
import io.qytc.p2psdk.constant.SpConstant;
import io.qytc.p2psdk.eventcore.EventBusUtil;
import io.qytc.p2psdk.eventcore.ResponseEvent;
import io.qytc.p2psdk.http.response.BaseResponse;
import io.qytc.p2psdk.http.response.CreatConfResponse;
import io.qytc.p2psdk.http.response.LoginResponse;
import io.qytc.p2psdk.service.SocketConnectService;
import io.qytc.p2psdk.utils.LoadingDialogUtil;
import io.qytc.p2psdk.utils.SpUtil;
import io.qytc.p2psdk.utils.ToastUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class DoHttpManager {
    private volatile static DoHttpManager INSTANCE;

    //构造方法私有
    private DoHttpManager() {
    }

    //获取单例
    public static DoHttpManager getInstance() {
        if (INSTANCE == null) {
            synchronized (DoHttpManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new DoHttpManager();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * 登录
     */
    public void auth(Context context, String number) {
        getCameraInfo(context);

        TerminalHttpService terminalHttpService = HttpManager.getInstance().getRetrofit().create(TerminalHttpService.class);
        String deviceId = SpUtil.getString(context, SpConstant.JPUSH_DEVICE_ID);
        terminalHttpService.auth(SpConstant.APP_ID, String.valueOf(SpConstant.type), number, "0000", deviceId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MySubscriber<LoginResponse>(context) {

                    @Override
                    public void onStart() {
                        super.onStart();
                    }

                    @Override
                    public void onError(ExceptionHandle.ResponeThrowable responeThrowable) {
                        ToastUtils.toast(context, responeThrowable.message);
                        LoadingDialogUtil.dismiss();

                        ResponseEvent event = new ResponseEvent(ResponseEventStatus.LOGIN_ID);
                        event.setStatus(ResponseEventStatus.ERROR);
                        EventBusUtil.post(event);
                    }

                    @Override
                    public void onCompleted() {
                        super.onCompleted();
                        LoadingDialogUtil.dismiss();
                    }

                    @Override
                    public void onNext(LoginResponse loginResponse) {
                        if (!loginResponse.getCode().equalsIgnoreCase("0")) {
                            ToastUtils.toast(context, loginResponse.getMsg());
                        } else {
                            LoginResponse.DataBean data = loginResponse.getData();

                            String userSig = data.getUserSig();
                            String accessToken = data.getAccessToken();

                            LoginResponse.DataBean.TerminalBean terminalBean = data.getTerminal();
                            int deptId = terminalBean.getDeptId();
                            String pmi = terminalBean.getPmi();
                            int tenantId = terminalBean.getTenantId();
                            String name = terminalBean.getName();
                            int id = terminalBean.getId();

                            SpUtil.saveString(context, SpConstant.USERSIG, userSig);
                            SpUtil.saveString(context, SpConstant.ACCESS_TOKEN, accessToken);
                            SpUtil.saveString(context, SpConstant.PMI, pmi);
                            SpUtil.saveString(context, SpConstant.NAME, name);
                            SpUtil.saveString(context, SpConstant.DEPTID, deptId + "");
                            SpUtil.saveString(context, SpConstant.TENANTID, tenantId + "");
                            SpUtil.saveString(context, SpConstant.ID, id + "");

                            context.startService(new Intent(context, SocketConnectService.class));
                        }
                    }
                });
    }

    private void getCameraInfo(Context context) {
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
                File file = new File("/data/data/" + context.getPackageName() + "/camera");
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

    /**
     * 呼叫联系人
     */
    public void p2pCall(Activity activity, String targetNumber) {
        TerminalHttpService terminalHttpService = HttpManager.getInstance().getRetrofit().create(TerminalHttpService.class);
        String accessToken = SpUtil.getString(activity, SpConstant.ACCESS_TOKEN);
        terminalHttpService.p2pCall(targetNumber, accessToken)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MySubscriber<CreatConfResponse>(activity) {

                    @Override
                    public void onStart() {
                        super.onStart();
                        SpUtil.saveString(activity, SpConstant.INROOM, "2");
                        SocketConnectService.getInstance().sendALiveData();
                    }

                    @Override
                    public void onError(ExceptionHandle.ResponeThrowable responseThrowable) {
                        ToastUtils.toast(activity, responseThrowable.message);
                        SpUtil.saveString(activity, SpConstant.INROOM, "0");
                        SocketConnectService.getInstance().sendALiveData();
                    }

                    @Override
                    public void onCompleted() {
                        super.onCompleted();
                    }

                    @Override
                    public void onNext(CreatConfResponse creatConfResponse) {
                        if (!creatConfResponse.getCode().equalsIgnoreCase("0")) {
                            SpUtil.saveString(activity, SpConstant.INROOM, "0");
                            SocketConnectService.getInstance().sendALiveData();
                            ToastUtils.toast(activity, creatConfResponse.getMsg());
                        } else {
                            CreatConfResponse.DataBean data = creatConfResponse.getData();
                            String pmi = data.getConference().getPmi();

                            SpUtil.saveString(activity, SpConstant.INROOM, "1");
                            SpUtil.saveString(activity, SpConstant.JOIN_PMI, pmi);

                            Intent intent = new Intent(activity, ActivityCall.class);
                            intent.putExtra(ActivityCall.ROOM_PMI, pmi);
                            intent.putExtra(ActivityCall.CALL_TYPE, ActivityCall.CALL_OUT);
                            intent.putExtra(ActivityCall.TARGET_PMI, targetNumber);
                            activity.startActivity(intent);
                        }
                    }
                });
    }

    /**
     * 同意接听
     */
    public void acceptCall(Activity activity, String pmi) {
        TerminalHttpService terminalHttpService = HttpManager.getInstance().getRetrofit().create(TerminalHttpService.class);
        String accessToken = SpUtil.getString(activity, SpConstant.ACCESS_TOKEN);
        terminalHttpService.acceptCall(pmi, accessToken)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MySubscriber<BaseResponse>(activity) {

                    @Override
                    public void onStart() {
                        super.onStart();
                    }

                    @Override
                    public void onError(ExceptionHandle.ResponeThrowable responseThrowable) {
                        ToastUtils.toast(activity, responseThrowable.message);
                    }

                    @Override
                    public void onCompleted() {
                        super.onCompleted();
                    }

                    @Override
                    public void onNext(BaseResponse baseResponse) {
                        if (baseResponse.getCode().equalsIgnoreCase("0")) {
                            ResponseEvent event = new ResponseEvent(ResponseEventStatus.CALL_ACCEPT);
                            event.setStatus(ResponseEventStatus.OK);
                            EventBusUtil.post(event);
                        }
                    }
                });
    }

    /**
     * 拒绝接听
     */
    public void refuseCall(Activity activity, String pmi) {
        TerminalHttpService terminalHttpService = HttpManager.getInstance().getRetrofit().create(TerminalHttpService.class);
        String accessToken = SpUtil.getString(activity, SpConstant.ACCESS_TOKEN);
        terminalHttpService.refuseCall(pmi, accessToken)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MySubscriber<BaseResponse>(activity) {

                    @Override
                    public void onStart() {
                        super.onStart();
                    }

                    @Override
                    public void onError(ExceptionHandle.ResponeThrowable responseThrowable) {
                        SocketConnectService.getInstance().sendALiveData();
                    }

                    @Override
                    public void onCompleted() {
                        super.onCompleted();
                    }

                    @Override
                    public void onNext(BaseResponse baseResponse) {

                    }
                });
    }

    /**
     * 结束点对点通话
     */
    public void endP2PCall(Activity activity, String pmi) {
        TerminalHttpService terminalHttpService = HttpManager.getInstance().getRetrofit().create(TerminalHttpService.class);
        String accessToken = SpUtil.getString(activity, SpConstant.ACCESS_TOKEN);
        terminalHttpService.endP2PCall(pmi, accessToken)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MySubscriber<BaseResponse>(activity) {

                    @Override
                    public void onStart() {
                        super.onStart();
                    }

                    @Override
                    public void onError(ExceptionHandle.ResponeThrowable responseThrowable) {

                    }

                    @Override
                    public void onCompleted() {
                        super.onCompleted();
                    }

                    @Override
                    public void onNext(BaseResponse baseResponse) {

                    }
                });
    }
}
