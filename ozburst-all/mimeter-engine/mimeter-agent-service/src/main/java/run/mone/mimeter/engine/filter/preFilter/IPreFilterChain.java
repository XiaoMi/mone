package run.mone.mimeter.engine.filter.preFilter;

import run.mone.mimeter.engine.agent.bo.data.CommonReqInfo;
import run.mone.mimeter.engine.agent.bo.task.Task;
import run.mone.mimeter.engine.agent.bo.task.TaskContext;

/**
 * @author dongzhenxing
 */
public interface IPreFilterChain {
    CommonReqInfo doPreFilter(Task task, TaskContext context,CommonReqInfo reqInfo);
}
