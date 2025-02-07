package run.mone.m78.service.dao.entity;



import run.mone.m78.api.constant.PromptConstant;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author wmin
 * @date 2024/1/16
 */
public enum ImageOperateType {

    extract_text(1, PromptConstant.PROMPT_EXTRACT_TEXT_FROM_IMAGE),

    extract_and_translate_img(2, PromptConstant.PROMPT_EXTRACT_TRANSLATE_TEXT_FROM_IMAGE);


    private final int code;
    private final String promptName;

    private static final Map<Integer, ImageOperateType> valMap = Arrays.stream(values()).collect(Collectors.toMap(ImageOperateType::getCode, Function.identity()));

    ImageOperateType(int code, String promptName) {
        this.code = code;
        this.promptName = promptName;
    }

    public int getCode() {
        return code;
    }

    public String getPromptName() {
        return promptName;
    }

    public static String getPromptNameByCode(Integer code) {
        if (code == null){
            return null;
        }
        return valMap.get(code).getPromptName();
    }
}
