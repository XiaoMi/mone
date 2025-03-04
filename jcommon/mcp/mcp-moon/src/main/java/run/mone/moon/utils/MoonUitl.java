package run.mone.moon.utils;

import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.rpc.service.GenericService;

import java.util.HashMap;

public class MoonUitl {

    public static void commonParam(ReferenceConfig<GenericService> referenceConfig) {
        referenceConfig.setGeneric("true");
        // 设置通信协议为 Dubbo
        referenceConfig.setProtocol("dubbo");
        referenceConfig.setVersion("1.0");
        referenceConfig.setParameters(new HashMap<>());
        referenceConfig.getParameters().put("dubbo", "2.0.2");
        referenceConfig.getParameters().put("migration.step", "FORCE_INTERFACE");
    }

    public static String getString(Object value) {
        return (value != null) ? String.valueOf(value) : null;
    }

    public static Long getLong(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        try {
            return Long.parseLong(value.toString());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid number format: " + value);
        }
    }

    public static Number getNumber(Object value) {
        if (value == null) return null;
        if (value instanceof Number) {
            return (Number) value;
        }
        try {
            // 更通用的数字解析
            return Double.parseDouble(value.toString());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid number format: " + value);
        }
    }

}
