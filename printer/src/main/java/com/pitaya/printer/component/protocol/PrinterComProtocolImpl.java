package com.pitaya.printer.component.protocol;

import android.util.Log;

import com.pitaya.commanager.AbsProtocol;
import com.pitaya.comprotocol.printer.PrinterComProtocol;

/**
 * Created by Smarking on 17/12/12.
 */

public class PrinterComProtocolImpl extends AbsProtocol implements PrinterComProtocol {

    @Override
    public void print(String msg) {
        Log.e("PrinterComProtocolImpl", msg);
    }
}
