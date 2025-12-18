package run.mone.hive.sink;

import run.mone.hive.configs.Const;
import run.mone.hive.mcp.grpc.transport.GrpcServerTransport;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.hive.roles.ReactorRole;

import java.util.HashMap;
import java.util.Map;

/**
 * @author goodjava@qq.com
 * @date 2025/10/9
 */
public class McpGrpcTransportSink {

    private final GrpcServerTransport transport;
    private final ReactorRole role;

    public McpGrpcTransportSink(GrpcServerTransport transport, ReactorRole role) {
        this.transport = transport;
        this.role = role;
    }

    //处理的地方,可以看看hive-manager 里的(页面里直接显示出来)
    public void sendNotification(String message) {
        Map<String, Object> params = new HashMap<>();
        // 通过 client_id 来找到接受者
        params.put(Const.CLIENT_ID, role.getClientId());
        params.put(Const.OWNER_ID, role.getOwner());
        params.put("cmd", Const.NOTIFY_HIVE_MANAGER);
        params.put("data", message);
        params.put("id", "1");
        transport.sendMessage(new McpSchema.JSONRPCNotification("", "msg", params));
    }
}
