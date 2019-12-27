package io.qytc.vc;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;

import java.util.List;

import io.qytc.p2psdk.SDKCore;
import io.qytc.p2psdk.utils.CmdUtil;


public class TRTCApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        if (!getPackageName().equals(getAppName(this, android.os.Process.myPid()))) {
            return;//非主线程不做初始化操作
        }

        SDKCore sdkCore = new SDKCore(this);

        String cardNo = CmdUtil.getCardNo();
        sdkCore.Login(cardNo);
    }

    /**
     * 根据Pid获取当前进程的名字
     */
    private String getAppName(Context context, int pid) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List list = activityManager.getRunningAppProcesses();
        for (Object o : list) {
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (o);
            try {
                if (info.pid == pid) {
                    return info.processName;// 根据进程的信息获取当前进程的名字
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
