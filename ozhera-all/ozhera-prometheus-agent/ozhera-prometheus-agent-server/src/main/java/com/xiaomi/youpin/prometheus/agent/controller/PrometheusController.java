package com.xiaomi.youpin.prometheus.agent.controller;

import com.xiaomi.youpin.prometheus.agent.domain.Ips;
import com.xiaomi.youpin.prometheus.agent.service.MioneMachineService;
import com.xiaomi.youpin.prometheus.agent.service.PrometheusIpService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RestController
@Slf4j
public class PrometheusController {

    @Autowired
    private PrometheusIpService prometheusIpService;
    @Autowired
    private MioneMachineService mioneMachineService;

    @GetMapping("/prometheus/getips")
    public List<Ips> getips(String type){
        //1 prometheusStarter(自定义指标) 2 javaagent(业务JVM) 3 jaegerquery(业务普通指标)  4 moneStarter（线程池指标）
        return prometheusIpService.getByType(type);
    }

    @GetMapping("/prometheus/getMachineList")
    public List<Ips> getMachineList(String type){
        //1 物理机  2容器
        return mioneMachineService.queryMachineList(type);
    }

    @GetMapping("/prometheus/getIpsByAppName")
    public List<Ips> getIpsByAppName(String appName) {
        //根据服务名获取nacos上该服务的所有实例ip
        Set<String> tmpResult = prometheusIpService.getIpsByAppName(appName);
        List<String> result = new ArrayList<>(tmpResult);
        List<Ips> defaultResult = new ArrayList<>();
        Ips ips = new Ips();
        ips.setTargets(result);
        defaultResult.add(ips);
        return defaultResult;
    }

    //获取所有etcd的监控
    @GetMapping("/prometheus/getEtcd")
    public List<Ips> getEtcd() {
        Set<String> tmpresult = prometheusIpService.getEtcdHosts();
        List<String> result = new ArrayList<>(tmpresult);
        List<Ips> defaultResult = new ArrayList<>();
        Ips ips = new Ips();
        ips.setTargets(result);
        defaultResult.add(ips);
        return defaultResult;
    }


    //获取k8s node节点ip
    @GetMapping("/prometheus/getK8sNodeIp")
    public List<Ips> getK8sNodeIp(String type){
       return prometheusIpService.getK8sNodeIp(type);
    }
}
