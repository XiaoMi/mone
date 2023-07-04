package com.xiaomi.mone.monitor.service.project.group;

import com.google.gson.Gson;
import com.xiaomi.mone.app.api.model.HeraAppBaseInfoModel;
import com.xiaomi.mone.app.api.model.project.group.HeraProjectGroupAppRequest;
import com.xiaomi.mone.app.api.model.project.group.HeraProjectGroupDataRequest;
import com.xiaomi.mone.app.api.model.project.group.ProjectGroupTreeNode;
import com.xiaomi.mone.app.api.service.HeraAppService;
import com.xiaomi.mone.app.api.service.HeraAuthorizationApi;
import com.xiaomi.mone.app.api.service.HeraProjectGroupServiceApi;
import com.xiaomi.mone.app.common.Result;
import com.xiaomi.mone.monitor.dao.model.AppMonitor;
import com.xiaomi.mone.monitor.service.AppMonitorService;
import com.xiaomi.mone.monitor.service.HeraBaseInfoService;
import com.xiaomi.mone.monitor.service.model.AppMonitorModel;
import com.xiaomi.mone.monitor.service.model.PageData;
import com.xiaomi.mone.monitor.service.model.project.group.ProjectGroupRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.checkerframework.checker.units.qual.C;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author gaoxihui
 * @date 2023/6/7 10:59 上午
 */
@Slf4j
@Service
public class ProjectGroupService {

    @Reference(registry = "registryConfig", check = false, interfaceClass = HeraProjectGroupServiceApi.class, group = "${dubbo.group.heraapp}",timeout = 3000)
    HeraProjectGroupServiceApi projectGroupServiceApi;

    @Reference(registry = "registryConfig", check = false, interfaceClass = HeraAuthorizationApi.class, group = "${dubbo.group.heraapp}",timeout = 3000)
    HeraAuthorizationApi heraAuthorizationApi;

    @Autowired
    AppMonitorService appMonitorService;

    @Autowired
    HeraBaseInfoService heraBaseInfoService;

    public Result checkAuthorization(HttpServletRequest request){
        String heraToken = request.getHeader("hera-token");
        log.info("checkAuthorization header hera-token:{}",heraToken);
        return heraAuthorizationApi.checkAuthorization(heraToken);
    }


    public Result<ProjectGroupTreeNode> getFullTree(Integer type){
        return projectGroupServiceApi.getFullTree(type);
    }

    public Result<ProjectGroupTreeNode> getTreeByUser(ProjectGroupRequest request){
        return projectGroupServiceApi.getTreeByUser(request.getUser(),request.getGroupType(),request.getProjectGroupName());
    }

    public Result<List<HeraAppBaseInfoModel>> searchGroupApps(ProjectGroupRequest request){
        return projectGroupServiceApi.searchGroupApps(request.getUser(),request.getGroupType(),request.getProjectGroupId(),request.getAppName(),request.getPage(),request.getPageSize());

    }

    public Result searchMyApps(ProjectGroupRequest request){

        Result<List<HeraAppBaseInfoModel>> listResult = projectGroupServiceApi.searchGroupApps(request.getUser(), request.getGroupType(), request.getProjectGroupId(), request.getAppName(), request.getPage(), request.getPageSize());

        if(!listResult.isSuccess()){
            return listResult;
        }

        PageData pd = new PageData();

        if(CollectionUtils.isEmpty(listResult.getData())){
            pd.setTotal(0l);
            pd.setList(null);
            return Result.success(pd);
        }

        List<HeraAppBaseInfoModel> data = listResult.getData();
        List<Integer> baseIds = data.stream().map(t -> t.getId()).collect(Collectors.toList());
        log.info("baseIds : {}",new Gson().toJson(baseIds));
        Long aLong = appMonitorService.countByBaseInfoId(baseIds);
        pd.setTotal(aLong);

        if(aLong.intValue() > 0){
            List<AppMonitor> appMonitors = appMonitorService.searchByBaseInfoId(baseIds, 1, 1000);
            pd.setList(appMonitors);
            pd.setPage(1);
            pd.setPageSize(1000);
        }

        return Result.success(pd);

    }

    public Result<Integer> create(HeraProjectGroupDataRequest request){

        Result result = projectGroupServiceApi.create(request);
        if(result.isSuccess()){
            appData(request.getUsers(),request.getApps());
        }
        return result;
    }

    public Result<Integer> update(HeraProjectGroupDataRequest request){
        Result update = projectGroupServiceApi.update(request);
        if(update.isSuccess()){
            appData(request.getUsers(),request.getApps());
        }
        return update;
    }

    public Result<Integer> delete(Integer id){
        return projectGroupServiceApi.delete(id);
    }

    private void appData(List<String> users,List<HeraProjectGroupAppRequest> apps){

        if(CollectionUtils.isEmpty(users)){
            /**
             * 项目组中解除用户关系，这里不做解绑关系，取消参与统一在页面完成！否则相互冲突！
             * 而且这里全部解绑也不合适，会把手动添加的部分也解除掉，手动添加的可能从来没有在项目组中添加过，所以直接解除就是错误！
             */
            return;

        }

        if(CollectionUtils.isEmpty(apps)){
            return;
        }

        for(HeraProjectGroupAppRequest app : apps){
            HeraAppBaseInfoModel appBaseInfo = heraBaseInfoService.getByBindIdAndPlat(String.valueOf(app.getAppId()), app.getPlatFormType());
            if(appBaseInfo == null){
                log.info("create or update appData no Data found! appId:{}, plat:{}",app.getAppId(),app.getPlatFormType());
                continue;
            }

            for(String user : users){
                AppMonitorModel appMonitorModel = new AppMonitorModel();
                appMonitorModel.setProjectId(Integer.valueOf(appBaseInfo.getBindId()));
                appMonitorModel.setProjectName(appBaseInfo.getAppName());
                appMonitorModel.setProjectCName(appBaseInfo.getAppCname());
                appMonitorModel.setOwner("yes");
                appMonitorModel.setIamTreeId(appBaseInfo.getIamTreeId());
                appMonitorModel.setAppSource(appBaseInfo.getPlatformType());
                appMonitorModel.setBindType(appBaseInfo.getBindType());
                appMonitorModel.setAppLanguage(appBaseInfo.getAppLanguage());
                appMonitorModel.setAppType(appBaseInfo.getAppType());
                appMonitorModel.setEnvMapping(appBaseInfo.getEnvsMap());
                com.xiaomi.mone.monitor.result.Result<String> result = appMonitorService.createWithBaseInfo(appMonitorModel, user);
                log.info("appData.addApp param : {} ,user : {} , result : {}", appMonitorModel, user, result);
            }
        }
    }
}
