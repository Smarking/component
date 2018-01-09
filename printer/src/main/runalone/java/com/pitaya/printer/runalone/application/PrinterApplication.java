package com.pitaya.printer.runalone.application;

import com.pitaya.baselib.BaseApplication;
import com.pitaya.commanager.ComManager;
import com.pitaya.comprotocol.printer.bean.PrinterComProtocol;

public class PrinterApplication extends BaseApplication {


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
        ComManager.getInstance().installComponent(this, PrinterComProtocol.ComponentName);
    }
}
