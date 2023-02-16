package com.xiaomi.mone.monitor.service.user;

import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2021/9/6 11:34
 */
@Data
@ToString
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
    private String deptDescr;
    private String fullDeptDescr;
    private List<DeptDescr> fullDeptDescrList;
    private String company;
    private String companyDescr;
    private String hrStatus;
    private String source;


    @Data
    public static class DeptDescr {
        private String deptEnName;
        private String deptId;
        private String deptName;
        private String level;
    }

}
