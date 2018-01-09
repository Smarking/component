package com.pitaya.vippay.runalone.application;

import com.pitaya.baselib.BaseApplication;
import com.pitaya.commanager.ComManager;
import com.pitaya.comprotocol.checkout.CheckoutComProtocol;
import com.pitaya.comprotocol.vippay.VipPayComProtocol;

/**
 * Created by Smarking on 17/12/10.
 */

public class VipPayApplication extends BaseApplication {

    @Override
    protected void notifyUserCenterChanged() {
        ComManager.getInstance().notifyUserCenterChanged();
    }

    @Override
    protected void notifyBuildConfigChanged() {
        ComManager.getInstance().notifyBuildConfigChanged();
    }

    @Override
    protected void initComponent() {
        ComManager.getInstance().installComponent(this, VipPayComProtocol.ComponentName, isComponentBuildConfig, isComponentUserInfo);
        ComManager.getInstance().installComponent(this, CheckoutComProtocol.ComponentName, isComponentBuildConfig, isComponentUserInfo);

    }
}
