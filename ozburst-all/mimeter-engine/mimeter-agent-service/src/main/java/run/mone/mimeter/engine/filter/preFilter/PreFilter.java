package run.mone.mimeter.engine.filter.preFilter;

import run.mone.mimeter.engine.agent.bo.data.CommonReqInfo;
import run.mone.mimeter.engine.agent.bo.task.Task;
import run.mone.mimeter.engine.agent.bo.task.TaskContext;
import run.mone.mimeter.engine.filter.common.BasePreFilter;

/**
 * @author dongzhenxing
 */
public abstract class PreFilter {
    public abstract CommonReqInfo doFilter(Task task, TaskContext context,CommonReqInfo commonReqInfo,BasePreFilter filter);

}
