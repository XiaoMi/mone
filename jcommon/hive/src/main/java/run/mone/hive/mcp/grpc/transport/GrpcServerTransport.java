package run.mone.hive.mcp.grpc.transport;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.grpc.*;
import io.grpc.stub.StreamObserver;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import run.mone.hive.common.Safe;
import run.mone.hive.configs.Const;
import run.mone.hive.mcp.common.ClientMeta;
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

    // 存储会话元数据的Map
    private final ConcurrentHashMap<String, ClientMeta> sessionMetadata = new ConcurrentHashMap<>();

    private McpAsyncServer mcpServer;

    private BiFunction<String, String, Boolean> authFunction = (id, token) -> true;

    private boolean openAuth = false;

    // 定义元数据键
    private static final Metadata.Key<String> CLIENT_ID_KEY =
            Metadata.Key.of(Const.CLIENT_ID, Metadata.ASCII_STRING_MARSHALLER);
    private static final Metadata.Key<String> TOKEN_KEY =
            Metadata.Key.of(Const.TOKEN, Metadata.ASCII_STRING_MARSHALLER);
    // 为元数据定义Context键
    private static final Context.Key<Metadata> METADATA_CONTEXT_KEY = Context.key("metadata");

    public GrpcServerTransport(int port) {
        this.port = port;
        this.objectMapper = new ObjectMapper();

        Executors.newSingleThreadScheduledExecutor().scheduleWithFixedDelay(() -> {
            Safe.run(() -> {
                long now = System.currentTimeMillis();
                Set<String> keys = new HashSet<>(sessionMetadata.keySet());
                for (String key : keys) {
                    sessionMetadata.computeIfPresent(key, (k, v) ->
                            now - v.getTime() >= TimeUnit.MINUTES.toMillis(5) ? null : v);
                }
            });
        }, 1, 1, TimeUnit.MINUTES);
    }

    /**
     * 元数据拦截器，用于从请求中提取元数据
     */
    private class MetadataInterceptor implements ServerInterceptor {
        @Override
        public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
                ServerCall<ReqT, RespT> call,
                Metadata headers,
                ServerCallHandler<ReqT, RespT> next) {

            // 提取元数据
            String clientId = headers.get(CLIENT_ID_KEY);
            String token = headers.get(TOKEN_KEY);

            //统一验证权限
            auth(clientId, token);

            // 将元数据存储到Context中
            Context context = Context.current().withValue(METADATA_CONTEXT_KEY, headers);
            return Contexts.interceptCall(context, call, headers, next);
        }
    }

    @Override
    public Mono<Void> connect(Function<Mono<JSONRPCMessage>, Mono<JSONRPCMessage>> handler) {
        return Mono.fromRunnable(() -> {
            try {
                // 创建 gRPC 服务实现
                McpServiceImpl serviceImpl = new McpServiceImpl();

                // 启动 gRPC 服务器，添加元数据拦截器
                server = ServerBuilder.forPort(port)
                        .addService(serviceImpl)
                        .intercept(new MetadataInterceptor())  // 添加元数据拦截器
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


    //允许通知到client
    @Override
    public Mono<Object> sendMessage(JSONRPCMessage message) {
        if (message instanceof McpSchema.JSONRPCNotification notification) {
            Map<String, Object> params = notification.params();
            if (null != params && params.containsKey(Const.CLIENT_ID)) {
                String clientId = params.get(Const.CLIENT_ID).toString();
                StreamObserver<StreamResponse> observer = userConnections.get(clientId);
                //直接通知到client
                if (null != observer) {
                    observer.onNext(StreamResponse.newBuilder().setData(GsonUtils.GSON.toJson(params)).build());
                }
            }
        }
        return Mono.empty();
    }

    @Override
    public <T> T unmarshalFrom(Object data, TypeReference<T> typeRef) {
        return objectMapper.convertValue(data, typeRef);
    }

    /**
     * 从请求或元数据中获取客户端ID
     *
     * @return 客户端ID
     */
    private String getClientIdFromContext() {
        // 从当前上下文获取元数据
        Metadata metadata = METADATA_CONTEXT_KEY.get();
        if (metadata != null) {
            String metadataClientId = metadata.get(CLIENT_ID_KEY);
            if (metadataClientId != null) {
                return metadataClientId;
            }
        }
        return "";
    }

    /**
     * 从请求或元数据中获取令牌
     *
     * @return 令牌
     */
    private String getTokenFromContext() {
        // 首先尝试从当前上下文获取
        Metadata metadata = METADATA_CONTEXT_KEY.get();
        if (metadata != null) {
            String metadataToken = metadata.get(TOKEN_KEY);
            if (metadataToken != null) {
                return metadataToken;
            }
        }
        return "";
    }


    /**
     * MCP 服务的 gRPC 实现
     */
    private class McpServiceImpl extends McpServiceGrpc.McpServiceImplBase {

        public McpServiceImpl() {
            Executors.newSingleThreadScheduledExecutor().scheduleWithFixedDelay(() -> {
                Safe.run(() -> {
                    userConnections.forEach((k, v) -> {
                        v.onNext(StreamResponse.newBuilder().setData("server ping :" + k).build());
                    });
                });
            }, 5, 5, TimeUnit.SECONDS);
        }


        //用户发过来的ping信息
        @Override
        public void ping(PingRequest request, StreamObserver<PingResponse> responseObserver) {
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
                        // 尝试从元数据获取ClientId，如果没有则使用请求中的
                        clientId = getClientIdFromContext();
                        // 尝试从元数据获取token，如果没有则使用请求中的
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
            responseObserver.onNext(response);
            responseObserver.onCompleted();
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

    private void auth() {
        if (openAuth) {
            // 从元数据或请求中获取客户端ID和令牌
            String clientId = getClientIdFromContext();
            String token = getTokenFromContext();
            auth(clientId, token);
        }
    }

    private void auth(String clientId, String token) {
        if (openAuth) {
            // 从元数据或请求中获取客户端ID和令牌
            long now = System.currentTimeMillis();

            if (!sessionMetadata.containsKey(clientId)) {
                if (!authFunction.apply(clientId, token)) {
                    throw new RuntimeException("call tool (auth error)");
                } else {
                    sessionMetadata.compute(clientId, (k, v) -> {
                        if (v == null) {
                            return ClientMeta.builder().time(now).token(token).build();
                        }
                        v.setTime(now);
                        return v;
                    });
                }
            } else {
                if (!sessionMetadata.get(clientId).getToken().equals(token)) {
                    throw new RuntimeException("auth error clientId:" + clientId);
                }
                sessionMetadata.get(clientId).setTime(now);
            }
        }
    }

} 