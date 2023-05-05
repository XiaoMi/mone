package com.xiaomi.mone.monitor.service;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.xiaomi.mone.app.api.model.HeraAppBaseInfoModel;
import com.xiaomi.mone.monitor.bo.AppLanguage;
import com.xiaomi.mone.monitor.bo.AppType;
import com.xiaomi.mone.monitor.bo.PlatFormType;
import com.xiaomi.mone.monitor.dao.AppGrafanaMappingDao;
import com.xiaomi.mone.monitor.dao.AppMonitorDao;
import com.xiaomi.mone.monitor.dao.GrafanaTemplateDao;
import com.xiaomi.mone.monitor.dao.model.AppGrafanaMapping;
import com.xiaomi.mone.monitor.dao.model.AppMonitor;
import com.xiaomi.mone.monitor.dao.model.GrafanaTemplate;
import com.xiaomi.mone.monitor.result.ErrorCode;
import com.xiaomi.mone.monitor.result.Result;
import com.xiaomi.mone.monitor.service.api.AppGrafanaMappingServiceExtension;
import com.xiaomi.mone.monitor.service.extension.PlatFormTypeExtensionService;
import com.xiaomi.mone.monitor.service.model.GrafanaResponse;
import com.xiaomi.mone.monitor.service.model.MutiGrafanaResponse;
import com.xiaomi.mone.monitor.service.serverless.ServerLessService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author gaoxihui
 * @date 2021/7/8 11:05 下午
 */
@Slf4j
@Service
public class AppGrafanaMappingService {

    @Autowired
    AppGrafanaMappingDao appGrafanaMappingDao;

    @Autowired
    GrafanaService grafanaService;

    @Autowired
    AppMonitorDao appMonitorDao;

    @Autowired
    GrafanaTemplateDao grafanaTemplateDao;

    @Autowired
    HeraBaseInfoService heraBaseInfoService;

    @Autowired
    PlatFormTypeExtensionService platFormTypeExtensionService;

    @Autowired
    private ServerLessService serverLessService;

    @Autowired
    private AppGrafanaMappingServiceExtension appGrafanaMappingServiceExtension;


    @Value("${server.type}")
    private String serverType;

    @Value("${grafana.domain}")
    private String grafanaDomain;

    ExecutorService executor = null;


    public static final String OLD_ST_GRAFANA_DOMAIN = "http://xxx";
    public static final String OLD_ONLINE_GRAFANA_DOMAIN = "http://xxx";

    public AppGrafanaMappingService() {
        executor = Executors.newSingleThreadExecutor();
    }

    public void exeReloadTemplateBase(Integer pSize) {

        log.info("exeReloadTemplateBase will be running!! pSize:" + pSize);
        Future result = executor.submit(() -> {

            log.info("exeReloadTemplateBase start running!! pSize:" + pSize);

            try {
                HeraAppBaseInfoModel baseInfoCondition = new HeraAppBaseInfoModel();
                Long total = heraBaseInfoService.count(baseInfoCondition);
                log.info("AppGrafanaMappingService.exeReloadTemplateBase data totalNum ====== {}", total);
                if (total == null || total.intValue() == 0) {
                    log.info("AppGrafanaMappingService.exeReloadTemplateBase no data found!!!");
                    return "No data found!!";
                }

                int pageCount = total.intValue() / pSize + (total.intValue() % pSize > 0 ? 1 : 0);

                for (int i = 0; i < pageCount; i++) {
                    int offset = i + 1;
                    List<HeraAppBaseInfoModel> list = heraBaseInfoService.query(baseInfoCondition, offset, pSize);
                    for (HeraAppBaseInfoModel heraAppBaseInfo : list) {

                        try {
                            this.createTmpByAppBaseInfo(heraAppBaseInfo);
                        } catch (Exception e1) {
                            log.error("grafanaMappingService.exeReloadTemplateBase error appName:{} error : {}", heraAppBaseInfo.getAppName(), e1.getMessage(), e1);
                            continue;
                        }

                    }
                }
            } catch (Exception e) {
                log.error("AppGrafanaMappingService.exeReloadTemplateBase error : {}", e.getMessage(), e);
                return ErrorCode.unknownError.getMessage();
            }

            return ErrorCode.success.getMessage();
        });
    }

    public void reloadTmpByAppId(Integer id) {
        HeraAppBaseInfoModel baseInfo = heraBaseInfoService.getById(id);
        if (baseInfo == null) {
            log.error("reloadTmpByAppId no data found id:{}", id);
            return;
        }
        createTmpByAppBaseInfo(baseInfo);
    }

