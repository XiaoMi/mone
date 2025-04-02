package run.mone.hive.mcp.grpc.transport;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.ByteString;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import lombok.Data;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import static run.mone.hive.mcp.spec.McpSchema.METHOD_TOOLS_CALL;
import static run.mone.hive.mcp.spec.McpSchema.METHOD_TOOLS_STREAM;

/**
 * goodjava@qq.com
 * gRPC 服务器端传输层实现
 */
@Slf4j
@Data
public class GrpcServerTransport implements ServerMcpTransport {

    private final int port;

    private final ObjectMapper objectMapper;

    private Server server;

    // 存储用户ID与其连接的映射
    private final ConcurrentHashMap<String, StreamObserver<StreamResponse>> userConnections = new ConcurrentHashMap<>();


    private SimpleMcpGrpcServer grpcServer;


    /**
     * 创建 gRPC 服务器端传输层
     *
     * @param port 监听端口
     */
    public GrpcServerTransport(int port, SimpleMcpGrpcServer simpleMcpGrpcServer) {
        this.port = port;
        this.objectMapper = new ObjectMapper();
        this.grpcServer = simpleMcpGrpcServer;
    }

    @Override
    public Mono<Void> connect(Function<Mono<JSONRPCMessage>, Mono<JSONRPCMessage>> handler) {
        return Mono.fromRunnable(() -> {
            try {
                // 创建 gRPC 服务实现
                McpServiceImpl serviceImpl = new McpServiceImpl();

                // 启动 gRPC 服务器
                server = ServerBuilder.forPort(port)
                        .addService(serviceImpl)
                        .build()
                        .start();

                log.info("gRPC Server started, listening on port " + port);

                // 添加关闭钩子
                Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                    System.out.println("Shutting down gRPC server");
                    GrpcServerTransport.this.close();
                }));
            } catch (IOException e) {
                throw new RuntimeException("Failed to start gRPC server", e);
            }

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

        //用户发过来的ping信息
        @Override
        public void ping(PingRequest request, StreamObserver<PingResponse> responseObserver) {
            responseObserver.onNext(PingResponse.newBuilder().setMessage("pong").setTimestamp(System.currentTimeMillis()).build());
            responseObserver.onCompleted();
        }

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
            if (name.equals(METHOD_TOOLS_CALL)) {
                DefaultMcpSession.RequestHandler rh = grpcServer.getMcpSession().getRequestHandlers().get(name);
                Object res = rh.handle(request).block();

                //目前只支持一个Content
                if (res instanceof McpSchema.CallToolResult ctr) {
                    List<McpSchema.Content> list = ctr.content();
                    if (!list.isEmpty() && list.get(0) instanceof McpSchema.TextContent tc) {
                        builder.setData(tc.data());
                        builder.setText(tc.text());
                    }
                }
            }

            responseObserver.onNext(CallToolResponse.newBuilder().addContent(Content.newBuilder().setText(builder).build()).build());
            responseObserver.onCompleted();
        }

        //调用工具(流式返回)
        @Override
        public void callToolStream(CallToolRequest request, StreamObserver<CallToolResponse> responseObserver) {
            String name = request.getName();
            //请求进来的
            if (name.equals(METHOD_TOOLS_STREAM)) {
                DefaultMcpSession.StreamRequestHandler rh = grpcServer.getMcpSession().getStreamRequestHandlers().get(name);
                rh.handle(request).subscribe(it -> {

                    List<Content> contentList = new ArrayList<>();
                    if (it instanceof McpSchema.CallToolResult ctr) {
                        List<McpSchema.Content> list = ctr.content();

                        contentList.addAll(list.stream().map(it2 -> {
                            if (it2 instanceof McpSchema.TextContent tc) {
                                return Content.newBuilder().setText(TextContent.newBuilder().setData(tc.data()).setText(tc.text()).build()).build();
                            }
                            return null;
                        }).filter(Objects::nonNull).toList());
                    }

                    responseObserver.onNext(CallToolResponse.newBuilder().addAllContent(contentList).build());
                }, responseObserver::onError, responseObserver::onCompleted);
            }
        }

    }
} 