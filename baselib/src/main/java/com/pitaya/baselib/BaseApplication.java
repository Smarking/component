package com.pitaya.baselib;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.UiThread;

import com.elvishew.xlog.XLog;
import com.pitaya.baselib.bean.BaseBuildConfig;
import com.pitaya.baselib.bean.BaseUserInfo;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Smarking on 18/1/9.
 */

public abstract class BaseApplication extends Application {
    protected boolean isComponentBuildConfig = false;
    protected boolean isComponentUserInfo = false;
    private Map<Integer, WeakReference<Activity>> mList = new HashMap<>();
    private Handler mHandler = new Handler();
    private static final String TAG = "DemoApplication";


    @Override
    public void onCreate() {
        super.onCreate();
        initBuildConfig();
        initXlog();
        initCheckLeaky();
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

    /**
     * 修改配置时调用该接口，通知所有组件
     *
     * @param baseBuildConfig
     */
    @UiThread
    private void initComponentBuildConfig(BaseBuildConfig baseBuildConfig) {
        if (baseBuildConfig == null) {
            isComponentBuildConfig = false;
            BuildConfigCenter.getInstance().init(null);
        } else {
            BuildConfigCenter.getInstance().init(baseBuildConfig);
            isComponentBuildConfig = true;
        }

        notifyBuildConfigChanged();
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

        notifyUserCenterChanged();
    }

    private void initXlog() {
        XLog.init();
    }

    private void initCheckLeaky() {
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                XLog.d(TAG, "新创建 " + activity.hashCode());
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
                final String className = activity.getClass().getName();
                mList.put(hashCode, new WeakReference(activity));
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mList.get(hashCode).get() == null) {
                            XLog.d(TAG, "没有泄漏 " + hashCode + " " + className);
                            return;
                        }

                        XLog.d(TAG, "有泄漏 " + hashCode + mList.get(hashCode).get().getClass().getName());
                    }
                }, 10000);
            }
        });
    }


    protected abstract void notifyUserCenterChanged();

    protected abstract void notifyBuildConfigChanged();

    protected abstract void initComponent();
}
