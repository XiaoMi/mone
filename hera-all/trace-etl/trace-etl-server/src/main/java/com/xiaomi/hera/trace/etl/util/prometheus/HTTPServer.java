package com.xiaomi.hera.trace.etl.util.prometheus;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.google.common.collect.Sets;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.xiaomi.hera.trace.etl.consumer.DataCacheService;
import com.xiaomi.hera.trace.etl.consumer.EnterManager;
import com.xiaomi.youpin.prometheus.client.binder.ClassLoaderMetricsReduced;
import com.xiaomi.youpin.prometheus.client.binder.JvmGcMetricsReduced;
import com.xiaomi.youpin.prometheus.client.binder.JvmMemoryMetricsReduced;
import com.xiaomi.youpin.prometheus.client.binder.JvmThreadMetricsReduced;
import io.micrometer.core.instrument.binder.system.FileDescriptorMetrics;
import io.micrometer.core.instrument.binder.system.ProcessorMetrics;
import io.micrometer.core.instrument.binder.system.UptimeMetrics;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import io.prometheus.client.Collector;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.exporter.common.TextFormat;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Component
public class HTTPServer {

    private static final Logger log = LoggerFactory.getLogger(HTTPServer.class);

    @Value("${prometheus.http.server.port}")
    private int port;
    @NacosValue(value = "${prometheus.token}")
    private String token;
    @Value("${security.scanner.ua}")
    private String ua;
    @Value("${app.name}")
    private String appName;
    @Value("${metrics.uri.whitelist}")
    private String uriWhitelist;

    @Resource
    private DataCacheService dataCacheService;
    @Resource
    private EnterManager enterManager;

    private HttpServer server;
    private ExecutorService executorService;
    public static final String APPLICATION = "application";

    @PostConstruct
    public void init() {
        try {
            server = HttpServer.create(new InetSocketAddress(port), 3);
            if (server == null || server.getAddress() == null) {
                throw new IllegalArgumentException("HttpServer hasn't been bound to an address");
            }
            HashMap<String, CollectorRegistry> handleMap = new HashMap<>();
            handleMap.put("default", CollectorRegistry.defaultRegistry);
            PrometheusMeterRegistry jvmRegistry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
            jvmRegistry.config().commonTags(APPLICATION, appName);
            bindJvm(jvmRegistry);
            handleMap.put("jvm", jvmRegistry.getPrometheusRegistry());
            HttpHandler mHandler = new HTTPServer.HTTPMetricHandler(handleMap);
            server.createContext("/", mHandler);
            server.createContext("/metrics", mHandler);
            server.createContext("/-/healthy", mHandler);
            executorService = Executors.newFixedThreadPool(5, HTTPServer.NamedDaemonThreadFactory.defaultThreadFactory(false));
            server.setExecutor(executorService);
            start(false);
        } catch (Exception e) {
            log.error("http server start fail：", e);
        }
    }

    /**
     * Handles Metrics collections from the given registry.
     */
    class HTTPMetricHandler implements HttpHandler {

        private final Map<String, CollectorRegistry> registryMap;

        private final byte[] HEALTHY_RESPONSE = "ok".getBytes();

        HTTPMetricHandler(Map<String, CollectorRegistry> registryMap) {
            this.registryMap = registryMap;
        }

        // Cache different metrics data for different ip+ URIs (/jvm, /metrics)
        private Map<String, byte[]> data = new ConcurrentHashMap<>();

        private Map<String, Long> uriLastTime = new ConcurrentHashMap<>();

