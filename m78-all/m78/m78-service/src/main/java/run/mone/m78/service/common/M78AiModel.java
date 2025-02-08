package run.mone.m78.service.common;

/**
 * @author goodjava@qq.com
 * @date 2024/3/14 23:11
 */
public enum M78AiModel {

    glm4,
    gpt4_1106_2,
    moonshot,

    moonshot_32k,

    moonshot_128k;

    // 根据枚举字面量获取M78AiModel
    public static M78AiModel getAiModelByLiteral(String literal) {
        for (M78AiModel modelEnum : M78AiModel.values()) {
            if (modelEnum.name().equalsIgnoreCase(literal)) {
                return modelEnum;
            }
        }
        return gpt4_1106_2;
    }
}
