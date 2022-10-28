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

package com.xiaomi.youpin.gwdash.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author goodjava@qq.com
 */
@Component
@Slf4j
public class LockUtils {


    private ConcurrentHashMap<String, String> lock = new ConcurrentHashMap<>();


    private ConcurrentHashMap<String, ReentrantLock> rlock = new ConcurrentHashMap<>();


    public boolean rtryLock(String key, long time) {
        rlock.putIfAbsent(key, new ReentrantLock());
        try {
            if (time == 0) {
                return rlock.get(key).tryLock();
            }

            return rlock.get(key).tryLock(time, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            log.error(e.getMessage());
            return false;
        }
    }

    public void runLock(String name, String key) {
        log.info("++++++++++++++>unlock:{} {}", name, key);
        rlock.get(key).unlock();
    }


    public <T> T lockAndRun(String name, String key, long time, Callable<T> callable) {
        boolean l = rtryLock(key, time);
        if (l) {
            log.info("-------------->lock:{} {}", name, key);
            try {
                return callable.call();
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                // todo: 尝试缓解数据库写读延迟数据不一致
                try {
                    TimeUnit.MILLISECONDS.sleep(300);
                } catch (InterruptedException e) {
                    log.info("lockAndRun", e);
                }
                runLock(name, key);
            }
        } else if (time == 0) {
            return null;
        } else {
            throw new RuntimeException("lock error:" + key + ":" + name);
        }
    }


    public boolean tryLock(String key) {
        return null == lock.putIfAbsent(key, key);
    }


    public void unLock(String key) {
        lock.remove(key);
    }


    public static String deployKey(long envId) {
        return "deploy:" + envId;
    }

    public static String lockKey(String key) {
        return "lock:" + key;
    }
}
