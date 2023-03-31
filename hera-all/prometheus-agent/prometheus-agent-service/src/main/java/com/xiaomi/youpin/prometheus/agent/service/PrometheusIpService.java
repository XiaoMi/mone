package com.xiaomi.youpin.prometheus.agent.service;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.api.naming.pojo.ListView;
import com.google.common.base.Stopwatch;
import com.google.gson.Gson;
import com.xiaomi.data.push.nacos.NacosNaming;
import com.xiaomi.youpin.prometheus.agent.domain.Ips;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1NodeAddress;
import io.kubernetes.client.openapi.models.V1NodeList;
import io.kubernetes.client.util.Config;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PrometheusIpService {

    @Autowired
    private NacosNaming nacosNaming;

    private static ConcurrentHashMap<String, Set<String>> appIpsCache = new ConcurrentHashMap<>();

    public static final int PAGINATION_SIZE = 200;

    public static final String PROMETHEUS_PORT = "prometheus_port";

    public static final String JAVAAGENT_PROMETHEUS_PORT = "javaagent_prometheus_port";

    public static final String JAEGERQUERY_PROMETHEUS_PORT = "jaegerQuery_port";

    public static final String MONESTARTER_PROMETHEUS_PORT = "monestarter_prometheus_port";

    public static final String APPLICATION = "application";

    public static final String[] TESLA_FLAG = new String[]{""};

    public static final String ST_K8S_NODE = "http://localhost";

    public static final String ONLINE_K8S_NODE = "http://localhost";

    private final Gson gson = new Gson();

    private List<Ips> starterIpsList = new ArrayList<>();
    private List<Ips> javaagentIpsList = new ArrayList<>();
    private List<Ips> jaegerqueryIpsList = new ArrayList<>();
    private List<Ips> monequeryIpsList = new ArrayList<>();

    @Value("${server.type}")
    private String serverType;

    @NacosValue(value = "${mione.k8s.node.port}", autoRefreshed = true)
    private String machinePort;

    @NacosValue(value = "${mione.k8s.container.port}", autoRefreshed = true)
    private String containerPort;

    @NacosValue(value = "${jaeger_query_token}")
    private String jaegerQueryToken;

    @PostConstruct
    public void init() {
        new ScheduledThreadPoolExecutor(1).scheduleWithFixedDelay(() -> {
            Stopwatch sw = Stopwatch.createStarted();
            log.info("开启异步获取nacos信息");
            ConcurrentHashMap<String, Set<String>> cache = new ConcurrentHashMap<>();
            Ips starterIps = new Ips();
            Ips javaagentIps = new Ips();
            Ips jaegerqueryIps = new Ips();
            Ips moneStarterIps = new Ips();
            Set<String> starterAddresssSet = new HashSet<>();
            Set<String> javaagentAddresssSet = new HashSet<>();
            Set<String> jaegerqueryAddresssSet = new HashSet<>();
            Set<String> monestarterPrometheusPortSet = new HashSet<>();
            List<Ips> starterIpsListTmp = new ArrayList<>();
            List<Ips> javaagentIpsListTmp = new ArrayList<>();
            List<Ips> jaegerqueryIpsListTmp = new ArrayList<>();
            List<Ips> monestarterIpsListTmp = new ArrayList<>();
            List<String> teslaFlag = Arrays.asList(TESLA_FLAG);
            try {
                Set<String> serviceNames = getAllServiceNames();
                log.info("service num:{} {}", serviceNames.size(), sw.elapsed(TimeUnit.MILLISECONDS));
                if (serviceNames != null && serviceNames.size() > 0) {
                    serviceNames.stream().forEach(it -> {
                        if (it == null || it.startsWith("consumers:")) {
                            return;
                        }
                        List<Instance> instances = null;
                        try {
                            instances = nacosNaming.getAllInstances(it);
//                            log.info("service:{} num:{}", it, instances.size());
                        } catch (NacosException e) {
                            log.error("Nacos.getAllInstance error,", e);
                        }
                        //  TODO:区分tesla内外网
                        assert instances != null;
                        instances.forEach(it5 -> {
                            if (teslaFlag.contains(it)) {
                                Set<String> tmpIps = cache.getOrDefault(it, new HashSet<>());
                                tmpIps.add(it5.getIp() + ":" + it5.getPort());
                                cache.putIfAbsent(it, tmpIps);
                            }
                        });
                        List<String> starterAddrs = instances.stream().filter(it1 -> it1.getMetadata() != null && StringUtils.isNotEmpty(it1.getMetadata().get(PROMETHEUS_PORT))).map(it2 -> {
                            String ip = it2.getIp();
                            String port = it2.getMetadata().get(PROMETHEUS_PORT);
                            return ip + ":" + port;
                        }).collect(Collectors.toList());

                        List<String> javaagentAddrs = instances.stream().filter(it1 -> it1.getMetadata() != null && StringUtils.isNotEmpty(it1.getMetadata().get(JAVAAGENT_PROMETHEUS_PORT))).map(it2 -> {
                            String ip = it2.getIp();
                            String port = it2.getMetadata().get(JAVAAGENT_PROMETHEUS_PORT);
                            return ip + ":" + port;
                        }).collect(Collectors.toList());

                        List<String> jaegerquery = instances.stream().filter(it1 -> it1.getMetadata() != null && StringUtils.isNotEmpty(it1.getMetadata().get(JAEGERQUERY_PROMETHEUS_PORT))).map(it2 -> {
                            String ip = it2.getIp();
                            String port = it2.getMetadata().get(JAEGERQUERY_PROMETHEUS_PORT);
                            return ip + ":" + port;
                        }).collect(Collectors.toList());

                        List<String> moneStarterAddrs = instances.stream().filter(it1 -> it1.getMetadata() != null && StringUtils.isNotEmpty(it1.getMetadata().get(MONESTARTER_PROMETHEUS_PORT))).map(it2 -> {
                            String ip = it2.getIp();
                            String port = it2.getMetadata().get(MONESTARTER_PROMETHEUS_PORT);
                            return ip + ":" + port;
                        }).collect(Collectors.toList());

                        instances.stream().filter(it3 -> it3.getMetadata() != null && StringUtils.isNotEmpty(it3.getMetadata().get(APPLICATION))).forEach(it4 -> {
                            String appName = it4.getMetadata().get(APPLICATION);
                            Set<String> tmpIps = cache.getOrDefault(appName, new HashSet<>());
                            if (appName.equals("tesla-gateway")) {
                                tmpIps.add(it4.getIp() + ":8080");
                            } else {
                                tmpIps.add(it4.getIp());
                            }
                            cache.putIfAbsent(appName, tmpIps);
                        });

                        starterAddresssSet.addAll(starterAddrs);
                        javaagentAddresssSet.addAll(javaagentAddrs);
                        jaegerqueryAddresssSet.addAll(jaegerquery);
                        monestarterPrometheusPortSet.addAll(moneStarterAddrs);
                    });
                }

                appIpsCache = cache;

                starterIps.setTargets(new ArrayList<>(starterAddresssSet));
                javaagentIps.setTargets(new ArrayList<>(javaagentAddresssSet));
                jaegerqueryIps.setTargets(new ArrayList<>(jaegerqueryAddresssSet));
                moneStarterIps.setTargets(new ArrayList<>(monestarterPrometheusPortSet));

                starterIpsListTmp.add(starterIps);
                javaagentIpsListTmp.add(javaagentIps);
                jaegerqueryIpsListTmp.add(jaegerqueryIps);
                monestarterIpsListTmp.add(moneStarterIps);

                starterIpsList = starterIpsListTmp;
                javaagentIpsList = javaagentIpsListTmp;
                jaegerqueryIpsList = jaegerqueryIpsListTmp;
                monequeryIpsList = monestarterIpsListTmp;
                log.info("异步获取nacos信息完成 use time:{}", sw.elapsed(TimeUnit.MILLISECONDS));
            } catch (Exception ex) {
                log.error("PrometheusService.init, get nacos value error", ex);
            }
        }, 0, 15, TimeUnit.SECONDS);
    }

    private Set<String> getAllServiceNames() {

        final Set<String> serviceNames = new LinkedHashSet<>();

        int pageIndex = 1;
        ListView<String> listView = nacosNaming.getServicesOfServer(pageIndex, PAGINATION_SIZE);
        // First page data
        List<String> firstPageData = listView.getData();
        // Append first page into list
        serviceNames.addAll(firstPageData);
        // the total count
        int count = listView.getCount();
        // the number of pages
        int pageNumbers = count / PAGINATION_SIZE;
        int remainder = count % PAGINATION_SIZE;
        // remain
        if (remainder > 0) {
            pageNumbers += 1;
        }
        // If more than 1 page
        while (pageIndex < pageNumbers) {
            listView = nacosNaming.getServicesOfServer(++pageIndex, PAGINATION_SIZE);
            serviceNames.addAll(listView.getData());
        }

        return serviceNames;
    }

    public List<Ips> getByType(String type) {
        if (StringUtils.isEmpty(type)) {
            log.info("获取ip列表时， type : " + type + " 为空");
            List<Ips> defaultResult = new ArrayList<>();
            Ips ips = new Ips();
            ips.setTargets(new ArrayList<>());
            defaultResult.add(ips);
            return defaultResult;
        }
        // type 1:自定义打点  2:javaagent jvm指标   3:jaegerquery
        if ("1".equals(type)) {
            return starterIpsList;
        } else if ("2".equals(type)) {
            return javaagentIpsList;
        } else if ("3".equals(type)) {
            return jaegerqueryIpsList;
        } else if ("4".equals(type)) {
            return monequeryIpsList;
        } else {
            log.info("获取ip列表时， type : " + type + " 非法");
            List<Ips> defaultResult = new ArrayList<>();
            Ips ips = new Ips();
            ips.setTargets(new ArrayList<>());
            defaultResult.add(ips);
            return defaultResult;
        }
    }

    public Set<String> getIpsByAppName(String name) {
        return appIpsCache.getOrDefault(name, new HashSet<>());
    }

    private String[] getEtcd() {
        return null;
    }

    public Set<String> getEtcdHosts() {
       return null;
    }


    public List<Ips> getK8sNodeIp(String type) {

        List<String> res = new ArrayList<>();
        V1NodeList nodes;
        try {
            ApiClient client = getClient();
            if (client == null) {
                return null;
            }
            nodes = new CoreV1Api(client).listNode(null, null, null, null, null, null, null, null, null);
        } catch (ApiException e) {
            log.error(e.getResponseBody());
            return null;
        }
        nodes.getItems().forEach(it -> {
            String info = it.getMetadata().getName();
            for (V1NodeAddress n : it.getStatus().getAddresses()) {
                if ("ExternalIP".equals(n.getType())) {
                    info = n.getAddress();
                } else if ("InternalIP".equals(n.getType())) {
                    info = n.getAddress();
                }
            }
            res.add(info);
        });
        log.info("getK8sNodeIp k8s node count:{}",res.size());
        List<String> result = new ArrayList<>();
        String port = "";
        if (type.equals("node")) {
            port = machinePort;
        } else if (type.equals("container")) {
            port = containerPort;
        } else {
            return null;
        }
        String finalPort = port;
        res.forEach(
                item -> {
                    result.add(item + ":" + finalPort);
                });
        List<Ips> defaultResult = new ArrayList<>();
        Ips ips2 = new Ips();
        ips2.setTargets(result);
        defaultResult.add(ips2);
        return defaultResult;
    }


    private String innerRequest(String data, String url, String apiKey, String method) {
        try {
            URL requestUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) requestUrl.openConnection();
            PrintWriter out = null;
            //设置URLConnection的参数和普通的请求属性
            conn.setRequestProperty("Expect", "");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            if (StringUtils.isNotBlank(apiKey)) {
                conn.setRequestProperty("Authorization", "Bearer " + apiKey);
            }
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod(method);
            conn.connect();
            if ("POST".equals(method)) {
                //POST请求
                BufferedWriter out1 = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream(), "UTF-8"));
                out1.write(data);
                out1.flush();
                out1.close();
            }
            InputStream is = conn.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String finalStr = "";
            String str = "";
            while ((str = br.readLine()) != null) {
                finalStr = new String(str.getBytes(), "UTF-8");
            }
            is.close();
            conn.disconnect();
            log.info("innerRequest param url:{},apiKey:{},method:{}", url, apiKey, method);
            return finalStr;
        } catch (IOException e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    private synchronized ApiClient getClient() {
        try {
            return Config.defaultClient();
        } catch (IOException e) {
            log.error("getClient error : {}",e.getMessage());
            return null;
        }
    }

}
