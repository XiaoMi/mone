///*
// *  Copyright 2020 Xiaomi
// *
// *    Licensed under the Apache License, Version 2.0 (the "License");
// *    you may not use this file except in compliance with the License.
// *    You may obtain a copy of the License at
// *
// *        http://www.apache.org/licenses/LICENSE-2.0
// *
// *    Unless required by applicable law or agreed to in writing, software
// *    distributed under the License is distributed on an "AS IS" BASIS,
// *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// *    See the License for the specific language governing permissions and
// *    limitations under the License.
// */
//
//package com.xiaomi.youpin.gwdash.service;
//
//import com.alibaba.nacos.client.naming.utils.CollectionUtils;
//import com.google.common.collect.Lists;
//import com.google.gson.Gson;
//import com.google.gson.reflect.TypeToken;
//import com.xiaomi.youpin.docker.Safe;
//import com.xiaomi.youpin.gwdash.common.Consts;
//import com.xiaomi.youpin.gwdash.common.Result;
//import com.xiaomi.youpin.gwdash.dao.model.GwStatistics;
//import com.xiaomi.youpin.gwdash.dao.model.Project;
//import com.xiaomi.youpin.gwdash.dao.model.ProjectEnv;
//import com.xiaomi.youpin.gwdash.exception.CommonError;
//import com.xiaomi.youpin.tesla.billing.bo.BResult;
//import com.xiaomi.youpin.tesla.billing.bo.ReportBo;
//import com.xiaomi.youpin.tesla.billing.bo.ReportRes;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.StringUtils;
//import org.apache.dubbo.config.annotation.Reference;
//import org.nutz.dao.Chain;
//import org.nutz.dao.Cnd;
//import org.nutz.dao.Dao;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.lang.reflect.Type;
//import java.math.BigDecimal;
//import java.util.*;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.function.Function;
//import java.util.stream.Collectors;
//
///**
// * @description: 计费
// * @author zhenghao
// *
// */
//@Slf4j
//@Service
//public class BillingService {
//
//    @Autowired
//    private ProjectService projectService;
//
//    @Autowired
//    private ProjectEnvService projectEnvService;
//
//    @Autowired
//    private Dao dao;
//
//    @Reference(group = "${ref.billing.service.group}", interfaceClass = com.xiaomi.youpin.tesla.billing.service.BillingService.class, check = false, timeout = 2000)
//    private com.xiaomi.youpin.tesla.billing.service.BillingService billingService;
//
//    public Result<Map<String, Object>> month(Integer projectId) {
//        BResult<ReportRes> bResult = billingService.getAppManagementBillingDetail(projectId);
//        if (bResult != null) {
//            ReportRes reportRes = bResult.getData();
//            if (reportRes != null) {
//                Map<String, Object> map = new LinkedHashMap<>();
//                map.put("price", reportRes.getPrice());
//                map.put("list", reportRes.getReportBoList());
//                return Result.success(map);
//            }
//        }
//        return Result.fail(CommonError.UnknownError);
//    }
//
//    public Result<Map<String, Object>> year(Integer year, Integer projectId, Integer envId) {
//        BResult<ReportRes> bResult = billingService.getBillingDetail(year, projectId, envId);
//        if (bResult != null) {
//            ReportRes reportRes = bResult.getData();
//            if (reportRes != null) {
//                Map<String, Object> map = new LinkedHashMap<>();
//                map.put("price", reportRes.getPrice());
//                map.put("list", reportRes.getReportBoList());
//                return Result.success(map);
//            }
//        }
//
//        return Result.fail(CommonError.UnknownError);
//    }
//
//    public Result<Object> detail(Integer projectId, Integer envId) {
//        Map<String, Object> map = billingService.lookBillingDetail(projectId, envId);
//        log.info("billingService lookBillingDetail result:{}",map.toString());
//        if (map != null) {
//            return Result.success(map);
//        }
//        return Result.success(null);
//    }
//
//    public Result<Map<Object, String>> topten() {
//        GwStatistics gwStatistics = dao.fetch(GwStatistics.class, Cnd.where("gw_key", "=", Consts.BILLING_TOP_TEN));
//        if (gwStatistics != null) {
//            Gson gson = new Gson();
//            Type typeToken = new TypeToken<TreeMap<Object, Integer>>(){}.getType();
//            Map<Object, Integer> map = gson.fromJson(gwStatistics.getValue(), typeToken);
//            Map<Object, Integer> formartMap = Consts.sortByValue(map);
//            Map<Object, String> resultMap = new LinkedHashMap<>();
//            formartMap.forEach((key, value) -> {
//                double doubleFen = Double.valueOf(value) * 0.01;
//                String yuan = String.format("%.2f", doubleFen);
//                resultMap.put(key, yuan);
//            });
//
//            log.info("resultMap:{}", resultMap);
//            return Result.success(resultMap);
//        }
//        return Result.success(null);
//    }
//
//
//    /**
//     * billing topten task
//     * @return
//     */
//    public void billingTopTenTask() {
//        // 异步处理
//        Safe.run(() -> {
//            billingTopTen();
//        });
//
//
//    }
//
//    private void billingTopTen() {
//        Map<Object, String> map = billingService.billingTopTen();
//        log.info("billingService topten result:{}", map);
//        List ids = new ArrayList<>();
//        map.forEach((key, value) -> {
//            ids.add(key);
//        });
//        Map<Object, Integer> resultMap = new HashMap<>();
//        Map<Object, String> nameMap = new HashMap<>();
//        List<Project> projectList = projectService.getProjectByIdS(ids);
//        projectList.forEach(it -> {
//            nameMap.put(it.getId(), it.getName());
//        });
//
//        // 把id换成name
//        map.forEach((key, value) -> {
//            if (resultMap.size() != 10) {
//                resultMap.put(nameMap.get(Long.valueOf(key.toString())), Integer.valueOf(value));
//            }
//        });
//
//        log.info("resultMap:{}", resultMap);
//        // 排序
//        Map sortMap = this.sortByValue(resultMap);
//        log.info("sortMap:{}", sortMap);
//
//        // 入库
//        GwStatistics gwStatistics = dao.fetch(GwStatistics.class, Cnd.where("gw_key", "=", Consts.BILLING_TOP_TEN));
//        if (gwStatistics == null) {
//            GwStatistics gwStatisticsInsert = new GwStatistics();
//            gwStatisticsInsert.setCtime(System.currentTimeMillis());
//            gwStatisticsInsert.setKey(Consts.BILLING_TOP_TEN);
//            gwStatisticsInsert.setValue(sortMap.toString());
//            dao.insert(gwStatisticsInsert);
//        } else {
//            dao.update(GwStatistics.class, Chain.make("gw_value", sortMap.toString()).add("ctime", System.currentTimeMillis()), Cnd.where("gw_key", "=", Consts.BILLING_TOP_TEN));
//        }
//
//    }
//
//
//    public <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
//        Map<K, V> result = new LinkedHashMap<>();
//
//        map.entrySet().stream()
//                .sorted(Map.Entry.<K, V>comparingByValue()
//                        .reversed()).forEachOrdered(e -> result.put(e.getKey(), e.getValue()));
//        return result;
//    }
//
//
//    public void open() {
//        Safe.run(() -> {
//            runOpen();
//        });
//    }
//
//    private void runOpen() {
//        List<Project> projectList = projectService.getProjects();
//        projectList.forEach(it->{
//            List<ProjectEnv> projectEnvList = projectEnvService.getProjectEnv(it.getId());
//            ReportBo reportBo = new ReportBo();
//            reportBo.setBizId(it.getId());
//            reportBo.setName(it.getName());
//            reportBo.setSubBizIdList(projectEnvList.stream().map(its -> its.getId()).collect(Collectors.toList()));
//            billingService.initReport(reportBo);
//        });
//    }
//
//    public Result<Map<Object, String>> topProjectId(Long projectId, Integer year) {
//        Map<Object, Long> map = billingService.getBillingByPorjectAndYear(projectId, year);
//        Map<Object, Integer> integerMap = new HashMap<>();
//        map.forEach((key, value) -> {
//            integerMap.put(key, value.intValue());
//        });
//
//        // 排序
//        Map<Object, Integer> formartMap = Consts.sortByValue(integerMap);
//        Map<Object, String> resultMap = new LinkedHashMap<>();
//        // 分转成元
//        formartMap.forEach((key, value) -> {
//            double doubleFen = Double.valueOf(value) * 0.01;
//            String yuan = String.format("%.2f", doubleFen);
//            resultMap.put(key, yuan);
//        });
//        log.info("topProjectId result:{}", resultMap);
//        return Result.success(resultMap);
//    }
//
//    public Result<Map<Object, String>> topTime(Integer year, Integer month) {
//        Map<Object, String> resultMap = new LinkedHashMap<>();
//        // key其实是Integer类型的project的id，value是耗资金额，单位分
//        Map<Object, Long> projectPriceMap = billingService.getTOPBillingByYearAndMonth(year, month);
//        if(projectPriceMap == null || projectPriceMap.size()==0){
//            return Result.success(resultMap);
//        }
//        Map<Integer, Long> linkedHashMap = new LinkedHashMap<>();
//        //倒叙排列，并截取前value最大的20个
//        projectPriceMap.entrySet().stream().sorted(((m1, m2) -> {
//            long result = m1.getValue()-m2.getValue();
//            return result>0?-1:(result==0?0:1);
//        })).limit(20).forEach(item->{
//            linkedHashMap.put((Integer)item.getKey(),item.getValue());
//        });
//
//        //根据要展示的project查询
//        List<Project> projectList = projectService.getProjectByIdS(Lists.newArrayList(linkedHashMap.keySet()));
//        if(CollectionUtils.isEmpty(projectList)){
//            log.info("projectList is empty ,ids = {}", StringUtils.join(linkedHashMap.keySet()));
//            return Result.success(resultMap);
//        }
//        Map<Long,Project> projectIdMap = projectList.stream().collect(Collectors.toMap(Project::getId, Function.identity(),(p1,p2)->p1));
//        // 分转成元
//        linkedHashMap.forEach((key, value) -> {
//            double doubleFen = Double.valueOf(value) * 0.01;
//            String yuan = String.format("%.2f", doubleFen);
//            Project project = projectIdMap.get(Long.valueOf(key));
//            if(project != null && project.getName() != null){
//                resultMap.put(project.getName(), yuan);
//            }
//        });
//        log.info("topTime result:{}", resultMap);
//        return Result.success(resultMap);
//    }
//
//}