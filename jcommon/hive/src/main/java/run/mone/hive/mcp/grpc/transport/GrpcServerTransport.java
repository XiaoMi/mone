package run.mone.hive.mcp.grpc.transport;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.ByteString;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import run.mone.hive.configs.Const;
import run.mone.hive.mcp.grpc.*;
import run.mone.hive.mcp.grpc.demo.SimpleMcpGrpcServer;
import run.mone.hive.mcp.spec.DefaultMcpSession;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.hive.mcp.spec.McpSchema.JSONRPCMessage;
import run.mone.hive.mcp.spec.ServerMcpTransport;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * goodjava@qq.com
 * gRPC 服务器端传输层实现
 */
@Slf4j
public class GrpcServerTransport implements ServerMcpTransport {

    private final int port;

    private final ObjectMapper objectMapper;

    private Server server;

    // 存储用户ID与其连接的映射
    private final ConcurrentHashMap<String, StreamObserver<StreamResponse>> userConnections = new ConcurrentHashMap<>();


    private SimpleMcpGrpcServer simpleMcpGrpcServer;


    /**
     * 创建 gRPC 服务器端传输层
     *
     * @param port 监听端口
     */
    public GrpcServerTransport(int port) {
        this.port = port;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public Mono<Void> connect(Function<Mono<JSONRPCMessage>, Mono<JSONRPCMessage>> handler) {
        return Mono.fromRunnable(() -> {
        });
    }

    @Override
    public Mono<Void> closeGracefully() {
        return Mono.fromRunnable(() -> {
            if (server != null) {
                server.shutdown();
            }
        });
    }

    @Override
    public Mono<Object> sendMessage(JSONRPCMessage message) {

        return Mono.empty();
    }

    @Override
    public <T> T unmarshalFrom(Object data, TypeReference<T> typeRef) {
        return objectMapper.convertValue(data, typeRef);
    }


    /**
     * MCP 服务的 gRPC 实现
     */
    private class McpServiceImpl extends McpServiceGrpc.McpServiceImplBase {

        //clientId 上来就先连接过来,同时持有这个 reponseObserver,合适的时候给用户回复信息
        @Override
        public StreamObserver<StreamRequest> bidirectionalToolStream(StreamObserver<StreamResponse> responseObserver) {
            return new StreamObserver<>() {

                private String userId = null;

                @Override
                public void onNext(StreamRequest streamRequest) {
                    String name = streamRequest.getName();
                    log.info("call name:{}", name);
                    if (name.equals("login")) {
                        ByteString userIdBytes = streamRequest.getArgumentsMap().get(Const.MC_CLIENT_ID);
                        userId = userIdBytes.toStringUtf8();
                        userConnections.put(userId, responseObserver);
                        TextContent content = TextContent.newBuilder().setText("success").build();
                        responseObserver.onNext(StreamResponse.newBuilder().addContent(Content.newBuilder().setText(content).build()).build());
                    }


                }

                @Override
                public void onError(Throwable throwable) {
                    log.error(throwable.getMessage(), throwable);
                    responseObserver.onError(throwable);
                    if (null != userId) {
                        userConnections.remove(userId);
                    }
                }

                @Override
                public void onCompleted() {
                    responseObserver.onCompleted();
                }
            };

        }

        @Override
        public void initialize(InitializeRequest request, StreamObserver<InitializeResponse> responseObserver) {
            // 简化的示例实现
            InitializeResponse response = InitializeResponse.newBuilder()
                    .setProtocolVersion("2024-11-05")
                    .setServerInfo(Implementation.newBuilder()
                            .setName("gRPC-MCP-Server")
                            .setVersion("1.0.0")
                            .build())
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }

        //返回工具列表
        @Override
        public void listTools(ListToolsRequest request, StreamObserver<ListToolsResponse> responseObserver) {
            responseObserver.onNext(ListToolsResponse.newBuilder().build());
            responseObserver.onCompleted();
        }

        //调用工具(返回一个结果)
        @Override
        public void callTool(CallToolRequest request, StreamObserver<CallToolResponse> responseObserver) {
            String name = request.getName();
            TextContent.Builder builder = TextContent.newBuilder();

            //请求进来的
            if (name.equals("callTool")) {
                String method = request.getArgumentsMap().getOrDefault("_method", "");
                DefaultMcpSession.RequestHandler rh = simpleMcpGrpcServer.getMcpSession().getRequestHandlers().get(method);
                Object res = rh.handle(request.getArgumentsMap()).block();
                builder.setText(res.toString());
            }

            responseObserver.onNext(CallToolResponse.newBuilder().addContent(Content.newBuilder().setText(builder.build()).build()).build());
            responseObserver.onCompleted();
        }

        //调用工具(流式返回)
        @Override
        public void callToolStream(CallToolRequest request, StreamObserver<CallToolResponse> responseObserver) {
            String name = request.getName();
            //请求进来的
            if (name.equals("callToolStream")) {
                String method = request.getArgumentsMap().getOrDefault("_method", "");
                DefaultMcpSession.StreamRequestHandler rh = simpleMcpGrpcServer.getMcpSession().getStreamRequestHandlers().get(method);
                rh.handle(request.getArgumentsMap()).subscribe(it -> {
                    TextContent.Builder builder = TextContent.newBuilder();
                    if (it instanceof McpSchema.CallToolResult ctr) {
                        List<McpSchema.Content> list = ctr.content();
                        if (list.size() > 0) {
                            if (list.get(0) instanceof McpSchema.TextContent tc)
                            builder.setText(tc.data());
                        }
                    }
                    responseObserver.onNext(CallToolResponse.newBuilder().addContent(Content.newBuilder().setText(builder).build()).build());
                }, responseObserver::onError, responseObserver::onCompleted);
            }
        }

    }
} 