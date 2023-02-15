package com.xiaomi.miapi.bo;

import lombok.Data;

import java.util.List;

/**
 * @author dongzhenxing
 * @date 2023/02/08
 */
@Data
public class ApiCacheItemBo {

    private Boolean async;

    private String name;

    private String apiName;

    private String apiEnv;

    private String apiDocName;

    private String apiVersion;

    private String apiGroup;

    private String description;

    private Integer apiNoteType;

    private String apiRemark;

    private String apiDesc;

    private String mavenAddr;

    private String apiRespDec;

    private String apiModelClass;

    private List<LayerItem> paramsLayerList;

    private LayerItem responseLayer;

    private Integer projectId;

    private Integer groupId;

    private String username;

    private Integer apiID;

    private String rspExp;

    private String reqExp = "";

    private String updateMsg;

    private String apiErrorCodes;
}
