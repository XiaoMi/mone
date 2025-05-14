# Server-Sent Events (SSE) Support in Docean MVC

This document explains how to use Server-Sent Events (SSE) with the Docean MVC framework.

## What are Server-Sent Events?

Server-Sent Events (SSE) is a server push technology enabling a client to receive automatic updates from a server via an HTTP connection. SSE is designed for scenarios where the server needs to send data to the client continuously without the client requesting it, such as:

- Real-time updates
- Notifications
- Log streaming
- Live data feeds

Unlike WebSockets, SSE is unidirectional (server to client only), making it simpler for many use cases.

## SSE vs. WebSockets

| Feature | SSE | WebSockets |
|---------|-----|------------|
| Communication | Unidirectional (server → client) | Bidirectional |
| Protocol | HTTP | WebSocket protocol |
| Reconnection | Automatic | Manual implementation required |
| Message types | Text only | Text and binary |
| Max connections | Limited by browser | Higher limit |
| Use case | Data streaming, updates | Real-time interactive applications |

## Using SSE in Docean MVC

Docean MVC now supports SSE through Project Reactor's `Flux` type. There are three ways to trigger SSE mode:

1. **Client sets Accept header**: If the client sends a request with `Accept: text/event-stream`
2. **Endpoint path convention**: If the endpoint path ends with `/sse`
3. **Custom header**: If the client includes `X-SSE-Request: true` header

### Implementation Example

Here's a sample controller demonstrating SSE usage:

```java
@Slf4j
@Controller
@RequestMapping(path = "/api/sse")
public class SSEExampleController {

    private static final DateTimeFormatter formatter = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * SSE endpoint that streams time updates every second for 60 seconds
     * Client needs to set Accept: text/event-stream header
     */
    @RequestMapping(path = "/stream")
    public Flux<Map<String, Object>> getSSEStream() {
        AtomicInteger counter = new AtomicInteger(0);
        
        return Flux.interval(Duration.ofSeconds(1))
                .map(tick -> {
                    Map<String, Object> event = new HashMap<>();
                    event.put("timestamp", LocalDateTime.now().format(formatter));
                    event.put("counter", counter.incrementAndGet());
                    event.put("message", "Server time update");
                    return event;
                })
                .take(60) // Limit to 60 events (1 minute)
                .doOnSubscribe(s -> log.info("Client subscribed to SSE stream"))
                .doOnComplete(() -> log.info("SSE stream completed"))
                .doOnCancel(() -> log.info("Client canceled SSE stream"));
    }

    /**
     * SSE endpoint that automatically uses SSE mode due to path ending with /sse
     */
    @RequestMapping(path = "/auto-sse")
    public Flux<String> getAutoSSEStream() {
        return Flux.interval(Duration.ofSeconds(1))
                .map(tick -> "Current time: " + LocalDateTime.now().format(formatter))
                .take(60); // 1 minute of updates
    }
}
```

### Client-Side JavaScript

Here's how to consume the SSE stream from a browser:

```javascript
// Create EventSource pointing to your SSE endpoint
const eventSource = new EventSource('/api/sse/stream');

// Listen for messages
eventSource.onmessage = function(event) {
    const data = JSON.parse(event.data);
    console.log('Received update:', data);
    // Update UI with the data
};

// Listen for errors
eventSource.onerror = function(error) {
    console.error('EventSource error:', error);
    eventSource.close();
};

// Close the connection when done
function closeConnection() {
    if (eventSource) {
        eventSource.close();
        console.log('Connection closed');
    }
}
```

## Advanced Usage

### Custom Event Types

You can send custom event types by using the framework's internal methods:

```java
@RequestMapping(path = "/custom-events")
public void customEvents(MvcContext context, MvcResponse response) {
    // Set up SSE connection
    response.initSSE(context);
    
    // Send different event types
    response.sendSSEEvent(context, "Hello", "greeting", "1");
    response.sendSSEEvent(context, "Warning message", "alert", "2");
    
    // Close when done
    response.closeSSE(context);
    
    return Mono.empty();
}
```

Client-side, you can listen for specific event types:

```javascript
const eventSource = new EventSource('/api/sse/custom-events');

// Listen for 'greeting' events
eventSource.addEventListener('greeting', function(event) {
    console.log('Greeting received:', event.data);
});

// Listen for 'alert' events
eventSource.addEventListener('alert', function(event) {
    console.log('Alert received:', event.data);
});
```

### Stream Transformation

You can transform, filter, or limit your Flux stream:

```java
@RequestMapping(path = "/filtered-stream")
public Flux<String> filteredStream() {
    return Flux.interval(Duration.ofSeconds(1))
            .map(i -> "Item " + i)
            .filter(item -> item.hashCode() % 2 == 0) // Only send even hash codes
            .take(10);                                // Stop after 10 items
}
```

## Performance Considerations

- **Connection Limits**: Browsers limit the number of concurrent SSE connections (typically 6 per domain)
- **Resource Management**: Long-lived connections consume server resources
- **Reconnection**: SSE has automatic reconnection, but you might want to implement backoff logic
- **Large Data**: For large volumes of data, consider compression or batching

## Compatibility

SSE is supported in all major browsers and HTTP clients. The implementation works with both HTTP/1.1 and HTTP/2.

## Troubleshooting

1. **Connection not established**: Ensure proper headers are set (Content-Type: text/event-stream)
2. **Messages not arriving**: Check network connectivity and server logs
3. **Performance issues**: Consider reducing update frequency or limiting event size 