package com.xiaomi.mone.monitor.service.model.prometheus;

import com.xiaomi.mone.monitor.bo.PlatForm;
import com.xiaomi.mone.monitor.bo.PlatFormType;
import lombok.Data;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author gaoxihui
 * @date 2021/9/3 8:49 上午
 * 参数
 */
@Data
@ToString
public class MetricDetailQuery implements Serializable {

    private static final String domain = "domain";
    private static final String domain_hera_value = "hera";
    private static final String domain_cloud_platform_value = "mitelemetry";
    private static final String domain_tesla_value_china = "china_tesla";
    private static final String domain_tesla_value_youpin = "youpin-tesla";
    private static final Long tesla_projectId = 1l;

    private static final String host_ = "host";
    private static final String type_ = "type";
    private static final String errorType_ = "errorType";
    private static final String serviceName_ = "serviceName";
    private static final String dataSource_ = "dataSource";
    private static final String url_ = "url";

    private String serverEnv;
    private String area;

    private Long projectId;
    private String projectName;
    private Integer appSource;
    private String type;// http/dubbo_consumer/mysql
    private String errorType;// error/timeout
    private String serverIp;

    //dubbo label （metheodName与http的uri共用，当type为http当时候，methodName代表uri）
    private String methodName;
    private String serviceName;

    //sql label
    private String sql;
    private String dataSource;
    private String sqlMethod;

    //详情对应的列表数据项的查询开始时间
    private Long startTime;
    //详情对应的列表数据项的查询结束时间
    private Long endTime;

    private Integer page;
    private Integer pageSize;

    public Map<String,String> convertPrometheusParam(){
        Map<String, String> map = new HashMap<>();

        map.put("application",projectId + "_" + projectName.replaceAll("-","_"));
        map.put("serverIp",serverIp);

        if(StringUtils.isNoneBlank(methodName)){
            map.put("methodName",methodName);
        }
        if(StringUtils.isNoneBlank(serviceName)){
            map.put("serviceName",serviceName);
        }
        if(StringUtils.isNoneBlank(sqlMethod)){
            map.put("sqlMethod",sqlMethod);
        }
        if(StringUtils.isNoneBlank(sql)){
            map.put("sql",sql);
        }
        if(StringUtils.isNoneBlank(dataSource)){
            map.put("dataSource",dataSource);
        }


        return map;
    }


    /**
     * //TODO 添加子类别（区分慢查询/异常 等待丁涛）、sql（对应url），errorCode，耗时-duration
     * @return
     */
    public Map<String,String> convertEsParam(){
        Map<String,String> map = new HashMap<>();

        map.put(domain,  domain_hera_value);

        map.put(serviceName_,projectId + "_" + projectName.replaceAll("-","_"));
        map.put(host_,serverIp);
        map.put(type_,type);
        map.put(errorType_,errorType);


        if(EsIndexDataType.http.name().equals(type)
                ||EsIndexDataType.http_client.name().equals(type)
                ||EsIndexDataType.mq_consumer.name().equals(type)
                ||EsIndexDataType.mq_producer.name().equals(type)
                ||EsIndexDataType.redis.name().equals(type) ){

            map.put(url_,methodName);
        }

        if(EsIndexDataType.dubbo_consumer.name().equals(type)
                || EsIndexDataType.dubbo_provider.name().equals(type)
                ||EsIndexDataType.grpc_client.name().equals(type)
                ||EsIndexDataType.grpc_server.name().equals(type)
                ||EsIndexDataType.thrift_client.name().equals(type)
                ||EsIndexDataType.thrift_server.name().equals(type)
                ||EsIndexDataType.apus_client.name().equals(type)
                ||EsIndexDataType.apus_server.name().equals(type)
        ){
            map.put(url_,serviceName + "/" + methodName);
        }

        if(EsIndexDataType.mysql.name().equals(type)){
            map.put(dataSource_,dataSource);
            map.put(url_,sql);
        }

        return map;
    }


}
