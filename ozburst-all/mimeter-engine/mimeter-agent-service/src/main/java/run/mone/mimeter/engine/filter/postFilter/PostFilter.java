package run.mone.mimeter.engine.filter.postFilter;

import run.mone.mimeter.engine.agent.bo.task.Task;
import run.mone.mimeter.engine.filter.common.BasePostFilter;

/**
 * @author dongzhenxing
 */
public abstract class PostFilter {
    public abstract Object doFilter(Task task, Object res, BasePostFilter filter);
}
