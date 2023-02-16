package com.xiaomi.mone.monitor.service.model.middleware;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author gaoxihui
 * @date 2021/10/20 9:40 上午
 */
@Data
@Slf4j
public class DbInstanceQuery implements Serializable {

    Integer projectId;
    String projectName;
    String userName;
    String password;
    String domainPort;//ip:port
    String dataBaseName;
    String type;//db\redis
    String timeStamp;
    String createTime;

    Integer page;
    Integer pageSize;

    public Map<String,String> convertEsParam(){
        Map<String,String> map = new HashMap<>();
        if(projectId == null || StringUtils.isBlank(projectName)){
            log.error("DbInstanceQuery.convertEsParam param error!projectId={},projectName={}",projectId,projectName);
            return map;
        }
        map.put("appName",projectId + "_" + projectName);

//        /**
//         * test for group will be deleted
//         */
//        map.put("appName",projectName);
        map.put("type",type);
        return map;
    }
}
