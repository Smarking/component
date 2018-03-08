package com.pitaya.printer.component;

import com.pitaya.printer.component.protocol.PrinterComProtocolImpl;

/**
 * Created by Smarking on 17/12/12.
 */
@SuppressWarnings("unused")
public class PrinterComponent {

    protected boolean onInstall() {
        return true;
    }

    public String getComponentName() {
        return PrinterComProtocolImpl.ComponentName;
    }

    public Class<?>[] getEvent() {
        return null;
    }
}
