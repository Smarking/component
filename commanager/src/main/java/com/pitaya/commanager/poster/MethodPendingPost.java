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

public final class MethodPendingPost {
    private final static List<MethodPendingPost> pendingPostPool = new ArrayList<MethodPendingPost>();

    public Method method;
    public Object target;
    public Object[] args;

    MethodPendingPost next;

    private MethodPendingPost(Method method, Object target, Object[] args) {
        this.method = method;
        this.target = target;
        this.args = args;
    }

    public static MethodPendingPost obtainPendingPost(Method method, Object target, Object[] args) {
        synchronized (pendingPostPool) {
            int size = pendingPostPool.size();
            if (size > 0) {
                MethodPendingPost pendingPost = pendingPostPool.remove(size - 1);
                pendingPost.method = method;
                pendingPost.target = target;
                pendingPost.args = args;
                pendingPost.next = null;
                return pendingPost;
            }
        }
        return new MethodPendingPost(method, target, args);
    }

    public static void releasePendingPost(MethodPendingPost pendingPost) {
        if (pendingPost == null) {
            return;
        }
        pendingPost.method = null;
        pendingPost.target = null;
        pendingPost.args = null;
        pendingPost.next = null;
        synchronized (pendingPostPool) {
            // Don't let the pool grow indefinitely
            if (pendingPostPool.size() < 1000) {
                pendingPostPool.add(pendingPost);
            }
        }
    }
}