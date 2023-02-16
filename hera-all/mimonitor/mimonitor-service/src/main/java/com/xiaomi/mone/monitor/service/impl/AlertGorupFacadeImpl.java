package com.xiaomi.mone.monitor.service.impl;

import com.xiaomi.mone.monitor.bo.AlertGroupInfo;
import com.xiaomi.mone.monitor.bo.AlertGroupParam;
import com.xiaomi.mone.monitor.service.AlertGorupFacade;
import com.xiaomi.mone.monitor.service.AlertGroupService;
import com.xiaomi.mone.monitor.service.bo.AlertGroupQryInfo;
import com.xiaomi.mone.monitor.service.model.PageData;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @project: mimonitor
 * @author: zgf1
 * @date: 2022/8/30 19:33
 */
@Slf4j
@Service(registry = "registryConfig",interfaceClass = AlertGorupFacade.class, retries = 0, group = "${dubbo.group}", version = "1.0")
public class AlertGorupFacadeImpl implements AlertGorupFacade {

    @Autowired
    private AlertGroupService alertGroupService;

    @Override
    public List<AlertGroupQryInfo> query(String account, String likeName) {
        try {
            AlertGroupParam searchParam = new AlertGroupParam();
            searchParam.setPage(1);
            searchParam.setPageSize(20);
            searchParam.setName(likeName);
            searchParam.setType("alert");
            com.xiaomi.mone.monitor.result.Result<PageData<List<AlertGroupInfo>>> searchResult =  alertGroupService.alertGroupSearch(account, searchParam);
            if (searchResult == null || searchResult.getData() == null || CollectionUtils.isEmpty(searchResult.getData().getList())) {
                return null;
            }
            return searchResult.getData().getList().stream().map(info -> {
                AlertGroupQryInfo qryInfo = new AlertGroupQryInfo();
                BeanUtils.copyProperties(info, qryInfo);
                return qryInfo;
            }).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("AlertGorupFacadeImpl.query;查询异常; account={}, likeName={}", account, likeName, e);
            return null;
        }
    }
}
