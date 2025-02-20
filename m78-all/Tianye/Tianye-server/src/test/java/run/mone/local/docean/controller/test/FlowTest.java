package run.mone.local.docean.controller.test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonPrimitive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.nutz.dao.util.DaoUp;
import run.mone.junit.DoceanConfiguration;
import run.mone.junit.DoceanExtension;
import run.mone.local.docean.fsm.BotReq;
import run.mone.local.docean.fsm.bo.*;
import run.mone.local.docean.fsm.flow.LLMFlow;
import run.mone.local.docean.service.BotService;
import run.mone.local.docean.tianye.common.CommonConstants;

import javax.annotation.Resource;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author goodjava@qq.com
 * @date 2024/3/4 14:13
 */

@ExtendWith(DoceanExtension.class)
@DoceanConfiguration(basePackage = {"run.mone.local.docean", "com.xiaomi.youpin"})
public class FlowTest {

    @Resource
    private BotService botService;

    @Test
    public void testStrExecute() {
        String str = "{ \"flowDataList\": [ { \"outputMap\": {}, \"id\": 59037263, \"type\": \"begin\", \"inputMap\": { \"e\": { \"originalInput\": \"true\", \"name\": \"e\", \"type\": \"value\", \"value\": \"1+1=?\" } } }, { \"outputMap\": { \"e\": { \"valueType\": \"\", \"name\": \"e\", \"type\": \"value\", \"flowId\": 0, \"value\": \"e\", \"referenceName\": \"\" } }, \"id\": 59037264, \"type\": \"end\", \"inputMap\": {} }, { \"outputMap\": { \"result\": { \"valueType\": \"string\", \"name\": \"result\", \"type\": \"value\", \"flowId\": 0, \"value\": \"\", \"referenceName\": \"\" } }, \"id\": 64385461, \"type\": \"precondition\", \"inputMap\": { \"e\": { \"flowId\": 59037263, \"referenceName\": \"e\", \"name\": \"e\", \"type\": \"reference\", \"operator\": \"EQUALS\", \"flowId2\": 59037263, \"type2\": \"reference\", \"referenceName2\": \"e\" } } } ], \"nodeEdges\": [ { \"sourceNodeId\": 59037263, \"targetNodeId\": 64385461, \"extraInfo\": \"\", \"conditionFlag\": \"\" }, { \"sourceNodeId\": 64385461, \"targetNodeId\": 59037264, \"extraInfo\": \"\", \"conditionFlag\": \"if\" } ], \"flowRecordId\": \"3779fe1c-018e-1000-b002-64cce1f7aaff\", \"syncFlowStatusToM78\": true }";
        System.out.println(botService.execute(new Gson().fromJson(str, BotReq.class)));
    }

    @Test
    public void testKnowledgeFlow() {
        // 创建一个BotReq对象并初始化它的属性
        BotReq mockReq = BotReq.builder().flowDataList(Lists.newArrayList(
                        FlowData.builder().id(0).type("begin").name("开始").inputMap(new ConcurrentHashMap<>(
                                ImmutableMap.of(
                                        "knowledgeBaseId", InputData.builder().value(new JsonPrimitive(1018)).build(),
                                        "maxRecall", InputData.builder().value(new JsonPrimitive(3)).build(),
                                        "minMatch", InputData.builder().value(new JsonPrimitive(0.7)).build(),
                                        "query", InputData.builder().value(new JsonPrimitive("介绍下Athena的收费规则")).build()
                                ))).build(),

                        //调用知识库flow
                        FlowData.builder().id(1).type("knowledge").name("知识库")
                                .inputMap(new ConcurrentHashMap<>(ImmutableMap.of(
                                        CommonConstants.TY_KNOWLEDGE_ID_MARK, InputData.builder().type("reference").name("knowledgeBaseId").referenceName("knowledgeBaseId").flowId(0).build(),
                                        CommonConstants.TY_KNOWLEDGE_MAX_RECALL_MARK, InputData.builder().type("reference").name("maxRecall").referenceName("maxRecall").flowId(0).build(),
                                        CommonConstants.TY_KNOWLEDGE_MIN_MATCH_MARK, InputData.builder().type("reference").name("minMatch").referenceName("minMatch").flowId(0).build(),
                                        CommonConstants.TY_KNOWLEDGE_QUERY_MARK, InputData.builder().type("reference").name("query").referenceName("query").flowId(0).build())))
                                .outputMap(new ConcurrentHashMap<>(ImmutableMap.of(
                                        "outputList", OutputData.builder().build(),
                                        "output", OutputData.builder().build()
                                )))
                                .build(),


                        FlowData.builder().id(2).type("end").name("结束")
                                .inputMap(new ConcurrentHashMap<>(ImmutableMap.of(
                                        CommonConstants.TY_END_MESSAGE_CONTENT_MARK, InputData.builder().value(new JsonPrimitive("${output}")).build()
                                )))

                                .outputMap(new ConcurrentHashMap<>(ImmutableMap.of(
                                        "output", OutputData.builder().type("reference").name("output").referenceName("output").flowId(1).build()
                                )))
                                .build()
                ))
                .nodeEdges(new ArrayList<>(Arrays.asList(NodeEdge.builder().sourceNodeId(0).targetNodeId(1).build(), NodeEdge.builder().sourceNodeId(1).targetNodeId(2).build())))
                .userName("shanwenbang")
                .build();

        // 调用execute方法
        EndFlowRes result = botService.execute(mockReq);
        System.out.println(result);

    }

    @Test
    public void testLLMFlow() {
//        JsonObject object= new JsonObject();
//        object.add("a",new JsonPrimitive("10"));
        // 创建一个BotReq对象并初始化它的属性
        BotReq mockReq = BotReq.builder().flowRecordId("9898").syncFlowStatusToM78(false).flowDataList(Lists.newArrayList(
                        FlowData.builder().id(0).type("begin").name("开始").inputMap(new ConcurrentHashMap<>(
                                ImmutableMap.of(
                                        "prompt", InputData.builder().value(new JsonPrimitive("你是java ${a}工程专家，精通java、mysql和mybatis，每当给你mysql的sql建表语句时，请帮忙生成表对应的Model实例类、mybatis的Mapper类和mybatis的xml,类的package是run.mone.m78，Model实体类都加上lombok的注解@lombok.Data，xml文件和Mapper里面的方法要完整，不要偷懒；\n" +
                                                "mysql表是:-- 图书表\n" +
                                                "CREATE TABLE book (\n" +
                                                "  id INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',\n" +
                                                "  title VARCHAR(255) NOT NULL COMMENT '书名',\n" +
                                                "  author VARCHAR(255) NOT NULL COMMENT '作者',\n" +
                                                "  isbn VARCHAR(20) NOT NULL COMMENT '国际标准书号',\n" +
                                                "  category_id INT NOT NULL COMMENT '分类ID',\n" +
                                                "  price BIGINT NOT NULL COMMENT '价格',\n" +
                                                "  status INT NOT NULL COMMENT '状态（1：可借阅，2：借出，3：维护中）',\n" +
                                                "  created_at BIGINT NOT NULL COMMENT '创建时间',\n" +
                                                "  updated_at BIGINT NOT NULL COMMENT '更新时间'\n" +
                                                ");")).build()
                                ))).build(),

                        //调用知识库flow
                        FlowData.builder().id(1).type("llm").name("大模型")
                                .inputMap(new ConcurrentHashMap<>(ImmutableMap.of(
                                        CommonConstants.TY_LLM_MODEL_MARK, InputData.builder().value(new JsonPrimitive("gpt4_1106_p")).build(),
                                        CommonConstants.TY_LLM_TIMEOUT_MARK, InputData.builder().value(new JsonPrimitive(300)).build(),
                                        CommonConstants.TY_LLM_PROMPT_MARK, InputData.builder().type("reference").name("prompt").referenceName("prompt").flowId(0).originalInput(false).build(),
                                        "a", InputData.builder().value(new JsonPrimitive("15")).build()
                                )))

                                .outputMap(new ConcurrentHashMap<>(ImmutableMap.of(
                                        "output", OutputData.builder().build()
                                )))

                                .build(),

                        FlowData.builder().id(2).type("end").name("结束")
                                .inputMap(new ConcurrentHashMap<>(ImmutableMap.of(
                                        CommonConstants.TY_END_MESSAGE_CONTENT_MARK, InputData.builder().value(new JsonPrimitive("${output}")).build()
                                )))

                                .outputMap(new ConcurrentHashMap<>(ImmutableMap.of(
                                        "output", OutputData.builder().type("reference").name("output").referenceName("output").valueType("string").flowId(1).build()
                                )))
                                .build()
                ))
                .nodeEdges(new ArrayList<>(Arrays.asList(NodeEdge.builder().sourceNodeId(0).targetNodeId(1).build(), NodeEdge.builder().sourceNodeId(1).targetNodeId(2).build())))
                .build();


        // 调用execute方法
        EndFlowRes result = botService.execute(mockReq);
        System.out.println(result);
    }

    @Test
    public void testPluginFlow() {
        // 创建一个BotReq对象并初始化它的属性
        BotReq mockReq = BotReq.builder()
                .flowDataList(Lists.newArrayList(
                        FlowData.builder().id(0).type("begin").name("开始").inputMap(new ConcurrentHashMap<>(
                                ImmutableMap.of(
                                        "content", InputData.builder().value(new JsonPrimitive("")).build()
                                ))).build(),

                        //调用plugin flow
                        FlowData.builder().id(1).type("plugin").name("plugin")
                                .inputMap(new ConcurrentHashMap<>(ImmutableMap.of(
                                        "content", InputData.builder().type("reference").name("content").referenceName("content").originalInput(false).flowId(0).build(),
                                        "userName", InputData.builder().value(new JsonPrimitive("wangyingjie3")).originalInput(false).build(),
                                        CommonConstants.TY_PLUGIN_ID_MARK, InputData.builder().value(new JsonPrimitive(3)).originalInput(false).build()

                                )))
                                .outputMap(new ConcurrentHashMap<>(ImmutableMap.of(
                                        "data", OutputData.builder().build()
                                )))
                                .build(),

                        FlowData.builder().id(2).type("end").name("结束")
                                .inputMap(new ConcurrentHashMap<>(ImmutableMap.of(
                                        CommonConstants.TY_END_MESSAGE_CONTENT_MARK, InputData.builder().value(new JsonPrimitive("${output}")).build()
                                )))

                                .outputMap(new ConcurrentHashMap<>(ImmutableMap.of(
                                        "output", OutputData.builder().type("reference").name("output").referenceName("data").flowId(1).build()
                                )))
                                .build()
                ))
                .nodeEdges(new ArrayList<>(Arrays.asList(NodeEdge.builder().sourceNodeId(0).targetNodeId(1).build(), NodeEdge.builder().sourceNodeId(1).targetNodeId(2).build())))
                .build();


        // 调用execute方法
        EndFlowRes result = botService.execute(mockReq);
        System.out.println(result);

    }

    @Test
    public void testAiNewsFlow() {
        String prompt = "你是一位高级新闻编辑专员，并且高度专注于AI资讯领域。\n" +
                "接下来我会以json数组的形式给你一系列AI资讯，请你帮忙整理下，要求如下：\n" +
                "1.给你的资讯里会有着几个属性：url、title、releaseTime、summary、分别代表文章url、标题、发布时间、文章的前几个章节内容(注意内容是markdown格式的)\n" +
                "2.整理时遵循如下规则：\n" +
                "2.1 汇总时请进行文章信息的分类。比如AI模型、AI理论、AI商业等。具体的分类以你专业编辑的知识来定\n" +
                "2.2 总结完的每篇文章必须要有：url、标题、发布时间、以及50字以内的总结摘要(基于summary的md文本进行总结)\n" +
                "2.3 请把标题和url以markdownde的链接形式合成一条\n" +
                "2.4 整理的内容简洁、分类清晰、重点突出\n" +
                "2.5 如果给的素材里有相似度极高的文章，请做去重处理，只保留一篇\n" +
                "2.6 releaseTime如果没有的情况下，需要你基于文章内容自己补一个时间\n" +
                "2.7 每个类别下的文章，请以内容重要度或者热度进行倒排\n" +
                "2.8 请以markdown的形式进行总结，并将总结完的整个markdown文本，塞入json字符串中返回，json具体格式我最后会给出\n" +
                "\n" +
                "原始素材如下：\n" +
                "${ai_news_json}\n\n";

        // 创建一个BotReq对象并初始化它的属性
        BotReq mockReq = BotReq.builder()
                .flowDataList(Lists.newArrayList(
                        FlowData.builder().id(0).type("begin").name("开始").inputMap(new ConcurrentHashMap<>(
                                ImmutableMap.of(
                                        "content", InputData.builder().value(new JsonPrimitive("")).build()
                                ))).build(),

                        //调用plugin flow
                        FlowData.builder().id(1).type("plugin").name("plugin")
                                .inputMap(new ConcurrentHashMap<>(ImmutableMap.of(
                                        "content", InputData.builder().type("reference").name("content").referenceName("content").flowId(0).build(),
                                        "userName", InputData.builder().value(new JsonPrimitive("shanwenbang")).build(),
                                        CommonConstants.TY_PLUGIN_ID_MARK, InputData.builder().value(new JsonPrimitive(10)).build()

                                )))
                                .outputMap(new ConcurrentHashMap<>(ImmutableMap.of(
                                        "data", OutputData.builder().build()
                                )))
                                .build(),

                        FlowData.builder().id(2).type("llm").name("大模型")
                                .inputMap(new ConcurrentHashMap<>(ImmutableMap.of(
                                        CommonConstants.TY_LLM_MODEL_MARK, InputData.builder().value(new JsonPrimitive("gpt4_1106_p")).build(),
                                        CommonConstants.TY_LLM_PROMPT_MARK, InputData.builder().value(new JsonPrimitive(prompt)).build(),
                                        "ai_news_json", InputData.builder().type("reference").name("data").referenceName("data").flowId(1).build()
                                )))

                                .outputMap(new ConcurrentHashMap<>(ImmutableMap.of(
                                        "output", OutputData.builder().build()
                                )))
                                .build(),


                        FlowData.builder().id(3).type("end").name("结束")
                                .inputMap(new ConcurrentHashMap<>(ImmutableMap.of(
                                        CommonConstants.TY_END_MESSAGE_CONTENT_MARK, InputData.builder().value(new JsonPrimitive("${output}")).build()
                                )))

                                .outputMap(new ConcurrentHashMap<>(ImmutableMap.of(
                                        "output", OutputData.builder().type("reference").name("output").referenceName("output").flowId(2).build()
                                )))
                                .build()
                ))
                .nodeEdges(new ArrayList<>(Arrays.asList(NodeEdge.builder().sourceNodeId(0).targetNodeId(1).build(),
                        NodeEdge.builder().sourceNodeId(1).targetNodeId(2).build(), NodeEdge.builder().sourceNodeId(2).targetNodeId(3).build())))
                .build();

        // 调用execute方法
        EndFlowRes result = botService.execute(mockReq);
        System.out.println(result);
    }


