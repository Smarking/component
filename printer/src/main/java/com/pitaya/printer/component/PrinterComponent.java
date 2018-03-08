package com.pitaya.printer.component;

import com.pitaya.commanager.BaseComLifecycle;
import com.pitaya.printer.component.protocol.PrinterComProtocolImpl;

/**
 * Created by Smarking on 17/12/12.
 */
@SuppressWarnings("unused")
public class PrinterComponent extends BaseComLifecycle {

    @Override
    protected boolean onInstall() {
        addProtocol(new PrinterComProtocolImpl());
        return true;
    }

    @Override
    public void onUserCenterChanged() {
        super.onUserCenterChanged();
    }

    @Override
    public String getComponentName() {
        return PrinterComProtocolImpl.ComponentName;
    }

    @Override
    public Class<?>[] getEvent() {
        return null;
    }
}
