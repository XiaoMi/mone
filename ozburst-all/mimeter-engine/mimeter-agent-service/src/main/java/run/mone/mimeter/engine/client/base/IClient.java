package run.mone.mimeter.engine.client.base;

import run.mone.mimeter.engine.agent.bo.data.CommonReqInfo;
import run.mone.mimeter.engine.agent.bo.data.Result;
import run.mone.mimeter.engine.agent.bo.stat.SceneTotalCountContext;
import run.mone.mimeter.engine.agent.bo.task.Task;
import run.mone.mimeter.engine.agent.bo.task.TaskContext;

/**
 * @author goodjava@qq.com
 * @author dongzhenxing
 * @date 2022/5/23
 */
public interface IClient {
    Result call(Task task, TaskContext context, CommonReqInfo commonReqInfo, SceneTotalCountContext totalCountContext);

}
