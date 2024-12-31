/*
 *  Copyright 2020 Xiaomi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package run.mone.mimeter.engine.service.test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.xiaomi.data.push.schedule.task.graph.GraphTaskContext;
import com.xiaomi.data.push.schedule.task.graph.TaskEdgeData;
import com.xiaomi.data.push.schedule.task.graph.TaskVertexData;
import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.common.StringUtils;
import common.HttpClientV6;
import common.HttpResult;
import common.Util;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import run.mone.mimeter.engine.agent.bo.data.*;
import run.mone.mimeter.engine.agent.bo.task.Task;
import run.mone.mimeter.engine.agent.bo.task.TaskContext;
import run.mone.mimeter.engine.agent.bo.task.TaskType;
import run.mone.mimeter.engine.client.base.DagClient;

import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

/**
 * @author goodjava@qq.com
 * @date 2022/5/30
 */
public class DagClientTest {

    private static Logger log = LoggerFactory.getLogger(DagClientTest.class);

    private static final Pattern EL_PATTERN = Pattern.compile("\\$\\{(.*)}");

    @Test
    public void test5(){
        Map<String,List<String>> map = new HashMap<>();
        List<String> tobeR = new ArrayList<>();
        tobeR.add("dzx");
        tobeR.add("dp");
        tobeR.add("yxr");

        map.put("username",tobeR);

        List<String> age = new ArrayList<>();
        age.add("18");
        age.add("19");
        age.add("20");
        map.put("age",age);

        String body = "[\n" +
                "  {\n" +
                "    \"id\": \"${username}\",\n" +
                "    \"obj\": {\n" +
                "      \"name\": \"${username}\",\n" +
                "      \"age\": \"${age}\"\n" +
                "    }\n" +
                "  }\n" +
                "]";

        Matcher m = EL_PATTERN.matcher(body);
        Random random = new Random();
        int lineFlag = random.nextInt(100);
        while (m.find()) {
            String expr = m.group(1);
            body = Util.Parser.parse$(expr, body, null);
        }
        System.out.println(body);
    }

    @Test
    public void testDagClient() {
        Ioc ioc = Ioc.ins();
        ioc.putBean(ioc).init("run.mone.mibench.engine");
        DagClient client = ioc.getBean("dagClient");
        Task task = new Task();
        task.setDagInfo(getContext());
        log.info("{}", task.getDagInfo());
        client.call(task, new TaskContext(),null,null);
    }

    /**
     * 测试纯http的dag链路任务(图的一个形式,一个链表)
     * 先获取token 然后根据token获取id  最后通过id获取age
     */
    @Test
    public void testDagClientHttp() throws InterruptedException {
        Ioc ioc = Ioc.ins();
        ioc.putBean(ioc).init("run.mone.mibench.engine");
        DagClient client = ioc.getBean("dagClient");
        Task task = new Task();
        task.setTimeout(5000);
        task.setDagInfo(getHttpDagContext());

        ExprKey key1 = new ExprKey(2,2, "", "params.toMap(){data}", Lists.newArrayList("httpData", "0", "a"));
                //获取1的结果中的data
        ExprKey key2 = new ExprKey(1,2, "", "params.toMap(){data}", Lists.newArrayList("httpData", "1", "b"));

        Map<ExprKey,String> map = new HashMap<>();
        map.put(key1,"");
        map.put(key2,"");

        String j = new Gson().toJson(map);

        log.info("{}", task.getDagInfo());
        client.call(task, new TaskContext(),null,null);
        Thread.currentThread().join();
    }

