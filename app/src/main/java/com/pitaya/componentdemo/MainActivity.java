package com.pitaya.componentdemo;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.pitaya.baselib.bean.BaseUserInfo;
import com.pitaya.comannotation.Unbinder;
import com.pitaya.commanager.ComManager;
import com.pitaya.componentdemo.application.DemoApplication;
import com.pitaya.comprotocol.checkout.CheckoutComProtocol;
import com.pitaya.comprotocol.printer.bean.PrinterComProtocol;
import com.pitaya.comprotocol.vippay.VipPayComProtocol;
import com.pitaya.comprotocol.vippay.bean.Coupon;
import com.pitaya.comprotocol.vippay.bean.VipUserInfo;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Unbinder mVipLoginUnbinder;
    private Unbinder mVipLogoutUnbinder;
    private Unbinder mOpenVipCampaignDialogUnbinder;

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
                ComManager.getInstance().getProtocol(CheckoutComProtocol.class).openCheckoutPage(MainActivity.this);
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void mockLogin() {
        Toast.makeText(getApplicationContext(), "门店登录中...", Toast.LENGTH_LONG).show();

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
        }, 1000 * 4);
    }


//    private void testInvoke() {
//        //TODO 设置回调的方式，走代理，为了线程切换，支持入参、方法、类三种注解
//
//        //方式一 独享
//        mOpenVipCampaignDialogUnbinder = ComManager.getInstance().getProtocol(VipPayComProtocol.class)
//                .openVipCampaignDialog(MainActivity.this, new Order(),
//                        ProxyTools.create(VipPayComProtocol.VipCampaignCallback.class, new VipClass()));
//
//        //方式二 匿名内部类，共享一个Callback
//        mOpenVipCampaignDialogUnbinder = ComManager.getInstance().getProtocol(VipPayComProtocol.class)
//                .openVipCampaignDialog(MainActivity.this, new Order(),
//
//                        ProxyTools.create(VipPayComProtocol.VipCampaignCallback.class, campaignCallback));
//        //方式三 用labmda呢
//        //用Labmda的方式
//
//        mVipLoginUnbinder = ComManager.getInstance().registerEventReceiver(mLoginEvent);
//    }


    VipPayComProtocol.VipCampaignCallback campaignCallback = new VipPayComProtocol.VipCampaignCallback() {


        @Override
        public void unbind() {
            mOpenVipCampaignDialogUnbinder.unbind();
        }

        @Override
        public void onSortedCouponList(List<Coupon> sortedList) {


        }

        @Override
        public void onSelectedCoupon(String calculateInfo) {

        }

        @Override
        public void onPresetPay(Coupon coupon) {

        }

        @Override
        public void onError(String msg) {

        }
    };

    class VipClass implements VipPayComProtocol.VipCampaignCallback {

        @Override
        public void onSortedCouponList(List<Coupon> sortedList) {

        }

        @Override
        public void onSelectedCoupon(String calculateInfo) {

        }

        @Override
        public void onPresetPay(Coupon coupon) {

        }

        @Override
        public void onError(String msg) {

        }

        @Override
        public void unbind() {

        }
    }

    private VipPayComProtocol.LoginEvent mLoginEvent = new VipPayComProtocol.LoginEvent() {

        @Override
        public void call(VipUserInfo param) {

        }
    };

}
