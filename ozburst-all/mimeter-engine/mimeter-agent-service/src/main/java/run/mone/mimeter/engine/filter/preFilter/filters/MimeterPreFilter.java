package run.mone.mimeter.engine.filter.preFilter.filters;

import run.mone.mimeter.engine.agent.bo.data.CommonReqInfo;
import run.mone.mimeter.engine.agent.bo.task.Task;
import run.mone.mimeter.engine.agent.bo.task.TaskContext;
import run.mone.mimeter.engine.filter.common.BasePreFilter;

/**
 * @author dongzhenxing
 */
public class MimeterPreFilter implements BasePreFilter {
    @Override
    public CommonReqInfo doFilter(Task task, TaskContext context, CommonReqInfo commonReqInfo) {
        //默认 filter 原样返回
        return commonReqInfo;
    }

}
