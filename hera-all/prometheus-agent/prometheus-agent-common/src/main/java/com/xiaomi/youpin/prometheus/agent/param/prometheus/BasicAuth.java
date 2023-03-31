package com.xiaomi.youpin.prometheus.agent.param.prometheus;

import lombok.Data;

import java.io.Serializable;

@Data
public class BasicAuth implements Serializable {
    private String username;
    private String password;
}
