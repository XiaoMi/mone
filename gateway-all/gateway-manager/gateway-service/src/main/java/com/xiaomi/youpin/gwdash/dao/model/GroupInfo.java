package com.xiaomi.youpin.gwdash.dao.model;

import lombok.Data;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;

import java.util.Date;


/**
 * @author jiangzh
 * created On 2021-01-18
 */
@Data
@Table(value="gw_group_info")
public class GroupInfo {
    @Id
    private int id;

    @Column
    private  String name;

    @Column
    private  String description;

    @Column(value="creation_date")
    private Date creationDate;

    @Column(value="modify_date")
    private  Date modifyDate;

    @Column
    private int status;

    /**
     * 租户
     */
    @Column
    private String tenement;

}
