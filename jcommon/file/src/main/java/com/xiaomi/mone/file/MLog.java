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

package com.xiaomi.mone.file;

import lombok.Data;
import lombok.Getter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @Author goodjava@qq.com
 * @Date 2021/7/8 15:57
 */
@Data
public class MLog {

    @Getter
    private Queue<String> msgQueue = new LinkedList<>();
    @Getter
    private Long appendTime;
    /**
     * 用户自定义的行首正则
     */
    private Pattern customLinePattern;

    private ReentrantLock lock = new ReentrantLock();

    /**
     * 匹配 20xx or [20xx
     */
    private static final Pattern DEFAULT_NEW_LINE_PATTERN = Pattern.compile("^20[0-9]{2}|^\\[20[0-9]{2}");
//    private static final Pattern ERROR_LINE_PATTERN = Pattern.compile(".*ERROR|.*(WARN|INFO).*(Exception|exception|error|Profiler)");
//    private static final int MAX_ERROR_LINE_MATCH_LENGTH = 300;
    /**
     * 最多聚合200行错误栈，避免queue无限增长
     */
    private static final int MAX_MERGE_LINE = 400;

    @Deprecated
    public List<String> append(String msg) {
        appendTime = Instant.now().toEpochMilli();
        List<String> res = new ArrayList<>();
        boolean isNew = isNew(msg);
        if (isNew) {
            try {
                lock.lock();
                if (msgQueue.size() > 0) {
                    String e = msgQueue.stream().collect(Collectors.joining("\r\n"));
                    res.add(e);
                    msgQueue.clear();
                }
                //消息入队列
                msgQueue.offer(msg);
            } finally {
                lock.unlock();
            }
        } else {
            try {
                lock.lock();
                //最大错误栈行数判断
                if (msgQueue.size() >= MAX_MERGE_LINE) {
                    String e = msgQueue.stream().collect(Collectors.joining("\r\n"));
                    res.add(e);
                    msgQueue.clear();
                }
                //消息入队列
                msgQueue.offer(msg);
            } finally {
                lock.unlock();
            }
        }

        return res;
    }

    public String append2(String msg) {
        appendTime = Instant.now().toEpochMilli();
        String res = null;
        boolean isNew = isNew(msg);
        if (isNew) {

            try {
                lock.lock();
                if (msgQueue.size() > 0) {
                    res = msgQueue.stream().collect(Collectors.joining("\r\n"));
                    msgQueue.clear();
                }
                //消息入队列
                msgQueue.offer(msg);
            } finally {
                lock.unlock();
            }
        } else {
            try {
                lock.lock();
                //最大错误栈行数判断
                if (msgQueue.size() >= MAX_MERGE_LINE) {
                    res = msgQueue.stream().collect(Collectors.joining("\r\n"));
                    msgQueue.clear();
                }
                //消息入队列
                msgQueue.offer(msg);
            } finally {
                lock.unlock();
            }
        }

        return res;
    }

    /**
     * 获取队列中剩余消息
     *
     * @return
     */
    @Deprecated
    public List<String> takeRemainMsg() {
        List<String> res = new ArrayList<>();
        try {
            lock.lock();
            if (msgQueue.size() > 0) {
                String e = msgQueue.stream().collect(Collectors.joining("\r\n"));
                res.add(e);
                msgQueue.clear();
            }
        } finally {
            lock.unlock();
        }
        return res;
    }

    public String takeRemainMsg2() {
        String res = null;
        try {
            lock.lock();
            if (msgQueue.size() > 0) {
                res = msgQueue.stream().collect(Collectors.joining("\r\n"));
                msgQueue.clear();
            }
        } finally {
            lock.unlock();
        }
        return res;
    }

    public void setCustomLinePattern(String customLinePattern) {
        this.customLinePattern = Pattern.compile(customLinePattern);
    }

    private boolean isNew(String str) {
        if (null != customLinePattern) {
            return customLinePattern.matcher(str).find();
        }
        return DEFAULT_NEW_LINE_PATTERN.matcher(str).find();
    }

}
