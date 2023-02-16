package com.xiaomi.mone.app.api.response;

import lombok.Data;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/11/2 11:45
 */
@Data
public class AppBaseInfo implements Serializable {
    private Integer id;
    private String bindId;
    private String appName;
    private String appCname;
    private Integer platformType;
    private String platformName;
    private Integer appType;
    private String appTypeName;
    private List<Integer> treeIds;
    private LinkedHashMap<String, List<String>> nodeIPs;
}
