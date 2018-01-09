package com.pitaya.vippay.runalone;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.pitaya.comprotocol.checkout.bean.Order;
import com.pitaya.vippay.R;
import com.pitaya.vippay.dialog.VerifyPhoneDialog;
import com.pitaya.vippay.dialog.VipDetailDialog;

public class VipPayTestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vippay_activity_vippay_test);

        final Order order = new Order();
        order.amount = 996f;
        order.orderId = "99ddff12345";
        order.receivable = order.amount;

        findViewById(R.id.openVerifyDialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VerifyPhoneDialog.newInstance(getSupportFragmentManager(), order).show(getSupportFragmentManager(), "openVerifyDialog");
            }
        });

        findViewById(R.id.openDetailDialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VipDetailDialog.newInstance(getSupportFragmentManager(), order).show(getSupportFragmentManager(), "openDetailDialog");

            }
        });
    }
}
