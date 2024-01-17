package run.mone.mimeter.engine.filter.postFilter.filters;

import run.mone.mimeter.engine.agent.bo.task.Task;
import run.mone.mimeter.engine.filter.common.BasePostFilter;

/**
 * @author dongzhenxing
 */
public class MimeterPostFilter implements BasePostFilter {

    @Override
    public Object doFilter(Task task, Object object) {
        System.out.println("MimeterPostFilter");
        return object;
    }
}
