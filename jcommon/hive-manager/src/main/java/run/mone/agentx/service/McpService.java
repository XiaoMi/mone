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


    public void callMcp(Result it, FluxSink sink) {
        MonerMcpClient.mcpCall(it, Const.DEFAULT, this.mcpInterceptor, sink);
    }

}
