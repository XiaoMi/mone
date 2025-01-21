package run.mone.local.docean.util.template.function;

import org.beetl.core.Context;
import org.beetl.core.Function;

import java.util.Map;

/**
 * @author goodjava@qq.com
 * @date 2024/2/4 13:51
 */
public class PromptFunction implements Function {

    public static final String name = "prompt_value";


    /**
     * 覆写call方法，根据参数和上下文获取全局变量的值。
     * 如果全局变量为空，则返回参数数组的第二个元素的字符串形式。
     * 否则，使用参数数组的第一个元素作为键，第二个元素作为默认值，从全局变量中获取对应的值。
     */
    @Override
    public Object call(Object[] paras, Context ctx) {
        Map<String, Object> map = ctx.globalVar;
        if (null == map) {
            return paras[1].toString();
        }
        String key = paras[0].toString();
        String defaultValue = paras[1].toString();
        return map.getOrDefault(key, defaultValue);
    }
}
