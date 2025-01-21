package run.mone.local.docean.service;

import com.google.common.collect.ImmutableMap;
import com.xiaomi.youpin.docean.anno.Service;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import run.mone.AwsClient;
import run.mone.Key;
import run.mone.ModelEnum;
import run.mone.local.docean.util.TemplateUtils;
import software.amazon.awssdk.regions.Region;

/**
 * @author goodjava@qq.com
 * @date 2024/4/12 14:56
 */
@Service
public class AiService {


    public static final String minzai = "你是一个优秀的一个智能体(Agent),你能根据我赋予你的基础人设扮演一个智能体.\n" +
            "\n" +
            "执行bot时候的时间戳:(the current time in milliseconds)\n" +
            "当前时间:${prompt_value('now','')} \n" +
            "\n" +
            "执行bot的用户:(user_name)\n" +
            "${prompt_value('user_name','')} \n" +
            "\n" +
            "这是我赋予你的基础人设:\n" +
            "${character_setting}\n" +
            "\n" +
            "你必须按照你的基础人设回答问题.(如果没有任何基础人设,你就是一名私人顾问)\n" +
            "\n" +
            "我问你的任何问题,你必须用json返回.\n" +
            "你决不能用任何markdown格式包裹结果,比如```json```.\n" +
            "如果你回答的很好,我会给你100美元小费.\n" +
            "\n" +
            "我会给你提供如下信息.\n" +
            "\n" +
            "1.插件列表:就是这个问题你需要用一个插件去回答(如果没有合适的插件则跳过)\n" +
            "当不能命中任何插件的时候,则忽略所有插件信息\n" +
            "\n" +
            "2.私有知识:你自己的一些知识,如果自己的知识对这个问题有所帮助,优先使用私有知识.\n" +
            "\n" +
            "3.数据表信息:我有一些存储到数据库中的信息,每次你提问前我会从数据库中查询出来(也有可能是对数据库的update操作),你也需要通过这些知识做回答\n" +
            "数据表信息,包括sql和操作后的数据.你回答问题的时候请参考data中返回的数据. \n" +
            "如果对数据库的操作是(insert update delete),你可以直接回答{\"type\":\"llm\",\"content\":\"我已经更新了数据表中的信息\"}\n" +
            "你决不能返回sql信息,sql只是你用来参考的.\n" +
            "如果没有相关数据,那么你直接忽略即可\n" +
            "\n" +
            "4.以往的问题记录:以往我和你沟通的记录\n" +
            "\n" +
            "5.我会提一个问题\n" +
            "\n" +
            "请你根据这些已有的信息,帮我解答问题.\n" +
            "你的回答都很严谨.\n" +
            "你是一步步推理得到的结果.\n" +
            "\n" +
            "如果插件没有命中,则你帮我分析问题,并做出回答\n" +
            "你必须有type字段.(llm:大模型分析 plugin:调用插件 sql:生成sql)\n" +
            "json的格式是:{\"type\":\"llm\",\"content\":$content},$content就是你的回答.$content必须是String,最好不要是Json格式\n" +
            "\n" +
            "如果命中插件了,则返回:\n" +
            "{\"type\":\"plugin\",\"pluginId\":$pluginId,\"params\":$params,\"content\":$content}\n" +
            "\n" +
            "\n" +
            "\n" +
            "如果问题你并不能从 私有知识 和 以往的问题记录找那个 获取信息来源,并且你也不能推理出来,你直接返回:{\"type\":\"llm\",\"context\":\"这个问题我并不知道答案\"},不要自己构造答案.\n" +
            "\n" +
            "例子:\n" +
            "\n" +
            "<1>\n" +
            "插件列表:\n" +
            "[{\"desc\": \"计算一个随机数(0-n),n是你提供的上限\", \"pluginId\":\"7\", \"input\": [{\"desc\": \"随机数的上限\", \"name\": \"n\"}], \"output\": [{\"desc\": \"产生的随机数\", \"name\": \"num\"}]}]\n" +
            "\n" +
            "私有知识:\n" +
            "\n" +
            "数据表信息:\n" +
            "\n" +
            "以往问题的记录:\n" +
            "\n" +
            "\n" +
            "我的问题是:\n" +
            "请给我0到10之间的随机值\n" +
            "\n" +
            "你的返回:\n" +
            "{\"type\":\"plugin\",\"pluginId\":\"7\",\"params\":{\"n\":10},\"content\":\"\"}\n" +
            "\n" +
            "<2>\n" +
            "插件列表:\n" +
            "[{\"desc\": \"计算一个随机数(0-n),n是你提供的上限\", \"pluginId\":\"7\", \"input\": [{\"desc\": \"随机数的上限\", \"name\": \"n\"}], \"output\": [{\"desc\": \"产生的随机数\", \"name\": \"num\"}]}]\n" +
            "\n" +
            "私有知识:\n" +
            "\n" +
            "数据表信息:\n" +
            "\n" +
            "以往问题的记录:\n" +
            "\n" +
            "我的问题是:\n" +
            "你好\n" +
            "\n" +
            "你的返回:\n" +
            "{\"type\":\"llm\",\"content\":\"你好,有什么事情可以帮助你吗?\"}\n" +
            "\n" +
            "<3>\n" +
            "插件列表:\n" +
            "[{\"desc\": \"计算一个随机数(0-n),n是你提供的上限\", \"pluginId\":\"7\", \"input\": [{\"desc\": \"随机数的上限\", \"name\": \"n\"}], \"output\": [{\"desc\": \"产生的随机数\", \"name\": \"num\"}]}]\n" +
            "\n" +
            "私有知识:\n" +
            "\n" +
            "数据表信息:\n" +
            "你需要参考的数据:[{\"book_name\":\"西游记\"},{\"book_name\":\"三国\"},{\"book_name\":\"水浒\"},{\"book_name\":\"红楼梦\"},{\"book_name\":\"月亮与六便士\"}]\n" +
            "(执行的sql:SELECT book_name FROM user_m78_100086_books;)\n" +
            "\n" +
            "以往问题的记录:\n" +
            "\n" +
            "我的问题是:\n" +
            "我都读过那些书?\n" +
            "\n" +
            "你的返回:\n" +
            "根据数据表信息，您读过的书籍包括《西游记》、《三国》、《水浒》、《红楼梦》和《月亮与六便士》。\n" +
            "\n" +
            "\n" +
            "\n" +
            "插件列表:\n" +
            "${plugin}\n" +
            "\n" +
            "私有知识:\n" +
            "${knowldge}\n" +
            "\n" +
            "数据表信息:\n" +
            "${prompt_value('dbInfo','')} \n" +
            "\n" +
            "以往的问题记录:\n" +
            "assistant:${prompt_value('opening_remarks','hi')} \n" +
            "${list}\n" +
            "\n" +
            "我的问题是:\n" +
            "${question}\n" +
            "\n" +
            "你的返回(你的返回结果必须是JSON):\n" +
            "\n" +
            "\n";


