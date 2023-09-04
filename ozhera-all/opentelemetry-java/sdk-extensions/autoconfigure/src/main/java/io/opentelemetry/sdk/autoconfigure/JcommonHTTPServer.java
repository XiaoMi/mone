package io.opentelemetry.sdk.autoconfigure;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.exporter.common.TextFormat;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URLDecoder;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings({"SystemOut","CatchAndPrintStackTrace","DefaultCharset"})
public class JcommonHTTPServer {

    /**
     * Handles Metrics collections from the given registry.
     */
    static class HTTPMetricHandler implements HttpHandler {

        private final Map<String, CollectorRegistry> registryMap;

        private final static byte[] HEALTHY_RESPONSE = "ok".getBytes();

        HTTPMetricHandler(Map<String, CollectorRegistry> registryMap) {
            this.registryMap = registryMap;
        }

        private byte[] data = new byte[]{};

        private long lastTime;

        private synchronized byte[] getData(String contentType, CollectorRegistry registry, String query) {
            long now = System.currentTimeMillis();
            if (now - lastTime > 5000L) {
                this.lastTime = now;
                try (ByteArrayOutputStream baos = new ByteArrayOutputStream(); OutputStreamWriter writer = new OutputStreamWriter(baos)) {
                    TextFormat.writeFormat(contentType, writer, registry.filteredMetricFamilySamples(parseQuery(query)));
                    writer.flush();
                    this.data = baos.toByteArray();
                    return this.data;
                } catch (Throwable ex) {
                  ex.printStackTrace();
                    return new byte[]{};
                }
            }
            return this.data;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            long a = System.currentTimeMillis();
            String path = exchange.getRequestURI().getPath();
            try (OutputStream os = exchange.getResponseBody()) {
                String query = exchange.getRequestURI().getRawQuery();
                if ("/-/healthy".equals(path)) {
                    exchange.sendResponseHeaders(200, HEALTHY_RESPONSE.length);
                    os.write(HEALTHY_RESPONSE);
                    os.flush();
                    return;
                } else {
                    String contentType = TextFormat.chooseContentType(exchange.getRequestHeaders().getFirst("Accept"));
                    exchange.getResponseHeaders().set("Content-Type", contentType);
                    CollectorRegistry registry = this.registryMap.get("default");
                    if ("/jvm".equals(path)) {
                        registry = this.registryMap.get("jvm");
                    }
                    byte[] data = getData(contentType, registry, query);
                    exchange.sendResponseHeaders(200, data.length);
                    os.write(data);
                    os.flush();
                }
            } catch (Throwable ex) {
              ex.printStackTrace();
                exchange.sendResponseHeaders(200, 0);
                exchange.getResponseBody().write(new byte[]{});
            } finally {
                long b = System.currentTimeMillis();
                System.out.println("prometheus request uri：" + path + " duration：" + (b - a));
            }
        }

    }

    protected static Set<String> parseQuery(String query) throws IOException {
        Set<String> names = new HashSet<String>();
        if (query != null) {
            String[] pairs = query.split("&");
            for (String pair : pairs) {
                int idx = pair.indexOf("=");
                if (idx != -1 && URLDecoder.decode(pair.substring(0, idx), "UTF-8").equals("name[]")) {
                    names.add(URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
                }
            }
        }
        return names;
    }


    static class NamedDaemonThreadFactory implements ThreadFactory {
        private static final AtomicInteger POOL_NUMBER = new AtomicInteger(1);

        private final int poolNumber = POOL_NUMBER.getAndIncrement();
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final ThreadFactory delegate;
        private final boolean daemon;

        NamedDaemonThreadFactory(ThreadFactory delegate, boolean daemon) {
            this.delegate = delegate;
            this.daemon = daemon;
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = delegate.newThread(r);
            t.setName(String.format("prometheus-http-%d-%d", poolNumber, threadNumber.getAndIncrement()));
            t.setDaemon(daemon);
            return t;
        }

        static ThreadFactory defaultThreadFactory(boolean daemon) {
            return new NamedDaemonThreadFactory(Executors.defaultThreadFactory(), daemon);
        }
    }

    protected final HttpServer server;
    protected final ExecutorService executorService;

    /**
     * Start a HTTP server serving Prometheus metrics from the given registry using the given {@link HttpServer}.
     * The {@code httpServer} is expected to already be bound to an address
     */
    public JcommonHTTPServer(HttpServer httpServer, Map<String, CollectorRegistry> registryMap, boolean daemon) {
        server = httpServer;
        HttpHandler mHandler = new HTTPMetricHandler(registryMap);
        server.createContext("/", mHandler);
        server.createContext("/metrics", mHandler);
        server.createContext("/-/healthy", mHandler);
        executorService = new ThreadPoolExecutor(10, 10,
                0L, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(1000));
        server.setExecutor(executorService);
        start(daemon);
    }

    /**
     * Start a HTTP server by making sure that its background thread inherit proper daemon flag.
     */
    private void start(boolean daemon) {
        if (daemon == Thread.currentThread().isDaemon()) {
            server.start();
        } else {
            FutureTask<Void> startTask = new FutureTask<Void>(new Runnable() {
                @Override
                public void run() {
                    server.start();
                }
            }, null);
            NamedDaemonThreadFactory.defaultThreadFactory(daemon).newThread(startTask).start();
            try {
                startTask.get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                // This is possible only if the current tread has been interrupted,
                // but in real use cases this should not happen.
                // In any case, there is nothing to do, except to propagate interrupted flag.
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Stop the HTTP server.
     */
    public void stop() {
        server.stop(0);
        executorService.shutdown(); // Free any (parked/idle) threads in pool
    }

    /**
     * Gets the port number.
     */
    public int getPort() {
        return server.getAddress().getPort();
    }
}

