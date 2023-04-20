package com.xiaomi.mone.monitor.service;

import com.xiaomi.mone.monitor.dao.AppQualityMarketDao;
import com.xiaomi.mone.monitor.dao.model.AppQualityMarket;
import com.xiaomi.mone.monitor.result.ErrorCode;
import com.xiaomi.mone.monitor.result.Result;
import com.xiaomi.mone.monitor.service.model.PageData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class QualityMarketService {
    @Autowired
    AppQualityMarketDao appQualityMarketDao;
    public Result createMarket(String user, String marketName, String serviceList, String remark) {
        try {
            //按;切分serviceList
            String[] services = serviceList.split(";");
            //入库
            AppQualityMarket appQualityMarket = new AppQualityMarket();
            appQualityMarket.setMarketName(marketName.trim());
            appQualityMarket.setServiceList(serviceList.trim());
            appQualityMarket.setCreator(user);
            appQualityMarket.setLastUpdater(user);
            appQualityMarket.setRemark(remark.trim());
            int dbResult =  appQualityMarketDao.insertServiceMarket(appQualityMarket);
            log.info("QualityMarketService.createMarket dbResult: {}", dbResult);
            return Result.success("success");
        }
        catch (Exception e) {
            log.error("QualityMarketService.createMarket error : {}",e.toString());
            return Result.fail(ErrorCode.unknownError);
        }
    }

    public Result searchMarket(String user,int id) {
        try {
            AppQualityMarket appQualityMarket = appQualityMarketDao.SearchAppQualityMarket(id);
            return Result.success(appQualityMarket);
        }catch (Exception e) {
            log.error("QualityMarketService.searchMarket error : {}",e.toString());
            return Result.fail(ErrorCode.unknownError);
        }
    }

    //改
    public Result updateMarket(String user,int id,String serviceList,String marketName,String remark) {
        try{
            //查库是否有该记录
            AppQualityMarket appQualityMarket = appQualityMarketDao.SearchAppQualityMarket(id);
            if (appQualityMarket == null) {
                return Result.fail(ErrorCode.nonExistentServiceMarketId);
            }
            appQualityMarket.setMarketName(marketName.trim());
            appQualityMarket.setServiceList(serviceList.trim());
            appQualityMarket.setLastUpdater(user);
            appQualityMarket.setRemark(remark.trim());
            int dbResult = appQualityMarketDao.updateQualityMarket(appQualityMarket);
            log.info("QualityMarketService.updateMarket dbResult: {}", dbResult);
            return Result.success("success");
        }catch (Exception e) {
            log.error("QualityMarketService.updateMarket error : {}",e.toString());
            return Result.fail(ErrorCode.unknownError);
        }
    }

    //删
    public Result deleteMarket(String user,Integer id) {
        try{
            //查库是否有该记录
            AppQualityMarket appQualityMarket = appQualityMarketDao.SearchAppQualityMarket(id);
            if (appQualityMarket == null) {
                return Result.fail(ErrorCode.nonExistentServiceMarketId);
            }
            //删除
            int result = appQualityMarketDao.deleteQualityMarket(id);
            log.info("QualityMarketService.deleteMarket dbResult:{}",result);
            return Result.success("success");
        }catch (Exception e) {
            log.error("QualityMarketService.deleteMarket error : {}",e.toString());
            return Result.fail(ErrorCode.unknownError);
        }
    }


    //查列表
    public Result searchMarketList(String user,int pageSize,int pageNo,String creator,String marketName,String serviceName) {
        AppQualityMarket appQualityMarket = new AppQualityMarket();
        if (StringUtils.isNotEmpty(creator)) {
            appQualityMarket.setCreator(creator);
        }
        PageData pd = new PageData();
        pd.setPage(pageNo);
        pd.setPageSize(pageSize);
        pd.setTotal(appQualityMarketDao.getTotal(creator,marketName,serviceName));
        pd.setList(appQualityMarketDao.SearchAppQualityMarketList(pageNo,pageSize,creator,marketName,serviceName));
        return Result.success(pd);
    }

}
