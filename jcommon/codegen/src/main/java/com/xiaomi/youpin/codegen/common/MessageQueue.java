package com.xiaomi.youpin.codegen.common;



import com.xiaomi.youpin.codegen.bo.Response;

import java.util.HashMap;
import java.util.Map;
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
