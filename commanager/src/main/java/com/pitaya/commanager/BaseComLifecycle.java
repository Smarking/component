package com.pitaya.commanager;

import android.app.Application;
import android.support.annotation.UiThread;
import android.text.TextUtils;

import com.pitaya.comannotation.ProtocolName;

/**
 * Created by Smarking on 17/12/11.
 */

@UiThread
public abstract class BaseComLifecycle implements ComLifecycle {
    private ComProtocolCenter mProtocolCenter = new ComProtocolCenter();

    protected boolean isAlive = false;

    public Application mApplication;

    @Override
    public boolean install(Application application) {
        isAlive = true;
        mApplication = application;
        return onInstall();
    }

    @Override
    public void unInstall() {
        //TODO 干掉当前组件相关缓存
        onUnInstall();
        mProtocolCenter.removeAllProtocol();
        mApplication = null;
        isAlive = false;
    }

    @Override
    public boolean isAlive() {
        return isAlive;
    }

    @Override
    public <T> T getProtocol(String protocolName) {
        if (TextUtils.isEmpty(protocolName)) {
            throw new NullPointerException("getProtocolAndBind protocolName is null");
        }
        return (T) mProtocolCenter.getProtocol(protocolName);
    }

    protected void addProtocol(Object protocolImpl) {
        if (protocolImpl == null || protocolImpl.getClass().isInterface()) {
            throw new NullPointerException("addProtocol protocolImpl is null or is interface");
        }

        if (!protocolImpl.getClass().getSuperclass().isAssignableFrom(AbsProtocol.class)) {
            throw new NullPointerException("addProtocol protocolImpl must extends AbsProtocol.class");
        }

        ((AbsProtocol) protocolImpl).setComponent(this);

        Class<?>[] interfaces = protocolImpl.getClass().getInterfaces();

        if (interfaces == null) {
            throw new IllegalArgumentException("addProtocol protocolImpl not implement interface");
        }

        if (interfaces.length > 1) {
            throw new IllegalArgumentException("addProtocol protocolImpl implement more than one interface");
        }

        ProtocolName annotation = interfaces[0].getAnnotation(ProtocolName.class);
        if (annotation == null) {
            throw new IllegalArgumentException("addProtocol " + protocolImpl.getClass().getName() + "has no ProtocolName Annotation");
        }

        String protocolName = annotation.value();
        mProtocolCenter.addProtocol(protocolName, protocolImpl);
    }

    protected boolean onInstall() {
        return true;
    }

    protected void onUnInstall() {
    }

    @Override
    public void onBuildConfigChanged() {

    }

    @Override
    public void onUserCenterChanged() {

    }
}
