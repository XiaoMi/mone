package com.xiaomi.miapi.bo;

import com.xiaomi.mone.http.docs.core.beans.HttpLayerItem;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author dongzhenxing
 * @date 2023/02/08
 */
@Data
public class HttpApiCacheItem implements Serializable {

    private String apiName;

    private String apiPath;

    private String apiMethod;

    private String description;

    private String apiRespDec;

    private String apiTag;

    private String paramsDesc;

    private List<HttpLayerItem> paramsLayerList;

    private String response;

    private HttpLayerItem responseLayer;

}
