package com.xiaomi.mone.monitor.service;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.xiaomi.mone.monitor.dao.AppServiceMarketDao;
import com.xiaomi.mone.monitor.dao.model.AppServiceMarket;
import com.xiaomi.mone.monitor.result.ErrorCode;
import com.xiaomi.mone.monitor.result.Result;
import com.xiaomi.mone.monitor.service.api.ServiceMarketExtension;
import com.xiaomi.mone.monitor.service.extension.PlatFormTypeExtensionService;
import com.xiaomi.mone.monitor.service.model.PageData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @author zhangxiaowei6
 */

@Slf4j
@Service
public class ServiceMarketService {
    @Autowired
    AppServiceMarketDao appServiceMarketDao;

    @Value("${server.type}")
    private String env;

    @NacosValue(value = "${grafana.domain}", autoRefreshed = true)
    private String grafanaDomain;

    @Autowired
    ServiceMarketExtension serviceMarketExtension;

    @Autowired
    PlatFormTypeExtensionService platFormTypeExtensionService;

    //线上mione 服务大盘url
    public static final String MIONE_ONLINE_SERVICE_MARKET_GRAFANA_URL = "/d/hera-serviceMarket/hera-fu-wu-da-pan?orgId=1";

    //增
    public Result createMarket(String user, String marketName, String belongTeam, String serviceList, String remark, Integer serviceType) {
        serviceType = platFormTypeExtensionService.getMarketType(serviceType);
        try {
            //按;切分serviceList
            String[] services = serviceList.split(";");
            //入库
            AppServiceMarket appServiceMarket = new AppServiceMarket();
            appServiceMarket.setMarketName(marketName.trim());
            appServiceMarket.setServiceList(serviceList.trim());
            appServiceMarket.setCreator(user);
            appServiceMarket.setLastUpdater(user);
            appServiceMarket.setRemark(remark.trim());
            appServiceMarket.setBelongTeam(belongTeam.trim());
            appServiceMarket.setServiceType(serviceType);
            int dbResult = appServiceMarketDao.insertServiceMarket(appServiceMarket);
            log.info("ServiceMarketService.createMarket dbResult: {}", dbResult);
            return Result.success("success");
        } catch (Exception e) {
            log.error("ServiceMarketService.createMarket error : {}", e.toString());
            return Result.fail(ErrorCode.unknownError);
        }
    }

    //查
    public Result searchMarket(String user, int id) {
        try {
            AppServiceMarket appServiceMarket = appServiceMarketDao.SearchAppServiceMarket(id);
            return Result.success(appServiceMarket);
        } catch (Exception e) {
            log.error("ServiceMarketService.searchMarket error : {}", e.toString());
            return Result.fail(ErrorCode.unknownError);
        }
    }

    //获取大盘grafanaUrl
    public String getServiceMarketGrafana(Integer serviceType) {
        return serviceMarketExtension.getServiceMarketGrafana(serviceType);
    }

    //查列表
    public Result searchMarketList(String user, int pageSize, int pageNo, String creator, String marketName, String serviceName) {
        AppServiceMarket appServiceMarket = new AppServiceMarket();
        if (StringUtils.isNotEmpty(creator)) {
            appServiceMarket.setCreator(creator);
        }
        PageData pd = new PageData();
        pd.setPage(pageNo);
        pd.setPageSize(pageSize);
        pd.setTotal(appServiceMarketDao.getTotal(creator, marketName, serviceName));
        pd.setList(appServiceMarketDao.SearchAppServiceMarketList(pageNo, pageSize, creator, marketName, serviceName));
        return Result.success(pd);
    }

    //改
    public Result updateMarket(String user, int id, String serviceList, String marketName, String remark, String belongTeam, int serviceType) {
        serviceType = platFormTypeExtensionService.getMarketType(serviceType);
        try {
            //查库是否有该记录
            AppServiceMarket appServiceMarket = appServiceMarketDao.SearchAppServiceMarket(id);
            if (appServiceMarket == null) {
                return Result.fail(ErrorCode.nonExistentServiceMarketId);
            }
            appServiceMarket.setMarketName(marketName.trim());
            appServiceMarket.setServiceList(serviceList.trim());
            appServiceMarket.setLastUpdater(user);
            appServiceMarket.setRemark(remark.trim());
            appServiceMarket.setBelongTeam(belongTeam.trim());
            appServiceMarket.setServiceType(serviceType);
            int dbResult = appServiceMarketDao.updateServiceMarket(appServiceMarket);
            log.info("ServiceMarketService.updateMarket dbResult: {}", dbResult);
            return Result.success("success");
        } catch (Exception e) {
            log.error("ServiceMarketService.updateMarket error : {}", e.toString());
            return Result.fail(ErrorCode.unknownError);
        }
    }

    //删
    public Result deleteMarket(String user, Integer id) {
        try {
            //查库是否有该记录
            AppServiceMarket appServiceMarket = appServiceMarketDao.SearchAppServiceMarket(id);
            if (appServiceMarket == null) {
                return Result.fail(ErrorCode.nonExistentServiceMarketId);
            }
            //删除
            int result = appServiceMarketDao.deleteServiceMarket(id);
            log.info("ServiceMarketService.deleteMarket dbResult:{}", result);
            return Result.success("success");
        } catch (Exception e) {
            log.error("ServiceMarketService.deleteMarket error : {}", e.toString());
            return Result.fail(ErrorCode.unknownError);
        }
    }
}
