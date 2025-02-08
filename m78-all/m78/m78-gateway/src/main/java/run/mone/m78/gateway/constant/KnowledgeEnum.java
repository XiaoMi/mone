package run.mone.m78.gateway.constant;

import run.mone.knowledge.api.enums.KnowledgeBaseFileTypeEnum;

public enum KnowledgeEnum {

    JSW("jsw"),
    KBS("kbs"),
    KNOWLEDGE("knowledge")
    ;

    private String type;

    KnowledgeEnum(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }


    public static KnowledgeEnum of(String type) {
        for (KnowledgeEnum typeEnum : KnowledgeEnum.values()) {
            if (typeEnum.getType().equals(type)) {
                return typeEnum;
            }
        }

        return null;
    }
}
