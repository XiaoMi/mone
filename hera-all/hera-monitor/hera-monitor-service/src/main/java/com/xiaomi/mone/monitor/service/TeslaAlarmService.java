package com.xiaomi.mone.monitor.service;

import com.xiaomi.mone.monitor.dao.AppTeslaAlarmRuleDao;
import com.xiaomi.mone.monitor.dao.AppTeslaFeishuMappingDao;
import com.xiaomi.mone.monitor.dao.model.AppTeslaAlarmRule;
import com.xiaomi.mone.monitor.dao.model.AppTeslaFeishuMapping;
import com.xiaomi.mone.monitor.result.ErrorCode;
import com.xiaomi.mone.monitor.result.Result;
import com.xiaomi.mone.monitor.service.model.PageData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author gaoxihui
 * @date 2021/11/22 10:15 上午
 */
@Slf4j
@Service
public class TeslaAlarmService {

//    @Autowired
//    AppTeslaAlarmRuleDao appTeslaAlarmRuleDao;
//
//    @Autowired
//    AppTeslaFeishuMappingDao appTeslaFeishuMappingDao;
//
//    public Result<PageData> list(String name, String teslaGroupName, Integer type, String remark, Integer page, Integer pageSize){
//
//        if(page == null){
//            page = 1;
//        }
//        if(pageSize == null){
//            pageSize = 10;
//        }
//
//        try {
//            PageData pd = new PageData();
//            pd.setPage(page);
//            pd.setPageSize(pageSize);
//
//            Long count = appTeslaAlarmRuleDao.count(name, teslaGroupName, type, remark);
//            pd.setTotal(count);
//
//            if(count == 0){
//                return Result.success(pd);
//            }
//
//            List<AppTeslaAlarmRule> list = appTeslaAlarmRuleDao.list(name, teslaGroupName, type, remark, page, pageSize);
//            pd.setList(list);
//
//            return Result.success(pd);
//        } catch (Exception e) {
//            log.error("TeslaAlarmService.list Error!{}",e.getMessage(),e);
//            return Result.fail(ErrorCode.unknownError);
//        }
//    }
//
//    public Result<Object> insertAlarmRule(AppTeslaAlarmRule appTeslaAlarmRule){
//        try {
//            int insert = appTeslaAlarmRuleDao.insert(appTeslaAlarmRule);
//            if(insert < 1){
//                log.error("TeslaAlarmService.insertAlarmRule faile!appTeslaAlarmRule:{}",appTeslaAlarmRule.toString());
//                return Result.fail(ErrorCode.unknownError);
//            }
//            return Result.success(insert);
//        } catch (Exception e) {
//            log.error("TeslaAlarmService.insertAlarmRule Error!appTeslaAlarmRule:{},{}",appTeslaAlarmRule.toString(),e.getMessage(),e);
//            return Result.fail(ErrorCode.unknownError);
//        }
//    }
//
//    public Result<Object> updateAlarmRule(AppTeslaAlarmRule appTeslaAlarmRule){
//        try {
//            int insert = appTeslaAlarmRuleDao.update(appTeslaAlarmRule);
//            if(insert < 1){
//                log.error("TeslaAlarmService.insertAlarmRule faile!appTeslaAlarmRule:{}",appTeslaAlarmRule.toString());
//                return Result.fail(ErrorCode.unknownError);
//            }
//            return Result.success(insert);
//        } catch (Exception e) {
//            log.error("TeslaAlarmService.insertAlarmRule Error!appTeslaAlarmRule:{},{}",appTeslaAlarmRule.toString(),e.getMessage(),e);
//            return Result.fail(ErrorCode.unknownError);
//        }
//    }
//
//    public Result<Object> deleteRule(Integer id){
//        try {
//            int insert = appTeslaAlarmRuleDao.delete(id);
//            if(insert < 1){
//                log.error("TeslaAlarmService.deleteRule faile!id:{}",id);
//                return Result.fail(ErrorCode.unknownError);
//            }
//            return Result.success(insert);
//        } catch (Exception e) {
//            log.error("TeslaAlarmService.deleteRule Error!id:{},{}",id,e.getMessage(),e);
//            return Result.fail(ErrorCode.unknownError);
//        }
//    }
//
//
//    public Result<Object> insertFeiShuConfig(AppTeslaFeishuMapping appTeslaFeishuMapping){
//        try {
//            int insert = appTeslaFeishuMappingDao.create(appTeslaFeishuMapping);
//            if(insert < 1){
//                log.error("TeslaAlarmService.insertFeiShuConfig faile!appTeslaFeishuMapping:{}",appTeslaFeishuMapping.toString());
//                return Result.fail(ErrorCode.unknownError);
//            }
//            return Result.success(insert);
//        } catch (Exception e) {
//            log.error("TeslaAlarmService.insertFeiShuConfig Error!appTeslaFeishuMapping:{},{}",appTeslaFeishuMapping.toString(),e.getMessage(),e);
//            return Result.fail(ErrorCode.unknownError);
//        }
//    }
//
//    public Result<Object> deleteConfig(Integer id){
//        try {
//            int insert = appTeslaFeishuMappingDao.delete(id);
//            if(insert < 1){
//                log.error("TeslaAlarmService.deleteConfig fail!id:{}",id);
//                return Result.fail(ErrorCode.unknownError);
//            }
//            return Result.success(insert);
//        } catch (Exception e) {
//            log.error("TeslaAlarmService.deleteConfig Error!id:{},{}",id,e.getMessage(),e);
//            return Result.fail(ErrorCode.unknownError);
//        }
//    }
//
//    public Result<Object> updateConfig(AppTeslaFeishuMapping appTeslaFeishuMapping){
//        try {
//            int insert = appTeslaFeishuMappingDao.update(appTeslaFeishuMapping);
//            if(insert < 1){
//                log.error("TeslaAlarmService.updateConfig fail!appTeslaFeishuMapping:{}",appTeslaFeishuMapping.toString());
//                return Result.fail(ErrorCode.unknownError);
//            }
//            return Result.success(insert);
//        } catch (Exception e) {
//            log.error("TeslaAlarmService.updateConfig Error!appTeslaFeishuMapping:{},{}",appTeslaFeishuMapping.toString(),e.getMessage(),e);
//            return Result.fail(ErrorCode.unknownError);
//        }
//    }
//
//    public Result<PageData> listConfig(String teslaGroup,String feishuGid,String remark, Integer page, Integer pageSize){
//
//        if(page == null){
//            page = 1;
//        }
//        if(pageSize == null){
//            pageSize = 10;
//        }
//
//        try {
//            PageData pd = new PageData();
//            pd.setPage(page);
//            pd.setPageSize(pageSize);
//
//            Long count = appTeslaFeishuMappingDao.count(teslaGroup, feishuGid, remark);
//            pd.setTotal(count);
//
//            if(count == 0){
//                return Result.success(pd);
//            }
//
//            List<AppTeslaFeishuMapping> list = appTeslaFeishuMappingDao.list(teslaGroup, feishuGid, remark, page, pageSize);
//            pd.setList(list);
//
//            return Result.success(pd);
//        } catch (Exception e) {
//            log.error("TeslaAlarmService.listConfig Error!{}",e.getMessage(),e);
//            return Result.fail(ErrorCode.unknownError);
//        }
//    }

}
