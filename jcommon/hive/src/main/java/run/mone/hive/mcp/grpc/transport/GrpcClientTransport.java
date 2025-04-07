package run.mone.hive.mcp.grpc.transport;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoSink;
import run.mone.hive.configs.Const;
import run.mone.hive.mcp.client.transport.ServerParameters;
import run.mone.hive.mcp.grpc.*;
import run.mone.hive.mcp.spec.ClientMcpTransport;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.hive.mcp.spec.McpSchema.JSONRPCMessage;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static run.mone.hive.mcp.spec.McpSchema.METHOD_TOOLS_CALL;
import static run.mone.hive.mcp.spec.McpSchema.METHOD_TOOLS_STREAM;

/**
 * goodjava@qq.com
 * gRPC 客户端传输层实现
 */
@Slf4j
@Data
public class GrpcClientTransport implements ClientMcpTransport {

    private final String host;
    private final int port;
    private final ObjectMapper objectMapper;
    private ManagedChannel channel;
    private McpServiceGrpc.McpServiceBlockingStub blockingStub;
    private McpServiceGrpc.McpServiceStub asyncStub;

    private Consumer<String> consumer = (msg) -> {
    };

    /**
     * 创建 gRPC 客户端传输层
     *
     * @param host 服务器主机名
     * @param port 服务器端口
     */
    public GrpcClientTransport(String host, int port) {
        this.host = host;
        this.port = port;
        this.objectMapper = new ObjectMapper();
    }

    public GrpcClientTransport(ServerParameters config) {
        this(config.getEnv().getOrDefault("host", "127.0.0.1"), Integer.valueOf(config.getEnv().getOrDefault("port", Const.GRPC_PORT + "")));
    }

    @Override
    public Mono<Void> connect(Function<Mono<JSONRPCMessage>, Mono<JSONRPCMessage>> handler) {
        return Mono.fromRunnable(() -> {
            this.channel = ManagedChannelBuilder.forAddress(host, port)
                    .usePlaintext()
                    // 启用自动重连
                    .enableRetry()
                    // 设置重连参数
//                    .defaultServiceConfig(Map.of(
//                            "methodConfig", List.of(Map.of(
//                                    "name", List.of(Map.of(
//                                            "service", "yourservice.YourService"  // 替换为您的服务名
//                                    )),
//                                    "retryPolicy", Map.of(
//                                            "maxAttempts", 5.0,
//                                            "initialBackoff", "1s",
//                                            "maxBackoff", "30s",
//                                            "backoffMultiplier", 2.0,
//                                            "retryableStatusCodes", List.of(
//                                                    "UNAVAILABLE",
//                                                    "UNKNOWN"
//                                            )
//                                    )
//                            ))
//                    ))

                    .build();
            this.blockingStub = McpServiceGrpc.newBlockingStub(channel);
            this.asyncStub = McpServiceGrpc.newStub(channel);
        });
    }

