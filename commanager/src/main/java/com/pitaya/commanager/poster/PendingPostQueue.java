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

public final class PendingPostQueue {
    private MethodPendingPost head;
    private MethodPendingPost tail;

    private volatile boolean isAlive = true;

    synchronized void clear() {
        isAlive = false;
        for (MethodPendingPost temp = null; head != null; head = head.next) {
            MethodPendingPost.releasePendingPost(temp);
            temp = head;
        }
        head = tail = null;
        notifyAll();
        //TODO clear
    }

    synchronized void enqueue(MethodPendingPost pendingPost) {
        if (!isAlive) {
            return;
        }

        if (pendingPost == null) {
            throw new NullPointerException("null cannot be enqueued");
        }
        if (tail != null) {
            tail.next = pendingPost;
            tail = pendingPost;
        } else if (head == null) {
            head = tail = pendingPost;
        } else {
            throw new IllegalStateException("Head present, but no tail");
        }
        notifyAll();
    }

    synchronized MethodPendingPost poll() {
        if (!isAlive) {
            return null;
        }

        MethodPendingPost pendingPost = head;
        if (head != null) {
            head = head.next;
            if (head == null) {
                tail = null;
            }
        }
        return pendingPost;
    }

    synchronized MethodPendingPost poll(int maxMillisToWait) throws InterruptedException {
        if (!isAlive) {
            return null;
        }

        if (head == null) {
            wait(maxMillisToWait);
        }
        return poll();
    }


}
