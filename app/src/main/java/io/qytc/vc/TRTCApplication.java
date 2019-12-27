package io.qytc.vc;

import android.app.Application;

import io.qytc.p2psdk.SDKCore;


public class TRTCApplication extends Application {

    private SDKCore sdkCore;

    @Override
    public void onCreate() {
        super.onCreate();
        sdkCore = new SDKCore(this);
    }
}
