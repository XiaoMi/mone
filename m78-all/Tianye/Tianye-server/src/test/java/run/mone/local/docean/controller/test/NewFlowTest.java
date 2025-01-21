package run.mone.local.docean.controller.test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import run.mone.junit.DoceanConfiguration;
import run.mone.junit.DoceanExtension;
import run.mone.local.docean.fsm.BotReq;
import run.mone.local.docean.fsm.bo.*;
import run.mone.local.docean.service.BotService;
import run.mone.local.docean.tianye.common.CommonConstants;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static run.mone.local.docean.tianye.common.FlowConstants.CMD_CONFIRM_FLOW;


/**
 * @author wmin
 * @date 2024/3/16
 */
@ExtendWith(DoceanExtension.class)
@DoceanConfiguration(basePackage = {"run.mone.local.docean", "com.xiaomi.youpin"})
public class NewFlowTest {
    @Resource
    private BotService botService;

    @Test
    public void testLLMFlow() throws InterruptedException {
        // 创建一个BotReq对象并初始化它的属性
        BotReq mockReq = BotReq.builder().flowRecordId("9898").syncFlowStatusToM78(true).flowDataList(Lists.newArrayList(
                        FlowData.builder().id(0).type("begin").name("开始").inputMap(new ConcurrentHashMap<>(
                                ImmutableMap.of(
                                        "prompt", InputData.builder().value(new JsonPrimitive("1+2=?")).build()
                                ))).build(),

                        //调用知识库flow
                        FlowData.builder().id(1).type("llm").name("大模型")
                                .inputMap(new ConcurrentHashMap<>(ImmutableMap.of(
                                        CommonConstants.TY_LLM_MODEL_MARK, InputData.builder().value(new JsonPrimitive("gpt4_1106_2")).build(),
                                        CommonConstants.TY_LLM_TIMEOUT_MARK, InputData.builder().value(new JsonPrimitive(18000)).build(),
                                        CommonConstants.TY_LLM_PROMPT_MARK, InputData.builder().type("reference").name("prompt").referenceName("prompt").flowId(0).originalInput(false).build(),
                                        "a", InputData.builder().value(new JsonPrimitive("15")).build()
                                )))

                                .outputMap(new ConcurrentHashMap<>(ImmutableMap.of(
                                        "llm_rst", OutputData.builder().build()
                                )))

                                .build(),

                        //code flow
                        FlowData.builder().id(3).type("code").name("code")
                                .inputMap(new ConcurrentHashMap<>(ImmutableMap.of(
                                        "content", InputData.builder().type("reference").name("content").referenceName("content").flowId(0).build(),
                                        "a", InputData.builder().name("a").value(new JsonPrimitive(29)).build(),
                                        "b", InputData.builder().name("b").value(new JsonPrimitive(201)).build(),
                                        CommonConstants.TY_CODE_INPUT_MARK, InputData.builder().name(CommonConstants.TY_CODE_INPUT_MARK).value(new JsonPrimitive("import com.google.gson.JsonObject;\n def execute(JsonObject input, Object context) {\n    if (!input.has('a') || !input.has('b')) {\n        throw new IllegalArgumentException(\"JSON对象必须包含键'a'和'b'。\");\n    }\n    Thread.sleep(1000);\nint a = input.get('a').getAsInt();\n    int b = input.get('b').getAsInt();\n    int sum = a + b;\n    JsonObject result = new JsonObject();\n    result.addProperty(\"code_sum\", sum);\n    return result;\n}")).build()
                                )))

                                .outputMap(new ConcurrentHashMap<>(ImmutableMap.of(
                                        "code_sum", OutputData.builder().build() // sum为脚本代码返回结果的键
                                )))
                                .build(),

                        //code flow - 1
                        FlowData.builder().id(5).type("code").name("code")
                                .inputMap(new ConcurrentHashMap<>(ImmutableMap.of(
                                        "content", InputData.builder().type("reference").name("content").referenceName("content").flowId(0).build(),
                                        "a", InputData.builder().name("a").value(new JsonPrimitive(29)).build(),
                                        "b", InputData.builder().name("b").value(new JsonPrimitive(201)).build(),
                                        CommonConstants.TY_CODE_INPUT_MARK, InputData.builder().name(CommonConstants.TY_CODE_INPUT_MARK).value(new JsonPrimitive("import com.google.gson.JsonObject;\n def execute(JsonObject input, Object context) {\n    if (!input.has('a') || !input.has('b')) {\n        throw new IllegalArgumentException(\"JSON对象必须包含键'a'和'b'。\");\n    }\n    Thread.sleep(1000);\nint a = input.get('a').getAsInt();\n    int b = input.get('b').getAsInt();\n    int sum = a + b;\n    JsonObject result = new JsonObject();\n    result.addProperty(\"code_sum\", sum);\n    return result;\n}")).build()
                                )))

                                .outputMap(new ConcurrentHashMap<>(ImmutableMap.of(
                                        "code_sum", OutputData.builder().build() // sum为脚本代码返回结果的键
                                )))
                                .build(),

                        //manualConfirm flow
                        FlowData.builder().id(4).type("manualConfirm").name("manualConfirm")
                                .inputMap(new ConcurrentHashMap<>())
                                .outputMap(new ConcurrentHashMap<>())
                                .build(),

                        FlowData.builder().id(2).type("end").name("结束")
                                .outputMap(new ConcurrentHashMap<>(ImmutableMap.of(
                                        "llm", OutputData.builder().type("reference").name("llm").referenceName("llm_rst").valueType("string").flowId(1).build(),
                                        "code", OutputData.builder().type("reference").name("code").referenceName("code_sum").valueType("string").flowId(3).build()
                                )))
                                .build()
                ))
                .nodeEdges(new ArrayList<>(Arrays.asList(
                        NodeEdge.builder().sourceNodeId(0).targetNodeId(1).build(),
                        NodeEdge.builder().sourceNodeId(0).targetNodeId(3).build(),
                        NodeEdge.builder().sourceNodeId(3).targetNodeId(5).build(),
                        NodeEdge.builder().sourceNodeId(5).targetNodeId(4).build(),
                        NodeEdge.builder().sourceNodeId(4).targetNodeId(2).build(),
                        NodeEdge.builder().sourceNodeId(1).targetNodeId(2).build())))
                .syncFlowStatusToM78(false)
                .userName("wangmin17")
                .m78RpcAddr("127.0.0.1:7678")
                .build();


        // 调用execute方法
        new Thread(() -> {
            System.out.println("start execute");
            EndFlowRes result = botService.execute(mockReq);
            System.out.println(result);
        }).start();
        Thread.sleep(6000);
//        BotReq cancelRequest = BotReq.builder().flowRecordId("9898").cmd("cancelFlow").build();

//        //goto 操作
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("cmd", "gotoFlow");
        JsonObject metaObject = new JsonObject();
        metaObject.addProperty("nodeId", "3");
        metaObject.addProperty("targetNodeId", "4");
        jsonObject.add("meta", metaObject);

        BotReq gotoRequest = BotReq.builder().flowRecordId("9898").cmd("gotoFlow").message(jsonObject.toString()).build();
        botService.sendMsg(gotoRequest);

        Thread.sleep(6000);

        //确定 操作
        BotReq cancelRequest = BotReq.builder().flowRecordId("9898").cmd(CMD_CONFIRM_FLOW).message(msg()).build();
        botService.sendMsg(cancelRequest);

        Thread.sleep(600000);
    }

