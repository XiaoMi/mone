package com.xiaomi.mone.log.manager.model.bo.alert;

public enum AlertStatus {

    OFF(0),
    ON(1),
    MISSING_FLINK_JOB(2);

    private int status;

    private AlertStatus(int status) {
        this.status = status;
    }


    public int getStatus() {
        return status;
    }
}
