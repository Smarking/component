package com.pitaya.componentdemo;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.pitaya.baselib.bean.BaseUserInfo;
import com.pitaya.comannotation.Unbinder;
import com.pitaya.comcallback.Callback1;
import com.pitaya.commanager.ComManager;
import com.pitaya.commanager.ProxyTools;
import com.pitaya.componentdemo.application.DemoApplication;
import com.pitaya.comprotocol.checkout.CheckoutComProtocol;
import com.pitaya.comprotocol.checkout.bean.Order;
import com.pitaya.comprotocol.printer.bean.PrinterComProtocol;
import com.pitaya.comprotocol.vippay.VipPayComProtocol;
import com.pitaya.comprotocol.vippay.bean.VipUserInfo;

public class MainActivity extends AppCompatActivity {

    private Unbinder mVipLoginUnbinder;
    private Unbinder mVipLogoutUnbinder;
    private Unbinder mOpenVipCampaignDialogUnbinder;


    VipPayComProtocol.VipCampaignCallback campaignCallback = new VipPayComProtocol.VipCampaignCallback() {

        @Override
        public void onPresetPay(String result) {

        }

        @Override
        public void onError(String msg) {

        }
    };


    class VipClass implements VipPayComProtocol.VipCampaignCallback {

        @Override
        public void onPresetPay(String result) {

        }

        @Override
        public void onError(String msg) {

        }
    }

    //在哪里


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mockLogin();


        findViewById(R.id.printerBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ComManager.getInstance().getProtocol(PrinterComProtocol.class).print("有新的打印任务");
            }
        });


        findViewById(R.id.checkoutBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ComManager.getInstance().getProtocol(CheckoutComProtocol.class).calculateDiscountAndUpdateView((getBaseContext(),"string", new Callback1<String>() {
                    @Override
                    public void call(String param) {

                    }
                });
            }
        });


        //TODO 线程切换 协议 K-V怎么办
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


    private void testInvoke() {
        //TODO 设置回调的方式，走代理，为了线程切换，支持入参、方法、类三种注解

        //方式一 独享
        mOpenVipCampaignDialogUnbinder = ComManager.getInstance().getProtocol(VipPayComProtocol.class)
                .openVipCampaignDialog(getBaseContext(), new Order(), ProxyTools.create(VipPayComProtocol.VipCampaignCallback.class, new VipClass()));

        //方式二 匿名内部类，共享一个Callback
        mOpenVipCampaignDialogUnbinder = ComManager.getInstance().getProtocol(VipPayComProtocol.class)
                .openVipCampaignDialog(getBaseContext(), new Order(), ProxyTools.create(VipPayComProtocol.VipCampaignCallback.class, campaignCallback));
        //方式三 用labmda呢
        //用Labmda的方式

        //TODO 线程切换 协议 K-V怎么办
        mVipLoginUnbinder = ComManager.getInstance().registerStatusReceiver(VipPayComProtocol.LoginStatus.class,
                ProxyTools.create(VipPayComProtocol.LoginStatus.class, mLoginStatus));

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mVipLoginUnbinder.unbind();
        mVipLogoutUnbinder.unbind();
    }

    private void mockLogin() {
        new Handler(getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                BaseUserInfo baseUserInfo = new BaseUserInfo();
                baseUserInfo.loginToken = "PitayaComponentDemo123456789";
                baseUserInfo.loginUserName = "yuandan";
                baseUserInfo.poiId = "790681";
                baseUserInfo.tenantId = "7986";
                DemoApplication.getApp().initComponentUserInfo(baseUserInfo);
            }
        }, 1000 * 10);
    }
}
