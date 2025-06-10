package run.mone.hive.mcp.grpc.transport;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.google.gson.reflect.TypeToken;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Metadata;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.MetadataUtils;
import io.grpc.stub.StreamObserver;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoSink;
import run.mone.hive.common.GsonUtils;
import run.mone.hive.common.Safe;
import run.mone.hive.configs.Const;
import run.mone.hive.mcp.client.transport.ServerParameters;
import run.mone.hive.mcp.grpc.CallToolRequest;
import run.mone.hive.mcp.grpc.CallToolResponse;
import run.mone.hive.mcp.grpc.InitializeRequest;
import run.mone.hive.mcp.grpc.InitializeResponse;
import run.mone.hive.mcp.grpc.ListToolsRequest;
import run.mone.hive.mcp.grpc.ListToolsResponse;
import run.mone.hive.mcp.grpc.McpServiceGrpc;
import run.mone.hive.mcp.grpc.NotificationInitializedRequest;
import run.mone.hive.mcp.grpc.NotificationInitializedResponse;
import run.mone.hive.mcp.grpc.PingRequest;
import run.mone.hive.mcp.grpc.PingResponse;
import run.mone.hive.mcp.grpc.StreamRequest;
import run.mone.hive.mcp.grpc.StreamResponse;
import run.mone.hive.mcp.spec.ClientMcpTransport;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.hive.mcp.spec.McpSchema.JSONRPCMessage;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
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

    // 存储元数据的 Map
    private Map<String, String> metaData = new HashMap<>();

    private AtomicBoolean close = new AtomicBoolean(false);

    private Consumer<Object> consumer = (msg) -> {
    };

    // 添加重连相关的字段
    private volatile boolean isReconnecting = false;
    private final ScheduledExecutorService reconnectExecutor = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r, "grpc-reconnect");
        t.setDaemon(true);
        return t;
    });

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
        this(config.getEnv().getOrDefault("host", "127.0.0.1"), Integer.parseInt(config.getEnv().getOrDefault("port", Const.GRPC_PORT + "")));
        Map<String, String> env = config.getEnv();
        if (env.containsKey(Const.CLIENT_ID) && env.containsKey(Const.TOKEN)) {
            setClientAuth(env.get(Const.CLIENT_ID), env.get(Const.TOKEN));
        }
    }

    /**
     * 设置元数据
     *
     * @param key   元数据键
     * @param value 元数据值
     */
    public void setMetaData(String key, String value) {
        metaData.put(key, value);
    }

    /**
     * 设置客户端ID和令牌
     *
     * @param clientId 客户端ID
     * @param token    令牌
     */
    public void setClientAuth(String clientId, String token) {
        setMetaData(Const.CLIENT_ID, clientId);
        setMetaData(Const.TOKEN, token);
    }

    /**
     * 创建并应用元数据到 gRPC 调用
     *
     * @return 元数据对象
     */
    private Metadata createMetadata() {
        Metadata metadata = new Metadata();
        for (Map.Entry<String, String> entry : metaData.entrySet()) {
            Metadata.Key<String> key = Metadata.Key.of(entry.getKey(), Metadata.ASCII_STRING_MARSHALLER);
            metadata.put(key, entry.getValue());
        }
        return metadata;
    }

    /**
     * 应用元数据到阻塞存根
     *
     * @return 携带元数据的阻塞存根
     */
    private McpServiceGrpc.McpServiceBlockingStub getMetadataBlockingStub() {
        if (metaData.isEmpty()) {
            return blockingStub;
        }
        return blockingStub.withInterceptors(MetadataUtils.newAttachHeadersInterceptor(createMetadata()));
    }

    /**
     * 应用元数据到异步存根
     *
     * @return 携带元数据的异步存根
     */
    private McpServiceGrpc.McpServiceStub getMetadataAsyncStub() {
        if (metaData.isEmpty()) {
            return asyncStub;
        }
        //会把meta信息放入到header中
        return asyncStub.withInterceptors(MetadataUtils.newAttachHeadersInterceptor(createMetadata()));
    }

    @Override
    public Mono<Void> connect(Function<Mono<JSONRPCMessage>, Mono<JSONRPCMessage>> handler) {
        return Mono.fromRunnable(() -> {
            this.channel = ManagedChannelBuilder.forAddress(host, port)
                    .usePlaintext()
                    // 启用自动重连
                    .enableRetry()
                    // 添加连接配置，提高连接稳定性
                    .keepAliveTime(30, TimeUnit.SECONDS)
                    .keepAliveTimeout(5, TimeUnit.SECONDS)
                    .keepAliveWithoutCalls(true)
                    .maxInboundMessageSize(4 * 1024 * 1024)
                    .maxRetryAttempts(3)
                    .build();
            this.blockingStub = McpServiceGrpc.newBlockingStub(channel);
            this.asyncStub = McpServiceGrpc.newStub(channel);
        });
    }

    // 改进后的 observer 方法
    public StreamObserver<StreamRequest> observer(StreamObserver<StreamResponse> observer) {
        // 确保连接已经建立再创建双向流
        waitForChannelReady();
        return createObserverWithReconnect(observer, 0);
    }
    
    // 等待 Channel 准备就绪
    private void waitForChannelReady() {
        if (channel == null) {
            throw new IllegalStateException("Channel not initialized. Call connect() first.");
        }
        
        // 等待连接就绪，最多等待5秒
        try {
            boolean ready = channel.getState(true) != io.grpc.ConnectivityState.READY;
            if (ready) {
                // 触发连接并等待状态变化
                for (int i = 0; i < 50; i++) { // 最多等待5秒
                    if (channel.getState(false) == io.grpc.ConnectivityState.READY) {
                        break;
                    }
                    Thread.sleep(100);
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private StreamObserver<StreamRequest> createObserverWithReconnect(StreamObserver<StreamResponse> observer, int attemptCount) {
        if (close.get()) {
            log.info("客户端已关闭，停止重连");
            return null;
        }

        // 重建连接（每次都重建，确保连接是新的）
        if (attemptCount > 0 || isReconnecting) {
            log.info("重建gRPC连接... 尝试次数: {}", attemptCount);
            recreateChannel();
        }

        // 创建带重连功能的包装观察者
        StreamObserver<StreamResponse> reconnectingObserver = new StreamObserver<>() {
            @Override
            public void onNext(StreamResponse response) {
                isReconnecting = false; // 连接成功，重置重连状态
                Safe.run(() -> {
                    if (response.getCmd().equals(Const.NOTIFY_MSG)) {
                        String data = response.getData();
                        Type typeOfT = new TypeToken<Map<String, String>>() {
                        }.getType();
                        Map map = GsonUtils.gson.fromJson(data, typeOfT);
                        consumer.accept(map);
                    }
                    // 直接转发响应
                    String data = response.getData();
                    consumer.accept(data);
                    observer.onNext(response);
                });
            }

            @Override
            public void onError(Throwable t) {
                log.error("连接错误: " + t.getMessage() + "，准备重连...");

                if (close.get()) {
                    log.info("client exit");
                    return;
                }

                if (isReconnecting) {
                    log.warn("已在重连中，跳过本次重连请求");
                    return;
                }

                isReconnecting = true;

                if (t instanceof StatusRuntimeException) {
                    StatusRuntimeException sre = (StatusRuntimeException) t;
                    log.error("- gRPC状态: {}", sre.getStatus());
                    log.error("- 状态描述: {}", sre.getStatus().getDescription());
                    log.error("- 状态原因: {}", sre.getStatus().getCause());
                }

                // 异步执行重连，避免阻塞gRPC事件循环
                scheduleReconnect(observer, attemptCount + 1);
            }

            @Override
            public void onCompleted() {
                log.info("服务端关闭连接，准备重连...");
                if (!close.get()) {
                    scheduleReconnect(observer, attemptCount + 1);
                } else {
                    observer.onCompleted();
                }
            }
        };

        try {
            // 添加连接状态检查
            if (channel == null || channel.isShutdown() || channel.isTerminated()) {
                throw new IllegalStateException("Channel is not available for creating stream");
            }
            
            // 在独立的 Context 中创建双向流，避免被上游取消影响
            io.grpc.Context independentContext = io.grpc.Context.ROOT.fork();
            
            req = independentContext.call(() -> 
                getMetadataAsyncStub().bidirectionalToolStream(reconnectingObserver)
            );

            // 构建请求时添加 token
            StreamRequest.Builder builder = StreamRequest.newBuilder()
                    .setName("observer");

            req.onNext(builder.build());
            log.info("连接建立成功，开始接收消息");
            return req;
        } catch (Exception e) {
            System.err.println("创建双向流失败: " + e.getMessage());
            scheduleReconnect(observer, attemptCount + 1);
            return null;
        }
    }

    private void scheduleReconnect(StreamObserver<StreamResponse> observer, int attemptCount) {
        if (close.get()) {
            log.info("客户端已关闭，停止重连");
            return;
        }

        // 计算重连延迟（指数退避，最大30秒）
        long delay = Math.min(5 * (1L << Math.min(attemptCount - 1, 3)), 30);
        
        log.info("{}秒后进行第{}次重连...", delay, attemptCount);
        
        reconnectExecutor.schedule(() -> {
            if (!close.get()) {
                try {
                    createObserverWithReconnect(observer, attemptCount);
                } catch (Exception e) {
                    System.err.println("重连失败: " + e.getMessage());
                    // 继续重连
                    scheduleReconnect(observer, attemptCount + 1);
                }
            }
        }, delay, TimeUnit.SECONDS);
    }

    // 改进 recreateChannel 方法
    private synchronized void recreateChannel() {
        System.out.println("开始重建gRPC通道...");
        
        // 关闭旧的连接
        if (channel != null && !channel.isShutdown()) {
            try {
                channel.shutdown();
                if (!channel.awaitTermination(3, TimeUnit.SECONDS)) {
                    System.err.println("通道关闭超时，强制关闭");
                    channel.shutdownNow();
                    channel.awaitTermination(2, TimeUnit.SECONDS);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                channel.shutdownNow();
            }
        }

        // 创建新的连接，添加更完善的配置
        this.channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .enableRetry()
                // 添加连接配置
                .keepAliveTime(30, TimeUnit.SECONDS)
                .keepAliveTimeout(5, TimeUnit.SECONDS)
                .keepAliveWithoutCalls(true)
                .maxInboundMessageSize(4 * 1024 * 1024)
                // 添加重试配置
                .maxRetryAttempts(3)
                .build();
                
        this.blockingStub = McpServiceGrpc.newBlockingStub(channel);
        this.asyncStub = McpServiceGrpc.newStub(channel);
        
        System.out.println("gRPC通道重建完成");
    }

    // 改进 closeGracefully 方法
    @Override
    public Mono<Void> closeGracefully() {
        System.out.println("closeGracefully");
        close.set(true);
        isReconnecting = false;
        
        return Mono.fromRunnable(() -> {
            try {
                // 关闭重连执行器
                if (reconnectExecutor != null) {
                    reconnectExecutor.shutdown();
                    if (!reconnectExecutor.awaitTermination(2, TimeUnit.SECONDS)) {
                        reconnectExecutor.shutdownNow();
                    }
                }
                
                // 关闭当前连接
                if (req != null) {
                    try {
                        req.onCompleted();
                    } catch (Exception e) {
                        System.err.println("关闭req时出错: " + e.getMessage());
                    }
                }
                
                if (channel != null) {
                    channel.shutdown();
                    if (!channel.awaitTermination(5, TimeUnit.SECONDS)) {
                        channel.shutdownNow();
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }

    // 添加连接状态检查方法
    public boolean isConnected() {
        return channel != null && 
               !channel.isShutdown() && 
               !channel.isTerminated() &&
               !isReconnecting;
    }


    @Override
    public Mono<Object> sendMessage(JSONRPCMessage message) {
        return Mono.create((sink) -> {
            if (message instanceof run.mone.hive.mcp.spec.McpSchema.JSONRPCRequest request) {
                handleToolCall(request, sink);
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
        return getMetadataBlockingStub().initialize(request);
    }

    //发送ping消息到服务端
    public PingResponse ping(PingRequest request) {
        return getMetadataBlockingStub().ping(request);
    }

    //列出所有可使用的工具
    public ListToolsResponse listTools(ListToolsRequest request) {
        return getMetadataBlockingStub().listTools(request);
    }

    //METHOD_NOTIFICATION_INITIALIZED
    public NotificationInitializedResponse methodNotificationInitialized() {
        return getMetadataBlockingStub().methodNotificationInitialized(NotificationInitializedRequest.newBuilder().build());
    }

    StreamObserver<StreamRequest> req;

    @SuppressWarnings("unchecked")
    private void handleToolCall(run.mone.hive.mcp.spec.McpSchema.JSONRPCRequest request, MonoSink sink) {
        McpSchema.CallToolRequest re = (McpSchema.CallToolRequest) request.params();
        Map<String, Object> objectMap = re.arguments();

        Map<String, String> stringMap = objectMap.entrySet().stream()
                .filter(e -> Objects.nonNull(e.getKey()) && Objects.nonNull(e.getValue()))
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
                            return Objects.toString(value, "");
                        }
                ));

        String methodName = getMethodName(request);

        // 从元数据或请求中获取 clientId
        String clientId = metaData.getOrDefault(Const.CLIENT_ID, request.clientId());

        CallToolRequest.Builder builder = CallToolRequest.newBuilder()
                .setName(METHOD_TOOLS_CALL)
                .setMethod(methodName)
                .putAllArguments(stringMap);

        // 发送请求并处理响应
        CallToolResponse response = getMetadataBlockingStub().callTool(builder.build());
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
                .filter(e -> Objects.nonNull(e.getKey()) && Objects.nonNull(e.getValue()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> Objects.toString(e.getValue(), null)
                ));

        String methodName = getMethodName(request);

        //protobuf map 只能是 <string,string>
        CallToolRequest.Builder builder = CallToolRequest.newBuilder()
                .setName(METHOD_TOOLS_STREAM)
                .putAllArguments(stringMap)
                .setMethod(methodName);

        // 创建一个独立的 Context，不会被上游的取消信号影响
        io.grpc.Context independentContext = io.grpc.Context.ROOT.fork();
        
        // 在独立的 Context 中执行 gRPC 调用
        independentContext.run(() -> {
            this.getMetadataAsyncStub().callToolStream(builder.build(), new StreamObserver<>() {
                private volatile boolean completed = false;
                
                @Override
                public void onNext(CallToolResponse callToolResponse) {
                    if (!completed && !sink.isCancelled()) {
                        sink.next(callToolResponse);
                    }
                }

                @Override
                public void onError(Throwable throwable) {
                    if (!completed) {
                        completed = true;
                        // 只在真正的错误时才传递错误，忽略取消信号
                        if (!(throwable instanceof StatusRuntimeException sre) || 
                            sre.getStatus().getCode() != io.grpc.Status.Code.CANCELLED) {
                            sink.error(throwable);
                        } else {
                            System.out.println("gRPC 调用被取消，但继续完成: " + throwable.getMessage());
                            // 可以选择继续等待结果或直接完成
                            sink.complete();
                        }
                    }
                }

                @Override
                public void onCompleted() {
                    if (!completed) {
                        completed = true;
                        sink.complete();
                    }
                }
            });
        });
    }

    //grpc 的返回结果,需要手动转换下
    @Override
    public <T> T unmarshalFrom(Object data, TypeReference<T> typeRef) {
        try {
            if (data instanceof CallToolResponse ctr) {
                return (T) new McpSchema.CallToolResult(ctr.getContentList().stream().map(it -> {
                    if (it.hasText()) {
                        return new McpSchema.TextContent(it.getText().getText(), it.getText().getData());
                    }
                    if (it.hasImage()) {
                        return new McpSchema.ImageContent(it.getImage().getData(), it.getImage().getMimeType());
                    }
                    return null;
                }).collect(Collectors.toUnmodifiableList()), false);
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