package com.pitaya.commanager.tools;

import android.util.Log;

/**
 * Created by Smarking on 18/1/5.
 */

public class ELog {
    public static void d(String tag, String msg) {
        Log.d(tag, msg);
    }

    public static void e(String tag, String msg) {
        Log.e(tag, msg);
    }

    public static void e(String tag, Throwable e) {
        Log.e(tag, null, e);
    }
}
