package com.xiaomi.data.push.common;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zhangzhiyong
 * @date 28/05/2018
 */
@Data
public class Health implements Serializable {

    private String version = "0.0.2";

    private String commitDate = "20190404";

    private long qps = 0L;

    public Health(String version, String commitDate, long qps) {
        this.version = version;
        this.commitDate = commitDate;
        this.qps = qps;
    }

    public Health(String version, String commitDate) {
        this.version = version;
        this.commitDate = commitDate;
    }

    public Health() {
    }

    @Override
    public String toString() {
        return "Version{" +
                "version='" + version + '\'' +
                ", commitDate='" + commitDate + '\'' +
                ", qps=" + qps +
                '}';
    }
}
