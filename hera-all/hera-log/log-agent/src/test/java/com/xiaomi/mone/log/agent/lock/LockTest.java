/*
 * Copyright 2020 Xiaomi
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.xiaomi.mone.log.agent.lock;

import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author shanwb
 * @date 2021-12-21
 */
public class LockTest {

    private static byte[] lock = new byte[0];

    @Test
    public void lockTest() {
        lock1();
        lock2();
    }

    private void lock1() {
        long now = System.currentTimeMillis();
        CountDownLatch countDownLatch = new CountDownLatch(9999999);
        for (int i=0; i< 9999999; i++) {
            new Runnable(){

                @Override
                public void run() {
                    synchronized (lock) {
                        countDownLatch.countDown();
                    }
                }
            }.run();
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("cost1:" + (System.currentTimeMillis() - now));
    }

    private void lock2() {
        long now = System.currentTimeMillis();
        CountDownLatch countDownLatch = new CountDownLatch(9999999);
        MoneSpinLock spinLock = new MoneSpinLock();
        for (int i=0; i< 9999999; i++) {
            new Runnable(){
                @Override
                public void run() {

                    try {
                        spinLock.lock();
                        countDownLatch.countDown();
                    } finally {
                        spinLock.unlock();
                    }
                }
            }.run();
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("cost2:" + (System.currentTimeMillis() - now));
    }

    public static class MoneSpinLock {
        //true: Can lock, false : in lock.
        private AtomicBoolean spinLock = new AtomicBoolean(true);

        public void lock() {
            boolean flag;
            do {
                flag = this.spinLock.compareAndSet(true, false);
            }
            while (!flag);
        }

        public void unlock() {
            this.spinLock.compareAndSet(false, true);
        }
    }
}
