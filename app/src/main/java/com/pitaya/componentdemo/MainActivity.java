package com.pitaya.componentdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.pitaya.comannotation.Unbinder;
import com.pitaya.comcallback.Callback1;
import com.pitaya.commanager.ComManager;
import com.pitaya.commanager.ProxyTools;
import com.pitaya.comprotocol.checkout.bean.Order;
import com.pitaya.comprotocol.vippay.VipPayComProtocol;
import com.pitaya.comprotocol.vippay.bean.VipUserInfo;

public class MainActivity extends AppCompatActivity {

    private Unbinder mVipLoginUnbinder;
    private Unbinder mVipLogoutUnbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        findViewById(R.id.printerBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                VipPayComProtocol.VipCampaignCallback campaignCallback = new VipPayComProtocol.VipCampaignCallback() {

                    @Override
                    public void onPresetPay(String result) {

                    }

                    @Override
                    public void onError(String msg) {

                    }
                };


                ComManager.getInstance().getProtocol(VipPayComProtocol.class).openVipCampaignDialog(getBaseContext(), new Order(),
                        ProxyTools.create(VipPayComProtocol.VipCampaignCallback.class, campaignCallback));
            }
        });


        findViewById(R.id.checkoutBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ComManager.getInstance().getProtocol(VipPayComProtocol.class).cancelCheckoutVip(getBaseContext(), "string", new Callback1<String>() {
                    @Override
                    public void call(String param) {

                    }
                });
            }
        });


        //TODO 线程切换 协议
        mVipLoginUnbinder = ComManager.getInstance().registerStatusReceiver(VipPayComProtocol.LoginStatus.class,
                ProxyTools.create(VipPayComProtocol.LoginStatus.class, mLoginStatus));

        //协议
        mVipLogoutUnbinder = ComManager.getInstance().registerStatusReceiver(VipPayComProtocol.LogoutStatus.class,
                ProxyTools.create(VipPayComProtocol.LogoutStatus.class, mLogoutStatus));
    }

    private VipPayComProtocol.LoginStatus mLoginStatus = new VipPayComProtocol.LoginStatus() {

        @Override
        public void call(VipUserInfo param) {

        }
    };


    private VipPayComProtocol.LogoutStatus mLogoutStatus = new VipPayComProtocol.LogoutStatus() {

        @Override
        public String call(VipUserInfo param) {
            return null;
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mVipLoginUnbinder.unbind();
        mVipLogoutUnbinder.unbind();
    }
}
