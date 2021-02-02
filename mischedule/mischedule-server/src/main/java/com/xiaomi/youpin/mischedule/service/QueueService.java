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

package com.xiaomi.youpin.mischedule.service;


import com.xiaomi.data.push.schedule.TaskManager;
import com.xiaomi.youpin.mischedule.bo.QTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author goodjava@qq.com
 * <p>
 * 提交的任务也同时串行化(减小数据库的压力,目前数据库太弱)
 */
@Slf4j
@Service
public class QueueService {


    @Autowired
    private TaskManager taskManager;


    private ArrayBlockingQueue<QTask> queue = new ArrayBlockingQueue<>(10000);


    public QueueService() {
        Executors.newSingleThreadScheduledExecutor().scheduleWithFixedDelay(() -> {
            log.info("QueueService queue size:{}", queue.size());
        }, 0, 10, TimeUnit.SECONDS);
    }

    public boolean offer(QTask task) {
        log.info("offer task");
        return queue.offer(task);
    }

    @PostConstruct
    public void init() {
        new Thread(() -> {
            try {
                execute();
            } catch (Throwable ex) {
                log.error(ex.getMessage());
            }
        }).start();

    }


    public void execute() throws InterruptedException {
        log.info("execute start");
        for (; ; ) {
            QTask qTask = queue.take();
            try {
                log.info("execute");
                taskManager.submitTask(qTask.getParam(), qTask.getContext(), qTask.isRun());
            } catch (Throwable ex) {
                log.error(ex.getMessage());
            }
        }
    }


}
