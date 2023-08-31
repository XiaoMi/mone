package com.xiaomi.mone.tpc.common.vo;

import lombok.Data;
import lombok.ToString;

/**
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/10/17 16:58
 */
@Data
@ToString
public class DepartmentInfoVo {
    private String dept_name_path;
    private String dept_id_path;
    private String deptid;
    private String dept_name;
    private String mi_dept_level2;
    private String mi_dept_level2_desc;
    private String mi_dept_level3;
    private String mi_dept_level3_desc;
    private String mi_dept_level4;
    private String mi_dept_level4_desc;
    private String mi_dept_level5;
    private String mi_dept_level5_desc;
    private String mi_dept_level6;
    private String mi_dept_level6_desc;
}
