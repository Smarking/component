package com.pitaya.vippay.utils;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

/**
 * Created by Smarking on 18/1/2.
 */

public class FragmentUtils {

    public static void showDialog(FragmentManager fragmentManager, Fragment fragment) {
        if (fragment.isAdded()) {
            fragmentManager.beginTransaction().show(fragment).commitAllowingStateLoss();
        } else {
            fragmentManager.beginTransaction().add(fragment, fragment.getClass().getName()).commitAllowingStateLoss();
        }
    }
}
