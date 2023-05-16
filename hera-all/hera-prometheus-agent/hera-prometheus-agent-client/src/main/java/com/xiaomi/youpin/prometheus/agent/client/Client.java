package com.xiaomi.youpin.prometheus.agent.client;

public interface Client {
    public void GetLocalConfigs();
    public void CompareAndReload();
}
