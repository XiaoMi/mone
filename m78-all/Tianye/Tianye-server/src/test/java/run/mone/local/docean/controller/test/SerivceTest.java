package run.mone.local.docean.controller.test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.xiaomi.youpin.docean.Ioc;
import lombok.SneakyThrows;
import org.junit.Test;
import org.nutz.dao.Dao;
import org.nutz.dao.impl.NutDao;
import org.nutz.dao.impl.SimpleDataSource;
//import run.mone.ai.z.dto.ZKnowledgeReq;
import run.mone.local.docean.dto.ExecCommandResult;
import run.mone.local.docean.enums.ImEnum;
import run.mone.local.docean.fsm.BotReq;
import run.mone.local.docean.fsm.bo.EndFlowRes;
import run.mone.local.docean.fsm.bo.FlowData;
import run.mone.local.docean.fsm.bo.InputData;
import run.mone.local.docean.fsm.bo.NodeEdge;
import run.mone.local.docean.po.Message;
import run.mone.local.docean.service.*;
import run.mone.local.docean.service.tool.ChromeService;
import run.mone.local.docean.service.tool.IdeService;
import run.mone.local.docean.service.tool.OSXService;
import run.mone.m78.api.bo.feature.router.FeatureRouterDTO;
import run.mone.m78.api.bo.gitlab.GitLabReq;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * @author goodjava@qq.com
 * @date 2024/2/24 16:52
 */
public class SerivceTest {

//    @Test
//    public void test1() {
//        ZService zService = Ioc.ins().init("com.xiaomi.youpin", "run.mone.local.docean").getBean(ZService.class);
//        ZKnowledgeReq req = new ZKnowledgeReq();
//        req.setKnowledgeBaseId(2061L);
//        req.setQueryText("kafka");
//        req.setLimit(1);
//        String res = zService.getKnowledgeBaseSummaryAnswer(req);
//        System.out.println(res);
//    }

//    @Test
//    public void testGetFileContent2(){
//        ZService zService = Ioc.ins().init("com.xiaomi.youpin", "run.mone.local.docean").getBean(ZService.class);
//        System.out.println(zService.getFileContent(1029L, "caobaoyu"));
//    }

    @Test
    public void testGetFileContent() {
        GitService gitService = Ioc.ins().init("com.xiaomi.youpin", "run.mone.local.docean").getBean(GitService.class);
        GitLabReq req = new GitLabReq();
        req.setGitDomain("X.com");
        req.setGitToken("");
        req.setProjectId("81650");
        req.setFilePath("gateway-service/src/main/java/com/xiaomi/youpin/gwdash/bo/AccountDetailResult.java");
        req.setBranch("master");

        //System.out.println(gitService.getProjectStructureTree(req).getData());
        System.out.println(gitService.getFileContent(req).getData());
        //System.out.println(gitService.parseProjectJavaFile(req).getData());
    }

    @Test
    public void testSendFeiShu() {
        Ioc.ins().init("run.mone.local.docean", "com.xiaomi.youpin");
        ImContext imContext = Ioc.ins().getBean(ImContext.class);
        boolean expected = true; // 假设这是我们期望的返回值
        boolean actual = imContext.sendMessage("hello world", "zhangping17@xiaomi.com", ImEnum.FEISHU);
        assertEquals(expected, actual);
    }

    @Test
    public void testSendWeiXin() {
        Ioc.ins().init("run.mone.local.docean", "com.xiaomi.youpin");
        ImContext imContext = Ioc.ins().getBean(ImContext.class);
        boolean expected = true; // 假设这是我们期望的返回值
        boolean actual = imContext.sendMessage("hello world", "X", ImEnum.WEIXIN);
        assertEquals(expected, actual);
    }

