/*
 *  Copyright 2020 Xiaomi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.xiaomi.youpin.tesla.ip.common;

import com.xiaomi.youpin.tesla.ip.bo.Response;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author goodjava@qq.com
 */
public class MessageQueue {

    private LinkedBlockingQueue<Response> queue = new LinkedBlockingQueue<>();
    private LinkedBlockingQueue<Map<String, Object>> sendQueue = new LinkedBlockingQueue<>();


    private static class LazyHolder {
        private static MessageQueue ins = new MessageQueue();
    }


    public static MessageQueue ins() {
        return LazyHolder.ins;
    }


    public void offer(Response msg) {
        queue.offer(msg);
    }

    public void send(Map<String, Object> msg) {
        sendQueue.offer(msg);
    }


    public Response poll() {
        try {
            return queue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Map<String, Object> pollSend() {
        try {
            return sendQueue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return new HashMap<>();
    }
}
