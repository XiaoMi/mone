package com.google.a2a.common.types;

/**
 * 表示A2A任务的状态
 */
public enum TaskState {
    SUBMITTED("submitted"),
    WORKING("working"),
    INPUT_REQUIRED("input-required"),
    COMPLETED("completed"),
    CANCELED("canceled"),
    FAILED("failed"),
    UNKNOWN("unknown");

    private final String value;

    TaskState(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static TaskState fromValue(String value) {
        for (TaskState state : TaskState.values()) {
            if (state.value.equals(value)) {
                return state;
            }
        }
        return UNKNOWN;
    }
} 