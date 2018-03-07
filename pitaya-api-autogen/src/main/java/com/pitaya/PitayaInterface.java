package com.pitaya;

import android.content.Context;
import android.support.annotation.MainThread;
import android.support.v7.widget.RecyclerView;

/**
 * Created by Smarking on 18/3/6.
 */

public class PitayaInterface {
    @Override
    public String toString() {
        return "com.pitaya.PitayaInterface";
    }

    @MainThread
    static void test() {
        Context context = null;
        RecyclerView recyclerView = new RecyclerView(context);
    }
}
