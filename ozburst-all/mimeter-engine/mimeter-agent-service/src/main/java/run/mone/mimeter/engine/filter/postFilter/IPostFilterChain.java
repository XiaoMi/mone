package run.mone.mimeter.engine.filter.postFilter;

import run.mone.mimeter.engine.agent.bo.task.Task;

/**
 * @author dongzhenxing
 */
public interface IPostFilterChain {
    Object doPostFilter(Task task, Object resInfo);
}
