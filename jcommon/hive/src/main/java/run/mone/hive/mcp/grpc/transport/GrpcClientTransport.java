package run.mone.hive.mcp.grpc.transport;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import reactor.core.publisher.Mono;
import run.mone.hive.mcp.grpc.CallToolRequest;
import run.mone.hive.mcp.grpc.CallToolResponse;
import run.mone.hive.mcp.grpc.McpServiceGrpc;
import run.mone.hive.mcp.spec.ClientMcpTransport;
import run.mone.hive.mcp.spec.McpSchema.JSONRPCMessage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * gRPC 客户端传输层实现
 */
public class GrpcClientTransport implements ClientMcpTransport {

    private final String host;
    private final int port;
    private final ObjectMapper objectMapper;
    private ManagedChannel channel;
    private McpServiceGrpc.McpServiceBlockingStub blockingStub;
    private McpServiceGrpc.McpServiceStub asyncStub;
    
    private Function<Mono<JSONRPCMessage>, Mono<JSONRPCMessage>> messageHandler;

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

    @Override
    public Mono<Void> connect(Function<Mono<JSONRPCMessage>, Mono<JSONRPCMessage>> handler) {
        return Mono.fromRunnable(() -> {
            this.messageHandler = handler;
            this.channel = ManagedChannelBuilder.forAddress(host, port)
                    .usePlaintext()
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
    public Mono<Void> sendMessage(JSONRPCMessage message) {
        return Mono.fromRunnable(() -> {
            try {
                // 根据消息类型和内容，调用不同的 gRPC 方法
                // 这里只实现了工具调用的例子，实际需要处理所有消息类型
                if (message instanceof run.mone.hive.mcp.spec.McpSchema.JSONRPCRequest request) {
                    if ("tools/call".equals(request.method())) {
                        handleToolCall(request);
                    } else if ("tools/streamCall".equals(request.method())) {
                        handleToolStreamCall(request);
                    }
                    // 处理其他类型的请求...
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @SuppressWarnings("unchecked")
    private void handleToolCall(run.mone.hive.mcp.spec.McpSchema.JSONRPCRequest request) throws JsonProcessingException {
        Map<String, Object> params = objectMapper.convertValue(request.params(), Map.class);
        String name = (String) params.get("name");
        Map<String, Object> arguments = (Map<String, Object>) params.get("arguments");
        
        // 转换为 gRPC 请求格式
        Map<String, String> grpcArgs = new HashMap<>();
        for (Map.Entry<String, Object> entry : arguments.entrySet()) {
            byte[] argBytes = objectMapper.writeValueAsBytes(entry.getValue());
            grpcArgs.put(entry.getKey(), new String(argBytes));
        }
        
        CallToolRequest grpcRequest = CallToolRequest.newBuilder()
                .setName(name)
                .putAllArguments(grpcArgs)
                .build();
        
        // 发送请求并处理响应
        CallToolResponse response = blockingStub.callTool(grpcRequest);
        
        // 将 gRPC 响应转换回 JSON-RPC 响应
        // 实际代码中需要完整地转换
    }

    private void handleToolStreamCall(run.mone.hive.mcp.spec.McpSchema.JSONRPCRequest request) {
        // 处理流式工具调用
        // 类似于 handleToolCall，但需要处理流式响应

        CallToolRequest req = CallToolRequest.newBuilder().build();

        this.asyncStub.callToolStream(req, new StreamObserver<CallToolResponse>() {
            @Override
            public void onNext(CallToolResponse callToolResponse) {

            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onCompleted() {

            }
        });

    }

    @Override
    public <T> T unmarshalFrom(Object data, TypeReference<T> typeRef) {
        try {
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