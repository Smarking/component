package com.pitaya.commanager;

import android.app.Application;
import android.support.annotation.UiThread;

/**
 * Created by Smarking on 17/12/11.
 */

@UiThread
public interface ComLifecycle {

    boolean install(Application application);

    void unInstall();

    boolean isAlive();

    void onBuildConfigChanged();

    void onUserCenterChanged();

    String getComponentName();

    <T> T getProtocol(String protocolName);
}
