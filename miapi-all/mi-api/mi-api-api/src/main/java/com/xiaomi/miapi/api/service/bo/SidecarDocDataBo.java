package com.xiaomi.miapi.api.service.bo;


import lombok.Data;

import java.io.Serializable;
/**
 * @author dongzhenxing
 * @date 2023/02/08
 */
@Data
public class SidecarDocDataBo implements Serializable {
    private String address;
    private String sidecarApiModuleInfo;
    private String sidecarApiModuleListAndApiInfo;
    private String sidecarApiParamsResponseInfo;
}