    private String msg() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("cmd", "manualConfirmFlow");
        JsonObject metaObject = new JsonObject();
        metaObject.addProperty("nodeId", "0");
        metaObject.addProperty("targetNodeId", "4");
        jsonObject.add("meta", metaObject);
        return jsonObject.toString();
    }

    @Test
    public void testSpecStartNodeIdLLMFlow() {
        //从指定的节点开始运行
        Map<Integer, Map<String, OutputData>> referenceData = new HashMap<>();
        referenceData.put(0, ImmutableMap.of(
                "prompt", OutputData.builder().value(new JsonPrimitive("2*3=?")).build()));
        //todo 存储reference
        BotReq mockReq = BotReq.builder().flowRecordId("9898").syncFlowStatusToM78(false)
                .specifiedStartNodeId(1)
                .flowDataList(Lists.newArrayList(
                        FlowData.builder().id(0).type("begin").name("开始").inputMap(new ConcurrentHashMap<>(
                                ImmutableMap.of(
                                        "prompt", InputData.builder().value(null).build()
                                ))).build(),

                        //调用知识库flow
                        FlowData.builder().id(1).type("llm").name("大模型")
                                .inputMap(new ConcurrentHashMap<>(ImmutableMap.of(
                                        CommonConstants.TY_LLM_MODEL_MARK, InputData.builder().value(new JsonPrimitive("gpt4_1106_2")).build(),
                                        CommonConstants.TY_LLM_TIMEOUT_MARK, InputData.builder().value(new JsonPrimitive(18000)).build(),
                                        CommonConstants.TY_LLM_PROMPT_MARK, InputData.builder().type("reference").name("prompt").referenceName("prompt").flowId(0).originalInput(false).build()
                                )))

                                .outputMap(new ConcurrentHashMap<>(ImmutableMap.of(
                                        "rst", OutputData.builder().build()
                                )))

                                .build(),

                        //调用知识库flow
                        FlowData.builder().id(2).type("knowledge").name("知识库")
                                .inputMap(new ConcurrentHashMap<>(ImmutableMap.of(
                                        CommonConstants.TY_KNOWLEDGE_ID_MARK, InputData.builder().value(new JsonPrimitive("1018")).build(),
                                        CommonConstants.TY_KNOWLEDGE_MAX_RECALL_MARK, InputData.builder().value(new JsonPrimitive("1")).build(),
                                        CommonConstants.TY_KNOWLEDGE_MIN_MATCH_MARK, InputData.builder().value(new JsonPrimitive("0.7")).build(),
                                        CommonConstants.TY_KNOWLEDGE_QUERY_MARK, InputData.builder().value(new JsonPrimitive("介绍下Athena的收费规则")).build())))
                                .outputMap(new ConcurrentHashMap<>(ImmutableMap.of(
                                        "outputList", OutputData.builder().build(),
                                        "output", OutputData.builder().build()
                                )))
                                .build(),

                        FlowData.builder().id(3).type("end").name("结束")
                                .outputMap(new ConcurrentHashMap<>(ImmutableMap.of(
                                        "rst", OutputData.builder().type("reference").name("rst").referenceName("rst").valueType("string").flowId(1).build()
                                )))
                                .build()
                ))
                .nodeEdges(new ArrayList<>(Arrays.asList(NodeEdge.builder().sourceNodeId(0).targetNodeId(1).build(),
                        NodeEdge.builder().sourceNodeId(1).targetNodeId(3).build(),
                        NodeEdge.builder().sourceNodeId(2).targetNodeId(3).build())))
                .syncFlowStatusToM78(true)
                .userName("wangmin17")
                .m78RpcAddr("127.0.0.1:7678")
                .referenceData(referenceData)
                .build();

        // 调用execute方法
        EndFlowRes result = botService.execute(mockReq);
        System.out.println(result);
    }

    @Test
    public void testBatchLLMFlow() {
        JsonArray jsonArray1 = new JsonArray();
        jsonArray1.add(9);
        jsonArray1.add(8);

        JsonArray jsonArray2 = new JsonArray();
        jsonArray2.add(1);
        jsonArray2.add(2);
        jsonArray2.add(3);
        // 创建一个BotReq对象并初始化它的属性
        BotReq mockReq = BotReq.builder().flowRecordId("9898").syncFlowStatusToM78(true).flowDataList(Lists.newArrayList(
                        FlowData.builder().id(0).type("begin").name("开始").inputMap(new ConcurrentHashMap<>(
                                ImmutableMap.of(
                                        "arrInput", InputData.builder().value(jsonArray1).build(),
                                        "nn", InputData.builder().value(jsonArray2).build()
                                ))).build(),

                        //调用知识库flow
                        FlowData.builder().id(1).type("llm").name("大模型")
                                .inputMap(new ConcurrentHashMap<>(ImmutableMap.of(
                                        CommonConstants.TY_LLM_MODEL_MARK, InputData.builder().value(new JsonPrimitive("gpt4_1106_2")).build(),
                                        CommonConstants.TY_LLM_TIMEOUT_MARK, InputData.builder().value(new JsonPrimitive(8000)).build(),
                                        CommonConstants.TY_LLM_PROMPT_MARK, InputData.builder().type("value").name(CommonConstants.TY_LLM_PROMPT_MARK).value(new JsonPrimitive("${num1}+${num2}=?")).originalInput(false).build(),
                                        "num1", InputData.builder().type("batch").name("num1").referenceName("arr").build(),
                                        "num2", InputData.builder().type("batch").name("num2").referenceName("tt").build()
                                )))

                                .outputMap(new ConcurrentHashMap<>(ImmutableMap.of(
                                        "outputList", OutputData.builder().valueType("Array<string>").build()
                                )))

                                .batchMap(new ConcurrentHashMap<>(ImmutableMap.of(
                                        CommonConstants.TY_BATCH_MAX_TIMES_MARK, InputData.builder().value(new JsonPrimitive("5")).build(),
                                        "tt", InputData.builder().type("reference").name("tt").referenceName("nn").flowId(0).build(),
                                        "arr", InputData.builder().type("reference").name("arr").referenceName("arrInput").flowId(0).build()
                                )))

                                .build(),

                        FlowData.builder().id(2).type("end").name("结束")
//                                .inputMap(new ConcurrentHashMap<>(ImmutableMap.of(
//                                        CommonConstants.TY_END_MESSAGE_CONTENT_MARK, InputData.builder().value(new JsonPrimitive("问题：${question},答案：${answer}")).build()
//                                )))

                                .outputMap(new ConcurrentHashMap<>(ImmutableMap.of(
                                        "endAnswer", OutputData.builder().type("reference").name("endAnswer").referenceName("outputList").valueType("Array<string>").flowId(1).build()
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
    public void testSingleLLMFlow() {
        // 创建一个BotReq对象并初始化它的属性
        BotReq mockReq = BotReq.builder().flowRecordId("9898").syncFlowStatusToM78(true)
                .singleNodeTest(true)
                .flowDataList(Lists.newArrayList(
                        //调用知识库flow
                        FlowData.builder().id(1).type("llm").name("大模型")
                                .inputMap(
                                        new ConcurrentHashMap<>(ImmutableMap.of(
                                                CommonConstants.TY_LLM_MODEL_MARK, InputData.builder().value(new JsonPrimitive("gpt4_1106_2")).build(),
                                                CommonConstants.TY_LLM_TIMEOUT_MARK, InputData.builder().value(new JsonPrimitive(15000)).build(),
                                                CommonConstants.TY_LLM_PROMPT_MARK, InputData.builder().type("value").name("prompt").value(new JsonPrimitive("帮我计算加法运算，要求返回json，格式为{\"my-question\":\"\",\"you-answer\":\"\"}，现在我给你真实的问题1+3=?，请返回json格式的答案，直接返回答案即可，不需要计算过程")).flowId(0).originalInput(false).build(),
                                                "a", InputData.builder().value(new JsonPrimitive("15")).build()
                                        )
                                ))

                                .outputMap(new ConcurrentHashMap<>(ImmutableMap.of(
                                        "my-question", OutputData.builder().build(),
                                        "my-answer", OutputData.builder().build()
                                )))

                                .build()
                ))
                .nodeEdges(new ArrayList<>(Arrays.asList(NodeEdge.builder().sourceNodeId(0).targetNodeId(1).build(), NodeEdge.builder().sourceNodeId(1).targetNodeId(2).build())))
                .build();

        System.out.println(botService.singleNodeExecute(mockReq));
    }

    //批处理的单节点测试
    @Test
    public void testSingleBatchLLMFlow() {
        JsonArray jsonArray1 = new JsonArray();
        jsonArray1.add(9);
        jsonArray1.add(8);

        JsonArray jsonArray2 = new JsonArray();
        jsonArray2.add(1);
        jsonArray2.add(2);
        jsonArray2.add(3);
        // 创建一个BotReq对象并初始化它的属性
        BotReq mockReq = BotReq.builder().flowRecordId("9898").syncFlowStatusToM78(true)
                .singleNodeTest(true)
                .flowDataList(Lists.newArrayList(
                        //调用知识库flow
                        FlowData.builder().id(1).type("llm").name("大模型")
                                .inputMap(new ConcurrentHashMap<>(ImmutableMap.of(
                                        CommonConstants.TY_LLM_MODEL_MARK, InputData.builder().value(new JsonPrimitive("gpt4_1106_2")).build(),
                                        CommonConstants.TY_LLM_TIMEOUT_MARK, InputData.builder().value(new JsonPrimitive(18000)).build(),
                                        CommonConstants.TY_LLM_PROMPT_MARK, InputData.builder().type("value").name(CommonConstants.TY_LLM_PROMPT_MARK).value(new JsonPrimitive("${num1}+${num2}=?")).originalInput(false).build(),
                                        "num1", InputData.builder().type("batch").name("num1").referenceName("arr").build(),
                                        "num2", InputData.builder().type("batch").name("num2").referenceName("tt").build()
                                )))

                                .outputMap(new ConcurrentHashMap<>(ImmutableMap.of(
                                        "outputList", OutputData.builder().valueType("Array<string>").build()
                                )))

                                .batchMap(new ConcurrentHashMap<>(ImmutableMap.of(
                                        CommonConstants.TY_BATCH_MAX_TIMES_MARK, InputData.builder().value(new JsonPrimitive("5")).build(),
                                        "tt", InputData.builder().type("value").name("tt").value(jsonArray2).build(),
                                        "arr", InputData.builder().type("value").name("arr").value(jsonArray1).build()
                                )))

                                .build()
                ))
                .nodeEdges(new ArrayList<>(Arrays.asList(NodeEdge.builder().sourceNodeId(0).targetNodeId(1).build(), NodeEdge.builder().sourceNodeId(1).targetNodeId(2).build())))
                .build();

        System.out.println(botService.singleNodeExecute(mockReq));
    }

    @Test
    public void testIntentRecognition() throws InterruptedException {
        JsonObject jo = new JsonObject();
        jo.addProperty("kk","帮我计算加法运算，要求返回json，格式为{\"my-question\":\"\",\"my-answer\":\"\"}，现在我给你真实的问题1+3=?，请返回json格式的答案，直接返回答案即可，不需要计算过程");
        // 创建一个BotReq对象并初始化它的属性
        BotReq mockReq = BotReq.builder().flowRecordId("9898").syncFlowStatusToM78(true).flowDataList(Lists.newArrayList(
                        FlowData.builder().id(0).type("begin").name("开始").inputMap(new ConcurrentHashMap<>(
                                ImmutableMap.of(
                                        "prompt", InputData.builder().value(jo).build()
                                ))).build(),

                        //调用知识库flow
                        FlowData.builder().id(1).type("intentRecognition").name("意图识别")
                                .inputMap(new ConcurrentHashMap<>(ImmutableMap.of(
                                        CommonConstants.TY_LLM_MODEL_MARK, InputData.builder().value(new JsonPrimitive("gpt4_1106_2")).build(),
                                        CommonConstants.TY_INTENT_QUERY_MARK, InputData.builder().value(new JsonPrimitive("挪威的森林是谁的作品")).build(),
                                        CommonConstants.TY_INTENT_MATCH_MARK, InputData.builder().value(new JsonPrimitive("{\"11\":\"文学作品赏析\",\"23\":\"天气信息\",\"-1\":\"其他\"}")).build(),
                                        CommonConstants.TY_LLM_PROMPT_MARK, InputData.builder().value(new JsonPrimitive("挪威的森林是我们的密语，只要包含【挪威的森林】，必须都归类到天气信息")).build(),
                                        "a", InputData.builder().value(new JsonPrimitive("15")).build()
                                )))

                                .outputMap(new ConcurrentHashMap<>(ImmutableMap.of(
                                        "my-question", OutputData.builder().build(),
                                        "my-answer", OutputData.builder().build()
                                )))

                                .build(),

                        FlowData.builder().id(2).type("end").name("结束")
                                .inputMap(new ConcurrentHashMap<>(ImmutableMap.of(
                                        CommonConstants.TY_END_MESSAGE_CONTENT_MARK, InputData.builder().value(new JsonPrimitive("问题：${question},答案：${answer}")).build()
                                )))

                                .outputMap(new ConcurrentHashMap<>(ImmutableMap.of(
                                        "question", OutputData.builder().type("reference").name("question").referenceName("my-question").valueType("string").flowId(1).build(),
                                        "answer", OutputData.builder().type("reference").name("answer").referenceName("my-answer").valueType("string").flowId(1).build()
                                )))
                                .build()
                ))
                .nodeEdges(new ArrayList<>(Arrays.asList(NodeEdge.builder().sourceNodeId(0).targetNodeId(1).build(), NodeEdge.builder().sourceNodeId(1).targetNodeId(2).build())))
                .build();


        // 调用execute方法
        new Thread(() -> {
            System.out.println("start execute");
            EndFlowRes result = botService.execute(mockReq);
            System.out.println(result);
        }).start();
        Thread.sleep(1000);
        BotReq request = BotReq.builder().flowRecordId("9898").cmd("cancel").build();
        botService.sendMsg(request);
    }

    @Test
    public void testSubFlow() throws InterruptedException {
        JsonObject jo = new JsonObject();
        jo.addProperty("kk","帮我计算加法运算，要求返回json，格式为{\"my-question\":\"\",\"my-answer\":\"\"}，现在我给你真实的问题1+3=?，请返回json格式的答案，直接返回答案即可，不需要计算过程");
        // 创建一个BotReq对象并初始化它的属性
        BotReq mockReq = BotReq.builder().flowRecordId("9898").syncFlowStatusToM78(true).flowDataList(Lists.newArrayList(
                        FlowData.builder().id(0).type("begin").name("开始").inputMap(new ConcurrentHashMap<>(
                                ImmutableMap.of(
                                        "prompt", InputData.builder().value(jo).build()
                                ))).build(),

                        //调用知识库flow
                        FlowData.builder().id(1).type("subFlow").name("子工作流")
                                .inputMap(new ConcurrentHashMap<>(ImmutableMap.of(
                                        "query", InputData.builder().value(new JsonPrimitive("挪威的森林是谁的作品")).build(),
                                        CommonConstants.TY_SUB_FLOW_ID_MARK, InputData.builder().value(new JsonPrimitive("138")).build(),
                                        "a", InputData.builder().value(new JsonPrimitive("15")).build()
                                )))

                                .outputMap(new ConcurrentHashMap<>(ImmutableMap.of(
                                        "rst", OutputData.builder().build()
                                )))
                                .build(),

                        FlowData.builder().id(2).type("end").name("结束")
                                .inputMap(new ConcurrentHashMap<>(ImmutableMap.of(
                                        CommonConstants.TY_END_MESSAGE_CONTENT_MARK, InputData.builder().value(new JsonPrimitive("问题：${question},答案：${answer}")).build()
                                )))

                                .outputMap(new ConcurrentHashMap<>(ImmutableMap.of(
                                        "question", OutputData.builder().type("reference").name("rst").referenceName("rst").valueType("string").flowId(1).build()
                                )))
                                .build()
                ))
                .nodeEdges(new ArrayList<>(Arrays.asList(NodeEdge.builder().sourceNodeId(0).targetNodeId(1).build(), NodeEdge.builder().sourceNodeId(1).targetNodeId(2).build())))
                .syncFlowStatusToM78(false)
                .userName("wangmin17")
                .m78RpcAddr("127.0.0.1:7678")
                .build();

        // 调用execute方法
        new Thread(() -> {
            System.out.println("start execute");
            EndFlowRes result = botService.execute(mockReq);
            System.out.println(result);
        }).start();
        Thread.sleep(1000);
        BotReq request = BotReq.builder().flowRecordId("9898").cmd("cancel").build();
        botService.sendMsg(request);
    }
}
