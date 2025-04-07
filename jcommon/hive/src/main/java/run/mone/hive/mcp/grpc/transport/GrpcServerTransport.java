package run.mone.hive.mcp.grpc.transport;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import run.mone.hive.common.Safe;
import run.mone.hive.mcp.grpc.*;
import run.mone.hive.mcp.server.McpAsyncServer;
import run.mone.hive.mcp.spec.DefaultMcpSession;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.hive.mcp.spec.McpSchema.JSONRPCMessage;
import run.mone.hive.mcp.spec.ServerMcpTransport;
import run.mone.m78.client.util.GsonUtils;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
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

    private final ConcurrentHashMap<String, Long> clientMap = new ConcurrentHashMap<>();

    private McpAsyncServer mcpServer;

    private BiFunction<String, String, Boolean> authFunction = (id, token) -> true;

    private boolean openAuth = false;

    public GrpcServerTransport(int port) {
        this.port = port;
        this.objectMapper = new ObjectMapper();

        Executors.newSingleThreadScheduledExecutor().scheduleWithFixedDelay(() -> {
            Safe.run(() -> {
                long now = System.currentTimeMillis();
                Set<String> keys = new HashSet<>(clientMap.keySet());
                for (String key : keys) {
                    clientMap.computeIfPresent(key, (k, v) ->
                            now - v >= TimeUnit.MINUTES.toMillis(5) ? null : v);
                }
            });
        }, 1, 1, TimeUnit.MINUTES);
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
                    log.info("Shutting down gRPC server");
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

        public McpServiceImpl() {
            Executors.newSingleThreadScheduledExecutor().scheduleWithFixedDelay(() -> {
                Safe.run(() -> {
                    userConnections.forEach((k, v) -> {
                        v.onNext(StreamResponse.newBuilder().setData("data:" + k).build());
                    });
                });
            }, 5, 5, TimeUnit.SECONDS);
        }


        //用户发过来的ping信息
        @Override
        public void ping(PingRequest request, StreamObserver<PingResponse> responseObserver) {
            if (openAuth) {
                String clientId = request.getClientId();
                clientMap.computeIfPresent(clientId, (k, v) -> System.currentTimeMillis());
            }

            responseObserver.onNext(PingResponse.newBuilder().setMessage("pong").setTimestamp(System.currentTimeMillis()).build());
            responseObserver.onCompleted();
        }

        //通知回来,mcpclient 已经初始化完毕了
        @Override
        public void methodNotificationInitialized(NotificationInitializedRequest request, StreamObserver<NotificationInitializedResponse> responseObserver) {
            responseObserver.onNext(NotificationInitializedResponse.newBuilder().build());
            responseObserver.onCompleted();
        }

        //clientId 上来就先连接过来,同时持有这个 reponseObserver,合适的时候给用户回复信息
        @Override
        public StreamObserver<StreamRequest> bidirectionalToolStream(StreamObserver<StreamResponse> responseObserver) {
            return new StreamObserver<>() {

                private String clientId = null;

                @Override
                public void onNext(StreamRequest streamRequest) {
                    String name = streamRequest.getName();
                    log.info("bidirectionalToolStream name:{}", name);
                    //连接过来,随时可以通过服务器推回去信息
                    if (name.equals("observer")) {
                        clientId = streamRequest.getClientId();
                        //验证权限
                        if (openAuth && !authFunction.apply(clientId, streamRequest.getToken())) {
                            throw new RuntimeException("auth error");
                        }
                        userConnections.putIfAbsent(clientId, responseObserver);
                    }

                }

                @Override
                public void onError(Throwable throwable) {
                    log.error(throwable.getMessage(), throwable);
                    responseObserver.onError(throwable);
                    if (null != clientId) {
                        userConnections.remove(clientId);
                    }
                }

                @Override
                public void onCompleted() {
                    responseObserver.onCompleted();
                }
            };

        }

        //初始化过来
        @Override
        public void initialize(InitializeRequest request, StreamObserver<InitializeResponse> responseObserver) {
            // 简化的示例实现
            InitializeResponse response = InitializeResponse.newBuilder()
                    .setProtocolVersion("2024-11-05")
                    .setCapabilities(ServerCapabilities.newBuilder().setTools(ToolCapabilities.newBuilder().build()).build())
                    .setServerInfo(Implementation.newBuilder()
                            .setName("gRPC-MCP-Server")
                            .setVersion("1.0.0")
                            .build())
                    .build();
            //权限校验
            if (openAuth && !authFunction.apply(request.getClientId(), request.getToken())) {
                responseObserver.onError(new RuntimeException("auth error"));
            } else {
                clientMap.putIfAbsent(request.getClientId(), System.currentTimeMillis());
                responseObserver.onNext(response);
                responseObserver.onCompleted();
            }

        }

        //返回工具列表
        @Override
        public void listTools(ListToolsRequest request, StreamObserver<ListToolsResponse> responseObserver) {
            List<Tool> tools = new ArrayList<>();
            mcpServer.getStreamTools().forEach(it -> {
                McpSchema.JsonSchema inputSchema = it.tool().inputSchema();
                String inputSchemaStr = GsonUtils.GSON.toJson(inputSchema);
                Tool tool = Tool.newBuilder()
                        .setName(it.tool().name()).setDescription(it.tool().description())
                        .setInputSchema(inputSchemaStr)
                        .build();
                tools.add(tool);
            });

            mcpServer.getTools().forEach(it -> {
                McpSchema.JsonSchema inputSchema = it.tool().inputSchema();
                String inputSchemaStr = GsonUtils.GSON.toJson(inputSchema);
                Tool tool = Tool.newBuilder()
                        .setName(it.tool().name()).setDescription(it.tool().description())
                        .setInputSchema(inputSchemaStr)
                        .build();
                tools.add(tool);
            });

            responseObserver.onNext(ListToolsResponse.newBuilder().addAllTools(tools).build());
            responseObserver.onCompleted();
        }

        //调用工具(返回一个结果)
        @Override
        public void callTool(CallToolRequest request, StreamObserver<CallToolResponse> responseObserver) {
            String name = request.getName();
            TextContent.Builder builder = TextContent.newBuilder();

            //验证下权限
            auth(request);

            //请求进来的
            if (name.equals(METHOD_TOOLS_CALL)) {
                DefaultMcpSession.RequestHandler rh = mcpServer.getMcpSession().getRequestHandlers().get(name);
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

            //验证下权限
            auth(request);

            //请求进来的
            if (name.equals(METHOD_TOOLS_STREAM)) {
                DefaultMcpSession.StreamRequestHandler rh = mcpServer.getMcpSession().getStreamRequestHandlers().get(name);
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

    private void auth(CallToolRequest request) {
        if (openAuth) {
            String clientId = request.getClientId();
            String token = request.getToken();
            if (!clientMap.containsKey(clientId)) {
                if (!authFunction.apply(clientId, token)) {
                    throw new RuntimeException("call tool (auth error)");
                } else {
                    clientMap.putIfAbsent(clientId, System.currentTimeMillis());
                }
            }
        }
    }
} 