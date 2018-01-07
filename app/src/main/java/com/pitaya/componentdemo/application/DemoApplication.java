package com.pitaya.componentdemo.application;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.UiThread;
import android.util.Log;

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

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Smarking on 17/12/12.
 */

public class DemoApplication extends Application {
    private static DemoApplication mInstance;
    private boolean isComponentBuildConfig = false;
    private boolean isComponentUserInfo = false;
    private Map<Integer, WeakReference<Activity>> mList = new HashMap<>();
    private Handler mHandler = new Handler();
    private static final String TAG = "DemoApplication";


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
        initCheckLeaky();
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

    private void initCheckLeaky() {
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                Log.d(TAG, "新创建 " + activity.hashCode());
            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {

            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                final int hashCode = activity.hashCode();
                mList.put(hashCode, new WeakReference(activity));
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mList.get(hashCode).get() == null) {
                            Log.d(TAG, "没有泄漏 " + hashCode);
                            return;
                        }

                        Log.d(TAG, "有泄漏 " + hashCode + mList.get(hashCode).get().getClass().getName());
                    }
                }, 5000);
            }
        });
    }
}
