package com.xiaomi.youpin.gwdash.service;

import com.xiaomi.youpin.gwdash.bo.OperationLogBo;
import com.xiaomi.youpin.gwdash.bo.Page;
import com.xiaomi.youpin.gwdash.bo.openApi.OperationLogRequest;
import com.xiaomi.youpin.infra.rpc.Result;
import com.xiaomi.youpin.gwdash.dao.model.OperationLogPO;
import com.xiaomi.youpin.infra.rpc.errors.GeneralCodes;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.pager.Pager;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

// todo: 后面使用dubbo调用gwdash提供的服务
@Slf4j
@Service
public class OperationLogServiceImpl implements OperationLogService {

    @Autowired
    Dao dao;

    /**
     * 存入操作日志
     * @param request
     */
    @Override
    public Result<Boolean> saveOperationLog(OperationLogRequest request) {
        log.info("operationLog request:{}",request);
        if (request == null || request.getUserName() == null || request.getAppName() ==null || request.getDataId() ==null){
            return Result.fail(GeneralCodes.ParamError, "参数错误");
        }
        OperationLogPO po = new OperationLogPO();
        po.setAppName(request.getAppName());
        po.setUserName(request.getUserName());
        po.setDataId(request.getDataId());
        po.setDataBefore(request.getDataBefore());
        po.setDataAfter(request.getDataAfter());
        po.setCreateTime(System.currentTimeMillis());
        po.setType(request.getType());
        po.setRemark(request.getRemark());
        dao.insert(po);
        return Result.success(true);
    }

    @Override
    public Page<OperationLogBo> queryLogInfoList(String appName, String dataId, int  type, int pageIndex, int pageSize) {

        log.info("queryLogInfoList appName:{},dataId:{},pageIndex:{},pageSize:{}",appName,dataId,pageIndex,pageSize);
        Cnd cnd = null;
        if (StringUtils.isNotBlank(appName)){
            cnd = Cnd.where("app_name","=",appName);
        }
        if (StringUtils.isNotBlank(dataId)){
            cnd = cnd.and("data_id","=",dataId);
        }
        List<OperationLogPO> resultPoList = new ArrayList<>();
        if (type == 0){
            if (cnd == null) cnd = (Cnd) Cnd.orderBy().asc("create_time");
            resultPoList = dao.query(OperationLogPO.class, cnd, new Pager(pageIndex, pageSize));
        }else {
            if (cnd == null) cnd = (Cnd) Cnd.orderBy().desc("create_time");
            resultPoList = dao.query(OperationLogPO.class, cnd, new Pager(pageIndex, pageSize));
        }
        List<OperationLogBo> list = resultPoList.stream().map(it -> {
            OperationLogBo operationLogBo = new OperationLogBo();
            BeanUtils.copyProperties(it, operationLogBo);
            return operationLogBo;
        }).collect(Collectors.toList());
        int count = dao.count(OperationLogPO.class);
        Page<OperationLogBo> page = new Page<OperationLogBo>(pageIndex, pageSize, count, list, true);
        log.info("result:{}",page);
        return page;
    }
}
