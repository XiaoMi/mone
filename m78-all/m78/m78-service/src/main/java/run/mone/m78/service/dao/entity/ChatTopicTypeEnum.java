package run.mone.m78.service.dao.entity;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


public enum ChatTopicTypeEnum {

    AI_CHAT(0, "ai chat"),
    M78_PROBOT_DEBUG(1, "m78 probot debug"),
    M78_PROBOT_CHAT(2, "m78 probot chat"),
    APP_PROBOT_CHAT(3, "app probot chat"),
    IDEA_CHAT(4, "idea chat");

    private final int code;
    private final String typeName;

    private static final Map<Integer, ChatTopicTypeEnum> valMap = Arrays.stream(values()).collect(Collectors.toMap(ChatTopicTypeEnum::getCode, Function.identity()));

    private static final Map<String, ChatTopicTypeEnum> nameMap = Arrays.stream(values()).collect(Collectors.toMap(ChatTopicTypeEnum::getTypeName, Function.identity()));

    ChatTopicTypeEnum(int code, String typeName) {
        this.code = code;
        this.typeName = typeName;
    }

    public int getCode() {
        return code;
    }

    public String getTypeName() {
        return typeName;
    }

}
