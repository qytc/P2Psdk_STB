package io.qytc.vc;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import io.qytc.p2psdk.constant.SpConstant;
import io.qytc.p2psdk.http.DoHttpManager;
import io.qytc.p2psdk.utils.SpUtil;
import io.qytc.p2psdk.utils.ToastUtils;


public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mEtTargetNumber;
    private TextView mTvPersonPmi;
    private String mTargetNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initView();
    }

    private void initView() {
        mEtTargetNumber = findViewById(R.id.et_target_number);

        mTvPersonPmi = findViewById(R.id.tv_person_pmi);
        String personPmi = SpUtil.getString(this, SpConstant.PMI);
        mTvPersonPmi.setText(personPmi);

        Button btCall = findViewById(R.id.btn_call);
        btCall.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_call) {
            callTarget();
        }
    }

    private void callTarget() {
        mTargetNumber = mEtTargetNumber.getText().toString().trim();

        if (TextUtils.isEmpty(mTargetNumber)) {
            ToastUtils.toast(this, R.string.please_input_number);
            return;
        }

        DoHttpManager.getInstance().p2pCall(this, mTargetNumber);
    }
}
