//package com.xiaomi.youpin.gwdash.task;
//
//import com.alibaba.nacos.api.exception.NacosException;
//import com.google.gson.Gson;
//import com.google.gson.reflect.TypeToken;
//import com.xiaomi.youpin.gwdash.common.Consts;
//import com.xiaomi.youpin.gwdash.service.FeiShuService;
//import com.xiaomi.youpin.gwdash.service.NacosService;
//import com.xiaomi.youpin.gwdash.service.ResourceService;
//import com.xiaomi.youpin.quota.bo.ResourceBo;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.PostConstruct;
//import java.lang.reflect.Type;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.Executors;
//import java.util.concurrent.ScheduledExecutorService;
//import java.util.concurrent.TimeUnit;
//
///**
// * @Description
// * @Author zhenxing.dong
// * @Date 2021/5/8 16:48
// */
//@Component
//@Slf4j
//public class AlarmTask {
//
//    @Autowired
//    private ResourceService resourceService;
//    @Autowired
//    private NacosService nacosService;
//
//    @Autowired
//    private FeiShuService feiShuService;
//
//    @PostConstruct
//    public void init() {
//        ScheduledExecutorService timer = Executors.newScheduledThreadPool(2);
//        RateAlarmTaskImpl rateAlarmTask = new RateAlarmTaskImpl(resourceService, nacosService,feiShuService);
//        NonTenementTagAlarm nonTenementTagAlarm = new NonTenementTagAlarm(resourceService,feiShuService);
//
//        //5秒后每10s执行一次
//        timer.scheduleAtFixedRate(rateAlarmTask, 5000, 60000, TimeUnit.MILLISECONDS);
//        //5秒后每小时执行一次
//        timer.scheduleAtFixedRate(nonTenementTagAlarm, 5000, 3600000, TimeUnit.MILLISECONDS);
//    }
//
//    /**
//     * 对资源使用率超标者进行报警
//     */
//    private static class RateAlarmTaskImpl implements Runnable {
//
//        private ResourceService resourceService;
//        private NacosService nacosService;
//        private FeiShuService feiShuService;
//
//        private static final double DEFAULT_LIMIT = 0.8;
//
//        private Gson gson = new Gson();
//
//        public RateAlarmTaskImpl(ResourceService resourceService, NacosService nacosService, FeiShuService feiShuService) {
//            this.resourceService = resourceService;
//            this.nacosService = nacosService;
//            this.feiShuService = feiShuService;
//        }
//
//        @Override
//        public void run() {
//            Map<String, Map<String, Long>> resourceUsedStats = resourceService.getTotalAndUesdCpuAndMemValues().getData();
//            try {
//                String tenementSettingStr = nacosService.getConfig(Consts.TENEMENT_DATA_ID, "DEFAULT_GROUP", 3000);
//                Type mapType = new TypeToken<HashMap<String,Map<String,Double>>>(){}.getType();
//                Map<String,Map<String,Double>> tenementSetting = gson.fromJson(tenementSettingStr,mapType);
//
//                resourceUsedStats.forEach((tenement, statMap) -> {
//                    Map<String,Double> singleTeneMentSetting = tenementSetting.get(tenement);
//                    if (null == singleTeneMentSetting){
//                        log.warn("resource used alarm error,can not found tenement setting dataId: {},tenement: {}", Consts.TENEMENT_DATA_ID,tenement);
//                        return;
//                    }
//                    Long totalCpu = statMap.get("totalCpu");
//                    if (null == totalCpu){
//                        log.warn("resource used alarm error,can not found tenement setting dataId: {},tenement: {},label:{}", Consts.TENEMENT_DATA_ID,tenement,"totalCpu");
//                        return;
//                    }
//                    Long totalMem = statMap.get("totalMem");
//                    if (null == totalMem){
//                        log.warn("resource used alarm error,can not found tenement setting dataId: {},tenement: {},label:{}", Consts.TENEMENT_DATA_ID,tenement,"totalMem");
//                        return;
//                    }
//                    Long remainCpu = statMap.get("remainCpu");
//                    if (null == remainCpu){
//                        log.warn("resource used alarm error,can not found tenement setting dataId: {},tenement: {},label:{}", Consts.TENEMENT_DATA_ID,tenement,"remainCpu");
//                        return;
//                    }
//                    Long remainMem = statMap.get("remainMem");
//                    if (null == remainMem){
//                        log.warn("resource used alarm error,can not found tenement setting dataId: {},tenement: {},label:{}", Consts.TENEMENT_DATA_ID,tenement,"remainMem");
//                        return;
//                    }
//                    log.info("check resource data,tenement:{},totalCpu:{},remainCpu:{},limit:{}",tenement,totalCpu,remainCpu,singleTeneMentSetting.getOrDefault("cpu_limit",DEFAULT_LIMIT));
//                    if ((double)(totalCpu-remainCpu)/totalCpu >= singleTeneMentSetting.getOrDefault("cpu_limit",DEFAULT_LIMIT)){
//                        sendFeishu(tenement,"cpu",(double)(totalCpu-remainCpu)/totalCpu);
//                    }
//                    log.info("check resource data,tenement:{},totalMem:{},remainMem:{},limit:{}",tenement,totalMem,remainMem,singleTeneMentSetting.getOrDefault("mem_limit",DEFAULT_LIMIT));
//                    if ((double)(totalMem-remainMem)/totalMem >= singleTeneMentSetting.getOrDefault("mem_limit",DEFAULT_LIMIT)){
//                        sendFeishu(tenement,"mem",(double)(totalMem-remainMem)/totalMem);
//                    }
//                });
//            } catch (NacosException e) {
//                log.error("resource used alarm error, dataId: {}", Consts.TENEMENT_DATA_ID);
//            }
//        }
//        /**
//         * 指标超标时需要给飞书来一条报警
//         */
//        private void sendFeishu(String tenement,String label,double usedRate) {
//            try {
//                String msg = "警告:\n" + "\n"
//                        + "租户: " + tenement + "\n"
//                        + "指标: " + label + "超过设定阈值"+ "\n"
//                        + "使用率: " + usedRate + "\n"
//                        + "请及时处理"+ "\n";
//                feiShuService.sendMsg("", msg);
//            } catch (Exception e) {
//                log.error("AlarmTask.sendFeishu, something wrong, msg: {}", e.getMessage());
//            }
//        }
//    }
//
//    /**
//     * 对没有租户标签的资源进行报警
//     */
//    private static class NonTenementTagAlarm implements Runnable{
//
//        private ResourceService resourceService;
//        private FeiShuService feiShuService;
//
//        public NonTenementTagAlarm(ResourceService resourceService, FeiShuService feiShuService) {
//            this.resourceService = resourceService;
//            this.feiShuService = feiShuService;
//        }
//
//        @Override
//        public void run() {
//            log.info("NonTenementTagAlarm.debug,enter!!!");
//            List<ResourceBo> resourceBoList =  resourceService.getAllResources();
//            log.info("NonTenementTagAlarm.debug,resourceListSize:{}",resourceBoList.size());
//            resourceBoList.stream().forEach(resourceBo -> {
//                log.info("NonTenementTagAlarm.debug,resourceIp:{}",resourceBo.getIp());
//                if (null == resourceBo.getLables()){
//                    log.error("NonTenementTagAlarm.sendFeishu, resource do not have labels!");
//                    return;
//                }
//                if (null == resourceBo.getLables().get("tenement")){
//                    sendFeishu(resourceBo);
//                }
//            });
//        }
//        /**
//         * 发现有未打租户标签的机器时需要给飞书来一条报警
//         */
//        private void sendFeishu(ResourceBo resourceBo) {
//            try {
//                String msg = "警告:\n" + "\n"
//                        + "机器未配置租户,hostname: " + resourceBo.getHostName() + "\n"
//                        + "IP: " + resourceBo.getIp() + "\n"
//                        + "请及时联系SRE进行处理"+ "\n";
//                feiShuService.sendMsg("", msg);
//            } catch (Exception e) {
//                log.error("NonTenementTagAlarm.sendFeishu, something wrong, msg: {}", e.getMessage());
//            }
//        }
//    }
//}