        private Map<String, Object> getData(String contentType, CollectorRegistry registry, String uri, String remoteAddr) {
            Map<String, Object> result = new HashMap<>();
            long now = System.currentTimeMillis();
            String url = remoteAddr + uri;
            Long lastTime = uriLastTime.get(url);
            long lastTime1 = lastTime == null ? 0L : lastTime;
            boolean isCache = true;
            if (now - lastTime1 > 5000L) {
                isCache = false;
                this.uriLastTime.put(url, now);
                List<String> list = new ArrayList<>();
                try {
                    Field field = registry.getClass().getDeclaredField("namesToCollectors");
                    field.setAccessible(true);
                    Map<String, Collector> namesToCollectors = (Map<String, Collector>) field.get(registry);
                    list = namesToCollectors.keySet().stream()
                            .filter(it -> !it.endsWith("created"))
                            .collect(Collectors.toList());
                } catch (Exception e) {
                    log.info("export metrics error : ", e);
                }
                try (ByteArrayOutputStream baos = new ByteArrayOutputStream(); OutputStreamWriter writer = new OutputStreamWriter(baos)) {
                    TextFormat.writeFormat(contentType, writer, registry.filteredMetricFamilySamples(Sets.newHashSet(list)));
                    writer.flush();
                    byte[] bytes = baos.toByteArray();
                    this.data.put(url, bytes);
                    result.put("data", bytes);
                    result.put("isCache", isCache);
                    return result;
                } catch (Throwable ex) {
                    log.error("httpserver getdata error:", ex);
                    result.put("data", new byte[]{});
                    result.put("isCache", isCache);
                    return result;
                }
            }
            result.put("data", this.data.get(url));
            result.put("isCache", isCache);
            return result;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            long a = System.currentTimeMillis();
            if (!filterRequest(exchange)) {
                return;
            }
            String hostString = exchange.getRemoteAddress().getHostString();
            String path = exchange.getRequestURI().getPath();
            byte[] data = null;
            try (OutputStream os = exchange.getResponseBody()) {
                if ("/-/healthy".equals(path)) {
                    exchange.sendResponseHeaders(200, HEALTHY_RESPONSE.length);
                    os.write(HEALTHY_RESPONSE);
                    os.flush();
                } else {
                    String acceptHeader = exchange.getRequestHeaders().getFirst("Accept");
                    log.info("prometheus pull header is : " + acceptHeader);
                    String contentType = TextFormat.chooseContentType(acceptHeader);
                    exchange.getResponseHeaders().set("Content-Type", contentType);
                    if ("/jvm".equals(path)) {
                        CollectorRegistry registry = this.registryMap.get("jvm");
                        Map<String, Object> dataMap = getData(contentType, registry, path, hostString);
                        data = (byte[]) dataMap.get("data");
                    } else {
                        if (dataCacheService.isStartCache()) {
                            data = dataCacheService.getData();
                        } else {
                            data = firstPull();
                        }
                    }
                    exchange.sendResponseHeaders(200, data.length);
                    os.write(data);
                    os.flush();
                }
            } catch (Throwable ex) {
                ex.printStackTrace();
                exchange.sendResponseHeaders(200, 0);
                exchange.getResponseBody().write(new byte[]{});
            } finally {
                String query = exchange.getRequestURI().getRawQuery();
                long b = System.currentTimeMillis();
                int len = null != data ? data.length : 0;
                log.info("prometheus request uri : " + path + " queryString : " + query + " remoteAddr：" + hostString + " duration : " + (b - a) + " data size:" + len);

            }
        }

        /**
         * 为了解决服务启动时，consumer消息已经进来很久，但是prometheus还没有开始拉取，
         * 导致DataCacheService中的cacheData长度超过4个，指标被清除的风险。
         * prometheus第一次拉取时，直接从CollectRegister中拉取；
         * ConsumerService中的cacheData操作，直到prometheus第一拉取的15s后才会执行
         *
         * @return
         */
        private byte[] firstPull() {
            try {
                enterManager.getMonitor().enter();
                while (enterManager.getProcessNum().get() > 0) {
                    TimeUnit.MILLISECONDS.sleep(200);
                }
                return dataCacheService.cacheDataSync();
            } catch (Throwable ex) {
                log.error("first pull error", ex);
            } finally {
                dataCacheService.setStartCache(true);
                enterManager.getMonitor().leave();
            }
            return null;
        }

        private boolean filterRequest(HttpExchange exchange) {
            if (StringUtils.isEmpty(ua)) {
                return true;
            }
            // Filter by User-Agent
            Headers requestHeaders = exchange.getRequestHeaders();
            if (requestHeaders != null && requestHeaders.size() > 0) {
                List<String> headers = requestHeaders.get("User-agent");
                if (headers != null && headers.size() > 0) {
                    for (String header : headers) {
                        for (String uaBlack : ua.split(";")) {
                            if (header.contains(uaBlack)) {
                                return false;
                            }
                        }
                    }
                }
            }
            String path = exchange.getRequestURI().getPath();
            if (StringUtils.isEmpty(path) || !uriWhitelist.contains(path)) {
                return false;
            }
            return true;
        }

    }

    protected static String getToken(String query) throws IOException {
        if (query != null) {
            String[] pairs = query.split("&");
            for (String pair : pairs) {
                int idx = pair.indexOf("=");
                if (idx != -1 && "token".equals(URLDecoder.decode(pair.substring(0, idx), "UTF-8"))) {
                    return URLDecoder.decode(pair.substring(idx + 1), "UTF-8");
                }
            }
        }
        return null;
    }

    private void bindJvm(PrometheusMeterRegistry jvmRegistry) {
        new ClassLoaderMetricsReduced().bindTo(jvmRegistry);
        new JvmMemoryMetricsReduced().bindTo(jvmRegistry);
        new JvmGcMetricsReduced().bindTo(jvmRegistry);
        new ProcessorMetrics().bindTo(jvmRegistry);
        new JvmThreadMetricsReduced().bindTo(jvmRegistry);
        new UptimeMetrics().bindTo(jvmRegistry);
        new FileDescriptorMetrics().bindTo(jvmRegistry);
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

}

