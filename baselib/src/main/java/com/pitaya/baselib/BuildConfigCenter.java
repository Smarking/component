package com.pitaya.baselib;

import com.pitaya.baselib.bean.BaseBuildConfig;

/**
 * Created by Smarking on 17/12/12.
 */

public class BuildConfigCenter {


    private BaseBuildConfig mBaseBuildConfig;

    private BuildConfigCenter() {
    }

    private static volatile BuildConfigCenter mInstance;

    public static BuildConfigCenter getInstance() {
        if (mInstance == null) {
            synchronized (UserCenter.class) {
                if (mInstance == null) {
                    mInstance = new BuildConfigCenter();
                }
            }
        }
        return mInstance;
    }

    public void init(BaseBuildConfig baseBuildConfig) {
        mBaseBuildConfig = baseBuildConfig;
    }

    public BaseBuildConfig getBaseBuildConfig() {
        return mBaseBuildConfig;
    }
}