    @Test
    public void testConditionFlow() {
        // 创建一个BotReq对象并初始化它的属性
        BotReq mockReq = BotReq.builder()
                //.ifEdgeMap(ImmutableMap.of(99, Lists.newArrayList(2)))
                .elseEdgeMap(ImmutableMap.of(99, Lists.newArrayList(3)))
                .flowDataList(Lists.newArrayList(
                        FlowData.builder().id(0).type("begin").name("开始").inputMap(new ConcurrentHashMap<>(
                                ImmutableMap.of(
                                        "question", InputData.builder().value(new JsonPrimitive("mione是什么系统")).build(),
                                        "flag", InputData.builder().value(new JsonPrimitive("1")).build()
                                ))).build(),

                        //condition flow
                        FlowData.builder().id(99).type("precondition").name("条件")
                                .inputMap(new ConcurrentHashMap<>(ImmutableMap.of(
                                        "flag", InputData.builder().type("reference").name("flag").referenceName("flag").flowId(0)
                                                .operator("EQUALS")
                                                .type2("value").value2(new JsonPrimitive("2")).build()
                                )))
                                //.flowMeta(ImmutableMap.of("ifFinish", "2", "elseFinish", "3"))
                                .build(),


                        //llm flow
                        FlowData.builder().id(2).type("llm").name("大模型")
                                .inputMap(new ConcurrentHashMap<>(ImmutableMap.of(
                                        CommonConstants.TY_LLM_MODEL_MARK, InputData.builder().value(new JsonPrimitive("moonshot")).build(),
                                        CommonConstants.TY_LLM_PROMPT_MARK, InputData.builder().type("reference").name("prompt").referenceName("question").flowId(0).build()
                                )))

                                .outputMap(new ConcurrentHashMap<>(ImmutableMap.of(
                                        "llm-output", OutputData.builder().build()
                                )))
                                .build(),

                        //knowledge flow
                        FlowData.builder().id(3).type("knowledge").name("知识库")
                                .inputMap(new ConcurrentHashMap<>(ImmutableMap.of(
                                        CommonConstants.TY_KNOWLEDGE_ID_MARK, InputData.builder().name("knowledgeBaseId").value(new JsonPrimitive("1018")).build(),
                                        CommonConstants.TY_KNOWLEDGE_MAX_RECALL_MARK, InputData.builder().name("maxRecall").value(new JsonPrimitive("1")).build(),
                                        CommonConstants.TY_KNOWLEDGE_MIN_MATCH_MARK, InputData.builder().name("minMatch").value(new JsonPrimitive("0.7")).build(),
                                        CommonConstants.TY_KNOWLEDGE_QUERY_MARK, InputData.builder().type("reference").name("query").referenceName("question").flowId(0).build())))
                                .outputMap(new ConcurrentHashMap<>(ImmutableMap.of(
                                        "outputList", OutputData.builder().build(),
                                        "knowledge-output", OutputData.builder().build()
                                )))
                                .build(),

                        FlowData.builder().id(4).type("end").name("结束")
                                .inputMap(new ConcurrentHashMap<>(ImmutableMap.of(
                                        CommonConstants.TY_END_MESSAGE_CONTENT_MARK, InputData.builder().value(new JsonPrimitive("${output}")).build()
                                )))

                                .outputMap(new ConcurrentHashMap<>(ImmutableMap.of(
                                        "llm-output", OutputData.builder().type("reference").name("llm-output").referenceName("llm-output").valueType("string").flowId(2).build(),
                                        "knowledge-output", OutputData.builder().type("reference").name("knowledge-output").referenceName("outputList").valueType("string").flowId(3).build()

                                )))
                                .build()
                ))
                .nodeEdges(new ArrayList<>(Arrays.asList(NodeEdge.builder().sourceNodeId(0).targetNodeId(99).build(),
                        NodeEdge.builder().sourceNodeId(99).targetNodeId(2).build(),
                        NodeEdge.builder().sourceNodeId(99).targetNodeId(3).build(),
                        NodeEdge.builder().sourceNodeId(2).targetNodeId(4).build(),
                        NodeEdge.builder().sourceNodeId(3).targetNodeId(4).build())))
                .build();

        // 调用execute方法
        EndFlowRes result = botService.execute(mockReq);
        System.out.println(result);
    }

    @Test
    public void testPostJsonProxy() {
//        String prompt = "李清照最好的三首词?";
//        String prompt = "1+2*68=?";

//        String prompt = "鲁迅最主要的五部著作?(json key 包含 books 著作名)";
        String prompt = "鲁迅最主要的五部著作? (著作给我一个json array) (json key 包含 books 著作名)";
        LLMFlow llmFlow = new LLMFlow(); // Assuming LLMFlow is accessible here, otherwise use @Resource to inject it.
        //llmFlow.setModel("moonshot");
        String actualResponse = llmFlow.postJsonProxy(prompt, "moonshot", null, false).toString();
        System.out.println(actualResponse);

    }

    @Test
    public void testCodeFlow() {
        // 创建一个BotReq对象并初始化它的属性
        BotReq mockReq = BotReq.builder()
                .flowDataList(Lists.newArrayList(
                        FlowData.builder().id(0).type("begin").name("开始").inputMap(new ConcurrentHashMap<>(
                                ImmutableMap.of(
                                        "content", InputData.builder().value(new JsonPrimitive("")).build()
                                ))).build(),

                        //调用plugin flow
                        FlowData.builder().id(1).type("code").name("plugin")
                                .inputMap(new ConcurrentHashMap<>(ImmutableMap.of(
                                        "content", InputData.builder().type("reference").name("content").referenceName("content").flowId(0).build(),
                                        "a", InputData.builder().name("a").value(new JsonPrimitive(29)).build(),
                                        "b", InputData.builder().name("b").value(new JsonPrimitive(201)).build(),
                                        CommonConstants.TY_CODE_INPUT_MARK, InputData.builder().name(CommonConstants.TY_CODE_INPUT_MARK).value(new JsonPrimitive("import com.google.gson.JsonObject;\n def execute(JsonObject input, Object context) {\n    if (!input.has('a') || !input.has('b')) {\n        throw new IllegalArgumentException(\"JSON对象必须包含键'a'和'b'。\");\n    }\n    int a = input.get('a').getAsInt();\n    int b = input.get('b').getAsInt();\n    int sum = a + b;\n    JsonObject result = new JsonObject();\n    result.addProperty(\"sum\", sum);\n    return result;\n}")).build()
                                )))

                                .outputMap(new ConcurrentHashMap<>(ImmutableMap.of(
                                        "sum", OutputData.builder().build() // sum为脚本代码返回结果的键
                                )))

//                                .flowMeta(ImmutableMap.of("codeId", "5"))

                                .build(),

                        FlowData.builder().id(2).type("end").name("结束")
                                .inputMap(new ConcurrentHashMap<>(ImmutableMap.of(
                                        CommonConstants.TY_END_MESSAGE_CONTENT_MARK, InputData.builder().value(new JsonPrimitive("${output}")).build()
                                )))

                                .outputMap(new ConcurrentHashMap<>(ImmutableMap.of(
                                        "output", OutputData.builder().type("reference").name("output").referenceName("sum").flowId(1).build()
                                )))
                                .build()
                ))
                .nodeEdges(new ArrayList<>(Arrays.asList(NodeEdge.builder().sourceNodeId(0).targetNodeId(1).build(), NodeEdge.builder().sourceNodeId(1).targetNodeId(2).build())))
                .build();


        // 调用execute方法
        EndFlowRes result = botService.execute(mockReq);
        System.out.println("Code Running Res: " + result);
    }

    @Test
    public void testDatabaseFlow() throws IOException {
        // 创建一个BotReq对象并初始化它的属性
        BotReq mockReq = BotReq.builder()
                .flowDataList(Lists.newArrayList(
                        FlowData.builder().id(0).type("begin").name("开始").inputMap(new ConcurrentHashMap<>(
                                ImmutableMap.of(
                                        "content", InputData.builder().value(new JsonPrimitive("")).build()
                                ))).build(),

                        //调用database flow
                        FlowData.builder().id(1).type("database").name("database")
                                .inputMap(new ConcurrentHashMap<>(ImmutableMap.of(
                                        "content", InputData.builder().type("reference").name("content").referenceName("content").flowId(0).build(),
                                        CommonConstants.TY_SQL_INPUT_MARK, InputData.builder().name(CommonConstants.TY_SQL_INPUT_MARK).value(new JsonPrimitive("select * from nyx_test limit 3;")).build()
                                )))

                                .outputMap(new ConcurrentHashMap<>(ImmutableMap.of(
                                        "data", OutputData.builder().build()
                                )))


                                .build(),

                        FlowData.builder().id(2).type("end").name("结束")
                                .inputMap(new ConcurrentHashMap<>(ImmutableMap.of(
                                        CommonConstants.TY_END_MESSAGE_CONTENT_MARK, InputData.builder().value(new JsonPrimitive("${output}")).build()
                                )))

                                .outputMap(new ConcurrentHashMap<>(ImmutableMap.of(
                                        "output", OutputData.builder().type("reference").name("output").referenceName("data").flowId(1).build()
                                )))
                                .build()
                ))
                .nodeEdges(new ArrayList<>(Arrays.asList(NodeEdge.builder().sourceNodeId(0).targetNodeId(1).build(), NodeEdge.builder().sourceNodeId(1).targetNodeId(2).build())))
                .build();


        // 调用execute方法
        EndFlowRes result = botService.execute(mockReq);
        System.out.println("Sql Running Res: " + result);
        DaoUp.me().close();
    }


