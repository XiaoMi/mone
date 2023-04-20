package com.xiaomi.mone.log.manager.user;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.List;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2021/9/6 11:34
 */
@Data
public class UseDetailInfo {

    private String uid;
    private String personId;
    private String originalPersonId;
    private String name;
    private String displayName;
    private String type;
    private String userName;
    private String email;
    private String sex;
    private String headUrl;
    private String deptId;
    @SerializedName("deptDescr")
    private String deptDesc;
    @SerializedName("fullDeptDescr")
    private String fullDeptDesc;
    private List<DeptDescriptor> fullDeptDescriptorList;
    private String company;
    @SerializedName("companyDescr")
    private String companyDesc;
    private String hrStatus;
    private String source;


    @Data
    public static class DeptDescriptor {
        private String deptEnName;
        private String deptId;
        private String deptName;
        private Integer level;
    }

}
