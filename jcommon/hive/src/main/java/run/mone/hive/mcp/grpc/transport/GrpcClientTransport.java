package run.mone.hive.mcp.grpc.transport;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoSink;
import run.mone.hive.mcp.grpc.CallToolRequest;
import run.mone.hive.mcp.grpc.CallToolResponse;
import run.mone.hive.mcp.grpc.McpServiceGrpc;
import run.mone.hive.mcp.spec.ClientMcpTransport;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.hive.mcp.spec.McpSchema.JSONRPCMessage;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import static run.mone.hive.mcp.spec.McpSchema.METHOD_TOOLS_CALL;
import static run.mone.hive.mcp.spec.McpSchema.METHOD_TOOLS_STREAM;

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

    @SuppressWarnings("unchecked")
    private void handleToolCall(run.mone.hive.mcp.spec.McpSchema.JSONRPCRequest request, MonoSink sink) throws JsonProcessingException {
        McpSchema.CallToolRequest re = (McpSchema.CallToolRequest) request.params();
        Map<String, Object> objectMap = re.arguments();

        Map<String, String> stringMap = objectMap.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> Objects.toString(e.getValue(), null)
                ));

        String methodName = "";
        if (request.params() instanceof McpSchema.CallToolRequest ctr) {
            methodName = ctr.name();
        }


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

    private void handleToolStreamCall(run.mone.hive.mcp.spec.McpSchema.JSONRPCRequest request, FluxSink sink) {
        McpSchema.CallToolRequest re = (McpSchema.CallToolRequest) request.params();
        Map<String, Object> objectMap = re.arguments();

        Map<String, String> stringMap = objectMap.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> Objects.toString(e.getValue(), null)
                ));

        //protobuf map 只能是 <string,string>
        CallToolRequest req = CallToolRequest.newBuilder()
                .setName(METHOD_TOOLS_STREAM)
                .putAllArguments(stringMap)
                .setMethod(request.method())
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

    @Override
    public <T> T unmarshalFrom(Object data, TypeReference<T> typeRef) {
        try {
            if (data instanceof CallToolResponse ctr) {
                return (T)new McpSchema.CallToolResult(ctr.getContentList().stream().map(it->{
                    return new McpSchema.TextContent(it.getText().getText());
                }).collect(Collectors.toUnmodifiableList()), false);
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