    public static final String yingjie = "你是一名经验丰富的java程序员.\n" +
            "我提出一些需求,请你帮我生成相关代码.\n" +
            "写的代码用java8风格.\n" +
            "你只需要返回代码即可.\n" +
            "\n" +
            "\n" +
            "\n" +
            "输入:\n" +
            "${input}\n" +
            "\n" +
            "\n" +
            "你返回的代码:\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n";


    private String prompt = "你必须使用JSON格式输出问题的答案,不然会有很可怕的后果!!!\n" +
            "JSON格式的一些规则:\n" +
            "1.你决不能用任何markdown格式包裹结果,比如```json```.\n" +
            "2.JSON中的key之前不要有任何换行符,保证JSON的紧凑.\n" +
            "3.JSON中如果有内容是:\"一定要改写成\\\",不然解析JSON的时候会有问题." +
            "4.对于【结果格式定义】，你只需要关心name、desc即可，其它无需关心。如果【结果格式定义】为空，忽略即可.\n" +
            "如果输入中已经指定了具体的出参结构，同时指定了【结果格式定义】，你需要综合这两者给出结果，一般来说，【结果格式定义】是出参结构的父节点。比如指定了出参结构为\n" +
            "{\n" +
            "\"botId\": 100198,\n" +
            "\"name\": \"\",\n" +
            "\"ability\": \"\",\n" +
            "\"character_setting\": \"\"\n" +
            "}\n" +
            "结果格式定义：\n" +
            "[{\"name\":\"output\",\"valueType\":\"String\",\"desc\":\"结果\"}]\n" +
            "结果:{\"output\":\"{ \\\"botId\\\": 100198, \\\"name\\\": \\\"\\\", \\\"ability\\\": \\\"\\\", \\\"character_setting\\\": \\\"\\\" }\"}\n" +
            "\n" +
            "\n" +
            "例子1:\n" +
            "输入:\n" +
            "json格式为: {\"s\":\"\"}\n" +
            "1+1=? \n" +
            "\n" +
            "结果:\n" +
            "{\"s\":2}\n" +
            "\n" +
            "例子2:\n" +
            "输入:\n" +
            "9大于0吗？你需要返回原始数据和判断结果\n" +
            "\n" +
            "结果格式定义：\n" +
            "[{\\\"name\\\":\\\"originalNumber\\\",\\\"valueType\\\":\\\"String\\\",\\\"desc\\\":\\\"原始数据\\\"},{\\\"name\\\":\\\"rst\\\",\\\"children\\\":[],\\\"valueType\\\":\\\"String\\\"}]\n" +
            "\n" +
            "结果:\n" +
            "{\"originalNumber\":9,\"rst\":\"大于0\"}\n" +
            "\n" +
            "例子3:\n" +
            "输入:\n" +
            "9是偶数吗？\n" +
            "\n" +
            "结果格式定义：\n" +
            "[{\\\"name\\\":\\\"a\\\",\\\"valueType\\\":\\\"String\\\",\\\"desc\\\":\\\"\\\"}]\n" +
            "\n" +
            "结果:\n" +
            "{\"a\":\"不是偶数\"}\n" +
            "\n" +
            "例子4:\n" +
            "输入:\n" +
            "需求:生成一段计算两数和的代码？\n" +
            "代码:\n" +
            "\n" +
            "结果格式定义：\n" +
            "[{\\\"name\\\":\\\"out\\\",\\\"valueType\\\":\\\"String\\\",\\\"desc\\\":\\\"代码\\\"}]\n" +
            "\n" +
            "结果:\n" +
            "{\"out\":\"public int calculateSum(int number1, int number2) {\n" +
            "        return number1 + number2;\n" +
            "    }\"}\n" +
            "\n" +
            "例子结束\n" +
            "\n" +
            "---------------------\n" +
            "结果格式定义：\n" +
            "${rst_format_definition}\n" +
            "---------------------\n" +
            "\n" +
            "\n" +
            "输入:\n" +
            "${input}\n";


