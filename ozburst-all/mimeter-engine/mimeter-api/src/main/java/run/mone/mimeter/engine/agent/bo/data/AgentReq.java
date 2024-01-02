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

package run.mone.mimeter.engine.agent.bo.data;

import lombok.Data;
import run.mone.mimeter.engine.agent.bo.stat.SceneTotalCountContextDTO;
import run.mone.mimeter.engine.agent.bo.task.ChangeQpsReq;
import run.mone.mimeter.engine.agent.bo.task.Task;
import run.mone.mimeter.engine.agent.bo.task.TaskResult;
import run.mone.mimeter.engine.agent.bo.task.TaskStatusBo;

import java.io.Serializable;
import java.util.List;

/**
 * @author goodjava@qq.com
 * @date 2022/5/19
 */
@Data
public class AgentReq implements Serializable {

    private User user;

    private String addr;

    private AgentInfoDTO agentInfoDTO;

    private List<AgentHostReq> agentHostReqList;

    private String cmd;

    /**
     * 修改压测 qps
     */
    private ChangeQpsReq changeQpsReq;

    /**
     * 任务信息
     */
    private Task task;

    /**
     * 任务结果
     */
    private TaskResult taskResult;

    /**
     * 打点统计
     */
    private SceneTotalCountContextDTO totalCountContextDTO;

    /**
     * 状态记录
     */
    private TaskStatusBo statusBo;

    public static final String TASK_RESULT_CMD = "taskResult";


    public static final String TOTAL_DATA_COUNT_CMD = "totalCountContext";

    /**
     * 提交任务
     */
    public static final String SUBMIT_TASK_CMD = "submitTask";

    /**
     * 提交修改host命令
     */
    public static final String EDIT_HOST_CMD = "editHost";

    /**
     * 提交加载host文件命令
     */
    public static final String LOAD_HOST_CMD = "loadHost";

    /**
     * 提交删除host配置命令
     */
    public static final String DEL_HOST_CMD = "delHost";

    /**
     * 取消任务
     */
    public static final String CANCEL_TASK_CMD = "cancelTask";

    /**
     * 变更任务qps
     */
    public static final String CHANGE_TASK_QPS = "upQpsTask";
    /**
     * 更新任务状态
     */
    public static final String UP_TASK_STATUS = "updateTaskStatus";


}
