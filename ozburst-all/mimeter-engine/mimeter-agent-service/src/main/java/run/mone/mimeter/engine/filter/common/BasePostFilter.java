package run.mone.mimeter.engine.filter.common;

import run.mone.mimeter.engine.agent.bo.task.Task;

/**
 * @author dongzhenxing
 */
public interface BasePostFilter {
    Object doFilter(Task task, Object object);
}
