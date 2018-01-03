package com.pitaya.componentdemo.application;

import android.app.Application;
import android.support.annotation.UiThread;

import com.elvishew.xlog.XLog;
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
        initBuildConfig();
        initXlog();
        initComponent();
        //to do something  ......
    }

    private void initBuildConfig() {
        BaseBuildConfig baseBuildConfig = new BaseBuildConfig();
        baseBuildConfig.deviceId = "SN123456789";
        baseBuildConfig.osType = "arm-v7";
        baseBuildConfig.envOffline = true;
        baseBuildConfig.envUrlType = 101;

        initComponentBuildConfig(baseBuildConfig);
    }

    private void initXlog() {
        XLog.init();
    }

    private void initComponent() {
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

    /**
     * 修改配置时调用该接口，通知所有组件
     *
     * @param baseBuildConfig
     */
    @UiThread
    public void initComponentBuildConfig(BaseBuildConfig baseBuildConfig) {
        if (baseBuildConfig == null) {
            isComponentBuildConfig = false;
            BuildConfigCenter.getInstance().init(null);
        } else {
            BuildConfigCenter.getInstance().init(baseBuildConfig);
            isComponentBuildConfig = true;
        }

        ComManager.getInstance().notifyBuildConfigChanged();
    }

    /**
     * 登录、注销调用该接口，通知所有组件，注销时baseUserInfo == null，由各个业务方方判断。
     *
     * @param baseUserInfo
     */
    @UiThread
    public void initComponentUserInfo(BaseUserInfo baseUserInfo) {
        if (baseUserInfo == null) {
            isComponentUserInfo = false;
            UserCenter.getInstance().init(null);
        } else {
            UserCenter.getInstance().init(baseUserInfo);
            isComponentUserInfo = true;
        }

        ComManager.getInstance().notifyUserCenterChanged();
    }
}