    @Test
    public void testClientHttp() {
        List<String> headers = new ArrayList<>();
        Map<String,String> map = new HashMap<>();
        Map<String,String> headerMap = new HashMap<>();
        headerMap.put("Content-Type","x-www-form-urlencoded");
        headerMap.put("Xm-Forged-Req","true");

        map.put("invoice_type","4");
        map.put("invoice_title","person");
        map.put("use_red_packet", "1");
        map.put("shipment_id", "2");
        HttpResult res = null;
        try {
            Gson gson = new Gson();
            //http://api2.order.mi.com/buy/submit
            res = HttpClientV6.httpPost(null,"http://www.test.com/submit",headerMap,map,"UTF-8",5000);
            String json = gson.toJson(res.content);
            System.out.println(json);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testDivide(){
        int totalErrCount = 10;
        int totalCount = 141;
        double rate = (1.0 * totalErrCount / totalCount)*100;

        System.out.println(Math.round(rate));
    }
    @Test
    public void testC() {
        double v = Double.parseDouble("99");
        System.out.println((int) v);
    }


    static class T {
        public String id;

        public String parent;

        public List<T> childList = new ArrayList<>();

        public CountDownLatch latch = null;

        public T(String id, String parent) {
            this.id = id;
            this.parent = parent;
            if (!StringUtils.isEmpty(parent)) {
                latch = new CountDownLatch(1);
            } else {
                latch = new CountDownLatch(0);
            }
        }

        public void execute() {
            System.out.println("execute:" + this.id);
            childList.stream().forEach(it -> {
                it.latch.countDown();
            });
        }


    }


    @Test
    public void testList() throws InterruptedException {
        ExecutorService pool = Executors.newVirtualThreadPerTaskExecutor();
        T t2 = new T("2", "1");
        T t1 = new T("1", "");
        t1.childList.add(t2);
        List<T> list = Lists.newArrayList(t2, t1);
        list.stream().parallel().forEach(it -> {
            System.out.println("run:" + it.id);
            CompletableFuture.supplyAsync(() -> {
                try {
                    it.latch.await();
                    it.execute();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                return it;
            }, pool);
        });
        Thread.currentThread().join();
    }

    @Test
    public void testLatch() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        new Thread(()->{
            try {
                TimeUnit.SECONDS.sleep(5);
                latch.countDown();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
        latch.await();
        System.out.println(latch.getCount());
    }

    @Test
    public void testCountdownLatch() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(0);
        countDownLatch.await();

        CountDownLatch countDownLatch1 = new CountDownLatch(10);

        IntStream.range(0, 12).forEach(it -> {
            System.out.println(it);
            countDownLatch1.countDown();
        });
        countDownLatch1.await();

        System.out.println("finish");
    }

    public GraphTaskContext<NodeInfo> getContext() {
        GraphTaskContext<NodeInfo> taskContext = new GraphTaskContext<>();
        taskContext.setTaskList(Lists.newArrayList(createVertex(0, 0, new ExprKey(0, 2,"1_p", "params", Lists.newArrayList()), "0"),
                createVertex(1, 3, new ExprKey(0, 2,"1_p", "params", Lists.newArrayList()), "1"),
                //直接把0的结果放到2的参数中(map->i)
                createVertex(2, 0, new ExprKey(0,2, "2_p", "params", Lists.newArrayList("demoData", "data", "i")), "2"),
                createVertex(3, 0, new ExprKey(2, 2,"3_p", "params", Lists.newArrayList("demoData", "data", "i")), "3")));
        //1依赖0   2依赖于0  3依赖于2   (1的执行时间比较久3秒,所以会先输出3的结果)
        taskContext.setDependList(Lists.newArrayList(new TaskEdgeData(0, 1), new TaskEdgeData(0, 2), new TaskEdgeData(2, 3)));
        return taskContext;
    }


    /**
     * 执行三步 获取 token -> 获取 id -> 获取 agent -> 求和
     *
     * @return
     */
    public GraphTaskContext<NodeInfo> getHttpDagContext() {
        GraphTaskContext<NodeInfo> taskContext = new GraphTaskContext<>();
        taskContext.setTaskList(Lists.newArrayList(
                        //获取token
                        createHttpCalVertex(0, "post", Lists.newArrayList(new ParamType(ParamTypeEnum.pojo, "pojo")),
                                Lists.newArrayList(new ExprKey(0, 2,"", "", Lists.newArrayList())),
                                "token", "http://127.0.0.1:7777/token", ImmutableMap.of("uuid", "123")),
                        //换取id
                        createHttpCalVertex(1, "post", Lists.newArrayList(new ParamType(ParamTypeEnum.pojo, "pojo")),
                                Lists.newArrayList(new ExprKey(0, 2,"", "params.toMap(){data}", Lists.newArrayList("httpData", "0", "token"))),
                                "id", "http://127.0.0.1:7777/id", ImmutableMap.of("uuid", "456")),
                        //获取age
                        createHttpCalVertex(2, "post", Lists.newArrayList(new ParamType(ParamTypeEnum.pojo, "pojo")),
                                Lists.newArrayList(new ExprKey(1, 2,"", "params.toMap(){data}", Lists.newArrayList("httpData", "0", "id"))),
                                "age", "http://127.0.0.1:7777/age", ImmutableMap.of("uuid", "789")),
                        //求和(多参数)
                        createHttpCalVertex(3, "get", Lists.newArrayList(
                                        new ParamType(ParamTypeEnum.primary, "a"),
                                        new ParamType(ParamTypeEnum.primary, "b")
                                ),
                                Lists.newArrayList(
                                        //获取2的结果中的data
                                        new ExprKey(2,2, "", "params.toMap(){data}", Lists.newArrayList("httpData", "0", "a")),
                                        //获取1的结果中的data
                                        new ExprKey(1,2, "", "params.toMap(){data}", Lists.newArrayList("httpData", "1", "b"))
                                ),
                                "sum", "http://127.0.0.1:7777/sum", ImmutableMap.of("uuid", "799"))
                )
        );
        //任务是有依赖关系的
        taskContext.setDependList(Lists.newArrayList(new TaskEdgeData(0, 1), new TaskEdgeData(1, 2), new TaskEdgeData(2, 3), new TaskEdgeData(1, 3)));
        return taskContext;
    }


    private TaskVertexData<NodeInfo> createHttpCalVertex(int id, String method, List<ParamType> types, List<ExprKey> keys, String resultName, String url, Map<String, String> d) {
        int time = 100;
        TaskVertexData<NodeInfo> data = new TaskVertexData<>();
        NodeInfo nodeInfo = new NodeInfo();
        nodeInfo.setId(id);
        nodeInfo.setResultName(resultName);
        Task task = new Task();
        task.setDebug(true);
        task.setId(id);
        task.setType(TaskType.http);
        task.setTimeout(time);
        HttpData httpData = new HttpData();
        httpData.setUrl(url);
        httpData.setMethod(method);
        httpData.initTypeList(types);
        if (httpData.getParamNum() == 1) {
            Map<String, Object> m = (Map<String, Object>) httpData.getParams().get(0);
            m.putAll(d);
        }

        task.setHttpData(httpData);
        nodeInfo.setTask(task);
        keys.forEach(key -> nodeInfo.getExprMap().put(key, ""));
        data.setData(nodeInfo);
        data.setIndex(id);
        return data;
    }


    private TaskVertexData<NodeInfo> createVertex(int id, int sleepTime, ExprKey key, String resultName) {
        TaskVertexData<NodeInfo> data = new TaskVertexData<>();
        NodeInfo nodeInfo = new NodeInfo();
        nodeInfo.setId(id);
        nodeInfo.setResultName(resultName);
        Task task = new Task();
        task.setDebug(true);
        task.getAttachments().put("sleep", String.valueOf(sleepTime));
        task.setId(id);
        task.setType(TaskType.demo);
        task.setDemoData(new DemoData());
        nodeInfo.setTask(task);
        nodeInfo.getExprMap().put(key, "");
        data.setData(nodeInfo);
        data.setIndex(id);
        return data;
    }

    @Test
    public void testFuture() throws ExecutionException, InterruptedException {
        ExecutorService pool = Executors.newVirtualThreadPerTaskExecutor();
        CompletableFuture<String> f = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return "abc";
        }, pool).handle((str, e) -> {
            log.info("str:{}", str);
            return str;
        });

        System.out.println("get");

        System.out.println(f.get());
    }

}
