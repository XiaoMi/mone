package run.mone.ai.codegen.bo;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author goodjava@qq.com, HawickMason@xiaomi.com
 * @date 7/12/24 14:15
 */
public enum FeatureGenerateType {

    CODE_WITH_GENERATOR(1, "使用mybatis-flex-generator生成"),

    CODE_WITH_TEMPLATE(2, "使用预制模板生成"),

    TABLE(3, "创建表");

    private final int code;

    private final String desc;

    private static final Map<Integer, FeatureGenerateType> valMap = Arrays.stream(values()).collect(Collectors.toMap(FeatureGenerateType::getCode, Function.identity()));

    FeatureGenerateType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static FeatureGenerateType getGenerateTypeByCode(int code) {
        return valMap.getOrDefault(code, CODE_WITH_TEMPLATE);
    }
}
