package com.xiaomi.youpin.gwdash.dao.model.transfer;

import lombok.Data;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;

import java.util.Date;


/**
 * @author zhangzhiyong
 */
@Data
@Table(value="gw_group_info")
public class GroupInfoTransfer {

    @Id(auto = false)
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
