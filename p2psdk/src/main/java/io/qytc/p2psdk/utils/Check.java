package io.qytc.p2psdk.utils;

public class Check {
    static {
        System.loadLibrary("native-lib");
    }

    public static native boolean supportHD();
}
