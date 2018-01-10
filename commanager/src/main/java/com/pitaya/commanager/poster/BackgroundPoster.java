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

import com.pitaya.commanager.proxy.ThreadProxyHandler;
import com.pitaya.commanager.tools.ELog;

/**
 * Posts events in background.
 *
 * @author Markus
 */
public final class BackgroundPoster implements Runnable, Poster {
    private static final String TAG = "BackgroundPoster";

    private final PendingPostQueue queue;

    private volatile boolean executorRunning;

    private ThreadProxyHandler threadProxyHandler;

    public BackgroundPoster(ThreadProxyHandler threadProxyHandler) {
        this.threadProxyHandler = threadProxyHandler;
        queue = new PendingPostQueue();
    }

    public void enqueue(MethodPendingPost pendingPost) {
        synchronized (this) {
            queue.enqueue(pendingPost);
            if (!executorRunning) {
                executorRunning = true;
                ThreadProxyHandler.DefaultBackgroundThreadPool.execute(this);
            }
        }
    }

    @Override
    public void onDestroy() {
        queue.clear();
    }

    @Override
    public void run() {
        try {
            try {
                while (true) {
                    MethodPendingPost pendingPost = queue.poll(1000);
                    if (pendingPost == null) {
                        synchronized (this) {
                            // Check again, this time in synchronized
                            pendingPost = queue.poll();
                            if (pendingPost == null) {
                                executorRunning = false;
                                return;
                            }
                        }
                    }
                    threadProxyHandler.innerInvoke(pendingPost.method, pendingPost.target, pendingPost.args);
                    MethodPendingPost.releasePendingPost(pendingPost);
                }
            } catch (InterruptedException e) {
                ELog.e(TAG, Thread.currentThread().getName() + " was interruppted", e);
            }
        } finally {
            executorRunning = false;
        }
    }
}
