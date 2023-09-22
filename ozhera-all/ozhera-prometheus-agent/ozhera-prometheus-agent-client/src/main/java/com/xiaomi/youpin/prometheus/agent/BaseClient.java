package com.xiaomi.youpin.prometheus.agent;

import com.xiaomi.youpin.prometheus.agent.client.Client;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class BaseClient implements Client {

    /*public static  Map<String, Client> clients;

    @Value("${job.prometheus.enabled}")
    private boolean prometheusEnabled;

    @Value("${job.alertManager.enabled}")
    private boolean alertManagerEnabled;
    public static PrometheusClient PROMETHEUS_CLIENT = null;
    public static AlertManagerClient ALERTMANAGER_CLIENT = null;

    @Override
    public void GetLocalConfigs() {

    }

    @Override
    public void CompareAndReload() {

    }

   /* @PostConstruct
    public void init(){

        log.info("begin init client prometheusEnabled: {} , alertManagerEnabled: {}",prometheusEnabled,alertManagerEnabled);

        if (prometheusEnabled) {
            PROMETHEUS_CLIENT = new PrometheusClient();
            Register(Commons.PROMETHEUS,PROMETHEUS_CLIENT);
        } else {
            PROMETHEUS_CLIENT = null;
        }

        if (alertManagerEnabled) {
            ALERTMANAGER_CLIENT = new AlertManagerClient();
            Register(Commons.ALERT_MANAGER,ALERTMANAGER_CLIENT);
        } else {
            ALERTMANAGER_CLIENT = null;
        }

        clients.forEach(
                (name,client)-> {
                    client.GetLocalConfigs();
                    client.CompareAndReload();
                });
        PROMETHEUS_CLIENT.GetLocalConfigs();
        PROMETHEUS_CLIENT.CompareAndReload();
        ALERTMANAGER_CLIENT.GetLocalConfigs();
        ALERTMANAGER_CLIENT.CompareAndReload();
    }

    private void Register(String name,Client client) {
        clients.put(name,client);
    }*/
}
