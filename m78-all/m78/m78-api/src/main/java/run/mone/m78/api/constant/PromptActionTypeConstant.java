package run.mone.m78.api.constant;


import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author wmin
 * @date 2024/2/23
 */
public enum PromptActionTypeConstant {

    TRANSLATE("translate", PromptConstant.PROMPT_TEXT_TRANSLATE_STREAM),
    ARTICLE_COMPLETE("article_complete", PromptConstant.PROMPT_ARTICLE_COMPLETE),
    CODE_LANGUAGE_IDENTIFY("code_language_identify", PromptConstant.PROMPT_CODE_LANGUAGE_IDENTIFY),
    CODE_COMMENT("code_comment", PromptConstant.PROMPT_CODE_COMMENT),
    CODE_EXPLAIN("code_explain", PromptConstant.PROMPT_CODE_COMMENT_2),
    GOLANG_CODE_EXPLAIN("golang_code_explain", PromptConstant.PROMPT_GOLANG_CODE_COMMENT_2),
    GENERAL_PROMPT_TURBO("general_prompt_turbo", PromptConstant.PROMPT_GENERAL_TURBO);

    private final String action;
    private final String promptName;

    private static final Map<String, PromptActionTypeConstant> actionMap = Arrays.stream(values()).collect(Collectors.toMap(PromptActionTypeConstant::getAction, Function.identity()));

    PromptActionTypeConstant(String action, String promptName) {
        this.action = action;
        this.promptName = promptName;
    }

    public String getAction() {
        return action;
    }

    public String getPromptName() {
        return promptName;
    }

    public static PromptActionTypeConstant getEnumByAction(String action) {
        return actionMap.get(action);
    }

}
