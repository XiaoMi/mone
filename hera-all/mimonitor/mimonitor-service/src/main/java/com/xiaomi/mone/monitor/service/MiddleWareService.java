package com.xiaomi.mone.monitor.service;

import com.xiaomi.mone.monitor.result.Result;
import com.xiaomi.mone.monitor.service.es.EsService;
import com.xiaomi.mone.monitor.service.model.PageData;
import com.xiaomi.mone.monitor.service.model.middleware.DbInstanceQuery;
import com.xiaomi.mone.monitor.service.model.middleware.MiddleType;
import com.xiaomi.mone.monitor.service.model.middleware.MiddlewareInstanceInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.List;

/**
 * @author gaoxihui
 * @date 2021/10/25 9:28 上午
 */
@Slf4j
@Service
public class MiddleWareService {

    @Autowired
    EsService esService;

    @Autowired
    KerService kerService;

    @Autowired
    ZeusService zeusService;

    public Result queryMiddlewareInstance(String user,DbInstanceQuery param, Integer page, Integer pageSize) throws IOException {

        Result result = esService.queryMiddlewareInstance(param, page, pageSize);

        PageData pd =  (PageData)result.getData();

        List<MiddlewareInstanceInfo> datas = (List<MiddlewareInstanceInfo>) pd.getList();

        if(CollectionUtils.isEmpty(datas)){
            log.info("queryMiddlewareInstance datas is empty!param:{}",param.toString());
            return result;
        }

        for(MiddlewareInstanceInfo info : datas){
            if(info.getType().equals(MiddleType.db.name())){
                String url = zeusService.requestZeusGrafana(user, info.getDomainPort(), info.getUserName(), info.getPassword());
                info.setUrl(url);
            }
            if(info.getType().equals(MiddleType.redis.name())){
                String url = kerService.requestKerGrafana(info.getPassword());
                info.setUrl(url);
            }
        }

        return result;
    }


}
