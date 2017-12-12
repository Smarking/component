package com.pitaya.componentdemo.application;

import android.app.Application;
import android.support.annotation.UiThread;

import com.pitaya.baselib.BuildConfigCenter;
import com.pitaya.baselib.UserCenter;
import com.pitaya.baselib.bean.BaseBuildConfig;
import com.pitaya.baselib.bean.BaseUserInfo;
import com.pitaya.commanager.ComLifecycle;
import com.pitaya.commanager.ComManager;
import com.pitaya.comprotocol.checkout.CheckoutComProtocol;
import com.pitaya.comprotocol.printer.bean.PrinterComProtocol;
import com.pitaya.comprotocol.vippay.VipPayComProtocol;

/**
 * Created by Smarking on 17/12/12.
 */

public class DemoApplication extends Application {
    private static DemoApplication mInstance;
    private boolean isComponentBuildConfig = false;
    private boolean isComponentUserInfo = false;


    public static DemoApplication getApp() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = DemoApplication.this;
        initComponent();
        //to do something  ......
    }

    private void initComponent() {
        installComponent(VipPayComProtocol.ComponentName);
        installComponent(CheckoutComProtocol.ComponentName);
        installComponent(PrinterComProtocol.ComponentName);

        BaseBuildConfig baseBuildConfig = new BaseBuildConfig();
        baseBuildConfig.deviceId = "SN123456789";
        baseBuildConfig.osType = "arm-v7";
        baseBuildConfig.envOffline = true;
        baseBuildConfig.envUrlType = 101;
        initComponentBuildConfig(baseBuildConfig);
    }

    /**
     * @param componentClassName
     * @return
     */
    @UiThread
    public ComLifecycle installComponent(String componentClassName) {
        ComLifecycle comLifecycle = ComManager.getInstance().installComponent(this, componentClassName);
        if (isComponentBuildConfig) {
            comLifecycle.onBuildConfigChanged();
        }
        if (isComponentUserInfo) {
            comLifecycle.onUserCenterChanged();
        }
        return comLifecycle;
    }

    @UiThread
    public void unInstallComponent(String componentClassName) {
        ComManager.getInstance().unInstallComponent(componentClassName);
    }

    @UiThread
    private void initComponentBuildConfig(BaseBuildConfig baseBuildConfig) {
        BuildConfigCenter.getInstance().init(baseBuildConfig);
        ComManager.getInstance().notifyBuildConfigChanged();
        isComponentBuildConfig = true;
    }

    @UiThread
    public void initComponentUserInfo(BaseUserInfo baseUserInfo) {
        UserCenter.getInstance().init(baseUserInfo);
        ComManager.getInstance().notifyUserCenterChanged();
        isComponentUserInfo = true;
    }
}
