package com.xiaomi.hera.trace.etl.util.prometheus;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.google.common.collect.Sets;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.xiaomi.hera.trace.etl.constant.LockUtil;
import com.xiaomi.youpin.prometheus.client.Metrics;
import com.xiaomi.youpin.prometheus.client.MetricsManager;
import com.xiaomi.youpin.prometheus.client.Prometheus;
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
import io.prometheus.client.Counter;
import io.prometheus.client.Gauge;
import io.prometheus.client.Histogram;
import io.prometheus.client.exporter.common.TextFormat;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Component
public class HTTPServer {

    private static final Logger log = LoggerFactory.getLogger(HTTPServer.class);

    @Value("${prometheus.http.server.port}")
    private int port;
    @Value("${prometheus.token}")
    private String token;
    @Value("${security.scanner.ua}")
    private String ua;
    @Value("${app.name}")
    private String appName;
    @Value("${metrics.uri.whitelist}")
    private String uriWhitelist;

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
            log.error("http server 启动异常：", e);
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

        // 缓存不同ip+uri（/jvm、/metrics）对应的不同metrics数据
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
            if(!filterRequest(exchange)){
                return;
            }
            String hostString = exchange.getRemoteAddress().getHostString();
            String path = exchange.getRequestURI().getPath();
            boolean isCache = false;
            synchronized (LockUtil.lock) {
                try (OutputStream os = exchange.getResponseBody()) {
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
                        Map<String, Object> dataMap = getData(contentType, registry, path, hostString);
                        byte[] data = (byte[]) dataMap.get("data");
                        isCache = (boolean) dataMap.get("isCache");
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
                    log.info("prometheus request uri : " + path + " queryString : " + query + " remoteAddr：" + hostString + " duration : " + (b - a));
                    if (!isCache && "/metrics".equals(path) && token.equals(getToken(query))) {
                        clearMetrics();
                    }
                }
            }
        }

        private boolean filterRequest(HttpExchange exchange){
            if(StringUtils.isEmpty(ua)){
                return true;
            }
            // 按照UA过滤安全部扫描请求
            Headers requestHeaders = exchange.getRequestHeaders();
            if(requestHeaders != null && requestHeaders.size() > 0) {
                List<String> headers = requestHeaders.get("User-agent");
                if (headers != null && headers.size() > 0) {
                    for (String header : headers){
                        for(String uaBlack : ua.split(";")){
                            if(header.contains(uaBlack)){
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

        private void clearMetrics() {
//            synchronized (LockUtil.lock) {
            try {
                // 清理不带serviceName的指标
                MetricsManager gMetricsMgr = Metrics.getInstance().gMetricsMgr;
                if (gMetricsMgr instanceof Prometheus) {
                    Prometheus prometheus = (Prometheus) gMetricsMgr;
                    Map<String, Object> prometheusMetrics = prometheus.prometheusMetrics;
                    clearTypeMetrics(prometheusMetrics);
                    prometheus.prometheusMetrics.clear();
                    prometheus.prometheusTypeMetrics.clear();
                }
            } catch (Exception e) {
                log.error("clear metrics error", e);
            }
//            }
        }

        private void clearTypeMetrics(Map<String, Object> prometheusMetrics) {
            for (String key : prometheusMetrics.keySet()) {
                Object o = prometheusMetrics.get(key);
                if (o instanceof Counter) {
                    Counter counter = (Counter) o;
                    CollectorRegistry.defaultRegistry.unregister(counter);
                } else if (o instanceof Gauge) {
                    Gauge gauge = (Gauge) o;
                    gauge.clear();
                    CollectorRegistry.defaultRegistry.unregister(gauge);
                } else if (o instanceof Histogram) {
                    Histogram histogram = (Histogram) o;
                    histogram.clear();
                    CollectorRegistry.defaultRegistry.unregister(histogram);
                } else {
                    log.error("指标：" + key + " 类型转换失败，原始类型：" + o.getClass().getName());
                }
            }
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

