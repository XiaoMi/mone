package com.xiaomi.miapi.bo;

import lombok.Data;

import java.util.List;
/**
 * @author dongzhenxing
 * @date 2023/02/08
 */
@Data
public class BatchImportApiBo {
    private String moduleClassName;
    private List<String> apiNames;
    private String env;
    private Integer projectID;
    private Integer groupID;
    private String ip;
    private Integer port;
    private Boolean forceUpdate;
    private String updateUserName;
}
