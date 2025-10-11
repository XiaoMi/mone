package run.mone.mcp.yijing.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import run.mone.hive.llm.LLM;
import run.mone.hive.schema.AiMessage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 易经数字卦计算服务
 * 负责卦象计算和LLM解析
 *
 * @author assistant
 */
@Service
public class YijingService {

    private static final Logger log = LoggerFactory.getLogger(YijingService.class);

    @Autowired
    private LLM llm;

    // 八卦对应关系
    private static final Map<Integer, String> GUA_MAP = new HashMap<>();
    static {
        GUA_MAP.put(1, "乾");
        GUA_MAP.put(2, "兑");
        GUA_MAP.put(3, "离");
        GUA_MAP.put(4, "震");
        GUA_MAP.put(5, "巽");
        GUA_MAP.put(6, "坎");
        GUA_MAP.put(7, "艮");
        GUA_MAP.put(8, "坤");
    }

    // 六十四卦对应关系
    private static final Map<String, String> COMPOUND_GUA_MAP = new HashMap<>();
    static {
        // 乾卦组合
        COMPOUND_GUA_MAP.put("乾乾", "乾为天卦");
        COMPOUND_GUA_MAP.put("坤坤", "坤为地卦");
        COMPOUND_GUA_MAP.put("坎坤", "水地比卦");
        COMPOUND_GUA_MAP.put("艮坤", "山地剥卦");
        COMPOUND_GUA_MAP.put("巽坤", "风地观卦");
        COMPOUND_GUA_MAP.put("兑坤", "泽地萃卦");
        COMPOUND_GUA_MAP.put("离坤", "火地晋卦");
        COMPOUND_GUA_MAP.put("震坤", "雷地豫卦");
        COMPOUND_GUA_MAP.put("乾坎", "天水讼卦");
        COMPOUND_GUA_MAP.put("坤坎", "地水师卦");
        COMPOUND_GUA_MAP.put("坎坎", "水为坎卦");
        COMPOUND_GUA_MAP.put("艮坎", "山水蒙卦");
        COMPOUND_GUA_MAP.put("巽坎", "风水涣卦");
        COMPOUND_GUA_MAP.put("兑坎", "泽水困卦");
        COMPOUND_GUA_MAP.put("离坎", "火水未济卦");
        COMPOUND_GUA_MAP.put("震坎", "雷水解卦");
        COMPOUND_GUA_MAP.put("乾艮", "天山遁卦");
        COMPOUND_GUA_MAP.put("坤艮", "地山谦卦");
        COMPOUND_GUA_MAP.put("坎艮", "水山蹇卦");
        COMPOUND_GUA_MAP.put("艮艮", "艮为山卦");
        COMPOUND_GUA_MAP.put("巽艮", "风山渐卦");
        COMPOUND_GUA_MAP.put("兑艮", "泽山小过卦");
        COMPOUND_GUA_MAP.put("离艮", "火山旅卦");
        COMPOUND_GUA_MAP.put("震艮", "雷山小过卦");
        COMPOUND_GUA_MAP.put("乾巽", "天风姤卦");
        COMPOUND_GUA_MAP.put("坤巽", "地风升卦");
        COMPOUND_GUA_MAP.put("坎巽", "水风井卦");
        COMPOUND_GUA_MAP.put("艮巽", "山风蛊卦");
        COMPOUND_GUA_MAP.put("巽巽", "巽为风卦");
        COMPOUND_GUA_MAP.put("兑巽", "泽风大过卦");
        COMPOUND_GUA_MAP.put("离巽", "火风鼎卦");
        COMPOUND_GUA_MAP.put("震巽", "雷风恒卦");
        COMPOUND_GUA_MAP.put("乾离", "天火同人卦");
        COMPOUND_GUA_MAP.put("坤离", "地火明夷卦");
        COMPOUND_GUA_MAP.put("坎离", "水火既济卦");
        COMPOUND_GUA_MAP.put("艮离", "山火贲卦");
        COMPOUND_GUA_MAP.put("巽离", "风火家人卦");
        COMPOUND_GUA_MAP.put("兑离", "泽火革卦");
        COMPOUND_GUA_MAP.put("离离", "离为火卦");
        COMPOUND_GUA_MAP.put("震离", "雷火丰卦");
        COMPOUND_GUA_MAP.put("乾震", "天雷无妄卦");
        COMPOUND_GUA_MAP.put("坤震", "地雷复卦");
        COMPOUND_GUA_MAP.put("坎震", "水雷屯卦");
        COMPOUND_GUA_MAP.put("艮震", "山雷颐卦");
        COMPOUND_GUA_MAP.put("巽震", "风雷益卦");
        COMPOUND_GUA_MAP.put("兑震", "泽雷随卦");
        COMPOUND_GUA_MAP.put("离震", "火雷噬嗑卦");
        COMPOUND_GUA_MAP.put("震震", "震为雷卦");
        COMPOUND_GUA_MAP.put("乾兑", "天泽履卦");
        COMPOUND_GUA_MAP.put("坤兑", "地泽临卦");
        COMPOUND_GUA_MAP.put("坎兑", "水泽节卦");
        COMPOUND_GUA_MAP.put("艮兑", "山泽损卦");
        COMPOUND_GUA_MAP.put("巽兑", "风泽中孚卦");
        COMPOUND_GUA_MAP.put("兑兑", "兑为泽卦");
        COMPOUND_GUA_MAP.put("离兑", "火泽睽卦");
        COMPOUND_GUA_MAP.put("震兑", "雷泽归妹卦");
    }