    public void createTmpByAppBaseInfo(HeraAppBaseInfoModel baseInfo) {
        GrafanaTemplate template = new GrafanaTemplate();
        template.setAppType(baseInfo.getAppType());
        template.setPlatform(baseInfo.getPlatformType());

        try {
            Integer langUageCode = AppLanguage.getCodeByMessage(baseInfo.getAppLanguage());
            //无语言配置，默认使用java语言模版
            if (langUageCode == null) {
                langUageCode = AppLanguage.java.getCode();
                log.error("the app base info no language set! set default language java. baseInfo:{}", new Gson().toJson(baseInfo));
            }
            template.setLanguage(langUageCode);

            appGrafanaMappingServiceExtension.setPlatFormByLanguage(template,baseInfo.getAppLanguage());

            List<GrafanaTemplate> search = grafanaTemplateDao.search(template);
            if (CollectionUtils.isEmpty(search)) {
                log.error("createTmpByAppBaseInfo,no template config found! baseInfo:{}", new Gson().toJson(baseInfo));
                return;
            }

            Optional<Integer> platformType = Optional.ofNullable(baseInfo.getPlatformType());
            //找不到平台类型，默认放在open下
            String grafanaDirByCode = platformType.isPresent() ? platFormTypeExtensionService.getGrafanaDirByTypeCode(platformType.get()) : PlatFormType.open.getGrafanaDir();


            List<String> funcList = new ArrayList<>();

            if(AppType.serverless.getCode().equals(baseInfo.getAppType())){
                funcList = serverLessService.getFaasFunctionList(Integer.valueOf(baseInfo.getBindId()));
            }
            
            MutiGrafanaResponse mutiGrafanaResponse = grafanaService.requestGrafanaTemplate(serverType, baseInfo.getBindId() + "_" + baseInfo.getAppName(), grafanaDirByCode, search.get(0), funcList);

            log.info("createTmpByAppBaseInfo response info : {}", mutiGrafanaResponse);

            appGrafanaMappingServiceExtension.dealRequestGrafanaTemplateCode(mutiGrafanaResponse.getCode(), baseInfo.getBindId(), baseInfo.getAppName());

            log.info("grafanaMappingService.createTmpByAppBaseInfo success appName : {}, version:{},area : {}, returnUrl :{}"
                    , baseInfo.getAppName(), mutiGrafanaResponse.getData().get(0).getMimonitor_version(), grafanaDirByCode, mutiGrafanaResponse);

            String url = new StringBuffer().append(grafanaDomain).append(mutiGrafanaResponse.getUrl()).toString();

            AppGrafanaMapping grafanaMapping = new AppGrafanaMapping();
            grafanaMapping.setGrafanaUrl(url);
            grafanaMapping.setAppName(baseInfo.getBindId() + "_" + baseInfo.getAppName());
            grafanaMapping.setMioneEnv(grafanaDirByCode);
            int i = this.saveOrUpdate(grafanaMapping);
            if (i > 0) {
                log.info("createTmpByAppBaseInfo sucess!grafanaMapping:{}", grafanaMapping.toString());
            } else {
                log.info("createTmpByAppBaseInfo fail!grafanaMapping:{}", grafanaMapping.toString());
            }
        } catch (JsonSyntaxException e) {
            log.error("createTmpByAppBaseInfo error!{}", e.getMessage(), e);
        }

    }

    @Deprecated
    public String createGrafanaUrlByAppName(String appName, String area) {
        if (StringUtils.isEmpty(appName) || StringUtils.isEmpty(area)) {
            log.error("GrafanaMappingController.createGrafanaUrlByAppName error! param appName or area is empty!");
            return ErrorCode.invalidParamError.getMessage();
        }

        try {

            String grafanaReqResult = grafanaService.requestGrafana(serverType, appName, area);
            log.info("GrafanaMappingController.createGrafanaUrlByAppName requestGrafana serverType:{} ,appName : {},area : {}, result : {}", serverType, appName, area, grafanaReqResult);

            GrafanaResponse grafanaResponse = new Gson().fromJson(grafanaReqResult, GrafanaResponse.class);
            String url = new StringBuffer().append(grafanaDomain).append(grafanaResponse.getUrl()).toString();

            AppGrafanaMapping appGrafanaMapping = new AppGrafanaMapping();
            appGrafanaMapping.setMioneEnv(area);
            appGrafanaMapping.setAppName(appName);
            appGrafanaMapping.setGrafanaUrl(url);
            Integer ret = this.saveOrUpdate(appGrafanaMapping);

            if (ret > 0) {
                log.info("GrafanaMappingController.createGrafanaUrlByAppName save data success  name : {}, area : {}, url : {} ", appName, area, url);
                return ErrorCode.success.getMessage();
            } else {
                log.info("GrafanaMappingController.createGrafanaUrlByAppName save data failed  name : {}, area : {}, url : {} ", appName, area, url);
                return "data save failed!!";
            }

        } catch (Exception e) {
            log.error("GrafanaMappingController.createGrafanaUrlByAppName error:{}", e.getMessage(), e);
            return ErrorCode.unknownError.getMessage();
        }
    }


