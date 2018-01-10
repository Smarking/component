package com.pitaya.commanager.proxy;

import com.pitaya.comannotation.annotation.Subscribe;

import java.lang.reflect.Proxy;

/**
 * Created by Smarking on 17/12/12.
 */

public class ProxyTools {

    private static final String TAG = "ProxyTools";

    /**
     * @param mainInterface 提供给外部用的接口，比如开发者自定义的协议
     * @param subInterface  组件库内使用的接口，不对外暴露
     * @param target        代理的目标实例
     * @param <T>           目标实例接口
     * @return
     */
    public static <T> T create(final Class<T> mainInterface, Class subInterface, final Object target) {
        return (T) Proxy.newProxyInstance(mainInterface.getClassLoader(),
                new Class<?>[]{mainInterface, subInterface},
                new ThreadProxyHandler(mainInterface, target, null));
    }

    public static <T> T create(final Class<T> interfaceName, final Object target) {
        return create(interfaceName, target, null);
    }

    public static <T> T create(final Class<T> interfaceName, final Object target, Subscribe threadMode) {
        return (T) Proxy.newProxyInstance(interfaceName.getClassLoader(),
                new Class<?>[]{interfaceName},
                new ThreadProxyHandler(interfaceName, target, threadMode));
    }
}
