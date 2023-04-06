package com.xiaomi.mone.monitor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author zhangxiaowei6
 * @date 2023-02-22
 */
public class DashboardConstant {
    public static final String DEFAULT_FOLDER_NAME = "Hera";
    public static final String GRAFANA_API_KEY_NAME = "hera";
    public static final String GRAFANA_API_KEY_ROLE = "Admin";
    public static final String GRAFANA_USER_NAME = "admin";
    public static final String GRAFANA_PASSWORD = "admin";
    public static final String GRAFANA_DATASOURCE_URL = "http://prometheus";
    public static final String GRAFANA_DATASOURCE_NAME = "Prometheus";
    public static final String GRAFANA_DATASOURCE_TYPE = "prometheus";

    public static final String GRAFANA_FOLDER_UID = "Hera";

    public static List<String> GRAFANA_SRE_TEMPLATES = Arrays.asList("nodeMonitor","dockerMonitor","serviceMarket","resourceUtilization","dubboProviderOverview","dubboProviderMarket","dubboConsumerOverview","dubboConsumerMarket","httpServerMarket","httpServerOverview");
    public static final String JAEGER_QUERY_File_NAME = "jaegerQuery.ftl";
    public static final String DEFAULT_PANEL_ID_LIST = "110,148,152,112,116,118,150,122,120,126,124,130,128,132,134,136,138,140,142,144,146,52,56,58,60,66,95,96,50,82,68,78,74,76,102,104,106,146,159,163,168,169,170,171,172,173,174,176,178";
    public static final String DEFAULT_JAEGER_QUERY_JOB_NAME = "jaeger_query";
    public static final String DEFAULT_JVM_JOB_NAME = "mione-yewujiankong-china-jvm";
    public static final String DEFAULT_DOCKER_JOB_NAME = "mione-china-cadvisor-k8s";
    public static final String DEFAULT_NODE_JOB_NAME = "mione-china-node-k8s";
    public static final String DEFAULT_CUSTOMIZE_JOB_NAME = "mione-china-customize";

    public static final String DEFAULT_MIMONITOR_NACOS_CONFIG = "mimonitor_open_config";
    public static final String DEFAULT_MIMONITOR_NACOS_GROUP = "DEFAULT_GROUP";
}