    public Result getGrafanaUrlByAppName(String appName) {

        if (StringUtils.isEmpty(appName)) {
            log.error("AppGrafanaMappingService#getGrafanaUrlByAppName param is empty");
            return Result.fail(ErrorCode.invalidParamError);
        }

        int i = appName.indexOf("_");
        String bindId = appName.substring(0, i);
        String appNameBase = appName.substring(i + 1);

        String appType = null;


        try {
            AppGrafanaMapping mapping = appGrafanaMappingDao.getByAppName(appName);
            if (mapping == null) {
                log.info("AppGrafanaMappingService#getGrafanaUrlByAppName can not find data for appName : {}", appName);
                return new Result<>(ErrorCode.success.getCode(), ErrorCode.success.getMessage(), null);
            }
            log.info("AppGrafanaMappingService#getGrafanaUrlByAppName by appName : {} ,return : {}", appName, mapping.getGrafanaUrl());


            HeraAppBaseInfoModel heraAppBaseInfo = new HeraAppBaseInfoModel();
            heraAppBaseInfo.setBindId(bindId);
            heraAppBaseInfo.setAppName(appNameBase);

            List<HeraAppBaseInfoModel> query = heraBaseInfoService.query(heraAppBaseInfo, null, null);
            if (CollectionUtils.isEmpty(query) || query.get(0).getAppType() == null) {
                log.error("no appType found for appName:{},has set default type by businessType type", appName);
                appType = AppType.businessType.getMessage();
            } else {
                AppType anEnum = AppType.getEnum(query.get(0).getAppType());
                if (anEnum == null) {
                    log.error("error AppType appName:{},has set default type by businessType type", appName);
                    appType = AppType.businessType.getMessage();
                }
                appType = anEnum.getMessage();
            }
            Map map = new HashMap();
            map.put("appType", appType);
            map.put("url", transferGrafanaUrl(mapping.getGrafanaUrl()));

            return Result.success(map);
        } catch (Exception e) {
            log.error("AppGrafanaMappingService#getGrafanaUrlByAppName error" + e.getMessage(), e);
            return Result.fail(ErrorCode.unknownError);
        }

    }

    @Deprecated
    public Result<String> getGrafanaUrlByAppId(Integer appId) {

        if (appId == null) {
            log.error("AppGrafanaMappingService#getGrafanaUrlByAppId param is empty");
            return Result.fail(ErrorCode.invalidParamError);
        }

        try {
            AppMonitor appMonitor = appMonitorDao.getByAppId(appId);
            if (appMonitor == null) {
                return Result.success(null);
            }

            String grafanaName = new StringBuilder().append(appMonitor.getProjectId()).append("_").append(appMonitor.getProjectName()).toString();
            AppGrafanaMapping mapping = appGrafanaMappingDao.getByAppName(grafanaName);
            if (mapping == null) {
                log.info("AppGrafanaMappingService#getGrafanaUrlByAppId can not find data for appName : {}", grafanaName);
                return new Result<>(ErrorCode.success.getCode(), ErrorCode.success.getMessage(), null);
            }
            log.info("AppGrafanaMappingService#getGrafanaUrlByAppId by appName : {} ,return : {}", grafanaName, mapping.getGrafanaUrl());
            return new Result<>(ErrorCode.success.getCode(), ErrorCode.success.getMessage(), mapping.getGrafanaUrl());
        } catch (Exception e) {
            log.error("AppGrafanaMappingService#getGrafanaUrlByAppId error" + e.getMessage(), e);
            return Result.fail(ErrorCode.unknownError);
        }

    }

    public Integer save(AppGrafanaMapping appGrafanaMapping) {
        try {
            return appGrafanaMappingDao.generateGrafanaMapping(appGrafanaMapping);
        } catch (Exception e) {
            log.error("AppGrafanaMappingService#save error" + e.getMessage(), e);
        }
        return 0;
    }

    public Integer saveOrUpdate(AppGrafanaMapping appGrafanaMapping) {
        try {
            AppGrafanaMapping byAppName = appGrafanaMappingDao.getByAppName(appGrafanaMapping.getAppName());
            if (byAppName == null) {
                return appGrafanaMappingDao.generateGrafanaMapping(appGrafanaMapping);
            }

            appGrafanaMapping.setId(byAppName.getId());
            appGrafanaMapping.setCreateTime(byAppName.getCreateTime());
            return appGrafanaMappingDao.updateByPrimaryKey(appGrafanaMapping);

        } catch (Exception e) {
            log.error("AppGrafanaMappingService#saveOrUpdate error" + e.getMessage(), e);
        }
        return 0;
    }

    private String transferGrafanaUrl(String grafanaUrl) {
        if (!serverType.equals("online")) {
            //测试环境由于grafana升级https 域名发生变换
            if (grafanaUrl.startsWith(OLD_ST_GRAFANA_DOMAIN)) {
                String[] grafanaUrlArr = grafanaUrl.split(OLD_ST_GRAFANA_DOMAIN);
                if (grafanaUrlArr.length < 2) {
                    return grafanaUrl;
                } else {
                    return grafanaDomain + grafanaUrlArr[1];
                }
            }
            return grafanaUrl;
        } else {
            //线上环境更换域名，替换名称
            if (grafanaUrl.startsWith(OLD_ONLINE_GRAFANA_DOMAIN)) {
                String[] grafanaUrlArr = grafanaUrl.split(OLD_ONLINE_GRAFANA_DOMAIN);
                if (grafanaUrlArr.length < 2) {
                    return grafanaUrl;
                } else {
                    return grafanaDomain + grafanaUrlArr[1];
                }
            }
            return grafanaUrl;
        }
    }

}
