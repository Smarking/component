package com.pitaya.componentdemo.application;

import android.support.annotation.UiThread;

import com.pitaya.baselib.BaseApplication;
import com.pitaya.commanager.ComLifecycle;
import com.pitaya.commanager.ComManager;
import com.pitaya.comprotocol.checkout.CheckoutComProtocol;
import com.pitaya.comprotocol.printer.bean.PrinterComProtocol;
import com.pitaya.comprotocol.vippay.VipPayComProtocol;

/**
 * Created by Smarking on 17/12/12.
 */

public class DemoApplication extends BaseApplication {
    private static DemoApplication mInstance;

    public static DemoApplication getApp() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = DemoApplication.this;
        //to do something  ......
    }

    @Override
    protected void notifyBuildConfigChanged() {
        ComManager.getInstance().notifyBuildConfigChanged();
    }

    @Override
    protected void notifyUserCenterChanged() {
        ComManager.getInstance().notifyUserCenterChanged();
    }

    @Override
    protected void initComponent() {
        installComponent(VipPayComProtocol.ComponentName);
        installComponent(CheckoutComProtocol.ComponentName);
        installComponent(PrinterComProtocol.ComponentName);
    }

    /**
     * 安装新组件，如果isComponentBuildConfig 、isComponentUserInfo 条件满足则立即更新
     *
     * @param componentClassName
     * @return
     */
    @UiThread
    public ComLifecycle installComponent(String componentClassName) {
        return ComManager.getInstance().installComponent(this, componentClassName,
                isComponentBuildConfig, isComponentUserInfo);
    }

    @UiThread
    public void unInstallComponent(String componentClassName) {
        ComManager.getInstance().unInstallComponent(componentClassName);
    }


}
