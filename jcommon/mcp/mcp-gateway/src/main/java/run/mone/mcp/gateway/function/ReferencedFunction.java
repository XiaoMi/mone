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
@RequiredArgsConstructor
@Component
public class ReferencedFunction implements McpFunction {
    private final GatewayService gatewayService;

    private static final String TOOL_SCHEMA = """
            {
                "type": "object",
                "properties": {
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
        return "api_referenced_by_filter";
    }

    @Override
    public String getDesc() {
        return """
                Gateway filter操作工具，用于查询filter信息。
                **使用场景：**
                - 根据网关filter英文名称或id查询使用此filter的api信息
                - 如：查询测试环境中国区哪些api使用了id为123456的filter
                - 如：查询测试环境中国区哪些api使用了名为“xxx”的filter
                **参数说明：**
                - env网关环境，可选值为staging(中国区测试环境)、sgpStaging(新加坡测试环境)、online(中国区线上环境)、sgpOnline(新加坡线上环境)、eurOnline(欧洲线上环境)，默认值为staging。
                - enName、id都是非必填，但是二者必须有一个有值。
                - tenant可取值如下，默认值为1：
                --新加坡测试环境或全球测试（sgpStaging）:1000(全球测试)、8(新加坡测试)
                --中国区测试环境（staging）：1(中国区测试)、2(中国区有品)、7(测试环境mifaas)
                --中国区线上环境（online）：1(中国区内网)、2(中国区外网)、3(有品内网)、4(有品外网)
                --新加坡线上或全球线上（sgpOnline）:999(全球内网)、1000(全球内网)、5(新加坡内网)、6(新加坡外网)
                --欧洲线上（eurOnline）:7(欧洲内网)、8(欧洲外网)
                **特别注意：**
                - 如果用户直接提供了filter中文名，则先使用filter_operation工具进行filter查询，然后在进行后续操作。
                """;
    }

    @Override
    public String getToolScheme() {
        return TOOL_SCHEMA;
    }

    @Override
    @ReportCallCount(businessName = "gateway-filter-referenced", description = "Gateway filter引用工具调用")
    public Flux<McpSchema.CallToolResult> apply(Map<String, Object> arguments) {
        log.info("ReferencedFunction.apply: {}", arguments);
        String tenant = getStringValue(arguments, "tenant");
        if (StringUtils.isBlank(tenant)) {
            return Flux.just(new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent("错误：缺少必填的租户参数'tenant'")),
                    true
            ));
        }
        String id = getStringValue(arguments, "id");
        String enName = getStringValue(arguments, "enName");
        if (Stream.of(id, enName).allMatch(StringUtils::isBlank)) {
            return Flux.just(new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent("错误：缺少必填的filter参数")),
                    true
            ));
        }

        Map<String, Object> params = Map.of(
                "tenant", tenant,
                "id", id,
                "enName", enName,
                "userName", getStringValue(arguments, Const.TOKEN_USERNAME)
        );
        String result = gatewayService.searchApiByFilter(getStringValue(arguments, "env"), params);
        return Flux.just(new McpSchema.CallToolResult(
                List.of(new McpSchema.TextContent("查询到使用此filter的api信息为："+result)),
                false
        ));
    }

    private String getStringValue(Map<String, Object> arguments, String key) {
        return Optional.ofNullable(arguments.get(key))
                .map(Object::toString)
                .orElse("");
    }
}
