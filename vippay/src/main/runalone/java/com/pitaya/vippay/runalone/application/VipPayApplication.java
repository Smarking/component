package com.pitaya.vippay.runalone.application;

import android.app.Application;

import com.elvishew.xlog.XLog;

/**
 * Created by Smarking on 17/12/10.
 */

public class VipPayApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        XLog.init();
    }
}
