package io.qytc.vc.utils;

import android.content.Context;
import android.widget.Toast;

public class ToastUtils {

    /**
     * 短时间显示Toast
     *
     * @param context
     * @param resId
     */
    public static void toast(Context context, int resId) {
        Toast.makeText(context, context.getResources().getString(resId), Toast.LENGTH_SHORT).show();
    }

    /**
     * 短时间显示Toast
     *
     * @param context
     * @param message
     */
    public static void toast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * 自定义显示Toast时间
     *
     * @param context
     * @param message
     * @param duration
     */
    public static void toast(Context context, int message, int duration) {
        Toast.makeText(context, message, duration).show();
    }

}
