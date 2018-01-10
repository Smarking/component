package com.pitaya.comprotocol.printer.bean;

import com.pitaya.comannotation.annotation.ProtocolName;

/**
 * Created by Smarking on 17/12/10.
 */
@ProtocolName("PrinterComProtocol")
public interface PrinterComProtocol {
    String ComponentName = "com.pitaya.printer.component.PrinterComponent";

    void print(String msg);

}
