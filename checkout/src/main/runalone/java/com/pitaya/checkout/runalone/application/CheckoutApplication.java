package com.pitaya.checkout.runalone.application;

import android.app.Application;

public class CheckoutApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //如果isRegisterCompoAuto为false，则需要通过反射加载组件
//        Router.registerComponent("com.mrzhang.share.applike.ShareApplike");
    }

}
