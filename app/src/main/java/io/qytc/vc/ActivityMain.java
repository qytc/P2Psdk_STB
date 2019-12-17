package io.qytc.vc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import io.qytc.p2psdk.activity.HomeActivity;

public class ActivityMain extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


//        启动通话页面的方法
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }
}
