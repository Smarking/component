package com.pitaya.checkout.runalone.application;

import com.pitaya.baselib.BaseApplication;
import com.pitaya.commanager.ComManager;
import com.pitaya.comprotocol.checkout.CheckoutComProtocol;
import com.pitaya.comprotocol.printer.bean.PrinterComProtocol;
import com.pitaya.comprotocol.vippay.VipPayComProtocol;

public class CheckoutApplication extends BaseApplication {

    @Override
    protected void notifyUserCenterChanged() {
        ComManager.getInstance().notifyBuildConfigChanged();
    }

    @Override
    protected void notifyBuildConfigChanged() {
        ComManager.getInstance().notifyUserCenterChanged();
    }

    @Override
    protected void initComponent() {
        ComManager.getInstance().installComponent(this, PrinterComProtocol.ComponentName);
        ComManager.getInstance().installComponent(this, VipPayComProtocol.ComponentName);
        ComManager.getInstance().installComponent(this, CheckoutComProtocol.ComponentName);
    }

}