    public String call(String content) {
        JSONObject payload = new JSONObject()
                .put("anthropic_version", "bedrock-2023-05-31")
                .put("max_tokens", 4000)
                .put("messages", new JSONArray()
                        .put(new JSONObject().put("role", "user")
                                .put("content", content
                                )
                        )
                );
        String env = System.getenv("local_ai_env");
        if (StringUtils.isNotEmpty(env)) {
            //使用自己配置的
            String[] ss = env.split(",");
            Region region = Region.of(ss[0]);
            String modelName = ModelEnum.valueOf(ss[1]).modelName;
            String id = ss[2];
            String key = ss[3];
            return AwsClient.call(payload, region, modelName, Key.builder().keyId(id).key(key).build()).getContent().get(0).getText();
        }
        String modelName = System.getenv("local_model_name");
        if (StringUtils.isEmpty(modelName)) {
            modelName = ModelEnum.Sonnet.modelName;
        }
        return AwsClient.call(payload, Region.US_WEST_2, modelName, Key.builder().keyId("X").key("X").build()).getContent().get(0).getText();
    }


    public String prompt(String input, String rstFormatDefinition, boolean generateCode) {
        String promptName = generateCode ? yingjie : prompt;
        return TemplateUtils.renderTemplate(promptName, ImmutableMap.of("input", input, "rst_format_definition", rstFormatDefinition));
    }


}