    /**
     * 进行易经数字卦计算和解析
     * @param num1 第一个数字
     * @param num2 第二个数字
     * @param num3 第三个数字
     * @param question 用户问题
     * @return 包含卦象信息和LLM解析的Flux流
     */
    public Flux<String> calculateYijingGua(int num1, int num2, int num3, String question) {
        log.info("开始易经数字卦计算，数字：{}, {}, {}，问题：{}", num1, num2, num3, question);

        // 计算下卦
        String lowerGua = calculateGua(num1);
        // 计算上卦
        String upperGua = calculateGua(num2);
        // 计算变爻
        String changeYao = calculateChangeYao(num3);
        // 计算复卦
        String compoundGua = calculateCompoundGua(upperGua, lowerGua);

        // 构建给LLM的prompt
        String prompt = buildAnalysisPrompt(compoundGua, changeYao, question);

        // 调用LLM进行解析
        return llm.call(List.of(new AiMessage("user", prompt)));
    }

    /**
     * 计算卦象
     * @param num 输入数字
     * @return 对应的卦象
     */
    private String calculateGua(int num) {
        int remainder = num % 8;
        if (remainder == 0) {
            remainder = 8; // 如果正好能除尽，就用八坤为卦
        }
        return GUA_MAP.get(remainder);
    }

    /**
     * 计算变爻
     * @param num 输入数字
     * @return 对应的变爻
     */
    private String calculateChangeYao(int num) {
        int remainder = num % 6;
        if (remainder == 0) {
            remainder = 6; // 如果正好能除尽，就用上爻
        }

        String[] yaoNames = {"初爻", "二爻", "三爻", "四爻", "五爻", "上爻"};
        return yaoNames[remainder - 1];
    }

    /**
     * 计算复卦
     * @param upperGua 上卦
     * @param lowerGua 下卦
     * @return 复卦名称
     */
    private String calculateCompoundGua(String upperGua, String lowerGua) {
        String key = upperGua + lowerGua;
        return COMPOUND_GUA_MAP.getOrDefault(key, upperGua + lowerGua + "卦");
    }

    /**
     * 构建给LLM的分析prompt
     * @param compoundGua 复卦
     * @param changeYao 变爻
     * @param question 问题
     * @return 构建好的prompt
     */
    private String buildAnalysisPrompt(String compoundGua, String changeYao, String question) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("你是一名易经专家，请根据以下卦象信息进行解析：\n\n");

        prompt.append("复卦：").append(compoundGua).append("\n");
        prompt.append("变爻：").append(changeYao).append("\n");
        prompt.append("问题：").append(question).append("\n\n");

        prompt.append("请根据这个卦象和变爻，结合易经的智慧，针对用户的问题给出专业的解析和建议。");

        return prompt.toString();
    }

    /**
     * 获取卦象计算结果（不包含LLM解析）
     * @param num1 第一个数字
     * @param num2 第二个数字
     * @param num3 第三个数字
     * @return 卦象计算结果
     */
    public Map<String, String> getGuaCalculation(int num1, int num2, int num3) {
        Map<String, String> result = new HashMap<>();
        
        String lowerGua = calculateGua(num1);
        String upperGua = calculateGua(num2);
        String changeYao = calculateChangeYao(num3);
        String compoundGua = calculateCompoundGua(upperGua, lowerGua);
        
        result.put("lowerGua", lowerGua);
        result.put("upperGua", upperGua);
        result.put("changeYao", changeYao);
        result.put("compoundGua", compoundGua);
        
        return result;
    }
}
