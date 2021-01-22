package com.xiaomi.youpin.tesla.agent.test.mesh;

import com.google.gson.Gson;
import com.xiaomi.data.push.client.HttpClient;
import com.xiaomi.youpin.tesla.rcurve.proxy.ProxyRequest;
import org.junit.Assert;
import org.junit.Test;

import java.util.stream.IntStream;

/**
 * @author goodjava@qq.com
 * @date 1/9/21
 */
public class HttpTest {

        private String url = "http://127.0.0.1:7778/proxy";

    //staging地址
//    private String url = "http://10.38.160.248:7778/proxy";

    /**
     * 测试和proxy的连通性
     */
    @Test
    public void testProxy() {
        ProxyRequest request = new ProxyRequest();
        request.setMethodName("$version$");
        String res = HttpClient.post(url, new Gson().toJson(request));
        System.out.println(res);
        Assert.assertNotNull(res);
    }

    /**
     * 测试是否能调用proxy下边的服务
     */
    @Test
    public void testDemo() {
        IntStream.range(0, 1).parallel().forEach(i -> {
            ProxyRequest request = new ProxyRequest();
            request.setApp("demo_one_app");
            request.setServiceName("com.xiaomi.mone.mds.service.DemoOneService");
            request.setMethodName("demo");
            request.setParamTypes(new String[]{"java.lang.Integer", "java.lang.Integer"});
            request.setParams(new String[]{"11", "22"});
            String res = HttpClient.post(url, new Gson().toJson(request));
            System.out.println(res);
            Assert.assertNotNull(res);
        });

    }

    /**
     * 测试中间件redis的调用
     */
    @Test
    public void testRedis() {
        ProxyRequest request = new ProxyRequest();
        request.setApp("demo_one_app");
        request.setServiceName("com.xiaomi.mone.mds.service.DemoOneService");
        request.setMethodName("redis");
        request.setTimeout(1000L);
        String res = HttpClient.post(url, new Gson().toJson(request));
        System.out.println(res);
        Assert.assertNotNull(res);
    }

    /**
     * 测试中间件redis的调用
     */
    @Test
    public void testSql() {
        ProxyRequest request = new ProxyRequest();
        request.setApp("demo_one_app");
        request.setServiceName("com.xiaomi.mone.mds.service.DemoOneService");
        request.setMethodName("sql");
        request.setTimeout(1000L);
        String res = HttpClient.post(url, new Gson().toJson(request));
        System.out.println(res);
        Assert.assertNotNull(res);
    }

    /**
     * 测试中间件rocketmq的调用
     */
    @Test
    public void testRocketmq() {
        ProxyRequest request = new ProxyRequest();
        request.setApp("demo_one_app");
        request.setServiceName("com.xiaomi.mone.mds.service.DemoOneService");
        request.setMethodName("rocketmq");
        request.setTimeout(1000L);
        String res = HttpClient.post("http://127.0.0.1:7778/proxy", new Gson().toJson(request));
        System.out.println(res);
        Assert.assertNotNull(res);
    }


    /**
     * 测试中间件rocketmq的调用
     */
    @Test
    public void testHttp() {
        ProxyRequest request = new ProxyRequest();
        request.setApp("demo_one_app");
        request.setServiceName("com.xiaomi.mone.mds.service.DemoOneService");
        request.setMethodName("http");
        request.setTimeout(1000L);
        String res = HttpClient.post(url, new Gson().toJson(request));
        System.out.println(res);
        Assert.assertNotNull(res);
    }

}
