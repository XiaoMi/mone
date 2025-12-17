package run.mone.mcp.gateway.function;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import run.mone.hive.annotation.ReportCallCount;
import run.mone.hive.configs.Const;
import run.mone.hive.mcp.function.McpFunction;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.mcp.gateway.service.GatewayService;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

@Slf4j
@Component
@RequiredArgsConstructor
public class FilterFunction implements McpFunction {

    private final GatewayService gatewayService;

    private static final String TOOL_SCHEMA = """
            {
                "type": "object",
                "properties": {
                    "zhName": {
                        "type": "string",
                        "description": "Chinese name of gateway filter"
                    },
                    "enName": {
                        "type": "string",
                        "description": "English name of gateway filter"
                    },
                    "id": {
                        "type": "string",
                        "description": "The ID of the gateway filter"
                    },
                    "tenant": {
                        "type": "string",
                        "description": "Gateway filter tenant"
                    },
                    "env": {
                        "type": "string",
                        "enum": ["staging","sgpStaging", "online","sgpOnline","eurOnline"],
                        "description": "The Environment for gateway"
                    }
                },
                "required": ["tenant", "env"]
            }
            """;

    @Override
    public String getName() {
        return "filter_operation";
    }

    @Override
    public String getDesc() {
        return """
                Gateway filter操作工具，用于查询filter信息。
                **使用场景：**
                - 根据网关filter中文名称、英文名称、filter的id查询网关filter信息
                - 如：查询中国区测试环境名为“xxx”的filter
                **参数说明：**
                - env网关环境，可选值为staging(中国区测试环境)、sgpStaging(新加坡测试环境)、online(中国区线上环境)、sgpOnline(新加坡线上环境)、eurOnline(欧洲线上环境)，默认值为staging。
                - zhName、enName、id都是非必填，但是三个参数必须有一个有值。
                - tenant可取值如下，默认值为1：
                --新加坡测试环境或全球测试（sgpStaging）:1000(全球测试)、8(新加坡测试)
                --中国区测试环境（staging）：1(中国区测试)、2(中国区有品)、7(测试环境mifaas)
                --中国区线上环境（online）：1(中国区内网)、2(中国区外网)、3(有品内网)、4(有品外网)
                --新加坡线上或全球线上（sgpOnline）:999(全球内网)、1000(全球内网)、5(新加坡内网)、6(新加坡外网)
                --欧洲线上（eurOnline）:7(欧洲内网)、8(欧洲外网)
                """;
    }

    @Override
    public String getToolScheme() {
        return TOOL_SCHEMA;
    }

    @Override
    @ReportCallCount(businessName = "gateway-filter", description = "Gateway filter操作工具调用")
    public Flux<McpSchema.CallToolResult> apply(Map<String, Object> arguments) {
        log.info("FilterFunction.apply: {}", arguments);
        String tenant = getStringValue(arguments, "tenant");
        if (StringUtils.isBlank(tenant)) {
            return Flux.just(new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent("错误：缺少必填的租户参数'tenant'")),
                    true
            ));
        }
        String id = getStringValue(arguments, "id");
        String zhName = getStringValue(arguments, "zhName");
        String enName = getStringValue(arguments, "enName");
        if (Stream.of(id, zhName, enName).allMatch(StringUtils::isBlank)) {
            return Flux.just(new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent("错误：缺少必填的filter参数")),
                    true
            ));
        }

        Map<String, Object> params = Map.of(
                "tenant", tenant,
                "id", id,
                "zhName", zhName,
                "enName", enName,
                "userName", getStringValue(arguments, Const.TOKEN_USERNAME)
        );
        String result = gatewayService.searchFilter(getStringValue(arguments, "env"), params);
        return Flux.just(new McpSchema.CallToolResult(
                List.of(new McpSchema.TextContent("查询到的filter信息为："+result)),
                false
        ));
    }

    private String getStringValue(Map<String, Object> arguments, String key) {
        return Optional.ofNullable(arguments.get(key))
                .map(Object::toString)
                .orElse("");
    }
}
