package com.pitaya.commanager.proxy;

import android.os.Looper;
import android.support.annotation.Nullable;

import com.pitaya.comannotation.Subscribe;
import com.pitaya.comannotation.ThreadMode;
import com.pitaya.comannotation.Unbinder;
import com.pitaya.commanager.Disposable;
import com.pitaya.commanager.poster.AsyncPoster;
import com.pitaya.commanager.poster.BackgroundPoster;
import com.pitaya.commanager.poster.HandlerPoster;
import com.pitaya.commanager.poster.MainThreadSupport;
import com.pitaya.commanager.poster.PendingPost;
import com.pitaya.commanager.tools.ComponentTools;
import com.pitaya.commanager.tools.ELog;

import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * 动态代理支持线程切换
 */
public class ThreadProxyHandler implements InvocationHandler {

    private static final String TAG = "ThreadProxyHandler";
    private static final Method OBJECT_EQUALS = getObjectMethod(Object.class, "equals", Object.class);
    private static final Method DISPOSE_unRegister = getObjectMethod(Disposable.class, "dispose");

    private static Method getObjectMethod(Class target, String name, Class... types) {
        try {
            // null 'types' is OK.
            return target.getMethod(name, types);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private static boolean equalsInternal(Object me, Object other) {
        if (other == null) {
            return false;
        }
        return me.hashCode() == other.hashCode();
    }

    public final static Executor DefaultBackgroundThreadPool = new ThreadPoolExecutor(0, Integer.MAX_VALUE,
            60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());


    // @Nullable
    private static MainThreadSupport mainThreadSupport;

    private MainThreadSupport getMainThreadSupport() {
        if (mainThreadSupport == null) {
            synchronized (MainThreadSupport.class) {
                if (mainThreadSupport == null) {
                    mainThreadSupport = new MainThreadSupport.AndroidHandlerMainThreadSupport(Looper.getMainLooper());
                }
            }
        }
        return mainThreadSupport;
    }

    // @Nullable
    private HandlerPoster mainThreadPoster;
    private BackgroundPoster backgroundPoster;
    private AsyncPoster asyncPoster;

    Class<?> interfaceName;
    Object target;
    Subscribe outsideThreadMode;

    public ThreadProxyHandler(final Class<?> interfaceName, final Object target, @Nullable Subscribe threadMode) {
        this.interfaceName = interfaceName;
        this.target = target;

        //由外部指定执行线程
        outsideThreadMode = threadMode;

        mainThreadPoster = getMainThreadSupport().createPoster(this);
        backgroundPoster = new BackgroundPoster(this);
        asyncPoster = new AsyncPoster(this);

        //提前解析 当前类
    }

    /**
     * 销毁
     */
    public void onDestroy() {
        mainThreadPoster.onDestroy();
        backgroundPoster.onDestroy();
        asyncPoster.onDestroy();
        target = null;
        if (mCallbackList.isEmpty()) {
            return;
        }
        for (WeakReference<Proxy> weakReference : mCallbackList) {
            Proxy proxy = weakReference.get();
            if (proxy == null) {
                continue;
            }
            Unbinder unbinder = ComponentTools.getInstance().getUnBinderCacheMap().remove(proxy);
            if (unbinder != null) {
                unbinder.unbind();
            }
            ThreadProxyHandler handler = (ThreadProxyHandler) Proxy.getInvocationHandler(proxy);
            handler.onDestroy();
        }
    }

    private List<WeakReference<Proxy>> mCallbackList = new ArrayList<>();

    @Override
    public Object invoke(Object proxy, final Method method, Object... args)
            throws Throwable {
        // If the method is a method from Object then defer to normal invocation.
        if (method.getDeclaringClass() == Object.class) {
            if (method.equals(OBJECT_EQUALS)) {
                return equalsInternal(proxy, args[0]);
            }
            return method.invoke(this, args);
        }

        //如果来自AbsProtocol的方法，则立即执行
        if (method.getDeclaringClass() == Disposable.class) {
            ELog.e("zxx", method.getName());
            if (method.equals(DISPOSE_unRegister)) {
                onDestroy();
                return null;
            }
        }

        if (unRegister(method)) {
            target = null;
            return null;
        }

        if (target == null) {
            ELog.e(TAG, "target object is null");
            return null;
        }

        //属性注解
        final Object[] reallyArgs = ProcessArgsTools.Instance.processArgs(mCallbackList, method, args);
        //判断是否实现 AbsProtocol类，如果是，则要记录下callback的proxy方法，方便生命周期结束时执行反注册，防止内存泄漏


        boolean isMainThread = false;
        if (Looper.getMainLooper() == Looper.myLooper()) {
            isMainThread = true;
        }

        //外部指定执行线程
        Subscribe thread = outsideThreadMode;

        //方法注解
        if (thread == null) {
            thread = method.getAnnotation(Subscribe.class);
        }

        if (thread == null) {
            //类注解
            thread = interfaceName.getAnnotation(Subscribe.class);
        }

        //default ThreadMode.POSTING
        if (thread == null) {
            return innerInvoke(method, target, reallyArgs);
        }

        //当前线程
        if (thread.threadMode().equals(ThreadMode.POSTING)) {
            return innerInvoke(method, target, reallyArgs);
        }

        //主线程
        if (thread.threadMode().equals(ThreadMode.MAIN)) {
            if (isMainThread) {
                return innerInvoke(method, target, reallyArgs);
            }
            mainThreadPoster.enqueue(PendingPost.obtainPendingPost(method, target, reallyArgs));
            return null;
        }

        //异步主线程
        if (thread.threadMode().equals(ThreadMode.MAIN_ORDERED)) {
            mainThreadPoster.enqueue(PendingPost.obtainPendingPost(method, target, reallyArgs));
            return null;
        }

        //后台线程
        if (thread.threadMode().equals(ThreadMode.BACKGROUND)) {
            if (isMainThread) {
                backgroundPoster.enqueue(PendingPost.obtainPendingPost(method, target, reallyArgs));
                return null;
            }
            return innerInvoke(method, target, reallyArgs);
        }

        //异步
        if (thread.threadMode().equals(ThreadMode.ASYNC)) {
            asyncPoster.enqueue(PendingPost.obtainPendingPost(method, target, reallyArgs));
            return null;
        }

        return innerInvoke(method, target, reallyArgs);
    }

    public Object innerInvoke(Method method, Object target, Object[] args) {
        //已经被释放了
        if (target == null) {
            ELog.d(TAG, "target is null ," + method.toString());
            return null;
        }
        try {
            long timeOld = System.currentTimeMillis();
            Object result = method.invoke(target, args);
            ELog.d(TAG, String.format("%1$dms | ThreadName:%2$s | MethodName:%3$s ", System.currentTimeMillis() - timeOld, Thread.currentThread().getName(), method.toString()));
            return result;
        } catch (IllegalAccessException e) {
            ELog.e(TAG, e);
        } catch (InvocationTargetException e) {
            ELog.e(TAG, e);
        }
        return null;
    }

    private boolean unRegister(Method method) {
        return false;
    }

}


//    /**
//     * 5个核心线程，阻塞队列最大容量20个，MAX_VALUE个最大线程数。根据业务类型可调优
//     */
//    private final static Executor mBackgroundThreadPool = new ThreadPoolExecutor(5, Integer.MAX_VALUE,
//            60L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(20));
//TODO 可以借鉴Eventbus mMainHandler.sendEmptyMessage() 这样的话有内存泄漏，最好的做法是，sendEmptyMessage通知。但是不传递有效内容
//    private static Handler mMainHandler = new Handler(Looper.getMainLooper()) {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            MethodInfo methodInfo = (MethodInfo) msg.obj;
//            try {
//                methodInfo.method.innerInvoke(methodInfo.target, methodInfo.args);
//            } catch (IllegalAccessException e) {
//                e.printStackTrace();
//            } catch (InvocationTargetException e) {
//                e.printStackTrace();
//            }
//        }
//    };

