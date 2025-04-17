package run.mone.agentx.service;

import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.FluxSink;
import run.mone.agentx.interceptor.CustomMcpInterceptor;
import run.mone.hive.common.McpResult;
import run.mone.hive.common.Result;
import run.mone.hive.common.RoleType;
import run.mone.hive.configs.Const;
import run.mone.hive.mcp.client.MonerMcpClient;
import run.mone.hive.mcp.client.MonerMcpInterceptor;
import run.mone.hive.mcp.hub.McpHub;
import run.mone.hive.mcp.hub.McpHubHolder;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.hive.schema.Message;

import javax.annotation.PostConstruct;
import java.nio.file.Paths;
import java.util.List;

@Slf4j
@Data
@Service
public class McpService {

    private MonerMcpInterceptor mcpInterceptor = new CustomMcpInterceptor();

    @Value("${mcp.hub.path:}")
    private String mcpPath;

    @PostConstruct
    @SneakyThrows
    public void init() {
        //启用mcp (这个Agent也可以使用mcp)
        if (StringUtils.isNotEmpty(mcpPath)) {
            McpHubHolder.put(Const.DEFAULT, new McpHub(Paths.get(mcpPath)));
        }
    }


    public Message callMcp(Result it, FluxSink sink) {
        McpResult result = MonerMcpClient.mcpCall(it, Const.DEFAULT, this.mcpInterceptor, sink);
        McpSchema.Content content = result.getContent();
        if (content instanceof McpSchema.TextContent textContent) {
            return Message.builder().role(RoleType.assistant.name()).data(textContent.text()).sink(sink).content("调用Tool的结果:" + textContent.text() + "\n" + "; 请继续").build();
        } else if (content instanceof McpSchema.ImageContent imageContent) {
            return Message.builder().role(RoleType.assistant.name()).data("图片占位符").sink(sink).images(List.of(imageContent.data())).content("图片占位符" + "\n" + "; 请继续").build();
        }

        return null;
    }

}
