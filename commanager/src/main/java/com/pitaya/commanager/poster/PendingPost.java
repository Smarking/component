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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public final class PendingPost {
    private final static List<PendingPost> pendingPostPool = new ArrayList<PendingPost>();

    public Method method;
    public Object target;
    public Object[] args;

    PendingPost next;

    private PendingPost(Method method, Object target, Object[] args) {
        this.method = method;
        this.target = target;
        this.args = args;
    }

    public static PendingPost obtainPendingPost(Method method, Object target, Object[] args) {
        synchronized (pendingPostPool) {
            int size = pendingPostPool.size();
            if (size > 0) {
                PendingPost pendingPost = pendingPostPool.remove(size - 1);
                pendingPost.method = method;
                pendingPost.target = target;
                pendingPost.args = args;
                pendingPost.next = null;
                return pendingPost;
            }
        }
        return new PendingPost(method, target, args);
    }

    public static void releasePendingPost(PendingPost pendingPost) {
        if (pendingPost == null) {
            return;
        }
        pendingPost.method = null;
        pendingPost.target = null;
        pendingPost.args = null;
        pendingPost.next = null;
        synchronized (pendingPostPool) {
            // Don't let the pool grow indefinitely
            if (pendingPostPool.size() < 10000) {
                pendingPostPool.add(pendingPost);
            }
        }
    }
}