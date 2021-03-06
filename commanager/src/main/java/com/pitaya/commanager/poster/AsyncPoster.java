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

/**
 * Posts events in background.
 *
 * @author Markus
 */
public class AsyncPoster implements Runnable, Poster {

    private final PendingPostQueue queue;
    private ThreadProxyHandler threadProxyHandler;

    public AsyncPoster(ThreadProxyHandler threadProxyHandler) {
        this.threadProxyHandler = threadProxyHandler;
        queue = new PendingPostQueue();
    }

    public void enqueue(MethodPendingPost pendingPost) {
        queue.enqueue(pendingPost);
        ThreadProxyHandler.DefaultBackgroundThreadPool.execute(this);
    }

    @Override
    public void onDestroy() {
        queue.clear();
        threadProxyHandler = null;
    }

    @Override
    public void run() {
        MethodPendingPost pendingPost = queue.poll();
        if (pendingPost == null) {
            return;
        }
        if (threadProxyHandler != null) {
            threadProxyHandler.innerInvoke(pendingPost.method, pendingPost.target, pendingPost.args);
        }
        MethodPendingPost.releasePendingPost(pendingPost);
    }
}
