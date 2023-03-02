package com.xiaomi.miapi.bo;

import lombok.Data;

import java.util.List;
/**
 * @author dongzhenxing
 * @date 2023/02/08
 */
@Data
public class ApiCacheItem {
    private Boolean async;
    private String apiName;
    private String apiDocName;
    private String apiVersion;
    private String apiGroup;
    private String description;
    private String mavenAddr;
    private String apiModelClass;
    private List<LayerItem> paramsLayerList;
    private LayerItem responseLayer;
    private String response;
    private String request;
}