    @Override
    public Mono<Void> closeGracefully() {
        return Mono.fromRunnable(() -> {
            try {
                if (channel != null) {
                    channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }

    @Override
    public Mono<Object> sendMessage(JSONRPCMessage message) {
        return Mono.create((sink) -> {
            try {
                if (message instanceof run.mone.hive.mcp.spec.McpSchema.JSONRPCRequest request) {
                    handleToolCall(request, sink);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public Flux<Object> sendStreamMessage(JSONRPCMessage message) {
        return Flux.create(sink -> {
            if (message instanceof run.mone.hive.mcp.spec.McpSchema.JSONRPCRequest request) {
                handleToolStreamCall(request, sink);
            }
        });
    }


    //获取初始化信息(主要是拿到Tools)
    public InitializeResponse initialize(InitializeRequest request) {
        return blockingStub.initialize(request);
    }

    //发送ping消息到服务端
    public PingResponse ping(PingRequest request) {
        return blockingStub.ping(request);
    }

    //列出所有可使用的工具
    public ListToolsResponse listTools(ListToolsRequest request) {
        return blockingStub.listTools(request);
    }

    //METHOD_NOTIFICATION_INITIALIZED
    public NotificationInitializedResponse methodNotificationInitialized() {
        return blockingStub.methodNotificationInitialized(NotificationInitializedRequest.newBuilder().build());
    }

    //连接到服务端,然后等待服务端推送消息回来(支持断线重连)
    public StreamObserver<StreamRequest> observer(StreamObserver<StreamResponse> observer, String clientId) {
        // 创建带重连功能的包装观察者
        StreamObserver<StreamResponse> reconnectingObserver = new StreamObserver<>() {
            @Override
            public void onNext(StreamResponse response) {
                // 直接转发响应
                String data = response.getData();
                consumer.accept(data);
                observer.onNext(response);
            }

            @Override
            public void onError(Throwable t) {
                log.error("连接错误: " + t.getMessage() + "，5秒后重连...");

                // 5秒后重连
                try {
                    Thread.sleep(5000);
                    log.info("正在重新连接...");
                    observer(observer, clientId);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    observer.onError(e);
                }
            }

            @Override
            public void onCompleted() {
                observer.onCompleted();
            }
        };


        StreamObserver<StreamRequest> req = asyncStub.bidirectionalToolStream(reconnectingObserver);
        req.onNext(StreamRequest.newBuilder().setName("observer").setClientId(clientId).build());
        return req;
    }

    @SuppressWarnings("unchecked")
    private void handleToolCall(run.mone.hive.mcp.spec.McpSchema.JSONRPCRequest request, MonoSink sink) {
        McpSchema.CallToolRequest re = (McpSchema.CallToolRequest) request.params();
        Map<String, Object> objectMap = re.arguments();

        Map<String, String> stringMap = objectMap.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> {
                            Object value = e.getValue();
                            if (value == null) {
                                return null;
                            }
                            // 检查是否是复杂类型
                            if (value instanceof Collection<?> ||    // List, Set等集合
                                    value.getClass().isArray() ||        // 数组
                                    value instanceof Map<?, ?> ||        // Map
                                    !value.getClass().isPrimitive() &&   // 不是基本类型
                                            !value.getClass().equals(String.class)) {  // 不是字符串
                                try {
                                    return objectMapper.writeValueAsString(value);
                                } catch (JsonProcessingException ex) {
                                    throw new RuntimeException("Failed to serialize value", ex);
                                }
                            }
                            return Objects.toString(value, null);

                        }
                ));

        String methodName = getMethodName(request);

        CallToolRequest grpcRequest = CallToolRequest.newBuilder()
                .setName(METHOD_TOOLS_CALL)
                .setMethod(methodName)
                .setClientId(request.clientId())
                .putAllArguments(stringMap)
                .build();

        // 发送请求并处理响应
        CallToolResponse response = blockingStub.callTool(grpcRequest);
        sink.success(response);
    }

    private static String getMethodName(McpSchema.JSONRPCRequest request) {
        String methodName = "";
        if (request.params() instanceof McpSchema.CallToolRequest ctr) {
            methodName = ctr.name();
        }
        return methodName;
    }

    private void handleToolStreamCall(run.mone.hive.mcp.spec.McpSchema.JSONRPCRequest request, FluxSink sink) {
        McpSchema.CallToolRequest re = (McpSchema.CallToolRequest) request.params();
        Map<String, Object> objectMap = re.arguments();

        Map<String, String> stringMap = objectMap.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> Objects.toString(e.getValue(), null)
                ));

        String methodName = getMethodName(request);

        //protobuf map 只能是 <string,string>
        CallToolRequest req = CallToolRequest.newBuilder()
                .setName(METHOD_TOOLS_STREAM)
                .putAllArguments(stringMap)
                .setMethod(methodName)
                .setClientId(request.clientId()).build();
        this.asyncStub.callToolStream(req, new StreamObserver<>() {
            @Override
            public void onNext(CallToolResponse callToolResponse) {
                sink.next(callToolResponse);
            }

            @Override
            public void onError(Throwable throwable) {
                sink.error(throwable);
            }

            @Override
            public void onCompleted() {
                sink.complete();
            }
        });

    }

    //grpc 的返回结果,需要手动转换下
    @Override
    public <T> T unmarshalFrom(Object data, TypeReference<T> typeRef) {
        try {
            if (data instanceof CallToolResponse ctr) {
                return (T) new McpSchema.CallToolResult(ctr.getContentList().stream().map(it -> new McpSchema.TextContent(it.getText().getText(), it.getText().getData())).collect(Collectors.toUnmodifiableList()), false);
            }

            if (data instanceof PingResponse pr) {
                return (T) pr;
            }

            if (data instanceof ListToolsResponse ltr) {
                List<McpSchema.Tool> tools = ltr.getToolsList().stream().map(it -> new McpSchema.Tool(it.getName(), it.getDescription(), it.getInputSchema())).toList();
                return (T) new McpSchema.ListToolsResult(tools, ltr.getNextCursor());
            }

            if (data instanceof InitializeResponse ir) {
                McpSchema.Implementation implementation = new McpSchema.Implementation(ir.getServerInfo().getName(), ir.getServerInfo().getVersion());
                return (T) new McpSchema.InitializeResult(ir.getProtocolVersion(), null, implementation, ir.getInstructions());
            }

            if (data instanceof NotificationInitializedResponse nir) {
                return (T) nir;
            }

            if (data instanceof String) {
                return objectMapper.readValue((String) data, typeRef);
            } else {
                return objectMapper.convertValue(data, typeRef);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error unmarshalling data", e);
        }
    }
} 