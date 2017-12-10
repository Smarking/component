package com.pitaya.vippay.runalone;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.pitaya.vippay.R;
import com.pitaya.vippay.dialog.VerifyPhoneDialog;
import com.pitaya.vippay.dialog.VipDetailDialog;

public class VipPayTestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vippay_activity_vippay_test);

        findViewById(R.id.openVerifyDialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VerifyPhoneDialog.newInstance("从哪里进来的").show(getSupportFragmentManager(), "openVerifyDialog");
            }
        });

        findViewById(R.id.openDetailDialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VipDetailDialog.newInstance("从哪里来的呢").show(getSupportFragmentManager(), "openDetailDialog");

            }
        });
    }
}
