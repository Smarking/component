/*
 * Copyright (C) 2012-2016 Markus Junginger, greenrobot (http://greenrobot.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.pitaya.commanager.poster;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;

import com.pitaya.commanager.exception.ComException;
import com.pitaya.commanager.proxy.ThreadProxyHandler;

public class HandlerPoster extends Handler implements Poster {

    private final PendingPostQueue queue;
    private final int maxMillisInsideHandleMessage;
    private boolean handlerActive;
    private ThreadProxyHandler threadProxyHandler;

    protected HandlerPoster(ThreadProxyHandler threadProxyHandler, Looper looper, int maxMillisInsideHandleMessage) {
        super(looper);
        this.threadProxyHandler = threadProxyHandler;
        this.maxMillisInsideHandleMessage = maxMillisInsideHandleMessage;
        queue = new PendingPostQueue();
    }

    public void enqueue(MethodPendingPost pendingPost) {
        synchronized (this) {
            queue.enqueue(pendingPost);
            if (!handlerActive) {
                handlerActive = true;
                if (!sendMessage(obtainMessage())) {
                    throw new ComException("Could not send handler message");
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        //TODO 防止内存泄漏 及时清理
        queue.clear();
    }

    @Override
    public void handleMessage(Message msg) {
        boolean rescheduled = false;
        try {
            long started = SystemClock.uptimeMillis();
            while (true) {
                MethodPendingPost pendingPost = queue.poll();
                if (pendingPost == null) {
                    synchronized (this) {
                        // Check again, this time in synchronized
                        pendingPost = queue.poll();
                        if (pendingPost == null) {
                            handlerActive = false;
                            return;
                        }
                    }
                }

                threadProxyHandler.innerInvoke(pendingPost.method, pendingPost.target, pendingPost.args);
                MethodPendingPost.releasePendingPost(pendingPost);

                long timeInMethod = SystemClock.uptimeMillis() - started;
                if (timeInMethod >= maxMillisInsideHandleMessage) {
                    if (!sendMessage(obtainMessage())) {
                        throw new ComException("Could not send handler message");
                    }
                    rescheduled = true;
                    return;
                }
            }
        } finally {
            handlerActive = rescheduled;
        }
    }
}