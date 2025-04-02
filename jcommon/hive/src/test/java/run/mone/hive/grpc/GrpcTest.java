package run.mone.hive.grpc;

import com.google.api.client.util.Lists;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import run.mone.hive.mcp.grpc.demo.SimpleMcpGrpcServer;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author goodjava@qq.com
 * @date 2025/4/2 09:29
 */
public class GrpcTest {


    @SneakyThrows
    @Test
    public void testServer() {
        SimpleMcpGrpcServer server = new SimpleMcpGrpcServer(null, new CopyOnWriteArrayList<>(), new CopyOnWriteArrayList<>());
        server.init();
        System.in.read();
    }

}
