package com.xiaomi.youpin.gwdash.dao.model.transfer;

import lombok.Data;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;

import java.util.Date;

/**
 * @author goodjava@qq.com
 * @date 2022/10/3 09:09
 */
@Data
@Table("gw_user_info")
public class UserInfoTransfer {

    @Id(auto = false)
    private int id;

    @Column("user_name")
    private String userName;

    @Column("user_phone")
    private String userPhone;

    @Column
    private String gids;

    @Column("create_date")
    private Date createDate;

    @Column("modify_date")
    private Date modifyDate;

    @Column
    private int status;

    @Column("tenant")
    private String tenement;
}
