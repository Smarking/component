package com.pitaya.takeout.runalone.application;

import android.app.Application;


/**
 * Created by mrzhang on 2017/6/20.
 */

public class TakeoutApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //如果isRegisterCompoAuto为false，则需要通过反射加载组件
//        Router.registerComponent("com.mrzhang.share.applike.ShareApplike");
    }

}