    /**
     * @author liuchuankang
     * @date 2024/02/27
     */
    @SneakyThrows
    @Test
    public void testChromeService() {
        Ioc.ins().init("run.mone.local.docean.service.tool", "com.xiaomi.youpin");
        ChromeService chromeService = Ioc.ins().getBean(ChromeService.class);
        chromeService.open("https://baike.baidu.com/item/%E5%9C%86%E6%98%8E%E5%9B%AD");
        System.out.println(chromeService.getCurrentUrl());
        System.out.println(chromeService.getTitle());
        System.out.println(chromeService.getContent("百度", null));
        System.out.println(chromeService.list());
        TimeUnit.SECONDS.sleep(5);
        chromeService.takeFullScreenShot("/tmp/a.png");
        System.out.println(chromeService.close());
        System.out.println(chromeService.quit());
    }
    @SneakyThrows
    @Test
    public void testTakeFullScreenShotWithCookies() {
        Ioc.ins().init("run.mone.local.docean.service.tool", "com.xiaomi.youpin");
        ChromeService chromeService = Ioc.ins().getBean(ChromeService.class);
        chromeService.openWithCookie("https://127.0.0.1/main-customer-service/call-center/service-list/list","XX");
        TimeUnit.SECONDS.sleep(5);
        chromeService.takeFullScreenShot("/tmp/a.png");
        System.out.println(chromeService.quit());


    }

    @SneakyThrows
    @Test
    public void initializeAndMonitorNetworkTraffic() {
        Ioc.ins().init("run.mone.local.docean.service.tool", "com.xiaomi.youpin");
        ChromeService chromeService = Ioc.ins().getBean(ChromeService.class);
        chromeService.newInit();
        chromeService.networkMonitor("https://www.zhihu.com/billboard");
        TimeUnit.SECONDS.sleep(150);
        System.out.println(chromeService.close());
        System.out.println(chromeService.quit());
    }


    /**
     * 测试执行IDE服务的方法。
     * 初始化IdeService实例，发送关闭所有标签页的命令，并验证返回结果。
     * 确保返回的JsonElement不为空，是JsonObject类型，并且返回码为0。
     */
    @Test
    public void testExecuteIdeService() {
        IdeService ideService = Ioc.ins().init("com.xiaomi.youpin", "run.mone.local.docean").getBean(IdeService.class);

        JsonObject cmdCloseAllTabs = new JsonObject();
        cmdCloseAllTabs.addProperty("cmd", "close_all_tab");

        JsonElement response = ideService.execute(cmdCloseAllTabs);

        assertNotNull(response);
        assertTrue(response.isJsonObject());
        JsonObject responseObj = response.getAsJsonObject();
        assertEquals(0, responseObj.get("code").getAsInt());
    }

    @Test
    public void testOSX() {
        Ioc.ins().init("run.mone.local.docean", "com.xiaomi.youpin");
        OSXService osxService = Ioc.ins().getBean(OSXService.class);
        osxService.open("/tmp");
//        osxService.open("Google Chrome.app");
//        osxService.open("Pages.app");
//        osxService.open("Goland.app");
//        osxService.notify("test","msg");
        ExecCommandResult res = osxService.dialog("test", "msg");
        if (0 == res.getResultCode()) {
            System.out.println("ok");
        } else {
            System.out.println("cancel");
        }
        osxService.open("Google Chrome.app");
        osxService.open("Pages.app");
        osxService.open("Goland.app");
        osxService.notify("test", "msg");
    }

    @Test
    public void testPersistMessage() throws ClassNotFoundException {
        SimpleDataSource dataSource = new SimpleDataSource();
        dataSource.setJdbcUrl("jdbc:sqlite:test.db");
        dataSource.setDriverClassName("org.sqlite.JDBC");
        Dao dao = new NutDao(dataSource);
        dao.create(Message.class, false);
        Ioc.ins().init("run.mone.local.docean", "com.xiaomi.youpin");
        AgentMsgPersistService agentMsgPersistService = Ioc.ins().getBean(AgentMsgPersistService.class);

        // 假设这是我们期望的返回值
        boolean expected = true;

        // 创建一个模拟的Message对象
        Message mockMessage = new Message();
        mockMessage.setRole("mock");
        mockMessage.setData("测试记录");

        // 调用persist方法并获取实际的返回值
        boolean actual = agentMsgPersistService.persist(mockMessage);

        // 断言期望值和实际值相等
        assertEquals(expected, actual);

        // 验证是否正确地记录了日志，这里假设log是一个可以被mock的对象
        // verify(log).info(contains("persisting msg:"), eq(mockMessage), eq(remoteRecord));

        // 如果需要，还可以验证storeLocal和storeRemote方法是否被正确调用
        // verify(agentMsgPersistService).storeLocal(mockMessage);
        // if (remoteRecord) {
        //     verify(agentMsgPersistService).storeRemote(mockMessage);
        // }
    }

