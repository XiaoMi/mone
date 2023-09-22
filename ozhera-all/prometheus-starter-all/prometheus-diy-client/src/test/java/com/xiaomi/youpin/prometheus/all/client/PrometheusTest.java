package com.xiaomi.youpin.prometheus.all.client;
import org.junit.Before;
import org.junit.Test;

import java.util.stream.IntStream;

public class PrometheusTest {

    @Before
    public void setUp() throws Exception {

        //HTTPServer newServer = new HTTPServer(8070);
        /*try {
            Enumeration<Collector.MetricFamilySamples> samples = CollectorRegistry.defaultRegistry.filteredMetricFamilySamples(Sets.newHashSet());
            StringWriter writer = new StringWriter();
            String str = "";
            try {
                TextFormat.write004(writer, samples);
                StringBuffer sb = writer.getBuffer();
                str = sb.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println(str);
            HttpServer server = HttpServer.create(new InetSocketAddress(8070), 0);
            server.createContext("/metrics", httpExchange -> {
                String response = Prometheus.registry.scrape();
                httpExchange.sendResponseHeaders(200, response.getBytes().length);
                try (OutputStream os = httpExchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
            });
            new Thread(server::start).start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //m = new Metrics("systech", "zxw_test2");*/
        Metrics.getInstance().init("systch","zxw_test2");
    }

    @Test
    public void testCounter() {
        IntStream.range(0, 100).forEach(i -> {

            Metrics.getInstance().newCounter("testCounter", "name").
                    with("zxw").
                    add(1,"zxw");
            Metrics.getInstance().newCounter("testCounter2", "age", "city").with("99", "china").add(1,"18","beijing");
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    @Test
    public void testGauge()  {
        IntStream.range(0, 100).forEach(i -> {
            Metrics.getInstance().newGauge("testGauge", "name").with("zxw").set(128,"zxw");
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    @Test
    public void testHistogram() {
        IntStream.range(0,100).forEach(i-> {
            Metrics.getInstance().newHistogram("testHistogramWithDefaultBucket",null,"name").with("zxw").observe(12,"zxw");
            Metrics.getInstance().newHistogram("testHistogramWithDiyBucket", new double[] {.01, .05, 0.7, 5 ,10, 50, 100, 200},"name").with("zxw").observe(12,"zxw");
            try {
                Thread.sleep(3000);
            }catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    @Test
    public void testException()   {
        IntStream.range(0, 100).forEach(i -> {
            Metrics.getInstance().newCounter("testNotMatchLabelNameAndLabelValueExceptionCounter", "a", "b").with("1").add(1);
            Metrics.getInstance().newGauge("testNotMatchLabelNameAndLabelValueExceptionGauge", "a", "b").with("1").add(1);
            Metrics.getInstance().newHistogram("testNotMatchLabelNameAndLabelValueExceptionHistogram", null, "b", "c").with("1").observe(1);
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }
    @Test
    public void testJvm() throws InterruptedException {
        Thread.sleep(3000000);
    }
}

