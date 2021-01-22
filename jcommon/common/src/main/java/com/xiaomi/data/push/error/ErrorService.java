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

package com.xiaomi.data.push.error;

import com.google.gson.Gson;
import com.xiaomi.data.push.dao.mapper.ErrorRecordMapper;
import com.xiaomi.data.push.dao.model.ErrorRecordWithBLOBs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * @author goodjava@qq.com
 */
@Service
public class ErrorService {

    private static final Logger logger = LoggerFactory.getLogger(ErrorService.class);

    @Autowired
    private ErrorRecordMapper errorRecordMapper;

    private final long timeoutMillis = 100;

    /**
     * 保证同时错误很多的时候,也不至于冲垮数据库  (timeoutMillio=100 的时候 大约 1秒 100条  )
     */
    private Semaphore semaphore = new Semaphore(10);

    public void recordError(ErrorRecordWithBLOBs errorRecord) {
        try {
            boolean acquired = semaphore.tryAcquire(timeoutMillis, TimeUnit.MILLISECONDS);
            if (acquired) {
                try {
                    errorRecordMapper.insert(errorRecord);
                } finally {
                    semaphore.release();
                }
            } else {
                String info =
                        String.format("recordError tryAcquire semaphore timeout, %dms, waiting thread nums: %d semaphoreAsyncValue: %d",
                                timeoutMillis,
                                this.semaphore.getQueueLength(),
                                this.semaphore.availablePermits()
                        );
                logger.warn(info + " error:{}", new Gson().toJson(errorRecord));
            }
        } catch (Exception e) {

        }
    }
}