    @Test
    public void testExecuteBotService() {
        // 初始化BotService实例
        BotService botService = Ioc.ins().init("com.xiaomi.youpin", "run.mone.local.docean").getBean(BotService.class);

        // 创建一个模拟的BotReq对象
        BotReq mockReq = BotReq.builder().flowDataList(Lists.newArrayList(
                        FlowData.builder().id(0).type("begin").name("开始").inputMap(new java.util.concurrent.ConcurrentHashMap<>(ImmutableMap.of("a", InputData.builder().value(new JsonPrimitive("1")).build(), "b", InputData.builder().value(new JsonPrimitive("2")).build()))).build(),
                        FlowData.builder().id(1).type("code").name("代码")
                                .inputMap(new ConcurrentHashMap<>(ImmutableMap.of("a1", InputData.builder().type("reference").name("a").flowId(0).build(),
                                        "a2", InputData.builder().type("reference").name("b").flowId(0).build())))
                                .build(),
                        FlowData.builder().id(2).type("end").name("结束").build()
                )).nodeEdges(new ArrayList<>(Arrays.asList(NodeEdge.builder().sourceNodeId(0).targetNodeId(1).build(), NodeEdge.builder().sourceNodeId(1).targetNodeId(2).build())))
                .build();
        // 调用execute方法并获取实际的返回值
        EndFlowRes actual = botService.execute(mockReq);
        System.out.println(actual);

    }

    @Test
    public void testMailBotService() {
        // 初始化BotService实例
        BotService botService = Ioc.ins().init("com.xiaomi.youpin", "run.mone.local.docean").getBean(BotService.class);
        // 创建一个模拟的BotReq对象
        BotReq mockReq = BotReq.builder().flowDataList(Lists.newArrayList(
                        FlowData.builder().id(0).type("begin").name("开始").build(),
                        FlowData.builder().id(1).type("mail").name("发邮件").inputMap(new ConcurrentHashMap<>(ImmutableMap.of("recipient", InputData.builder().value(new JsonPrimitive("429867478@qq.com")).build(), "title", InputData.builder().value(new JsonPrimitive("title2")).build(), "content", InputData.builder().value(new JsonPrimitive("content2")).build()))).build(),
                        FlowData.builder().id(2).type("end").name("结束").build()
                )).nodeEdges(new ArrayList<>(Arrays.asList(NodeEdge.builder().sourceNodeId(0).targetNodeId(1).build(), NodeEdge.builder().sourceNodeId(1).targetNodeId(2).build())))
                .build();
        // 调用execute方法并获取实际的返回值
        EndFlowRes actual = botService.execute(mockReq);
        System.out.println(actual);

    }


    public void testGetFeatureRouterDetail() {
        FeatureRouterService featureRouterService = Ioc.ins().init("com.xiaomi.youpin", "run.mone.local.docean").getBean(FeatureRouterService.class);

        // 假设这是我们期望的返回值
        FeatureRouterDTO expected = new FeatureRouterDTO();
        expected.setId(40L);
        expected.setName("lucky");

        // 调用getFeatureRouterDetail方法并获取实际的返回值
        FeatureRouterDTO actual = featureRouterService.getFeatureRouterDetail(40L);

        // 断言期望值和实际值相等
        assertEquals(expected.getName(), actual.getName());

    }


}
