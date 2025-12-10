package run.mone.mcp.miapi.function;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import run.mone.hive.annotation.ReportCallCount;
import run.mone.hive.configs.Const;
import run.mone.hive.mcp.function.McpFunction;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.mcp.miapi.utils.HttpUtils;

import java.util.*;

@Slf4j
@Component
public class DubboTestFunction implements McpFunction {

    @Autowired
    private HttpUtils httpUtils;
    public static final String TOOL_SCHEMA = """
            {
                "type": "object",
                "properties": {
                    "interfaceName": {
                        "type": "string",
                        "description": "dubbo服务全限定名（必填）"
                    },
                    "methodName": {
                        "type": "string",
                        "description": "dubbo服务方法名（必填）"
                    },
                    "paramType": {
                        "type": "string",
                        "description": "dubbo服务方法参数类型（必填）"
                    },
                    "parameter": {
                        "type": "string",
                        "description": "dubbo服务方法参数（必填）"
                    },
                    "env": {
                        "type": "string",
                        "description": "dubbo服务环境（必填）"
                    },
                    "group": {
                        "type": "string",
                        "description": "dubbo服务分组（必填）"
                    },
                    "version": {
                        "type": "string",
                        "description": "dubbo服务版本（必填）"
                    },
                    "attachment": {
                        "type": "string",
                        "description": "dubbo服务RpcContext（非必填）"
                    },
                    "timeout": {
                        "type": "string",
                        "description": "dubbo服务超时时间（非必填）"
                    },
                    "retries": {
                        "type": "string",
                        "description": "dubbo服务重试次数（非必填）"
                    },
                    "addr": {
                        "type": "string",
                        "description": "dubbo服务ip:port（非必填）"
                    },
                    "dubboTag": {
                        "type": "string",
                        "description": "dubbo服务tag（非必填）"
                    }
                },
                "required": ["interfaceName","methodName","paramType","parameter","env"]
            }
            """;

    private static final String BASE_URL = System.getenv("gateway_host");

    @Override
    @ReportCallCount(businessName = "miapi-api-dubbo_test", description = "通过mcp进行dubbo接口调用")
    public Flux<McpSchema.CallToolResult> apply(Map<String, Object> arguments) {
        log.info("miapi mcp arguments: {}", arguments);
        try {
            if (BASE_URL == null) {
                return Flux.just(new McpSchema.CallToolResult(
                        List.of(new McpSchema.TextContent("错误：配置错误: gateway_host 环境变量未设置")),
                        true
                ));
            }

            Map<String, Object> userMap = handleParams(arguments);

            if (userMap.isEmpty()) {
                return Flux.just(new McpSchema.CallToolResult(
                        List.of(new McpSchema.TextContent("错误：缺少必填参数")),
                        true
                ));
            }

            String resultText = httpUtils.request("/mtop/miapi/dubboTestAgent", userMap, Map.class);
            resultText = String.format("dubbo调用结果为: %s", resultText);
            return Flux.just(new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent(resultText)),
                    false
            ));
        } catch (Exception e) {
            log.error("执行miapi操作时发生异常", e);
            return Flux.just(new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent("错误：执行操作失败: " + e.getMessage())),
                    true
            ));
        }
    }

    private Map<String, Object> handleParams(Map<String, Object> arguments) {
        final String[] REQUIRED_PARAMS = {"interfaceName", "methodName", "paramType", "parameter", "env", "group", "version"};

        if (hasInvalidRequiredParams(arguments, REQUIRED_PARAMS)) {
            return Collections.emptyMap();
        }

        Map<String, Object> result = new LinkedHashMap<>();

        addParamIfPresent(result, arguments, "interfaceName");
        addParamIfPresent(result, arguments, "methodName");
        addParamIfPresent(result, arguments, "paramType");
        addParamIfPresent(result, arguments, "parameter");
        addParamIfPresent(result, arguments, "env");
        addParamIfPresent(result, arguments, "group");
        addParamIfPresent(result, arguments, "version");
        addParamIfPresent(result, arguments, "attachment");
        addParamIfPresent(result, arguments, "timeout");
        addParamIfPresent(result, arguments, "retries");
        addParamIfPresent(result, arguments, "addr");
        addParamIfPresent(result, arguments, "dubboTag");

        result.put("userName", getSafeString(arguments, Const.TOKEN_USERNAME, ""));

        return result;
    }

    private boolean hasInvalidRequiredParams(Map<String, Object> arguments, String[] requiredParams) {
        for (String param : requiredParams) {
            Object value = arguments.get(param);
            if (isBlank(value)) {
                log.warn("Required parameter '{}' is missing or empty", param);
                return true;
            }
        }
        return false;
    }

    private void addParamIfPresent(Map<String, Object> target, Map<String, Object> source, String key) {
        Object value = source.get(key);
        if (value != null) {
            target.put(key, value);
        }
    }

    private String getSafeString(Map<String, Object> map, String key, String defaultValue) {
        Object value = map.get(key);
        return (value instanceof String str && StringUtils.isNotBlank(str)) ? str : defaultValue;
    }

    private boolean isBlank(Object value) {
        if (value == null) {
            return true;
        }
        if (value instanceof CharSequence) {
            return StringUtils.isBlank((CharSequence) value);
        }
        return StringUtils.isBlank(value.toString());
    }

    @Override
    public String getName() {
        return "dubbo_test";
    }

    @Override
    public String getDesc() {
        return """
                工具说明：对dubbo接口进行测试或泛化调用。
                参数说明和默认值：
                - interfaceName dubbo服务全限定名，必填，如："com.xiaomi.youpin.test0618.api.testService"
                - methodName dubbo方法名，必填，如："backslash"
                - group dubbo服务分组，必填，如："staging"
                - version dubbo服务版本，必填，如："1.0"
                - attachment 调用时RpcContext上下文，非必填，如："{\\"name\\":\\"张三\\"}"，默认值为""
                - paramType dubbo方法参数类型，必填，如："[\\"com.xiaomi.youpin.test0618.api.dto.BackslashReq\\"]"，默认值为："[]"
                - parameter dubbo方法参数，必填，如："[{\\"name\\":\\"张三\\",\\"id\\":12,\\"map\\":{}}]"，默认值为："[]"
                - timeout dubbo服务超时时间，单位ms，非必填，如："5000"，默认值为: "3000"
                - retries dubbo调用重试次数，非必填，如："1"，默认值为:"1"
                - addr dubbo调用时指定的ip:port，非必填，如："10.112.113.11:20880"，默认值为:""
                - dubboTag dubbo调用时指定的dubbo tag，非必填，如："test"，默认值为:""
                - env dubbo调用时dubbo服务注册中心环境，必填，如："st"，可选值：中国区测试或国内测试（st）、新加坡测试或全球测试（singaporeSt）
                使用示例：
                1.帮我针对当前项目中testService的dubbo服务的backslash方法进行指定本地ip调用，参数为[{\"name\":\"张三\",\"id\":12,\"map\":{}}]
                """;
    }

    @Override
    public String getToolScheme() {
        return TOOL_SCHEMA;
    }
}