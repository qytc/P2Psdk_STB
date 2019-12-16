package io.qytc.vc.utils;

public class Check {
    static {
        System.loadLibrary("native-lib");
    }

    public static native boolean supportHD();
}
