package run.mone.mimeter.engine.filter.postFilter.filters;

import com.xiaomi.youpin.docean.anno.Component;
import run.mone.mimeter.engine.agent.bo.task.Task;
import run.mone.mimeter.engine.filter.common.BasePostFilter;
import run.mone.mimeter.engine.filter.common.FilterOrder;
import run.mone.mimeter.engine.filter.postFilter.PostFilter;
import run.mone.mimeter.engine.filter.postFilter.PostFilterAnno;

/**
 * @author dongzhenxing
 */
@Component
@PostFilterAnno
@FilterOrder(2)
public class Demo2PostFilter extends PostFilter {
    @Override
    public Object doFilter(Task task, Object res, BasePostFilter filter) {
        System.out.println(res.toString());
        return filter.doFilter(task,res);
    }
}
