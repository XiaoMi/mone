package com.xiaomi.youpin.tesla.ip.bo;

public enum Action {

    CHAT(1, "chat"),
    GENERATE_CODE(2, "generate_code"),
    CODE_SUGGESTION(3, "code_suggestion"),
    GENERATE_COMMENT(4, "generate_comment"),
    SMART_NAMING(5, "smart_naming"),
    GIT_PUSH(6, "git_push"),
    UNIT_TEST(7, "unit_test"),
    BUG_FIX(8, "bug_fix"),
    INSERT_CODE_FROM_CHAT(9, "insert_code_from_chat"),
    INLAY(10, "inlay");

    private final int code;
    private final String name;

    Action(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static Action fromCode(int code) {
        for (Action action : Action.values()) {
            if (action.getCode() == code) {
                return action;
            }
        }
        throw new IllegalArgumentException("Invalid code: " + code);
    }

    public static Action fromName(String name) {
        for (Action action : Action.values()) {
            if (action.getName().equalsIgnoreCase(name)) {
                return action;
            }
        }
        throw new IllegalArgumentException("Invalid name: " + name);
    }
}