    //实现一个静态类，从resources目录下读取指定路径文件的内容，注意内容编码用UTF8
    public static String readFileFromResources(String path) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try (InputStream inputStream = classLoader.getResourceAsStream(path);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining(System.lineSeparator()));
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read file from resources: " + path, e);
        }
    }

    @Test
    public void testAutoApp() {
        String requirement_analysis_prompt = readFileFromResources("autoapp/requirement_analysis_prompt.txt");
        String function_analysis_prompt = readFileFromResources("autoapp/function_analysis_prompt.txt");
        String po2dto_prompt = readFileFromResources("autoapp/po2dto_prompt.txt");
        String controllerPrompt = readFileFromResources("autoapp/controller_prompt.txt");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
        String currentTime = sdf.format(new Date());
        String projectName = "books-system" + currentTime;
        System.out.println("goto generate:" + projectName);
        // 创建一个BotReq对象并初始化它的属性
        BotReq mockReq = BotReq.builder()
                .flowDataList(Lists.newArrayList(
                        FlowData.builder().id(0).type("begin").name("开始").inputMap(new ConcurrentHashMap<>(
                                ImmutableMap.of(
                                        "origin_requirement", InputData.builder().value(new JsonPrimitive("图书管理系统")).build(),
                                        "projectName", InputData.builder().value(new JsonPrimitive(projectName)).build(),
                                        "packageName", InputData.builder().value(new JsonPrimitive("run.mone.m78")).build()
                                ))).build(),

                        //调用plugin flow 生成基础项目代码
                        FlowData.builder().id(1).type("plugin").name("plugin")
                                .inputMap(new ConcurrentHashMap<>(ImmutableMap.of(
                                        "projectName", InputData.builder().type("reference").name("projectName").referenceName("projectName").flowId(0).build(),
                                        "packageName", InputData.builder().type("reference").name("packageName").referenceName("packageName").flowId(0).build(),
                                        CommonConstants.TY_PLUGIN_ID_MARK, InputData.builder().value(new JsonPrimitive(22)).build()
                                )))
                                .outputMap(new ConcurrentHashMap<>(ImmutableMap.of(
                                        "data", OutputData.builder().build()
                                )))
                                .build(),

                        //生成需求分析文档
                        FlowData.builder().id(2).type("llm").name("大模型")
                                .inputMap(new ConcurrentHashMap<>(ImmutableMap.of(
                                        CommonConstants.TY_LLM_MODEL_MARK, InputData.builder().value(new JsonPrimitive("gpt4_1106_p")).build(),
                                        CommonConstants.TY_LLM_PROMPT_MARK, InputData.builder().value(new JsonPrimitive(requirement_analysis_prompt)).build(),
                                        CommonConstants.TY_LLM_USE_CACHE, InputData.builder().value(new JsonPrimitive("true")).build(),
                                        "origin_requirement", InputData.builder().type("reference").name("origin_requirement").referenceName("origin_requirement").flowId(0).build()
                                )))
                                .outputMap(new ConcurrentHashMap<>(ImmutableMap.of(
                                        "requirement_doc", OutputData.builder().build()
                                )))
                                .build(),

                        //生成概要设计(功能点)
                        FlowData.builder().id(5).type("llm").name("大模型2")
                                .inputMap(new ConcurrentHashMap<>(ImmutableMap.of(
                                        //CommonConstants.TY_LLM_MODEL_MARK, InputData.builder().value(new JsonPrimitive("gpt4_1106_p")).build(),
                                        CommonConstants.TY_LLM_MODEL_MARK, InputData.builder().value(new JsonPrimitive("gpt4_1106_p")).build(),
                                        CommonConstants.TY_LLM_PROMPT_MARK, InputData.builder().value(new JsonPrimitive(function_analysis_prompt)).build(),
                                        CommonConstants.TY_LLM_USE_CACHE, InputData.builder().value(new JsonPrimitive("true")).build(),
                                        "document", InputData.builder().type("reference").name("requirement_doc").referenceName("requirement_doc").flowId(2).build()
                                ))).outputMap(new ConcurrentHashMap<>(ImmutableMap.of(
                                        "output", OutputData.builder().build()
                                )))
                                .build(),

                        //调用plugin 生成dao代码
                        FlowData.builder().id(6).type("plugin").name("dao plugin")
                                .inputMap(new ConcurrentHashMap<>(ImmutableMap.of(
                                        CommonConstants.TY_PLUGIN_ID_MARK, InputData.builder().value(new JsonPrimitive(77)).build(),
                                        CommonConstants.TY_LLM_USE_CACHE, InputData.builder().value(new JsonPrimitive("true")).build(),
                                        "model", InputData.builder().value(new JsonPrimitive("gpt4_1106_p")).build(),
                                        "token", InputData.builder().value(new JsonPrimitive("X")).build(),
                                        "document", InputData.builder().type("reference").name("requirement_doc").referenceName("requirement_doc").flowId(2).build()
                                )))
                                .outputMap(new ConcurrentHashMap<>(ImmutableMap.of(
                                        "data", OutputData.builder().build()
                                )))
                                .build(),

                        //merge dao 代码
                        FlowData.builder().id(7).type("plugin").name("plugin")
                                .inputMap(new ConcurrentHashMap<>(ImmutableMap.of(
                                        CommonConstants.TY_PLUGIN_ID_MARK, InputData.builder().value(new JsonPrimitive(24)).build(),
                                        "projectName", InputData.builder().type("reference").name("projectName").referenceName("projectName").flowId(0).build(),
                                        "packageName", InputData.builder().type("reference").name("packageName").referenceName("packageName").flowId(0).build(),
                                        "codeDetail", InputData.builder().type("reference").name("data").referenceName("data.dataList").flowId(6).build()
                                )))
                                .outputMap(new ConcurrentHashMap<>(ImmutableMap.of(
                                        "data", OutputData.builder().build()
                                )))
                                .build(),

                        //dao 生成dto
                        FlowData.builder().id(777).type("llm").name("dao2dto")
                                .batchMap(new ConcurrentHashMap<>(ImmutableMap.of(
                                        CommonConstants.TY_BATCH_MAX_TIMES_MARK, InputData.builder().value(new JsonPrimitive("5")).build(),
                                        "poList", InputData.builder().type("reference").name("data").referenceName("data.poList").flowId(6).build()
                                )))

                                .inputMap(new ConcurrentHashMap<>(ImmutableMap.of(
                                        CommonConstants.TY_LLM_MODEL_MARK, InputData.builder().value(new JsonPrimitive("gpt4_1106_p")).build(),
                                        CommonConstants.TY_LLM_PROMPT_MARK, InputData.builder().value(new JsonPrimitive(po2dto_prompt)).build(),
                                        CommonConstants.TY_LLM_USE_CACHE, InputData.builder().value(new JsonPrimitive("true")).build(),
                                        "po_content", InputData.builder().type("batch").name("po_content").referenceName("poList").build()
                                )))
                                .outputMap(new ConcurrentHashMap<>(ImmutableMap.of(
                                        "outputList", OutputData.builder().valueType("Array<string>").schema("[{\"name\":\"output\",\"valueType\":\"String\",\"children\":[],\"desc\":\"dto代码输出\"}]").build()
                                )))
                                .build(),

                        //merge dto 代码
                        FlowData.builder().id(778).type("plugin").name("merge dto")
                                .inputMap(new ConcurrentHashMap<>(ImmutableMap.of(
                                        CommonConstants.TY_PLUGIN_ID_MARK, InputData.builder().value(new JsonPrimitive(24)).build(),
                                        "projectName", InputData.builder().type("reference").name("projectName").referenceName("projectName").flowId(0).build(),
                                        "packageName", InputData.builder().type("reference").name("packageName").referenceName("packageName").flowId(0).build(),
                                        "codeDetail", InputData.builder().type("reference").name("output").referenceName("outputList.output").flowId(777).build()
                                )))
                                .outputMap(new ConcurrentHashMap<>(ImmutableMap.of(
                                        "data", OutputData.builder().build()
                                )))
                                .build(),

                        FlowData.builder().id(66).type("llm").name("controller大模型")
                                .inputMap(new ConcurrentHashMap<>(ImmutableMap.of(
                                        CommonConstants.TY_LLM_MODEL_MARK, InputData.builder().value(new JsonPrimitive("gpt4_1106_p")).build(),
                                        CommonConstants.TY_LLM_PROMPT_MARK, InputData.builder().value(new JsonPrimitive(controllerPrompt)).build(),
                                        "requirement_doc", InputData.builder().type("reference").name("requirement_doc").referenceName("requirement_doc").flowId(2).build(),
                                        "func_point", InputData.builder().type("reference").name("output").referenceName("output").flowId(5).build(),
                                        "code_meta", InputData.builder().type("reference").name("data").referenceName("data.metaList").flowId(6).build()
                                )))
                                .outputMap(new ConcurrentHashMap<>(ImmutableMap.of(
                                        "output", OutputData.builder().build()
                                )))
                                .build(),

                        //merge controller 代码
                        FlowData.builder().id(67).type("plugin").name("plugin")
                                .inputMap(new ConcurrentHashMap<>(ImmutableMap.of(
                                        CommonConstants.TY_PLUGIN_ID_MARK, InputData.builder().value(new JsonPrimitive(24)).build(),
                                        "projectName", InputData.builder().type("reference").name("projectName").referenceName("projectName").flowId(0).build(),
                                        "packageName", InputData.builder().type("reference").name("packageName").referenceName("packageName").flowId(0).build(),
                                        "codeDetail", InputData.builder().type("reference").name("output").referenceName("output").flowId(66).build()
                                )))
                                .outputMap(new ConcurrentHashMap<>(ImmutableMap.of(
                                        "data", OutputData.builder().build()
                                )))
                                .build(),


                        //创建miline项目+push代码
                        FlowData.builder().id(68).type("plugin").name("init miline project")
                                .inputMap(new ConcurrentHashMap<>(ImmutableMap.of(
                                        CommonConstants.TY_PLUGIN_ID_MARK, InputData.builder().value(new JsonPrimitive(40)).build(),
                                        "projectName", InputData.builder().type("reference").name("projectName").referenceName("projectName").flowId(0).build(),
                                        "packageName", InputData.builder().type("reference").name("packageName").referenceName("packageName").flowId(0).build(),
                                        "projectPath", InputData.builder().type("reference").name("data").referenceName("data").flowId(1).build()
                                )))
                                .outputMap(new ConcurrentHashMap<>(ImmutableMap.of(
                                        "data", OutputData.builder().build()
                                )))
                                .build(),

                        //创建miline流水线
                        FlowData.builder().id(69).type("plugin").name("create miline pipeline")
                                .inputMap(new ConcurrentHashMap<>(
                                        ImmutableMap.<String, InputData>builder()
                                                .put(CommonConstants.TY_PLUGIN_ID_MARK, InputData.builder().value(new JsonPrimitive(43)).build())
                                                .put("projectId", InputData.builder().type("reference").name("data").referenceName("data.id").flowId(68).build())
                                                .put("userName", InputData.builder().value(new JsonPrimitive("shanwenbang")).build())
                                                .put("env", InputData.builder().value(new JsonPrimitive("staging")).build())
                                                .put("pipelineName", InputData.builder().value(new JsonPrimitive("staging")).build())
                                                .put("deployEnvGroup", InputData.builder().value(new JsonPrimitive("staging")).build())
                                                .put("profile", InputData.builder().value(new JsonPrimitive("staging")).build())
                                                .put("gitUrl", InputData.builder().type("reference").name("data").referenceName("data.gitUrl").flowId(68).build())
                                                .put("jarPath", InputData.builder().type("reference").name("data").referenceName("data.jarPath").flowId(68).build())
                                                .put("deployCpu", InputData.builder().value(new JsonPrimitive("1")).build())
                                                .put("deployMemory", InputData.builder().value(new JsonPrimitive("2048")).build())
                                                .put("logPath", InputData.builder().value(new JsonPrimitive("/home/work/log")).build())
                                                .put("replicas", InputData.builder().value(new JsonPrimitive("1")).build())
                                                .build()
                                ))
                                .outputMap(new ConcurrentHashMap<>(ImmutableMap.of(
                                        "data", OutputData.builder().build()
                                )))
                                .build(),

                        //启动miline流水线
                        FlowData.builder().id(70).type("plugin").name("start miline pipeline")
                                .inputMap(new ConcurrentHashMap<>(
                                        ImmutableMap.<String, InputData>builder()
                                                .put(CommonConstants.TY_PLUGIN_ID_MARK, InputData.builder().value(new JsonPrimitive(44)).build())
                                                .put("projectId", InputData.builder().type("reference").name("data").referenceName("data.id").flowId(68).build())
                                                .put("pipelineId", InputData.builder().type("reference").name("data").referenceName("data").flowId(69).build())
                                                .put("userName", InputData.builder().value(new JsonPrimitive("shanwenbang")).build())
                                                .put("gitBranch", InputData.builder().value(new JsonPrimitive("master")).build())
                                                .build()
                                ))
                                .outputMap(new ConcurrentHashMap<>(ImmutableMap.of(
                                        "data", OutputData.builder().build()
                                )))
                                .build(),

                        FlowData.builder().id(77).type("end").name("结束")
                                .inputMap(new ConcurrentHashMap<>(ImmutableMap.of(
                                        CommonConstants.TY_END_MESSAGE_CONTENT_MARK, InputData.builder().value(new JsonPrimitive("${output}")).build()
                                )))
                                .outputMap(new ConcurrentHashMap<>(ImmutableMap.of(
                                        "output", OutputData.builder().type("reference").name("data").referenceName("data").flowId(6).build()
                                )))
                                .build()
                ))
                .nodeEdges(new ArrayList<>(Arrays.asList(
                        NodeEdge.builder().sourceNodeId(0).targetNodeId(1).build(),
                        NodeEdge.builder().sourceNodeId(1).targetNodeId(2).build(),
                        NodeEdge.builder().sourceNodeId(2).targetNodeId(5).build(),
                        NodeEdge.builder().sourceNodeId(5).targetNodeId(6).build(),
                        NodeEdge.builder().sourceNodeId(6).targetNodeId(7).build(),

                        NodeEdge.builder().sourceNodeId(7).targetNodeId(777).build(),
                        NodeEdge.builder().sourceNodeId(777).targetNodeId(778).build(),

                        NodeEdge.builder().sourceNodeId(778).targetNodeId(66).build(),
                        NodeEdge.builder().sourceNodeId(66).targetNodeId(67).build(),
                        NodeEdge.builder().sourceNodeId(67).targetNodeId(68).build(),
                        NodeEdge.builder().sourceNodeId(68).targetNodeId(69).build(),
                        NodeEdge.builder().sourceNodeId(69).targetNodeId(70).build(),
                        NodeEdge.builder().sourceNodeId(70).targetNodeId(77).build())
                ))
                .build();

        // 调用execute方法
        EndFlowRes result = botService.execute(mockReq);
        System.out.println(result);
    }

    @Test
    public void testAutoAppFront() {
        String requirement_doc = "1.需求文档\n" +
                "项目名称：图书管理系统\n" +
                "背景：\n" +
                "为了提高图书馆的工作效率，方便读者借阅和归还图书，本项目旨在开发一个图书管理系统，实现对图书的统一管理、借阅、归还、查询等功能。\n" +
                "\n" +
                "用户群体：\n" +
                "1. 图书馆管理员：负责图书的入库、借阅、归还等操作。\n" +
                "2. 读者：可以查询、借阅和归还图书。\n" +
                "\n" +
                "核心功能：\n" +
                "1. 图书信息管理：\n" +
                "   - 添加图书：录入图书的基本信息，如书名、作者、出版社、出版日期等。\n" +
                "   - 修改图书信息：对已有图书信息进行修改。\n" +
                "   - 删除图书：从系统中移除不再需要的图书。\n" +
                "   - 查询图书：根据书名、作者等条件查询图书。\n" +
                "\n" +
                "2. 借阅管理：\n" +
                "   - 借书：读者提交借阅申请，系统记录借阅信息。\n" +
                "   - 还书：读者归还图书，系统更新借阅信息。\n" +
                "   - 借阅查询：查询读者的借阅记录。\n" +
                "\n" +
                "3. 读者管理：\n" +
                "   - 添加读者：录入读者的基本信息，如姓名、性别、联系方式等。\n" +
                "   - 修改读者信息：对已有读者信息进行修改。\n" +
                "   - 删除读者：从系统中移除不再需要的读者。\n" +
                "   - 查询读者：根据姓名等条件查询读者。\n" +
                "\n" +
                "非功能性需求：\n" +
                "1. 性能要求：系统应具备较高的响应速度，确保用户操作流畅。\n" +
                "2. 安全要求：系统应具备用户权限管理功能，防止未授权访问。\n" +
                "3. 可用性要求：系统应具备友好的用户界面，便于用户快速上手。\n" +
                "4. 扩展性要求：系统应具备一定的扩展性，便于后期增加新功能。";

        String frontend_design_prompt = "您是一位资深的前端技术专家，擅长使用vue3，vue-router，vant组件库和vite。\n" +
                "\n" +
                "您从用户那里获取需求文档，然后将需求转化为实际的系统架构和设计，创建详细的UI和交互设计。\n" +
                "您可能还会收到您已经做出的部分设计，并被要求做局部修改，使其更符合要求。\n" +
                "\n" +
                "- 项目的技术选型是vue3，vue-router，vant组件库和vite\n" +
                "- 主要关注用户界面（UI）和用户体验（UX）设计，包括颜色方案、字体选择、布局、图标设计、动画和过渡效果等。\n" +
                "- 尽可能详细的描述页面/组件的具体实现。\n" +
                "- 设计出页面路path信息，path不要出现中文等特殊字符。\n" +
                "- 设计需要符合移动端的体验操作。\n" +
                "\n" +
                "可以参考如下demo：\n" +
                "输入:\n" +
                "```md\n" +
                "1.需求文档\n" +
                "项目名称：图书管理系统\n" +
                "背景：\n" +
                "为了提高图书馆的工作效率，方便读者借阅和归还图书，本项目旨在开发一个图书管理系统，实现对图书的统一管理、借阅、归还、查询等功能。\n" +
                "\n" +
                "用户群体：\n" +
                "1. 图书馆管理员：负责图书的入库、借阅、归还等操作。\n" +
                "2. 读者：可以查询、借阅和归还图书。\n" +
                "\n" +
                "核心功能：\n" +
                "1. 图书信息管理：\n" +
                "   - 添加图书：录入图书的基本信息，如书名、作者、出版社、出版日期等。\n" +
                "   - 修改图书信息：对已有图书信息进行修改。\n" +
                "   - 删除图书：从系统中移除不再需要的图书。\n" +
                "   - 查询图书：根据书名、作者等条件查询图书。\n" +
                "\n" +
                "非功能性需求：\n" +
                "1. 性能要求：系统应具备较高的响应速度，确保用户操作流畅。\n" +
                "2. 安全要求：系统应具备用户权限管理功能，防止未授权访问。\n" +
                "3. 可用性要求：系统应具备友好的用户界面，便于用户快速上手。\n" +
                "4. 扩展性要求：系统应具备一定的扩展性，便于后期增加新功能。\n" +
                "```\n" +
                "\n" +
                "输出：\n" +
                "```md\n" +
                "1.需求文档\n" +
                "项目名称：图书管理系统\n" +
                "背景：\n" +
                "为了提高图书馆的工作效率，方便读者借阅和归还图书，本项目旨在开发一个图书管理系统，实现对图书的统一管理、借阅、归还、查询等功能。\n" +
                "\n" +
                "用户群体：\n" +
                "1. 图书馆管理员：负责图书的入库、借阅、归还等操作。\n" +
                "2. 读者：可以查询、借阅和归还图书。\n" +
                "\n" +
                "核心功能：\n" +
                "1. 图书列表页面，path: /books：\n" +
                "  - 支持通过按钮跳到新增页面\n" +
                "  - 支持获取图书信息列表\n" +
                "  - 支持通过书名、作者信息查询列表\n" +
                "  - 支持通过按钮跳转到更新页面\n" +
                "  - 支持根据id删除图书，并成功时刷新图书列表\n" +
                "\n" +
                "2. 图书新增页面，path：/book/new\n" +
                "  - FORM表单填写图书信息\n" +
                "  - 支持提交添加图书\n" +
                "\n" +
                "3. 图书更新页面，path：/book/:id\n" +
                "  - 支持Form表单回显图书信息\n" +
                "  - 支持编辑图书信息\n" +
                "  - 支持提交更新图书\n" +
                "```\n" +
                "\n" +
                "注意必须按上述输出格式组织。\n" +
                "不需要额外的解释描述！\n" +
                "\n" +
                "这是新的输入输入：\n" +
                "${requirement_doc}\n" +
                "\n" +
                "请给我输出";

        String frontend_code_prompt = "您是一位专业的vue3/vue-router/vant/vite/TypeScript/Tailwind开发者。\n" +
                "\n" +
                "您从用户那里获取相关的需求描述和接口描述，然后使用vue3, vue-router, vant组件库, TypeScript和Tailwind构建实现对应的单文件组件和相关的接口。\n" +
                "您可能还会收到您已经构建的单文件组件或接口请求，并被要求做局部修改，使其更符合要求。\n" +
                "\n" +
                "要求：\n" +
                "- 项目是基于vue3，ts和vite搭建的脚手架。\n" +
                "- 项目中的ts使用分号分隔语句。\n" +
                "- 项目按如下目录规范组织单文件组件和相关接口：\n" +
                "```\n" +
                "index.html  // index.html文件\n" +
                "public      // 用于存放静态文件，如 index.html 文件、favicon.ico 图标等\n" +
                "src\n" +
                " |-- api    // 接口相关的请求\n" +
                " |-- components  // 用于存放项目的通用组件\n" +
                " |-- assets      // 用于存放项目所需的静态资源，如图片、字体等\n" +
                " |-- pages       // 用于存放项目的页面\n" +
                " |-- router      // 页面间的url导航\n" +
                " |-- utils       // 用于存放项目的工具函数\n" +
                "```\n" +
                "\n" +
                "- 命名中不要出现中文等特殊字符。\n" +
                "- 使用@来替代src目录做引用，当引用单文件组件时，必须加上.vue后缀，.ts后缀可省略。\n" +
                "- 只需要关心业务实现，如页面，通用组件，路由以及业务类型定义等，不用关心其他非业务实现，如main.ts、index.html等。\n" +
                "- template中使用vue3支持的语法\n" +
                "- 可对生成的代码添加适当注释\n" +
                "- vue-router使用createWebHashHistory作为history\n" +
                "- 注意如果import中有type类型，需要明确标注出type类型，如下所示：\n" +
                "```ts\n" +
                "import { createWebHashHistory, createRouter, type RouteRecordRaw } from 'vue-router';\n" +
                "```\n" +
                "\n" +
                "- 当需要使用到vue-router中的useRoute和useRouter时，需要按如下模式来使用：\n" +
                "```vue\n" +
                "<script setup lang=\"ts\">\n" +
                "import { useRoute, useRouter } from 'vue-router'\n" +
                "\n" +
                "const router = useRouter()\n" +
                "const route = useRoute()\n" +
                "</script>\n" +
                "```\n" +
                "\n" +
                "- 自动检查删除ts中没有使用的语句\n" +
                "- 使用组合式来生成单文件组件，代码中不要使用jsx相关的语法糖，需要设置lang=ts，单文件组件参考如下：\n" +
                "\n" +
                "```vue\n" +
                "<template>\n" +
                "  <div class=\"book-item\">\n" +
                "    <div class=\"info\">\n" +
                "      <h2>{{ book.title }}</h2>\n" +
                "      <p>作者: {{ book.author }}</p>\n" +
                "      <p>出版社: {{ book.publisher }}</p>\n" +
                "      <p>价格: {{ book.price }}</p>\n" +
                "    </div>\n" +
                "    <div class=\"actions\">\n" +
                "      <div class=\"py-[10px]\">\n" +
                "        <van-button type=\"primary\" block :to=\"'/book/' + book.id\">编辑</van-button>\n" +
                "      </div>\n" +
                "      <div>\n" +
                "        <van-button type=\"danger\" block @click=\"handleDeleteBook\">删除</van-button>\n" +
                "      </div>\n" +
                "    </div>\n" +
                "  </div>\n" +
                "</template>\n" +
                "\n" +
                "<script setup lang=\"ts\">\n" +
                "import { computed } from 'vue'\n" +
                "import { showConfirmDialog, showNotify } from 'vant'\n" +
                "import { deleteBook } from '@/api/book.ts'\n" +
                "\n" +
                "const props = defineProps<{\n" +
                "  book: {\n" +
                "    id: string\n" +
                "    title: string\n" +
                "    publisher: string\n" +
                "    price: number\n" +
                "  }\n" +
                "}>()\n" +
                "\n" +
                "const book = computed(() => {\n" +
                "  return props.book\n" +
                "})\n" +
                "\n" +
                "const handleDeleteBook = async () => {\n" +
                "  showConfirmDialog({\n" +
                "    title: '删除',\n" +
                "    message: '确定删除这本书吗？'\n" +
                "  })\n" +
                "    .then(async () => {\n" +
                "      await deleteBook(book.value.id)\n" +
                "      showNotify({ type: 'success', message: '删除成功' })\n" +
                "    })\n" +
                "    .catch(() => {\n" +
                "      showNotify({ type: 'warning', message: '删除取消' })\n" +
                "    })\n" +
                "}\n" +
                "</script>\n" +
                "\n" +
                "<style scoped>\n" +
                ".book-item {\n" +
                "  border: 1px solid #ccc;\n" +
                "  padding: 10px;\n" +
                "  margin-bottom: 10px;\n" +
                "}\n" +
                ".info h2 {\n" +
                "  margin: 0;\n" +
                "  color: #333;\n" +
                "}\n" +
                ".info p {\n" +
                "  color: #666;\n" +
                "  font-size: 0.9em;\n" +
                "}\n" +
                ".actions {\n" +
                "  margin-top: 10px;\n" +
                "}\n" +
                "</style>\n" +
                "```\n" +
                "\n" +
                "- 不要忘记通过props传递的参数在组件中使用defineProps接收。\n" +
                "- 必须符合移动端的体验, 特别是安卓和IOS等手机中的体验，注意不是pc端的体验，尽量避免使用table。\n" +
                "- 单文件组件中只允许使用vant提供的组件，不满足需求时开发新组件。\n" +
                "- 需要开发新通用组件，新组件放到src/components目录中。\n" +
                "- 对于vant中的showNotify等组件需要明确的使用import语句。\n" +
                "- 注意不要使用element-ui和element-plus等element相关组件库，否则会发生不好的事情。\n" +
                "- 样式尽量使用Tailwind CSS来创建。\n" +
                "- 密切关注背景颜色、文字颜色、字体大小、字体家族、内边距、外边距、边框等。精确匹配颜色和尺寸。\n" +
                "- 编写完整的代码，不使用注释代替，否则会发生不好的事情。\n" +
                "- 根据需求写重复和相似的代码。例如，需求有5个模块就写5个模块的代码。不要留下像“以上代码提供了图书信息管理的基本组件和API请求函数。其他功能如借阅管理、归还管理、读者管理和系统管理可以按照类似的结构进行开发。”这样的注释，否则会发生不好的事情。\n" +
                "- 接口描述使用axios来实现，根据接口描述信息适当的把接口实现拆分到src/api中，并通过export暴露出来，方便单文件组件调用。\n" +
                "- 如果需求没有找到合适的接口描述信息，可以适当添加接口到src/api中\n" +
                "- 接口返回值是标准的JSON格式，接口实现可参考如下：\n" +
                "```ts\n" +
                "import axios from 'axios'\n" +
                "import { showNotify } from 'vant'\n" +
                "\n" +
                "export const fetchBooksList = async function () {\n" +
                "  try {\n" +
                "    const data = await (await axios.get('http://127.0.0.1:8080/books')).data\n" +
                "    if (data) {\n" +
                "      return data\n" +
                "    } else {\n" +
                "      showNotify({ type: 'danger', message: '请求出错啦' })\n" +
                "    }\n" +
                "  } catch (e) {\n" +
                "    console.error(e)\n" +
                "    showNotify({ type: 'danger', message: JSON.stringify(e) });\n" +
                "  }\n" +
                "}\n" +
                "```\n" +
                "\n" +
                "- 已经安装了vue3，vue-router，axios，vant，Tailwind，TypeScript和vite相关的依赖。\n" +
                "- vue使用的是3的最新版本。\n" +
                "- vant使用的是4以上的最新版本, 其提供所有组件demo示例链接如下:\n" +
                "```md\n" +
                "[Button](https://www.w3cschool.cn/pcauz/pcauz-x1qd3qga.html)\n" +
                "[Cell](https://www.w3cschool.cn/pcauz/pcauz-36cp3qgb.html)\n" +
                "[ConfigProvider](https://www.w3cschool.cn/pcauz/pcauz-edsn3qgc.html)\n" +
                "[Icon](https://www.w3cschool.cn/pcauz/pcauz-912j3qgd.html)\n" +
                "[Image](https://www.w3cschool.cn/pcauz/pcauz-9jtn3qge.html)\n" +
                "[Layout](https://www.w3cschool.cn/pcauz/pcauz-8d7g3qgf.html)\n" +
                "[Popupu](https://www.w3cschool.cn/pcauz/pcauz-5c2f3qgg.html)\n" +
                "[Space](https://www.w3cschool.cn/pcauz/pcauz-nqui3qgh.html)\n" +
                "[Style](https://www.w3cschool.cn/pcauz/pcauz-mx4p3qgi.html)\n" +
                "[Toast](https://www.w3cschool.cn/pcauz/pcauz-8zry3qgj.html)\n" +
                "[Calendar](https://www.w3cschool.cn/pcauz/pcauz-nfty3qgl.html)\n" +
                "[Cascader](https://www.w3cschool.cn/pcauz/pcauz-5vhe3qgm.html)\n" +
                "[Checkbox](https://www.w3cschool.cn/pcauz/pcauz-b1ny3qgn.html)\n" +
                "[DatePicker](https://www.w3cschool.cn/pcauz/pcauz-xm5t3qgo.html)\n" +
                "[Field](https://www.w3cschool.cn/pcauz/pcauz-439z3qgp.html)\n" +
                "[Form](https://www.w3cschool.cn/pcauz/pcauz-6w9x3qgq.html)\n" +
                "[NumberKeyboard](https://www.w3cschool.cn/pcauz/pcauz-yonc3qgr.html)\n" +
                "[PasswordInput](https://www.w3cschool.cn/pcauz/pcauz-fm9n3qgs.html)\n" +
                "[Picker](https://www.w3cschool.cn/pcauz/pcauz-dwni3qgt.html)\n" +
                "[PickerGroup](https://www.w3cschool.cn/pcauz/pcauz-wqav3qgu.html)\n" +
                "[Radio](https://www.w3cschool.cn/pcauz/pcauz-pzlc3qgv.html)\n" +
                "[Rate](https://www.w3cschool.cn/pcauz/pcauz-tm653qgw.html)\n" +
                "[Search](https://www.w3cschool.cn/pcauz/pcauz-7cld3qgx.html)\n" +
                "[Slider](https://www.w3cschool.cn/pcauz/pcauz-w1ov3qgy.html)\n" +
                "[Stepper](https://www.w3cschool.cn/pcauz/pcauz-jmux3qgz.html)\n" +
                "[Switch](https://www.w3cschool.cn/pcauz/pcauz-zubp3qh0.html)\n" +
                "[TimePicker](https://www.w3cschool.cn/pcauz/pcauz-tkgi3qh1.html)\n" +
                "[Uploader](https://www.w3cschool.cn/pcauz/pcauz-yj5a3qh2.html)\n" +
                "[ActionSheet](https://www.w3cschool.cn/pcauz/pcauz-9unx3qh4.html)\n" +
                "[Dialog](https://www.w3cschool.cn/pcauz/pcauz-etqz3qh5.html)\n" +
                "[DropdownMenu](https://www.w3cschool.cn/pcauz/pcauz-fegk3qh6.html)\n" +
                "[Loading](https://www.w3cschool.cn/pcauz/pcauz-26tv3qh7.html)\n" +
                "[Notify](https://www.w3cschool.cn/pcauz/pcauz-4ldh3qh8.html)\n" +
                "[Overlay](https://www.w3cschool.cn/pcauz/pcauz-kv3l3qh9.html)\n" +
                "[PullRefresh](https://www.w3cschool.cn/pcauz/pcauz-r2bp3qha.html)\n" +
                "[ShareSheet](https://www.w3cschool.cn/pcauz/pcauz-jbuo3qhb.html)\n" +
                "[SwipeCell](https://www.w3cschool.cn/pcauz/pcauz-g56r3qhc.html)\n" +
                "[Badge](https://www.w3cschool.cn/pcauz/pcauz-f58z3qhe.html)\n" +
                "[Circle](https://www.w3cschool.cn/pcauz/pcauz-4uke3qhf.html)\n" +
                "[Collapse](https://www.w3cschool.cn/pcauz/pcauz-kvbq3qhg.html)\n" +
                "[CountDown](https://www.w3cschool.cn/pcauz/pcauz-up7x3qhh.html)\n" +
                "[Divider](https://www.w3cschool.cn/pcauz/pcauz-3iye3qhi.html)\n" +
                "[Empty](https://www.w3cschool.cn/pcauz/pcauz-wkuo3qhj.html)\n" +
                "[ImagePreview](https://www.w3cschool.cn/pcauz/pcauz-9plc3qhk.html)\n" +
                "[Lazyload](https://www.w3cschool.cn/pcauz/pcauz-3xdp3qhl.html)\n" +
                "[List](https://www.w3cschool.cn/pcauz/pcauz-xfan3qhm.html)\n" +
                "[NoticeBar](https://www.w3cschool.cn/pcauz/pcauz-8qnc3qhn.html)\n" +
                "[Popover](https://www.w3cschool.cn/pcauz/pcauz-6dyo3qho.html)\n" +
                "[Progress](https://www.w3cschool.cn/pcauz/pcauz-1gmz3qhp.html)\n" +
                "[Skeleton](https://www.w3cschool.cn/pcauz/pcauz-bowc3qhq.html)\n" +
                "[Steps](https://www.w3cschool.cn/pcauz/pcauz-yosi3qhr.html)\n" +
                "[Sticky](https://www.w3cschool.cn/pcauz/pcauz-v57r3qhs.html)\n" +
                "[Swipe](https://www.w3cschool.cn/pcauz/pcauz-kg9y3qht.html)\n" +
                "[Tag](https://www.w3cschool.cn/pcauz/pcauz-dogv3qhu.html)\n" +
                "[ActionBar](https://www.w3cschool.cn/pcauz/pcauz-m3jr3qhw.html)\n" +
                "[Grid](https://www.w3cschool.cn/pcauz/pcauz-9fzr3qhx.html)\n" +
                "[IndexBar](https://www.w3cschool.cn/pcauz/pcauz-bthl3qhy.html)\n" +
                "[NavBar](https://www.w3cschool.cn/pcauz/pcauz-g6cv3qhz.html)\n" +
                "[Pagination](https://www.w3cschool.cn/pcauz/pcauz-xi753qi0.html)\n" +
                "[Sidebar](https://www.w3cschool.cn/pcauz/pcauz-hrim3qi1.html)\n" +
                "[Tab](https://www.w3cschool.cn/pcauz/pcauz-ycno3qi2.html)\n" +
                "[Tabbar](https://www.w3cschool.cn/pcauz/pcauz-5qo93qi3.html)\n" +
                "[TreeSelect](https://www.w3cschool.cn/pcauz/pcauz-ygtr3qi4.html)\n" +
                "[BackTop](https://www.w3cschool.cn/pcauz/pcauz-jqz73qi5.html)\n" +
                "[AddressEdit](https://www.w3cschool.cn/pcauz/pcauz-ermh3qi7.html)\n" +
                "[AddressList](https://www.w3cschool.cn/pcauz/pcauz-mz5y3qi8.html)\n" +
                "[Area](https://www.w3cschool.cn/pcauz/pcauz-4rwh3qi9.html)\n" +
                "[Card](https://www.w3cschool.cn/pcauz/pcauz-ws8m3qia.html)\n" +
                "[ContactCard](https://www.w3cschool.cn/pcauz/pcauz-tvn63qib.html)\n" +
                "[ContactEdit](https://www.w3cschool.cn/pcauz/pcauz-5y9n3qic.html)\n" +
                "[ContactList](https://www.w3cschool.cn/pcauz/pcauz-16vx3qid.html)\n" +
                "[Coupon](https://www.w3cschool.cn/pcauz/pcauz-qbcw3qie.html)\n" +
                "[SubmitBar](https://www.w3cschool.cn/pcauz/pcauz-ekwg3qif.html)\n" +
                "```\n" +
                "\n" +
                "- 返回结果以JSON数组形式给出，数组中元素是包含两个字段对象：path和content：\n" +
                "```json\n" +
                "[\n" +
                "  {\n" +
                "    \"path\": \"src/pages/Books.vue\",\n" +
                "    \"content\": \"代码内容\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"path\": \"src/pages/BooAdd.vue\",\n" +
                "    \"content\": \"代码内容\"\n" +
                "  },\n" +
                "]\n" +
                "```\n" +
                "- 注意必须按上述格式组织，content字段必须只包含代码，path字段必须是代码完整路径。\n" +
                "- 不需要额外的解释描述！\n" +
                "\n" +
                "需求文档：\n" +
                "${frontend_design_doc}\n" +
                "\n接口相关信息：\n" +
                "```json\n" +
                "${api_doc}\n" +
                "```\n" +
                "\n" +
                "请按要求生成指定的json格式返回\n";

        // String miapi_project_url = "https://X.com/miapi/#/api?projectID=123453";
        int projectId = 123453;
        int pageNum = 1;
        int pageSize = 100;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
        String currentTime = sdf.format(new Date());
        String projectName = "books-system-front" + currentTime;
        System.out.println("goto generate:" + projectName);
        // 创建一个BotReq对象并初始化它的属性
        BotReq mockReq = BotReq.builder()
                .flowDataList(Lists.newArrayList(
                        FlowData.builder().id(0).type("begin").name("开始").inputMap(new ConcurrentHashMap<>(
                                ImmutableMap.of(
                                        "requirement_doc", InputData.builder().value(new JsonPrimitive(requirement_doc)).build(),
                                        "projectName", InputData.builder().value(new JsonPrimitive(projectName)).build(),
                                        "projectId", InputData.builder().value(new JsonPrimitive(projectId)).build(),
                                        "pageNum", InputData.builder().value(new JsonPrimitive(pageNum)).build(),
                                        "pageSize", InputData.builder().value(new JsonPrimitive(pageSize)).build()
                                ))).build(),

                        // 调用plugin 获取接口 todo @yingjie
                        FlowData.builder().id(1).type("plugin").name("MiApi信息获取")
                                .inputMap(new ConcurrentHashMap<>(ImmutableMap.of(
                                        "projectId", InputData.builder().type("reference").name("projectId").referenceName("projectId").flowId(0).build(),
                                        "pageNum", InputData.builder().type("reference").name("pageNum").referenceName("projectId").flowId(0).build(),
                                        "pageSize", InputData.builder().type("reference").name("pageSize").referenceName("projectId").flowId(0).build(),
                                        CommonConstants.TY_PLUGIN_ID_MARK, InputData.builder().value(new JsonPrimitive(92)).build()
                                )))
                                .outputMap(new ConcurrentHashMap<>(ImmutableMap.of(
                                        "data", OutputData.builder().build()
                                )))
                                .build(),

                        // 大模型 UI相关详细描述 @qingfu
                        FlowData.builder().id(2).type("llm").name("大模型（设计）")
                                .inputMap(new ConcurrentHashMap<>(ImmutableMap.of(
                                        CommonConstants.TY_LLM_MODEL_MARK, InputData.builder().value(new JsonPrimitive("moonshot")).build(),
                                        CommonConstants.TY_LLM_PROMPT_MARK, InputData.builder().value(new JsonPrimitive(frontend_design_prompt)).build(),
                                        CommonConstants.TY_LLM_USE_CACHE, InputData.builder().value(new JsonPrimitive("false")).build(),
                                        "requirement_doc", InputData.builder().type("reference").name("requirement_doc").referenceName("requirement_doc").flowId(0).build()
                                )))
                                .outputMap(new ConcurrentHashMap<>(ImmutableMap.of(
                                        "frontend_design_doc", OutputData.builder().build()
                                )))
                                .build(),

                        // 前端代码生成 todo @qingfu
                        FlowData.builder().id(3).type("llm").name("大模型（生成代码）")
                                .inputMap(new ConcurrentHashMap<>(ImmutableMap.of(
                                        CommonConstants.TY_LLM_MODEL_MARK, InputData.builder().value(new JsonPrimitive("moonshot")).build(),
                                        CommonConstants.TY_LLM_PROMPT_MARK, InputData.builder().value(new JsonPrimitive(frontend_code_prompt)).build(),
                                        CommonConstants.TY_LLM_USE_CACHE, InputData.builder().value(new JsonPrimitive("false")).build(),
                                        "api_doc", InputData.builder().type("reference").name("api_doc").referenceName("data").flowId(1).build(),
                                        "frontend_design_doc", InputData.builder().type("reference").name("frontend_design_doc").referenceName("requirement_doc").flowId(0).build()
                                )))
                                .outputMap(new ConcurrentHashMap<>(ImmutableMap.of(
                                        "front_code_detail", OutputData.builder().build()
                                )))
                                .build(),

                        //合并前端 代码 todo @yingjie
                        FlowData.builder().id(4).type("plugin").name("前端代码")
                                .inputMap(new ConcurrentHashMap<>(ImmutableMap.of(
                                        CommonConstants.TY_PLUGIN_ID_MARK, InputData.builder().value(new JsonPrimitive(94)).build(),
                                        "projectName", InputData.builder().type("reference").name("projectName").referenceName("projectName").flowId(0).build(),
                                        "source", InputData.builder().type("reference").name("front_code_detail").referenceName("front_code_detail").flowId(2).build()
                                )))
                                .outputMap(new ConcurrentHashMap<>(ImmutableMap.of(
                                        "data", OutputData.builder().build()
                                )))
                                .build(),

                        FlowData.builder().id(77).type("end").name("结束")
                                .inputMap(new ConcurrentHashMap<>(ImmutableMap.of(
                                        CommonConstants.TY_END_MESSAGE_CONTENT_MARK, InputData.builder().value(new JsonPrimitive("${output}")).build()
                                )))
                                .outputMap(new ConcurrentHashMap<>(ImmutableMap.of(
                                        "output", OutputData.builder().type("reference").name("data").referenceName("data").flowId(3).build()
                                )))
                                .build()
                ))
                .nodeEdges(new ArrayList<>(Arrays.asList(
                        NodeEdge.builder().sourceNodeId(0).targetNodeId(1).build(),
                        NodeEdge.builder().sourceNodeId(1).targetNodeId(2).build(),
                        NodeEdge.builder().sourceNodeId(2).targetNodeId(3).build(),
                        NodeEdge.builder().sourceNodeId(3).targetNodeId(4).build(),
                        NodeEdge.builder().sourceNodeId(4).targetNodeId(77).build())
                ))
                .build();

        // 调用execute方法
        EndFlowRes result = botService.execute(mockReq);
        System.out.println(result);
    }

    @Test
    public void testAutoAppController() {
        String requirement_doc = "{\"项目名称\":\"图书管理系统\",\"背景\":\"本项目旨在开发一个图书管理系统，用以提高图书馆的管理效率，方便读者查找、借阅和归还图书，同时也为图书馆工作人员提供一个高效的图书管理平台。\",\"用户群体\":\"主要用户群体包括图书馆管理员、图书馆工作人员以及读者。\",\"核心功能\":{\"1.图书信息管理\":\"系统应能够录入、修改和删除图书信息，包括书名、作者、出版社、ISBN号、位置等信息。\",\"2.借阅管理\":\"系统应支持读者的借阅操作，包括借书和还书，同时能够跟踪图书的借阅状态和历史记录。\",\"3.用户管理\":\"系统应能够管理用户信息，包括读者和图书馆工作人员的注册、登录、信息修改等。\",\"4.搜索功能\":\"系统应提供强大的搜索功能，允许用户通过书名、作者、ISBN等信息快速找到所需图书。\",\"5.逾期罚款处理\":\"系统应自动计算逾期图书的罚款，并提供罚款支付的功能。\"},\"非功能性需求\":{\"性能要求\":\"系统应能支持至少100名活跃用户同时在线操作。\",\"安全性要求\":\"系统应实现用户身份验证和授权，保障数据安全。\"}";
        String func_point = "{\"ProjectName\":\"图书管理系统\",\"Background\":\"随着数字化时代的到来，图书管理的需求日益增长。本项目旨在开发一个图书管理系统，以便于图书馆等机构能够更高效地管理图书资料，提供更好的借阅服务，同时也方便读者查找和借阅图书。\",\"UserGroups\":[\"图书管理员\",\"读者\",\"图书馆管理人员\"],\"CoreFunctions\":{\"BookEntry\":{\"AddNewBook\":[\"Name\",\"Author\",\"ISBN\",\"Publisher\",\"PublicationDate\",\"Price\"]},\"BookBorrowing\":{\"SearchAndBorrow\":true,\"RecordBorrowingInfo\":true,\"UpdateBookStatus\":true},\"BookReturning\":{\"RecordReturningInfo\":true,\"UpdateBookStatus\":true},\"OverdueHandling\":{\"OverdueDetection\":true,\"Reminder\":true,\"FineCalculation\":true},\"UserManagement\":{\"ManageReaderRegistration\":true,\"EditReaderInfo\":true,\"QueryBorrowingHistory\":true},\"Reporting\":{\"GenerateReports\":[\"Borrowing\",\"Returning\",\"Inventory\"]}},\"NonFunctionalRequirements\":{\"Performance\":\"The system should support at least 100 active users online simultaneously.\",\"Security\":\"The system should implement user authentication and authorization to ensure data security.\"}}";
        String code_meta = "[{\"type\":\"MongoDBConfig\",\"packageName\":\"run.mone.mongodb\",\"name\":\"MongoDBConfig\",\"content\":\"package run.mone.mongodb;\\n\\nimport org.springframework.beans.factory.annotation.Value;\\nimport org.springframework.context.annotation.Bean;\\nimport org.springframework.context.annotation.Configuration;\\nimport dev.morphia.Morphia;\\nimport com.mongodb.MongoClient;\\nimport com.mongodb.MongoClientURI;\\n\\n@Configuration\\npublic class MongoDBConfig {\\n\\n    @Value(\\\"${uri}\\\")\\n    private String uri;\\n\\n    @Value(\\\"${dbName}\\\")\\n    private String dbName;\\n\\n    @Bean\\n    public Morphia morphia() {\\n        return new Morphia();\\n    }\\n\\n    @Bean\\n    public MongoClient mongoClient() {\\n        return new MongoClient(new MongoClientURI(uri));\\n    }\\n\\n    @Bean\\n    public Datastore datastore() {\\n        return morphia().createDatastore(mongoClient(), dbName);\\n    }\\n}\"},{\"type\":\"PO\",\"packageName\":\"run.mone.model\",\"name\":\"Book\",\"content\":\"package run.mone.model;\\n\\nimport lombok.Data;\\nimport dev.morphia.annotations.Entity;\\nimport dev.morphia.annotations.Id;\\nimport org.bson.types.ObjectId;\\n\\n@Data\\n@Entity(\\\"books\\\")\\npublic class Book {\\n\\n    @Id\\n    private ObjectId id;\\n    private String title;\\n    private String author;\\n    private String isbn;\\n    private String category;\\n    private boolean isBorrowed;\\n    private String borrowerId;\\n}\"},{\"type\":\"PO\",\"packageName\":\"run.mone.model\",\"name\":\"User\",\"content\":\"package run.mone.model;\\n\\nimport lombok.Data;\\nimport dev.morphia.annotations.Entity;\\nimport dev.morphia.annotations.Id;\\nimport org.bson.types.ObjectId;\\n\\n@Data\\n@Entity(\\\"users\\\")\\npublic class User {\\n\\n    @Id\\n    private ObjectId id;\\n    private String username;\\n    private String password;\\n    private String role;\\n}\"},{\"type\":\"Mapper\",\"packageName\":\"run.mone.repository\",\"name\":\"BookRepository\",\"content\":\"package run.mone.repository;\\n\\nimport run.mone.model.Book;\\nimport java.util.List;\\n\\npublic interface BookRepository {\\n\\n    Book findById(String id);\\n    List<Book> findAll();\\n    Book save(Book book);\\n    void delete(Book book);\\n}\"},{\"type\":\"Mapper\",\"packageName\":\"run.mone.repository\",\"name\":\"UserRepository\",\"content\":\"package run.mone.repository;\\n\\nimport run.mone.model.User;\\nimport java.util.List;\\n\\npublic interface UserRepository {\\n\\n    User findById(String id);\\n    List<User> findAll();\\n    User save(User user);\\n    void delete(User user);\\n}\"},{\"type\":\"MapperImpl\",\"packageName\":\"run.mone.repository\",\"name\":\"BookRepositoryImpl\",\"content\":\"package run.mone.repository;\\n\\nimport org.springframework.beans.factory.annotation.Autowired;\\nimport org.springframework.stereotype.Repository;\\nimport run.mone.model.Book;\\nimport dev.morphia.Datastore;\\nimport java.util.List;\\n\\n@Repository\\npublic class BookRepositoryImpl implements BookRepository {\\n\\n    @Autowired\\n    private Datastore datastore;\\n\\n    @Override\\n    public Book findById(String id) {\\n        return datastore.get(Book.class, new ObjectId(id));\\n    }\\n\\n    @Override\\n    public List<Book> findAll() {\\n        return datastore.createQuery(Book.class).asList();\\n    }\\n\\n    @Override\\n    public Book save(Book book) {\\n        datastore.save(book);\\n        return book;\\n    }\\n\\n    @Override\\n    public void delete(Book book) {\\n        datastore.delete(book);\\n    }\\n}\"},{\"type\":\"MapperImpl\",\"packageName\":\"run.mone.repository\",\"name\":\"UserRepositoryImpl\",\"content\":\"package run.mone.repository;\\n\\nimport org.springframework.beans.factory.annotation.Autowired;\\nimport org.springframework.stereotype.Repository;\\nimport run.mone.model.User;\\nimport dev.morphia.Datastore;\\nimport java.util.List;\\n\\n@Repository\\npublic class UserRepositoryImpl implements UserRepository {\\n\\n    @Autowired\\n    private Datastore datastore;\\n\\n    @Override\\n    public User findById(String id) {\\n        return datastore.get(User.class, new ObjectId(id));\\n    }\\n\\n    @Override\\n    public List<User> findAll() {\\n        return datastore.createQuery(User.class).asList();\\n    }\\n\\n    @Override\\n    public User save(User user) {\\n        datastore.save(user);\\n        return user;\\n    }\\n\\n    @Override\\n    public void delete(User user) {\\n        datastore.delete(user);\\n    }\\n}\"}]";
        //String code_meta = "[{\\\"type\\\":\\\"PO\\\",\\\"packageName\\\":\\\"run.mone.m78\\\",\\\"name\\\":\\\"Book\\\",\\\"content\\\":\\\"package run.mone.m78;\\\\n\\\\nimport lombok.Data;\\\\n\\\\n@Data\\\\npublic class Book {\\\\n\\\\n    private Integer id;\\\\n    private String title;\\\\n    private String author;\\\\n    private String isbn;\\\\n    private Integer categoryId;\\\\n    private Long price;\\\\n    private Integer status;\\\\n    private Long createdAt;\\\\n    private Long updatedAt;\\\\n\\\\n}\\\"},{\\\"type\\\":\\\"Mapper\\\",\\\"packageName\\\":\\\"run.mone.m78\\\",\\\"name\\\":\\\"BookMapper\\\",\\\"content\\\":\\\"package run.mone.m78;\\\\n\\\\nimport org.apache.ibatis.annotations.Mapper;\\\\nimport org.apache.ibatis.annotations.Param;\\\\nimport java.util.List;\\\\n\\\\n@Mapper\\\\npublic interface BookMapper {\\\\n\\\\n    Book selectByPrimaryKey(@Param(\\\\\\\"id\\\\\\\") Integer id);\\\\n\\\\n    int deleteByPrimaryKey(@Param(\\\\\\\"id\\\\\\\") Integer id);\\\\n\\\\n    int insert(Book record);\\\\n\\\\n    int insertSelective(Book record);\\\\n\\\\n    int updateByPrimaryKeySelective(Book record);\\\\n\\\\n    int updateByPrimaryKey(Book record);\\\\n\\\\n    List\\ selectAll();\\\\n\\\\n}\\\"},{\\\"type\\\":\\\"Xml\\\",\\\"packageName\\\":\\\"\\\",\\\"name\\\":\\\"bookMapper\\\",\\\"content\\\":\\\"\\\\\\n\\\\\\n\\\\n    \\\\\\n        \\\\\\n        \\\\\\n        \\\\\\n        \\\\\\n        \\\\\\n        \\\\\\n        \\\\\\n        \\\\\\n        \\\\\\n    \\\\\\n\\\\n    \\\\\\n        INSERT INTO book (title, author, isbn, category_id, price, status, created_at, updated_at)\\\\n        VALUES (#{title,jdbcType\\=VARCHAR}, #{author,jdbcType\\=VARCHAR}, #{isbn,jdbcType\\=VARCHAR}, #{categoryId,jdbcType\\=INTEGER}, #{price,jdbcType\\=BIGINT}, #{status,jdbcType\\=INTEGER}, #{createdAt,jdbcType\\=BIGINT}, #{updatedAt,jdbcType\\=BIGINT})\\\\n    \\\\\\n\\\\n    \\\\\\n        INSERT INTO book\\\\n        \\\\\\n            \\\\\\n                title,\\\\n            \\\\\\n            \\\\\\n                author,\\\\n            \\\\\\n            \\\\\\n                isbn,\\\\n            \\\\\\n            \\\\\\n                category_id,\\\\n            \\\\\\n            \\\\\\n                price,\\\\n            \\\\\\n            \\\\\\n                status,\\\\n            \\\\\\n            \\\\\\n                created_at,\\\\n            \\\\\\n            \\\\\\n                updated_at,\\\\n            \\\\\\n        \\\\\\n        \\\\\\n            \\\\\\n                #{title,jdbcType\\=VARCHAR},\\\\n            \\\\\\n            \\\\\\n                #{author,jdbcType\\=VARCHAR},\\\\n            \\\\\\n            \\\\\\n                #{isbn,jdbcType\\=VARCHAR},\\\\n            \\\\\\n            \\\\\\n                #{categoryId,jdbcType\\=INTEGER},\\\\n            \\\\\\n            \\\\\\n                #{price,jdbcType\\=BIGINT},\\\\n            \\\\\\n            \\\\\\n                #{status,jdbcType\\=INTEGER},\\\\n            \\\\\\n            \\\\\\n                #{createdAt,jdbcType\\=BIGINT},\\\\n            \\\\\\n            \\\\\\n                #{updatedAt,jdbcType\\=BIGINT},\\\\n            \\\\\\n        \\\\\\n    \\\\\\n\\\\n    \\\\\\n        UPDATE book\\\\n        \\\\\\n            \\\\\\n                title \\= #{title,jdbcType\\=VARCHAR},\\\\n            \\\\\\n            \\\\\\n                author \\= #{author,jdbcType\\=VARCHAR},\\\\n            \\\\\\n            \\\\\\n                isbn \\= #{isbn,jdbcType\\=VARCHAR},\\\\n            \\\\\\n            \\\\\\n                category_id \\= #{categoryId,jdbcType\\=INTEGER},\\\\n            \\\\\\n            \\\\\\n                price \\= #{price,jdbcType\\=BIGINT},\\\\n            \\\\\\n            \\\\\\n                status \\= #{status,jdbcType\\=INTEGER},\\\\n            \\\\\\n            \\\\\\n                created_at \\= #{createdAt,jdbcType\\=BIGINT},\\\\n            \\\\\\n            \\\\\\n                updated_at \\= #{updatedAt,jdbcType\\=BIGINT},\\\\n            \\\\\\n        \\\\\\n        WHERE id \\= #{id,jdbcType\\=INTEGER}\\\\n    \\\\\\n\\\\n    \\\\\\n        SELECT * FROM book WHERE id \\= #{id,jdbcType\\=INTEGER}\\\\n    \\\\\\n\\\\n    \\\\\\n        DELETE FROM book WHERE id \\= #{id,jdbcType\\=INTEGER}\\\\n    \\\\\\n\\\\n    \\\\\\n        SELECT * FROM book\\\\n    \\\\\\n\\\\n\\\\\"}]";
        String prompt = "你是一名资深的Java软件工程师，我有一个springboot工程，我需要你帮我完成一些代码。\n" +
                "我会给你一份需求文档、一份关于该需求文档的json格式的需求清单、一份关于该系统的数据库操作的实体类和Mapper类清单，请你根据我提供的内容和我需要实现的需求，生成相关springboot controller层代码\n" +
                "\n" +
                "要求：\n" +
                "1. 使用Java8的语法，代码尽可能简单\n" +
                "2. 你只需要返回代码块和代码注释，不要有任何的其他多余的解释\n" +
                "3. 生成的代码简洁易懂\n" +
                "4. 你的返回应该是一个List<Controller>的json。这个List里边的内容应该是：\n" +
                "\n" +
                "[\n" +
                "    {\n" +
                "        \"type\": \"controller\",\n" +
                "        \"packageName\": \"run.mone.m78.controller\",\n" +
                "        \"name\": \"BookController\",\n" +
                "        \"content\":\"类的完整代码 包括package import等元数据信息\"\n" +
                "\n" +
                "    }\n" +
                "]\n" +
                "需求文档：\n" +
                "\n" +
                "${requirement_doc}\n" +
                "\n" +
                "\n" +
                "功能清单：\n" +
                "${func_point}\n" +
                "\n" +
                "PO和mapper信息：\n" +
                "${code_meta}\n" +
                "\n" +
                "\n" +
                "请你返回";

        String prompt1 = "你是一名资深的Java软件工程师，我有一个springboot工程，我需要你帮我完成一些代码。\n" +
                "我会给你一份需求文档、一份关于该需求文档的json格式的需求清单、一份关于该系统的数据库操作的实体类和Mapper类清单，请你根据我提供的内容和我需要实现的需求，生成相关springboot controller层代码\n" +
                "\n" +
                "要求：\n" +
                "1. 使用Java8的语法，代码尽可能简单\n" +
                "2. 你只需要返回代码块和代码注释，不要有任何的其他多余的解释\n" +
                "3. 生成的代码简洁易懂,但是不要省略任何的需求\n" +
                "4. 你的返回应该是一个List<Controller>的json。这个List里边的内容应该是：\n" +
                "\n" +
                "[\n" +
                "    {\n" +
                "        \"packageName\": \"run.mone.m78.controller\",\n" +
                "        \"name\": \"BookController\",\n" +
                "        \"content\":\"类的完整代码 包括package import等元数据信息\"\n" +
                "\n" +
                "    }\n" +
                "]\n" +
                "需求文档：\n" +
                "\n" +
                "{\"项目名称\":\"图书管理系统\",\"背景\":\"本项目旨在开发一个图书管理系统，用以提高图书馆的管理效率，方便读者查找、借阅和归还图书，同时也为图书馆工作人员提供一个高效的图书管理平台。\",\"用户群体\":\"主要用户群体包括图书馆管理员、图书馆工作人员以及读者。\",\"核心功能\":{\"1.图书信息管理\":\"系统应能够录入、修改和删除图书信息，包括书名、作者、出版社、ISBN号、位置等信息。\",\"2.借阅管理\":\"系统应支持读者的借阅操作，包括借书和还书，同时能够跟踪图书的借阅状态和历史记录。\",\"3.用户管理\":\"系统应能够管理用户信息，包括读者和图书馆工作人员的注册、登录、信息修改等。\",\"4.搜索功能\":\"系统应提供强大的搜索功能，允许用户通过书名、作者、ISBN等信息快速找到所需图书。\",\"5.逾期罚款处理\":\"系统应自动计算逾期图书的罚款，并提供罚款支付的功能。\"},\"非功能性需求\":{\"性能要求\":\"系统应能支持至少100名活跃用户同时在线操作。\",\"安全性要求\":\"系统应实现用户身份验证和授权，保障数据安全。\"}\n" +
                "\n" +
                "\n" +
                "功能清单：\n" +
                "{\"ProjectName\":\"图书管理系统\",\"Background\":\"随着数字化时代的到来，图书管理的需求日益增长。本项目旨在开发一个图书管理系统，以便于图书馆等机构能够更高效地管理图书资料，提供更好的借阅服务，同时也方便读者查找和借阅图书。\",\"UserGroups\":[\"图书管理员\",\"读者\",\"图书馆管理人员\"],\"CoreFunctions\":{\"BookEntry\":{\"AddNewBook\":[\"Name\",\"Author\",\"ISBN\",\"Publisher\",\"PublicationDate\",\"Price\"]},\"BookBorrowing\":{\"SearchAndBorrow\":true,\"RecordBorrowingInfo\":true,\"UpdateBookStatus\":true},\"BookReturning\":{\"RecordReturningInfo\":true,\"UpdateBookStatus\":true},\"OverdueHandling\":{\"OverdueDetection\":true,\"Reminder\":true,\"FineCalculation\":true},\"UserManagement\":{\"ManageReaderRegistration\":true,\"EditReaderInfo\":true,\"QueryBorrowingHistory\":true},\"Reporting\":{\"GenerateReports\":[\"Borrowing\",\"Returning\",\"Inventory\"]}},\"NonFunctionalRequirements\":{\"Performance\":\"The system should support at least 100 active users online simultaneously.\",\"Security\":\"The system should implement user authentication and authorization to ensure data security.\"}}\n" +
                "\n" +
                "PO和mapper信息：\n" +
                "[{\\\"type\\\":\\\"PO\\\",\\\"packageName\\\":\\\"run.mone.m78\\\",\\\"name\\\":\\\"Book\\\",\\\"content\\\":\\\"package run.mone.m78;\\\\n\\\\nimport lombok.Data;\\\\n\\\\n@Data\\\\npublic class Book {\\\\n\\\\n    private Integer id;\\\\n    private String title;\\\\n    private String author;\\\\n    private String isbn;\\\\n    private Integer categoryId;\\\\n    private Long price;\\\\n    private Integer status;\\\\n    private Long createdAt;\\\\n    private Long updatedAt;\\\\n\\\\n}\\\"},{\\\"type\\\":\\\"Mapper\\\",\\\"packageName\\\":\\\"run.mone.m78\\\",\\\"name\\\":\\\"BookMapper\\\",\\\"content\\\":\\\"package run.mone.m78;\\\\n\\\\nimport org.apache.ibatis.annotations.Mapper;\\\\nimport org.apache.ibatis.annotations.Param;\\\\nimport java.util.List;\\\\n\\\\n@Mapper\\\\npublic interface BookMapper {\\\\n\\\\n    Book selectByPrimaryKey(@Param(\\\\\\\"id\\\\\\\") Integer id);\\\\n\\\\n    int deleteByPrimaryKey(@Param(\\\\\\\"id\\\\\\\") Integer id);\\\\n\\\\n    int insert(Book record);\\\\n\\\\n    int insertSelective(Book record);\\\\n\\\\n    int updateByPrimaryKeySelective(Book record);\\\\n\\\\n    int updateByPrimaryKey(Book record);\\\\n\\\\n    List\\ selectAll();\\\\n\\\\n}\\\"},{\\\"type\\\":\\\"Xml\\\",\\\"packageName\\\":\\\"\\\",\\\"name\\\":\\\"bookMapper\\\",\\\"content\\\":\\\"\\\\\\n\\\\\\n\\\\n    \\\\\\n        \\\\\\n        \\\\\\n        \\\\\\n        \\\\\\n        \\\\\\n        \\\\\\n        \\\\\\n        \\\\\\n        \\\\\\n    \\\\\\n\\\\n    \\\\\\n        INSERT INTO book (title, author, isbn, category_id, price, status, created_at, updated_at)\\\\n        VALUES (#{title,jdbcType\\=VARCHAR}, #{author,jdbcType\\=VARCHAR}, #{isbn,jdbcType\\=VARCHAR}, #{categoryId,jdbcType\\=INTEGER}, #{price,jdbcType\\=BIGINT}, #{status,jdbcType\\=INTEGER}, #{createdAt,jdbcType\\=BIGINT}, #{updatedAt,jdbcType\\=BIGINT})\\\\n    \\\\\\n\\\\n    \\\\\\n        INSERT INTO book\\\\n        \\\\\\n            \\\\\\n                title,\\\\n            \\\\\\n            \\\\\\n                author,\\\\n            \\\\\\n            \\\\\\n                isbn,\\\\n            \\\\\\n            \\\\\\n                category_id,\\\\n            \\\\\\n            \\\\\\n                price,\\\\n            \\\\\\n            \\\\\\n                status,\\\\n            \\\\\\n            \\\\\\n                created_at,\\\\n            \\\\\\n            \\\\\\n                updated_at,\\\\n            \\\\\\n        \\\\\\n        \\\\\\n            \\\\\\n                #{title,jdbcType\\=VARCHAR},\\\\n            \\\\\\n            \\\\\\n                #{author,jdbcType\\=VARCHAR},\\\\n            \\\\\\n            \\\\\\n                #{isbn,jdbcType\\=VARCHAR},\\\\n            \\\\\\n            \\\\\\n                #{categoryId,jdbcType\\=INTEGER},\\\\n            \\\\\\n            \\\\\\n                #{price,jdbcType\\=BIGINT},\\\\n            \\\\\\n            \\\\\\n                #{status,jdbcType\\=INTEGER},\\\\n            \\\\\\n            \\\\\\n                #{createdAt,jdbcType\\=BIGINT},\\\\n            \\\\\\n            \\\\\\n                #{updatedAt,jdbcType\\=BIGINT},\\\\n            \\\\\\n        \\\\\\n    \\\\\\n\\\\n    \\\\\\n        UPDATE book\\\\n        \\\\\\n            \\\\\\n                title \\= #{title,jdbcType\\=VARCHAR},\\\\n            \\\\\\n            \\\\\\n                author \\= #{author,jdbcType\\=VARCHAR},\\\\n            \\\\\\n            \\\\\\n                isbn \\= #{isbn,jdbcType\\=VARCHAR},\\\\n            \\\\\\n            \\\\\\n                category_id \\= #{categoryId,jdbcType\\=INTEGER},\\\\n            \\\\\\n            \\\\\\n                price \\= #{price,jdbcType\\=BIGINT},\\\\n            \\\\\\n            \\\\\\n                status \\= #{status,jdbcType\\=INTEGER},\\\\n            \\\\\\n            \\\\\\n                created_at \\= #{createdAt,jdbcType\\=BIGINT},\\\\n            \\\\\\n            \\\\\\n                updated_at \\= #{updatedAt,jdbcType\\=BIGINT},\\\\n            \\\\\\n        \\\\\\n        WHERE id \\= #{id,jdbcType\\=INTEGER}\\\\n    \\\\\\n\\\\n    \\\\\\n        SELECT * FROM book WHERE id \\= #{id,jdbcType\\=INTEGER}\\\\n    \\\\\\n\\\\n    \\\\\\n        DELETE FROM book WHERE id \\= #{id,jdbcType\\=INTEGER}\\\\n    \\\\\\n\\\\n    \\\\\\n        SELECT * FROM book\\\\n    \\\\\\n\\\\n\\\\\"}]\n" +
                "\n" +
                "\n" +
                "请你返回";

        // 创建一个BotReq对象并初始化它的属性
        BotReq mockReq = BotReq.builder()
                .flowDataList(Lists.newArrayList(
                        FlowData.builder().id(0).type("begin").name("开始").inputMap(new ConcurrentHashMap<>(
                                ImmutableMap.of(
                                        "requirement_doc", InputData.builder().value(new JsonPrimitive(requirement_doc)).build(),
                                        "func_point", InputData.builder().value(new JsonPrimitive(func_point)).build(),
                                        "code_meta", InputData.builder().value(new JsonPrimitive(code_meta)).build()
                                ))).build(),

                        //需求分析
                        FlowData.builder().id(1).type("llm").name("大模型")
                                .inputMap(new ConcurrentHashMap<>(ImmutableMap.of(
                                        CommonConstants.TY_LLM_MODEL_MARK, InputData.builder().value(new JsonPrimitive("gpt4_1106_p")).build(),
                                        CommonConstants.TY_LLM_PROMPT_MARK, InputData.builder().value(new JsonPrimitive(prompt)).build(),
                                        "requirement_doc", InputData.builder().type("reference").name("requirement_doc").referenceName("requirement_doc").flowId(0).build(),
                                        "func_point", InputData.builder().type("reference").name("func_point").referenceName("func_point").flowId(0).build(),
                                        "code_meta", InputData.builder().type("reference").name("code_meta").referenceName("code_meta").flowId(0).build()
                                )))
                                .outputMap(new ConcurrentHashMap<>(ImmutableMap.of(
                                        "output", OutputData.builder().build()
                                )))
                                .build(),
                        FlowData.builder().id(2).type("end").name("结束")
                                .inputMap(new ConcurrentHashMap<>(ImmutableMap.of(
                                        CommonConstants.TY_END_MESSAGE_CONTENT_MARK, InputData.builder().value(new JsonPrimitive("${output}")).build()
                                )))
                                .outputMap(new ConcurrentHashMap<>(ImmutableMap.of(
                                        "output", OutputData.builder().type("reference").name("output").referenceName("output").flowId(1).build()
                                )))
                                .build()

                ))
                .nodeEdges(new ArrayList<>(Arrays.asList(
                        NodeEdge.builder().sourceNodeId(0).targetNodeId(1).build(),
                        NodeEdge.builder().sourceNodeId(1).targetNodeId(2).build()
                )
                ))
                .build();

        // 调用execute方法
        EndFlowRes result = botService.execute(mockReq);
        System.out.println(result);
    }

    @Test
    public void testApiDocTrans() {

        String input = "[\n" +
                "    {\n" +
                "        \"apiUrl\": \"/getStr\",\n" +
                "        \"apiDesc\": \"返回String\",\n" +
                "        \"apiRequestRaw\":\n" +
                "        {},\n" +
                "        \"apiResponseRaw\":\n" +
                "        {},\n" +
                "        \"httpMethod\": \"GET\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"apiUrl\": \"/goods/addOrder\",\n" +
                "        \"apiDesc\": \"获取用户信息\",\n" +
                "        \"apiRequestRaw\":\n" +
                "        {\n" +
                "            \"goodId\": 14124\n" +
                "        },\n" +
                "        \"apiResponseRaw\":\n" +
                "        {\n" +
                "            \"traceId\": \"\",\n" +
                "            \"SUCCESS_MESSAGE\": \"\",\n" +
                "            \"code\": 0,\n" +
                "            \"attachments\":\n" +
                "            {\n" +
                "                \"value\": \"\",\n" +
                "                \"key\": \"\"\n" +
                "            },\n" +
                "            \"data\":\n" +
                "            {\n" +
                "                \"goodName\": \"小米手机11\",\n" +
                "                \"strList\":\n" +
                "                [\n" +
                "                    \"\"\n" +
                "                ],\n" +
                "                \"goodId\": 114121,\n" +
                "                \"goodPrice\": \"1999.0\"\n" +
                "            },\n" +
                "            \"message\": \"\"\n" +
                "        },\n" +
                "        \"httpMethod\": \"POST\",\n" +
                "        \"headerInfo\":\n" +
                "        [\n" +
                "            {\n" +
                "                \"headerName\": \"Content-Type\",\n" +
                "                \"headerValue\": \"application/json;charset=UTF-8\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    {\n" +
                "        \"apiUrl\": \"/goods/addToCart\",\n" +
                "        \"apiDesc\": \"获取用户信息\",\n" +
                "        \"apiRequestRaw\":\n" +
                "        {\n" +
                "            \"goodId\": 14124\n" +
                "        },\n" +
                "        \"apiResponseRaw\":\n" +
                "        {\n" +
                "            \"traceId\": \"\",\n" +
                "            \"SUCCESS_MESSAGE\": \"\",\n" +
                "            \"code\": 0,\n" +
                "            \"attachments\":\n" +
                "            {\n" +
                "                \"value\": \"\",\n" +
                "                \"key\": \"\"\n" +
                "            },\n" +
                "            \"data\":\n" +
                "            {\n" +
                "                \"goodName\": \"小米手机11\",\n" +
                "                \"strList\":\n" +
                "                [\n" +
                "                    \"\"\n" +
                "                ],\n" +
                "                \"goodId\": 114121,\n" +
                "                \"goodPrice\": \"1999.0\"\n" +
                "            },\n" +
                "            \"message\": \"\"\n" +
                "        },\n" +
                "        \"httpMethod\": \"POST\",\n" +
                "        \"headerInfo\":\n" +
                "        [\n" +
                "            {\n" +
                "                \"headerName\": \"Content-Type\",\n" +
                "                \"headerValue\": \"application/json;charset=UTF-8\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    {\n" +
                "        \"apiUrl\": \"/goods/getGoodsInfo\",\n" +
                "        \"apiDesc\": \"获取用户信息\",\n" +
                "        \"apiRequestRaw\":\n" +
                "        {\n" +
                "            \"goodName\": \"小米手机11\"\n" +
                "        },\n" +
                "        \"apiResponseRaw\":\n" +
                "        {\n" +
                "            \"traceId\": \"qaQlwYx1xD\",\n" +
                "            \"SUCCESS_MESSAGE\": \"ctPMwKYplg\",\n" +
                "            \"code\": 801697124,\n" +
                "            \"attachments\":\n" +
                "            {\n" +
                "                \"value\": \"ySidv6WcxY\",\n" +
                "                \"key\": \"quvDE5z1Aq\"\n" +
                "            },\n" +
                "            \"data\":\n" +
                "            {\n" +
                "                \"goodName\": \"小米手机11\",\n" +
                "                \"strList\":\n" +
                "                [\n" +
                "                    \"vO1K8jRbYR\"\n" +
                "                ],\n" +
                "                \"goodId\": 114121,\n" +
                "                \"goodPrice\": \"1999.0\"\n" +
                "            },\n" +
                "            \"message\": \"LNQDtHpFgT\"\n" +
                "        },\n" +
                "        \"httpMethod\": \"POST\",\n" +
                "        \"headerInfo\":\n" +
                "        [\n" +
                "            {\n" +
                "                \"headerName\": \"Content-Type\",\n" +
                "                \"headerValue\": \"application/json;charset=UTF-8\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    {\n" +
                "        \"apiUrl\": \"/goods/pay\",\n" +
                "        \"apiDesc\": \"下单结算\",\n" +
                "        \"apiRequestRaw\":\n" +
                "        {},\n" +
                "        \"apiResponseRaw\":\n" +
                "        {\n" +
                "            \"traceId\": \"\",\n" +
                "            \"SUCCESS_MESSAGE\": \"\",\n" +
                "            \"code\": 0,\n" +
                "            \"attachments\":\n" +
                "            {\n" +
                "                \"value\": \"\",\n" +
                "                \"key\": \"\"\n" +
                "            },\n" +
                "            \"data\": 0,\n" +
                "            \"message\": \"\"\n" +
                "        },\n" +
                "        \"httpMethod\": \"GET\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"apiUrl\": \"/goods/putGoodsInfo\",\n" +
                "        \"apiDesc\": \"获取用户信息\",\n" +
                "        \"apiRequestRaw\":\n" +
                "        {\n" +
                "            \"goodName\": \"小米手机11\",\n" +
                "            \"strList\":\n" +
                "            [\n" +
                "                \"\"\n" +
                "            ],\n" +
                "            \"goodId\": 114121,\n" +
                "            \"goodPrice\": \"1999.0\"\n" +
                "        },\n" +
                "        \"apiResponseRaw\":\n" +
                "        {\n" +
                "            \"traceId\": \"\",\n" +
                "            \"SUCCESS_MESSAGE\": \"\",\n" +
                "            \"code\": 0,\n" +
                "            \"attachments\":\n" +
                "            {\n" +
                "                \"value\": \"\",\n" +
                "                \"key\": \"\"\n" +
                "            },\n" +
                "            \"data\": false,\n" +
                "            \"message\": \"\"\n" +
                "        },\n" +
                "        \"httpMethod\": \"POST\",\n" +
                "        \"headerInfo\":\n" +
                "        [\n" +
                "            {\n" +
                "                \"headerName\": \"Content-Type\",\n" +
                "                \"headerValue\": \"application/json;charset=UTF-8\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    {\n" +
                "        \"apiUrl\": \"/goods/putGoodsInfoFrom\",\n" +
                "        \"apiDesc\": \"获取用户信息\",\n" +
                "        \"apiRequestRaw\":\n" +
                "        {\n" +
                "            \"goodName\": \"小米手机11\",\n" +
                "            \"strList\":\n" +
                "            [\n" +
                "                \"\"\n" +
                "            ],\n" +
                "            \"goodId\": 114121,\n" +
                "            \"goodPrice\": \"1999.0\"\n" +
                "        },\n" +
                "        \"apiResponseRaw\":\n" +
                "        {\n" +
                "            \"traceId\": \"\",\n" +
                "            \"SUCCESS_MESSAGE\": \"\",\n" +
                "            \"code\": 0,\n" +
                "            \"attachments\":\n" +
                "            {\n" +
                "                \"value\": \"\",\n" +
                "                \"key\": \"\"\n" +
                "            },\n" +
                "            \"data\": false,\n" +
                "            \"message\": \"\"\n" +
                "        },\n" +
                "        \"httpMethod\": \"POST\",\n" +
                "        \"headerInfo\":\n" +
                "        [\n" +
                "            {\n" +
                "                \"headerName\": \"Content-Type\",\n" +
                "                \"headerValue\": \"application/json;charset=UTF-8\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    {\n" +
                "        \"apiUrl\": \"/goods/test\",\n" +
                "        \"apiDesc\": \"测试接口\",\n" +
                "        \"apiRequestRaw\":\n" +
                "        {},\n" +
                "        \"apiResponseRaw\":\n" +
                "        {\n" +
                "            \"traceId\": \"\",\n" +
                "            \"SUCCESS_MESSAGE\": \"\",\n" +
                "            \"code\": 0,\n" +
                "            \"attachments\":\n" +
                "            {\n" +
                "                \"value\": \"\",\n" +
                "                \"key\": \"\"\n" +
                "            },\n" +
                "            \"data\": \"\",\n" +
                "            \"message\": \"\"\n" +
                "        },\n" +
                "        \"httpMethod\": \"GET\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"apiUrl\": \"/goods/timeout\",\n" +
                "        \"apiDesc\": \"超时接口\",\n" +
                "        \"apiRequestRaw\":\n" +
                "        {},\n" +
                "        \"apiResponseRaw\":\n" +
                "        {\n" +
                "            \"traceId\": \"\",\n" +
                "            \"SUCCESS_MESSAGE\": \"\",\n" +
                "            \"code\": 0,\n" +
                "            \"attachments\":\n" +
                "            {\n" +
                "                \"value\": \"\",\n" +
                "                \"key\": \"\"\n" +
                "            },\n" +
                "            \"data\": false,\n" +
                "            \"message\": \"\"\n" +
                "        },\n" +
                "        \"httpMethod\": \"GET\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"apiUrl\": \"/goods/shGoodsInfo\",\n" +
                "        \"apiDesc\": \"获取用户信息11111\",\n" +
                "        \"apiRequestRaw\":\n" +
                "        {},\n" +
                "        \"apiResponseRaw\":\n" +
                "        {\n" +
                "            \"traceId\": \"JqbYrsqPXo\",\n" +
                "            \"SUCCESS_MESSAGE\": \"6bryBe5TbX\",\n" +
                "            \"code\": 2125446733,\n" +
                "            \"attachments\":\n" +
                "            {\n" +
                "                \"value\": \"B6bo8Wad5i\",\n" +
                "                \"key\": \"ghU8INmLPP\"\n" +
                "            },\n" +
                "            \"data\":\n" +
                "            {\n" +
                "                \"goodName\": \"小米手机11\",\n" +
                "                \"strList\":\n" +
                "                [\n" +
                "                    \"yUWj1WuGMR\"\n" +
                "                ],\n" +
                "                \"goodId\": 114121,\n" +
                "                \"goodPrice\": \"1999.0\"\n" +
                "            },\n" +
                "            \"message\": \"Iflt468TeI\"\n" +
                "        },\n" +
                "        \"httpMethod\": \"POST\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"apiUrl\": \"/goods/searchGoodsInfo\",\n" +
                "        \"apiDesc\": \"获取用户信息\",\n" +
                "        \"apiRequestRaw\":\n" +
                "        {},\n" +
                "        \"apiResponseRaw\":\n" +
                "        {\n" +
                "            \"traceId\": \"\",\n" +
                "            \"SUCCESS_MESSAGE\": \"\",\n" +
                "            \"code\": 0,\n" +
                "            \"attachments\":\n" +
                "            {\n" +
                "                \"value\": \"\",\n" +
                "                \"key\": \"\"\n" +
                "            },\n" +
                "            \"data\":\n" +
                "            {\n" +
                "                \"goodName\": \"小米手机11\",\n" +
                "                \"strList\":\n" +
                "                [\n" +
                "                    \"\"\n" +
                "                ],\n" +
                "                \"goodId\": 114121,\n" +
                "                \"goodPrice\": \"1999.0\"\n" +
                "            },\n" +
                "            \"message\": \"\"\n" +
                "        },\n" +
                "        \"httpMethod\": \"POST\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"apiUrl\": \"/getname\",\n" +
                "        \"apiDesc\": \"asdafadf\",\n" +
                "        \"apiRequestRaw\":\n" +
                "        {\n" +
                "            \"data\": 111111\n" +
                "        },\n" +
                "        \"apiResponseRaw\":\n" +
                "        {\n" +
                "            \"data\": 222222\n" +
                "        },\n" +
                "        \"httpMethod\": \"POST\",\n" +
                "        \"headerInfo\":\n" +
                "        [\n" +
                "            {\n" +
                "                \"headerName\": \"Accept-Charset\",\n" +
                "                \"headerValue\": \"11111\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    {\n" +
                "        \"apiUrl\": \"/get\",\n" +
                "        \"apiDesc\": \"\",\n" +
                "        \"apiRequestRaw\":\n" +
                "        {},\n" +
                "        \"apiResponseRaw\":\n" +
                "        {},\n" +
                "        \"httpMethod\": \"POST\",\n" +
                "        \"headerInfo\":\n" +
                "        []\n" +
                "    },\n" +
                "    {\n" +
                "        \"apiUrl\": \"/MiApi/addUserInfo\",\n" +
                "        \"apiDesc\": \"添加用户信息\",\n" +
                "        \"apiRequestRaw\":\n" +
                "        {\n" +
                "            \"username2\": \"dongzhenixng2\"\n" +
                "        },\n" +
                "        \"apiResponseRaw\":\n" +
                "        {\n" +
                "            \"traceId\": \"lcykhHfz0o\",\n" +
                "            \"SUCCESS_MESSAGE\": \"InCQ2Z5kDw\",\n" +
                "            \"code\": 1823425314,\n" +
                "            \"attachments\":\n" +
                "            {\n" +
                "                \"value\": \"4U9SBQBxWB\",\n" +
                "                \"key\": \"ZfSr35RM2n\"\n" +
                "            },\n" +
                "            \"data\":\n" +
                "            {\n" +
                "                \"msg\": \"success\",\n" +
                "                \"code\": 200\n" +
                "            },\n" +
                "            \"message\": \"E3CNfHjnoE\"\n" +
                "        },\n" +
                "        \"httpMethod\": \"POST\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"apiUrl\": \"/MiApi/getUserInfo\",\n" +
                "        \"apiDesc\": \"获取用户信息\",\n" +
                "        \"apiRequestRaw\":\n" +
                "        {\n" +
                "            \"username2\": \"dongzhenixng2\"\n" +
                "        },\n" +
                "        \"apiResponseRaw\":\n" +
                "        {\n" +
                "            \"traceId\": \"7ktG4Zajo7\",\n" +
                "            \"SUCCESS_MESSAGE\": \"WQlnyr8v4m\",\n" +
                "            \"code\": 2144070330,\n" +
                "            \"attachments\":\n" +
                "            {\n" +
                "                \"value\": \"Y2OTLYSSu4\",\n" +
                "                \"key\": \"3EyLNg0jMA\"\n" +
                "            },\n" +
                "            \"data\":\n" +
                "            {\n" +
                "                \"msg\": \"success\",\n" +
                "                \"code\": 200\n" +
                "            },\n" +
                "            \"message\": \"I9QbnhtM4x\"\n" +
                "        },\n" +
                "        \"httpMethod\": \"POST\"\n" +
                "    }\n" +
                "]";

        String prompt = "给你一个json数组，请帮我做转换，下面是一个例子供你参考：\n" +
                "\n" +
                "例子开始\n" +
                "-------------------------------------\n" +
                "\n" +
                "输入：\n" +
                "```json\n" +
                "[{\n" +
                "\t\t\"apiUrl\": \"/goods/getGoodsInfo\",\n" +
                "\t\t\"apiName\": \"getGoodsInfo\",\n" +
                "\t\t\"apiDesc\": \"获取用户信息\",\n" +
                "\t\t\"apiRequestRaw\": {\n" +
                "\t\t\t\"goodName\": \"小米手机11\"\n" +
                "\t\t},\n" +
                "\t\t\"apiResponseRaw\": {\n" +
                "\t\t\t\"traceId\": \"qaQlwYx1xD\",\n" +
                "\t\t\t\"SUCCESS_MESSAGE\": \"ctPMwKYplg\",\n" +
                "\t\t\t\"code\": 801697124,\n" +
                "\t\t\t\"attachments\": {\n" +
                "\t\t\t\t\"value\": \"ySidv6WcxY\",\n" +
                "\t\t\t\t\"key\": \"quvDE5z1Aq\"\n" +
                "\t\t\t},\n" +
                "\t\t\t\"data\": {\n" +
                "\t\t\t\t\"goodName\": \"小米手机11\",\n" +
                "\t\t\t\t\"strList\": [\n" +
                "\t\t\t\t\t\"vO1K8jRbYR\"\n" +
                "\t\t\t\t],\n" +
                "\t\t\t\t\"goodId\": 114121,\n" +
                "\t\t\t\t\"goodPrice\": \"1999.0\"\n" +
                "\t\t\t},\n" +
                "\t\t\t\"message\": \"LNQDtHpFgT\"\n" +
                "\t\t},\n" +
                "\t\t\"httpMethod\": \"POST\",\n" +
                "\t\t\"headerInfo\": [{\n" +
                "\t\t\t\"headerName\": \"Content-Type\",\n" +
                "\t\t\t\"headerValue\": \"application/json;charset=UTF-8\"\n" +
                "\t\t}]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"apiUrl\": \"/goods/pay\",\n" +
                "\t\t\"apiName\": \"pay\",\n" +
                "\t\t\"apiDesc\": \"下单结算\",\n" +
                "\t\t\"apiRequestRaw\": {},\n" +
                "\t\t\"apiResponseRaw\": {\n" +
                "\t\t\t\"traceId\": \"\",\n" +
                "\t\t\t\"SUCCESS_MESSAGE\": \"\",\n" +
                "\t\t\t\"code\": 0,\n" +
                "\t\t\t\"attachments\": {\n" +
                "\t\t\t\t\"value\": \"\",\n" +
                "\t\t\t\t\"key\": \"\"\n" +
                "\t\t\t},\n" +
                "\t\t\t\"data\": 0,\n" +
                "\t\t\t\"message\": \"\"\n" +
                "\t\t},\n" +
                "\t\t\"httpMethod\": \"GET\"\n" +
                "\t}\n" +
                "]\n" +
                "```\n" +
                "\n" +
                "输出：\n" +
                "```markdown\n" +
                "1. getGoodsInfo\n" +
                "- 描述：获取用户信息\n" +
                "- url：/goods/getGoodsInfo\n" +
                "- 接口参数样例：\n" +
                "{\n" +
                "   \"goodName\": \"小米手机11\"\n" +
                "}\n" +
                "- 接口返回值样例：\n" +
                "{\n" +
                "\t\"traceId\": \"qaQlwYx1xD\",\n" +
                "\t\"SUCCESS_MESSAGE\": \"ctPMwKYplg\",\n" +
                "\t\"code\": 801697124,\n" +
                "\t\"attachments\": {\n" +
                "\t\t\"value\": \"ySidv6WcxY\",\n" +
                "\t\t\"key\": \"quvDE5z1Aq\"\n" +
                "\t},\n" +
                "\t\"data\": {\n" +
                "\t\t\"goodName\": \"小米手机11\",\n" +
                "\t\t\"strList\": [\n" +
                "\t\t\t\"vO1K8jRbYR\"\n" +
                "\t\t],\n" +
                "\t\t\"goodId\": 114121,\n" +
                "\t\t\"goodPrice\": \"1999.0\"\n" +
                "\t},\n" +
                "\t\"message\": \"LNQDtHpFgT\"\n" +
                "}\n" +
                "- 请求方法：POST\n" +
                "- 额外header信息：\n" +
                "{\n" +
                "   \"Content-Type\":   \"application/json;charset=UTF-8\"\n" +
                "}\n" +
                "\n" +
                "2. pay\n" +
                "- 描述：下单结算\n" +
                "- url：/goods/pay\n" +
                "- 接口参数样例：没有\n" +
                "- 接口返回值样例：\n" +
                "{\n" +
                "\t\"traceId\": \"\",\n" +
                "\t\"SUCCESS_MESSAGE\": \"\",\n" +
                "\t\"code\": 0,\n" +
                "\t\"attachments\": {\n" +
                "\t\t\"value\": \"\",\n" +
                "\t\t\"key\": \"\"\n" +
                "\t},\n" +
                "\t\"data\": 0,\n" +
                "\t\"message\": \"\"\n" +
                "}\n" +
                "- 请求方法：GET\n" +
                "- 额外header信息：没有\n" +
                "```\n" +
                "\n" +
                "例子结束\n" +
                "-------------------------------------\n" +
                "\n" +
                "要求：\n" +
                "- 完整输出，有几个对象输出几个描述，不要使用“其他接口描述省略”的情况，否则会有不好的事情发生\n" +
                "- 输出内容严格遵循上面给出的输出格式，不要输出任何多余的内容！\n" +
                "\n" +
                "新的输入：\n" +
                "```json\n" +
                "${content}\n" +
                "```\n" +
                "\n" +
                "请给我输出";

        // 创建一个BotReq对象并初始化它的属性
        BotReq mockReq = BotReq.builder()
                .flowDataList(Lists.newArrayList(
                        FlowData.builder().id(0).type("begin").name("开始").inputMap(new ConcurrentHashMap<>(
                                ImmutableMap.of(
                                        "content", InputData.builder().value(new JsonPrimitive(input)).build()
                                ))).build(),

                        // 调用flow
                        FlowData.builder().id(1).type("llm").name("llm-test")
                                .inputMap(new ConcurrentHashMap<>(ImmutableMap.of(
                                        CommonConstants.TY_LLM_MODEL_MARK, InputData.builder().value(new JsonPrimitive("claude3")).build(),
                                        CommonConstants.TY_LLM_PROMPT_MARK, InputData.builder().value(new JsonPrimitive(prompt)).build(),
                                        "content", InputData.builder().type("reference").name("content").referenceName("content").flowId(0).build()
                                )))

                                .outputMap(new ConcurrentHashMap<>(ImmutableMap.of(
                                        "output", OutputData.builder().build()
                                )))


                                .build(),

                        FlowData.builder().id(2).type("end").name("结束")
                                .inputMap(new ConcurrentHashMap<>(ImmutableMap.of(
                                        CommonConstants.TY_END_MESSAGE_CONTENT_MARK, InputData.builder().value(new JsonPrimitive("${output}")).build()
                                )))

                                .outputMap(new ConcurrentHashMap<>(ImmutableMap.of(
                                        "output", OutputData.builder().type("reference").name("output").referenceName("data").flowId(1).build()
                                )))
                                .build()
                ))
                .nodeEdges(new ArrayList<>(Arrays.asList(NodeEdge.builder().sourceNodeId(0).targetNodeId(1).build(), NodeEdge.builder().sourceNodeId(1).targetNodeId(2).build())))
                .build();


        // 调用execute方法
        EndFlowRes result = botService.execute(mockReq);
        System.out.println("Res: " + result);
    }

    public static void main(String[] args) {
        String prompt2 = "你是一位领域建模和软件设计高级专家，我会给你一份json根式的需求文档，请按要求完成数据建模及封装对应模型的dao层代码。\n" +
                "要求如下：\n" +
                "1.生成代码的基础package是run.mone.m78；\n" +
                "2.使用的orm框架是dev.morphia.morphia,版本号是1.6.1；\n" +
                "3.你需要充分理解需求文档，完成数据建模，建模要求如下：\n" +
                "  3.1把各个表的model实体类生成出来，并且都加上注解@lombok.Data\n" +
                "  3.2model实体类的package是run.mone.m78.model\n" +
                "4.生成实体类对应的Repository层代码，要求如下：\n" +
                "  4.1每一个model实体都要生成基于morphia操作的接口类和接口的实现类\n" +
                "  4.2Repository层package是run.mone.m78.repository，生成的接口类方法要齐全，对应的实现类需要完整的实现所有接口方法，不能有任何缺失；\n" +
                "  4.3实现类使用Spring的Bean管理机制，请加好相关的注解; 实现类里请使用spring注解注入Datastore这个bean来完成mongo的相关操作\n" +
                "  4.4findAll实现请使用这个写法：datastore.find(XModel.class).asList()\n" +
                "5.确保生成的类代码里正确导入了所使用的类，务必不能遗漏\n" +
                "  若类里使用了ObjectId，则需导入org.bson.types.ObjectId；\n" +
                "  若类里使用了List，则需导入java.util.List；\n" +
                "  若类里使用了Collectors，则需导入java.util.stream.Collectors；\n" +
                "  导入示例：package run.mone.m78.repository;\n" +
                "\n" +
                "import org.bson.types.ObjectId;\n" +
                "import run.mone.m78.model.Book;\n" +
                "import java.util.List;\n" +
                "\n" +
                "public interface BookRepository {\n" +
                "    void save(Book book);\n" +
                "    void update(Book book);\n" +
                "    void delete(ObjectId id);\n" +
                "    List<Book> findAll();\n" +
                "    Book findById(ObjectId id);\n" +
                "    List<Book> findByTitle(String title);\n" +
                "}\n"+
                "6.生成的结果，请用json格式返回。生成结果的要求和格式如下：\n" +
                "  6.1生成的类和接口要完整，不能把字段和注解和方法分开展示，每一个类的内容在一个属性里面完整展示；\n" +
                "  6.2需求不要在结果里面输出了，生成的类和接口需要输出名称和代码内容，同时需要区分生成的文件类型：Model、Repository、RepositoryImpl；\n" +
                "  6.3返回的格式要求，\"{\\n\" +\n" +
                "            \"    \\\"output\\\": {\\n\" +\n" +
                "            \"        \\\"Model\\\": [\\n\" +\n" +
                "            \"            {\\n\" +\n" +
                "            \"                \\\"className\\\": \\\"生成的Model实体类名称\\\",\\n\" +\n" +
                "            \"                \\\"classContent\\\": \\\"这里内容是生成的Model实体类的类文本\\\"\\n\" +\n" +
                "            \"            }\\n\" +\n" +
                "            \"        ],\\n\" +\n" +
                "            \"        \\\"Repository\\\": [\\n\" +\n" +
                "            \"            {\\n\" +\n" +
                "            \"                \\\"className\\\": \\\"生成的Repository接口名称\\\",\\n\" +\n" +
                "            \"                \\\"classContent\\\": \\\"这里内容是生成的Repository的接口文本\\\"\\n\" +\n" +
                "            \"            }\\n\" +\n" +
                "            \"        ],\\n\" +\n" +
                "            \"        \\\"RepositoryImpl\\\": [\\n\" +\n" +
                "            \"            {\\n\" +\n" +
                "            \"                \\\"className\\\": \\\"生成的RepositoryImpl类名称\\\",\\n\" +\n" +
                "            \"                \\\"classContent\\\": \\\"这里内容是生成的RepositoryImpl的类文本\\\"\\n\" +\n" +
                "            \"            }\\n\" +\n" +
                "            \"        ]\\n\" +\n" +
                "            \"    }\\n\" +\n" +
                "            \"}\";\n" +
                "\n" +
                "7.请严格按照上面的要求进行代码生成，生成的代码一定要准确、完整、专业。我们的项目很紧急很关键不能有任何的遗漏和差错，你要是完成的好，我会奖励你100美金，我们非常信任你；\n";


        System.out.println(prompt2);
    }
}
