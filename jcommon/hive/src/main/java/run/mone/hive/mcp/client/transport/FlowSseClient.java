/*
 * ORIGINAL CODE IS FROM SPRING AI!!!
 *
 */
package run.mone.hive.mcp.client.transport;

import lombok.extern.slf4j.Slf4j;
import run.mone.hive.configs.Const;
import run.mone.hive.mcp.client.transport.exception.CloseException;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Flow;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * A Server-Sent Events (SSE) client implementation using Java's Flow API for reactive
 * stream processing. This client establishes a connection to an SSE endpoint and
 * processes the incoming event stream, parsing SSE-formatted messages into structured
 * events.
 *
 * <p>
 * The client supports standard SSE event fields including:
 * <ul>
 * <li>event - The event type (defaults to "message" if not specified)</li>
 * <li>id - The event ID</li>
 * <li>data - The event payload data</li>
 * </ul>
 *
 * <p>
 * Events are delivered to a provided {@link SseEventHandler} which can process events and
 * handle any errors that occur during the connection.
 *
 * @see SseEventHandler
 * @see SseEvent
 */
@Slf4j
public class FlowSseClient {

    private final HttpClient httpClient;

    private volatile boolean close;

    /**
     * Pattern to extract the data content from SSE data field lines. Matches lines
     * starting with "data:" and captures the remaining content.
     */
    private static final Pattern EVENT_DATA_PATTERN = Pattern.compile("^data:(.+)$", Pattern.MULTILINE);

    /**
     * Pattern to extract the event ID from SSE id field lines. Matches lines starting
     * with "id:" and captures the ID value.
     */
    private static final Pattern EVENT_ID_PATTERN = Pattern.compile("^id:(.+)$", Pattern.MULTILINE);

    /**
     * Pattern to extract the event type from SSE event field lines. Matches lines
     * starting with "event:" and captures the event type.
     */
    private static final Pattern EVENT_TYPE_PATTERN = Pattern.compile("^event:(.+)$", Pattern.MULTILINE);

    /**
     * Record class representing a Server-Sent Event with its standard fields.
     *
     * @param id   the event ID (may be null)
     * @param type the event type (defaults to "message" if not specified in the stream)
     * @param data the event payload data
     */
    public static record SseEvent(String id, String type, String data) {
    }

    /**
     * Interface for handling SSE events and errors. Implementations can process received
     * events and handle any errors that occur during the SSE connection.
     */
    public interface SseEventHandler {

        /**
         * Called when an SSE event is received.
         *
         * @param event the received SSE event containing id, type, and data
         */
        void onEvent(SseEvent event);

        /**
         * Called when an error occurs during the SSE connection.
         *
         * @param error the error that occurred
         */
        void onError(Throwable error);

    }

    /**
     * Creates a new FlowSseClient with the specified HTTP client.
     *
     * @param httpClient the {@link HttpClient} instance to use for SSE connections
     */
    public FlowSseClient(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public void close() {
        close = true;
    }

    /**
     * Subscribes to an SSE endpoint and processes the event stream.
     *
     * <p>
     * This method establishes a connection to the specified URL and begins processing the
     * SSE stream. Events are parsed and delivered to the provided event handler. The
     * connection remains active until either an error occurs or the server closes the
     * connection.
     *
     * @param url          the SSE endpoint URL to connect to
     * @param eventHandler the handler that will receive SSE events and error
     *                     notifications
     * @throws RuntimeException if the connection fails with a non-200 status code
     */
    public void subscribe(String url, SseEventHandler eventHandler, String clientId) {
        new Thread(() -> {
            for (; ; ) {
                log.info("connect:{}", url);
                if (close) {
                    break;
                }
                try {
                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(URI.create(url))
                            .header("Accept", "text/event-stream")
                            .header("Cache-Control", "no-cache")
                            .header(Const.MC_CLIENT_ID, clientId)
                            .GET()
                            .build();

                    StringBuilder eventBuilder = new StringBuilder();
                    AtomicReference<String> currentEventId = new AtomicReference<>();
                    AtomicReference<String> currentEventType = new AtomicReference<>("message");

                    //这是连接到sse,用来接受数据并处理
                    Flow.Subscriber<String> lineSubscriber = new Flow.Subscriber<>() {
                        private Flow.Subscription subscription;

                        @Override
                        public void onSubscribe(Flow.Subscription subscription) {
                            this.subscription = subscription;
                            subscription.request(Long.MAX_VALUE);
                        }

                        @Override
                        public void onNext(String line) {
                            if (close) {
                                throw new CloseException();
                            }
                            if (line.isEmpty()) {
                                // Empty line means end of event
                                if (eventBuilder.length() > 0) {
                                    String eventData = eventBuilder.toString();
                                    SseEvent event = new SseEvent(currentEventId.get(), currentEventType.get(), eventData.trim());
                                    eventHandler.onEvent(event);
                                    eventBuilder.setLength(0);
                                }
                            } else {
                                if (line.startsWith("data:")) {
                                    var matcher = EVENT_DATA_PATTERN.matcher(line);
                                    if (matcher.find()) {
                                        eventBuilder.append(matcher.group(1).trim()).append("\n");
                                    }
                                } else if (line.startsWith("id:")) {
                                    var matcher = EVENT_ID_PATTERN.matcher(line);
                                    if (matcher.find()) {
                                        currentEventId.set(matcher.group(1).trim());
                                    }
                                } else if (line.startsWith("event:")) {
                                    var matcher = EVENT_TYPE_PATTERN.matcher(line);
                                    if (matcher.find()) {
                                        currentEventType.set(matcher.group(1).trim());
                                    }
                                }
                            }
                            subscription.request(1);
                        }

                        @Override
                        public void onError(Throwable throwable) {
                            eventHandler.onError(throwable);
                        }

                        @Override
                        public void onComplete() {
                            // Handle any remaining event data
                            if (eventBuilder.length() > 0) {
                                String eventData = eventBuilder.toString();
                                SseEvent event = new SseEvent(currentEventId.get(), currentEventType.get(), eventData.trim());
                                eventHandler.onEvent(event);
                            }
                        }
                    };

                    Function<Flow.Subscriber<String>, HttpResponse.BodySubscriber<Void>> subscriberFactory = subscriber -> HttpResponse.BodySubscribers
                            .fromLineSubscriber(subscriber);

                    this.httpClient.send(request,
                            info -> subscriberFactory.apply(lineSubscriber));

                } catch (CloseException closeException) {
                    log.info("close!!!");
                } catch (Throwable ex) {
                    log.error(ex.getMessage(), ex);
                }
                try {
                    TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();

    }


}
