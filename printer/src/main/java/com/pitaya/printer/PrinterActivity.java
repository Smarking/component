package com.pitaya.printer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.pitaya.componenta.ComponentAInterface;

//import com.pitaya.lib.MyClass;

public class PrinterActivity extends AppCompatActivity {
    private static final String TAG = "PrinterActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.printer_activity_printer);
        System.out.print(ComponentAInterface.class.toString());

//        MyClass myClass = new MyClass();
//        Log.d(TAG, myClass.toString());
    }
}
