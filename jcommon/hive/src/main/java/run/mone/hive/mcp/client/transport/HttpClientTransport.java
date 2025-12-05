package run.mone.hive.mcp.client.transport;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import run.mone.hive.mcp.spec.ClientMcpTransport;
import run.mone.hive.mcp.spec.McpSchema;

import java.time.Duration;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * HTTP客户端传输适配器
 * 用于将 io.modelcontextprotocol.client.transport.HttpClientStreamableHttpTransport
 * 适配到 run.mone.hive.mcp.spec.ClientMcpTransport 接口
 * 
 * 注意：这个类主要用于兼容性，实际使用时建议直接使用V2的transport
 */
@Deprecated
@Slf4j
public class HttpClientTransport implements ClientMcpTransport {

    private final io.modelcontextprotocol.client.transport.HttpClientStreamableHttpTransport delegate;

    public HttpClientTransport(String url) {
        this.delegate = io.modelcontextprotocol.client.transport.HttpClientStreamableHttpTransport
                .builder(url)
                .connectTimeout(Duration.ofSeconds(30))
                .build();
    }

    public HttpClientTransport(io.modelcontextprotocol.client.transport.HttpClientStreamableHttpTransport delegate) {
        this.delegate = delegate;
    }

    @Override
    public Mono<Void> connect(Function<Mono<McpSchema.JSONRPCMessage>, Mono<McpSchema.JSONRPCMessage>> handler) {
        // 将 run.mone.hive.mcp.spec.McpSchema 转换为 io.modelcontextprotocol.spec.McpSchema
        Function<Mono<io.modelcontextprotocol.spec.McpSchema.JSONRPCMessage>, 
                 Mono<io.modelcontextprotocol.spec.McpSchema.JSONRPCMessage>> adaptedHandler = msgMono -> {
            return msgMono.flatMap(msg -> {
                // 这里需要做类型转换，由于两个类库的 JSONRPCMessage 不兼容
                // 暂时使用简单的日志记录
                log.debug("Received message from delegate: {}", msg);
                return Mono.empty();
            });
        };
        
        return this.delegate.connect(adaptedHandler);
    }

    @Override
    public Mono<Void> closeGracefully() {
        return this.delegate.closeGracefully();
    }

    @Override
    public void close() {
        try {
            this.delegate.closeGracefully().block(Duration.ofSeconds(5));
        } catch (Exception e) {
            log.warn("Failed to close HttpClientTransport gracefully", e);
        }
    }

    public void setExceptionHandler(Consumer<Throwable> handler) {
        this.delegate.setExceptionHandler(handler);
    }

    /**
     * 发送消息到服务端
     * 注意：这个方法需要处理两个不同类库之间的类型转换
     */
    @Override
    public Mono<Object> sendMessage(McpSchema.JSONRPCMessage message) {
        // 由于两个类库的 JSONRPCMessage 类型不兼容，这里需要做转换
        // 具体实现取决于实际的消息格式
        log.warn("sendMessage is not fully implemented for HttpClientTransport adapter");
        // 暂时返回空实现
        return Mono.empty();
    }

    /**
     * 反序列化方法
     */
    @Override
    public <T> T unmarshalFrom(Object data, TypeReference<T> typeRef) {
        // 委托给 delegate 的反序列化方法
        // 注意：io.modelcontextprotocol 使用的是不同的 TypeReference
        log.warn("unmarshalFrom is not fully implemented for HttpClientTransport adapter");
        return null;
    }

    /**
     * 获取底层的 delegate，用于直接访问
     */
    public io.modelcontextprotocol.client.transport.HttpClientStreamableHttpTransport getDelegate() {
        return delegate;
    }
}

