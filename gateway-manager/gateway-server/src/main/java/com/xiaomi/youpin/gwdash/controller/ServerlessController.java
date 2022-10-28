//package com.xiaomi.youpin.gwdash.controller;
//
//import com.google.gson.Gson;
//import com.xiaomi.data.push.redis.Redis;
//import com.xiaomi.youpin.gwdash.common.Keys;
//import com.xiaomi.youpin.gwdash.common.Result;
//import com.xiaomi.youpin.gwdash.dao.ApiInfoDao;
//import com.xiaomi.youpin.gwdash.dao.model.ApiInfo;
//import com.xiaomi.youpin.gwdash.dao.model.ProjectApi;
//import com.xiaomi.youpin.gwdash.exception.CommonError;
//import com.youpin.xiaomi.tesla.bo.ScriptInfo;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.StringUtils;
//import org.nutz.dao.Cnd;
//import org.nutz.dao.Dao;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.io.IOException;
//import java.util.LinkedHashMap;
//import java.util.List;
//import java.util.Map;
//
///**
// * @author tsingfu
// */
//@RestController
//@Slf4j
//@RequestMapping("/api/serverless")
//public class ServerlessController {
//
//    @Autowired
//    private Dao dao;
//
//    @Autowired
//    private Redis redis;
//
//    @Autowired
//    private ApiInfoDao apiInfoDao;
//
//    @RequestMapping(value = "/apis", method = RequestMethod.GET)
//    public Result<List<ProjectApi>> getApiIds(@RequestParam(value = "projectId") long projectId) throws IOException {
//        return Result.success(dao.query(ProjectApi.class, Cnd.where("project_id", "=", projectId)));
//    }
//
//    @RequestMapping(value = "/api/set", method = RequestMethod.POST)
//    public Result<ProjectApi> setApiId(
//            @RequestParam(value = "projectId") long projectId,
//            @RequestParam(value = "apiId") long apiId) throws IOException {
//
//        log.info("ServerlessController /api/set projectId:{},apiId{}",projectId,apiId);
//        List<ApiInfo> apiInfos = apiInfoDao.getApiInfoById(apiId);
//        if (apiInfos == null) {
//            return Result.fail(CommonError.InvalidParamError);
//        }
//        ProjectApi projectApi = dao.fetch(ProjectApi.class,Cnd.where("project_id","=",projectId).and("api_id","=",apiId));
//        if (null != projectApi) {
//            return new Result<ProjectApi>(1, "该记录已经存在");
//        }
//        ProjectApi projectApi1 = new ProjectApi();
//        projectApi1.setApiId(apiId);
//        projectApi1.setProjectId(projectId);
//        return Result.success(dao.insert(projectApi1));
//    }
//
//
//    /**
//     * 列表 serverLess API
//     * @param projectId
//     * @return
//     */
//    @RequestMapping(value = "/list",method = RequestMethod.GET)
//    public Result<Map<String, Object>> getList(@RequestParam(value = "projectId") long projectId){
//
//        log.info("ServerlessController /api/serverless/list projectId:{}",projectId);
//        Cnd cnd =  Cnd.where("project_id","=",projectId);
//        Map<String,Object> result = new LinkedHashMap<>();
//        result.put("total",dao.count(ProjectApi.class,cnd));
//        List<ProjectApi> list = dao.query(ProjectApi.class,cnd);
//        if (null != list && list.size()>0){
//            for (ProjectApi projectApi : list){
//                Gson gson = new Gson();
//                String scriptData = redis.get(Keys.scriptKey(projectApi.getApiId()));
//                if (StringUtils.isNotBlank(scriptData)){
//                    ScriptInfo scriptInfo = gson.fromJson(scriptData, ScriptInfo.class);
//                    projectApi.setEntryClassName(scriptInfo.getEntryClassName());
//                    projectApi.setJarUrl(scriptInfo.getJarUrl());
//                }
//                List<ApiInfo> apiInfos = apiInfoDao.getApiInfoById(projectApi.getApiId());
//                if (null != apiInfos && apiInfos.size()>0){
//                    projectApi.setGroupId(apiInfos.get(0).getGroupId());
//                }
//            }
//        }
//        result.put("list",list);
//        return Result.success(result);
//    }
//
//    /**
//     * 删除 serverLess APi
//     */
//    @RequestMapping(value = "/del",method = RequestMethod.GET)
//    public Result<Boolean> update(
//            @RequestParam(value = "projectId") long projectId,
//            @RequestParam(value = "apiId") long apiId){
//
//        log.info("ServerlessController /api/serverless/del projectId:{},apiId{}",projectId,apiId);
//        ProjectApi projectApi = dao.fetch(ProjectApi.class,Cnd.where("project_id","=",projectId).and("api_id","=",apiId));
//        if (null == projectApi) {
//            return new Result<>(1, "该记录已经不存在", false);
//        }
//        dao.delete(ProjectApi.class,projectApi.getId());
//        return Result.success(true);
//    }
//
//}
