package com.xiaomi.mone.app.api.message;

import com.google.gson.JsonObject;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * @author gaoxihui
 * @date 2023/4/26 3:29 下午
 */
@Data
@ToString
public class HeraAppInfoModifyMessage implements Serializable {

        private Integer id;

        private Integer appId;

        private Integer iamTreeId;

        private Integer iamTreeType;

        private String appName;

        private String appCname;

        private String owner;

        private Integer platformType;

        private Integer bindType;

        private String appLanguage;

        private Integer appType;

        private JsonObject envMapping;

        private List<String> joinedMembers;

        private Boolean isNameChange;

        private Boolean isPlatChange;

        private Boolean isIamTreeIdChange;

        private Boolean isIamTreeTypeChange;

        private HeraAppModifyType modifyType;

}
