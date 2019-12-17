package io.qytc.vc;

import android.app.Application;

import io.qytc.p2psdk.SDKCore;
import io.qytc.p2psdk.utils.CmdUtil;


public class TRTCApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SDKCore sdkCore = new SDKCore(this);

        String cardNo = CmdUtil.getCardNo();
        sdkCore.Login(cardNo);
    }
}
