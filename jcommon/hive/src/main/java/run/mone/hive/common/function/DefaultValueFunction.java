package run.mone.hive.common.function;

import org.beetl.core.Context;
import org.beetl.core.Function;
import org.apache.commons.lang3.StringUtils;

/**
 * A function that returns a default value if the provided value is null or empty.
 * Usage: ${defaultValue(value, defaultValue)}
 * 
 * @author goodjava@qq.com
 */
public class DefaultValueFunction implements Function {
    @Override
    public Object call(Object[] paras, Context ctx) {
        if (paras.length < 2) {
            throw new RuntimeException("defaultValue方法需要两个参数：值和默认值");
        }

        Object value = paras[0];
        Object defaultValue = paras[1];

        // If value is null, return default value
        if (value == null) {
            return defaultValue;
        }

        // If value is an empty string, return default value
        if (value instanceof String && StringUtils.isEmpty((String) value)) {
            return defaultValue;
        }

        // Otherwise return the original value
        return value;
    }
} 