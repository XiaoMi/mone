package com.xiaomi.youpin.prometheus.agent.domain;

import java.util.List;

public class Ips {

    private List<String> targets;

    public List<String> getTargets() {
        return targets;
    }

    public void setTargets(List<String> targets) {
        this.targets = targets;
    }
}
