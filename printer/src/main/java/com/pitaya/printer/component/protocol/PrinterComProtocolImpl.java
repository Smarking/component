package com.pitaya.printer.component.protocol;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.pitaya.comprotocol.printer.PrinterComProtocol;
import com.pitaya.printer.PrinterActivity;

/**
 * Created by Smarking on 17/12/12.
 */

public class PrinterComProtocolImpl implements PrinterComProtocol {

    @Override
    public void print(String msg) {
        Log.e("PrinterComProtocolImpl", msg);
    }

    @Override
    public void openPrinterPage(Context context) {
        Intent intent = new Intent(context, PrinterActivity.class);
        context.startActivity(intent);
    }

}
