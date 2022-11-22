package com.xiaomi.youpin.gwdash.dao.model;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * gw_user_info
 * @author jiangzh
 */
@Data
public class GwUserInfo implements Serializable {
    private Integer id;

    /**
     * 分组名称
     */
    private String userName;

    /**
     * 分组描述
     */
    private String userPhone;

    private String gids;

    private Date createDate;

    private Date modifyDate;

    private Byte status;

    private String tenement;

    private static final long serialVersionUID = 1L;
}