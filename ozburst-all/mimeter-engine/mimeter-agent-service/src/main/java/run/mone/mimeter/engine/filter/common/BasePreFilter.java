package run.mone.mimeter.engine.filter.common;

import run.mone.mimeter.engine.agent.bo.data.CommonReqInfo;
import run.mone.mimeter.engine.agent.bo.task.Task;
import run.mone.mimeter.engine.agent.bo.task.TaskContext;

/**
 * @author dongzhenxing
 */
public interface BasePreFilter {
    CommonReqInfo doFilter(Task task, TaskContext context,CommonReqInfo commonReqInfo);

}
