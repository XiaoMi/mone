package com.xiaomi.miapi.api.service.bo;

import lombok.Data;

import java.io.Serializable;

@Data
public class MiApiData implements Serializable {
    private int projectNum;

    private int apiNum;

    public int getProjectNum() {
        return projectNum;
    }

    public void setProjectNum(int projectNum) {
        this.projectNum = projectNum;
    }

    public int getApiNum() {
        return apiNum;
    }

    public void setApiNum(int apiNum) {
        this.apiNum = apiNum;
    }
}
