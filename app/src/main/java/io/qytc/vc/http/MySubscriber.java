package io.qytc.vc.http;

import android.content.Context;
import android.util.Log;

import io.qytc.vc.utils.NetUtil;
import io.qytc.vc.utils.ToastUtils;
import rx.Subscriber;

public abstract class MySubscriber<T> extends Subscriber<T> {
    private Context context;

    public MySubscriber(Context context) {
        this.context = context;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i("tag","MySubscriber.onStart()");
        //接下来可以检查网络连接等操作
       /* if (NetUtil.NET_NOCONNECT == NetUtil.isNetworkAvailable()) {

            ToastUtils.toast(context,"当前网络不可用，请检查网络情况",false);
            // 一定好主动调用下面这一句,取消本次Subscriber订阅
            if (!isUnsubscribed()) {
                unsubscribe();
            }
            return;
        }*/
    }

    @Override
    public void onError(Throwable e) {
        Log.e("tag","MySubscriber.throwable ="+e.toString());
        Log.e("tag","MySubscriber.throwable ="+e.getMessage());

        if(e instanceof Exception){
            //访问获得对应的Exception
            onError(ExceptionHandle.handleException(e));
        }else {
            //将Throwable 和 未知错误的status code返回
            onError(new ExceptionHandle.ResponeThrowable(e,ExceptionHandle.ERROR.UNKNOWN));
        }
    }

    public abstract void onError(ExceptionHandle.ResponeThrowable responeThrowable);

    @Override
    public void onCompleted() {
        Log.i("tag","MySubscriber.onComplete()");
    }
}
