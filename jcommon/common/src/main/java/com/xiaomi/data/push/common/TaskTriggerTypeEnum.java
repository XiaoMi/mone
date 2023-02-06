package com.xiaomi.data.push.common;

/**
 * @author wmin
 * @date 2021/11/2
 */
public enum TaskTriggerTypeEnum {

    AUTOMATIC(0, "automatic"),
    MANUAL(1, "manual");

    private int id;
    private String status;

    public int getId () {return id;}

    public String getStatus() { return status; }

    TaskTriggerTypeEnum(int id, String status) {
        this.id = id;
        this.status = status;
    }
}
