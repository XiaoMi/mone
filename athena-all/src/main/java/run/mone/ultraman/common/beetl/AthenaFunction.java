package run.mone.ultraman.common.beetl;

import org.beetl.core.Context;
import org.beetl.core.Function;

/**
 * @author goodjava@qq.com
 * @date 2023/8/13 22:07
 */
public class AthenaFunction implements Function {
    @Override
    public Object call(Object[] paras, Context ctx) {
        return "hello " + paras[0];
    }

    @Override
    public String toString() {
        return "athena";
    }
}
