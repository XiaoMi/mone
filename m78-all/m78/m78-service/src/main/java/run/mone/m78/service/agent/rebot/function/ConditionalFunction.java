package run.mone.m78.service.agent.rebot.function;

import org.beetl.core.Context;
import org.beetl.core.Function;

import java.util.Map;

/**
 * @author goodjava@qq.com
 * @date 2024/3/17 12:03
 *
 * 如果有某个变量,则显示某个文本,否则忽略
 */
public class ConditionalFunction implements Function {

    public static final String name = "conditional";

    @Override
    public Object call(Object[] paras, Context ctx) {
        String key = paras[0].toString();
        String defaultValue = paras[1].toString();
        Map<String, Object> map = ctx.globalVar;
        if (null == map) {
            return "";
        }
        if (map.containsKey(key)) {
            return defaultValue;
        }
        return "";
    }
}
