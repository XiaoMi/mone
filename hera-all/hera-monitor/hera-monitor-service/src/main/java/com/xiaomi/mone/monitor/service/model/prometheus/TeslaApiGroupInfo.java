package com.xiaomi.mone.monitor.service.model.prometheus;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author gaoxihui
 * @date 2021/11/26 3:29 下午
 */
@Data
public class TeslaApiGroupInfo implements Serializable {

    private Integer id;

    private Integer gid;

    private String name;

    private String description;

    private String baseUrl;

    private Long ctime;

    private Long utime;

    private List<String> domainList;

    private String metaDataName;

}
