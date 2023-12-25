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
import run.mone.mimeter.engine.agent.bo.data.CommonReqInfo;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author goodjava@qq.com
 * @date 2022/5/19
 */
@Data
public class TaskResult implements Serializable {

    /**
     * 任务id
     */
    private int id;

    private Context context;

    private String addr;

    private String opUser;

    private AtomicLong success = new AtomicLong();

    private AtomicLong failure = new AtomicLong();

    private int code;

    private boolean ok;

    private String result;

    private CommonReqInfo commonReqInfo;

    /**
     * 返回的header
     */
    private Map<String,String> respHeaders;

    private String debugUrl;

    private long rt;

    //bytes
    private long size;

    private String triggerCpInfo;

    /**
     * 触发的检查点条件
     */
    private String triggerFilterCondition;

    private boolean debug;

    private String reportId;

    private Integer sceneId;

    private boolean quitByManual;

    private int cancelType;

    public String cancelBySlaRule;

}
