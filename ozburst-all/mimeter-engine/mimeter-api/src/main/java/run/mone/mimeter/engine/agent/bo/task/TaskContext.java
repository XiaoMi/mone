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

package run.mone.mimeter.engine.agent.bo.task;

import lombok.Data;
import run.mone.mimeter.engine.agent.bo.stat.SceneTotalCountContext;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author goodjava@qq.com
 * @date 2022/6/2
 */
@Data
public class TaskContext implements Serializable {

    private int num;

    /**
     * 是否发生了错误
     */
    private volatile boolean error;

    /**
     * 完成任务数
     */
    private AtomicInteger finishTaskNum = new AtomicInteger(0);


    private ConcurrentHashMap<String, Object> attachments = new ConcurrentHashMap<>();

    /**
     * 存储各个node的结果
     */
    private ConcurrentHashMap<String, Object> resultMap = new ConcurrentHashMap<>();

    /**
     * 统计错误信息
     */
    private SceneTotalCountContext sceneTotalCountContext = new SceneTotalCountContext();

}
