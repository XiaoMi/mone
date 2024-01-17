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

import java.io.Serializable;

/**
* @author goodjava@qq.com
* @date 2022/5/19
*/
@Data
public class Context implements Serializable {

    /**
     * 完成
     */
    private volatile boolean finish;

    /**
     * 被取消掉
     */
    private volatile boolean cancel;

    /**
     * 取消的方式
     */
    private volatile int cancelType;

    /**
     * 可控的图任务qps
     */
    private volatile int taskQps;

    /**
     * 可控的场景发压比例
     */
    private volatile int rpsRate;
}
