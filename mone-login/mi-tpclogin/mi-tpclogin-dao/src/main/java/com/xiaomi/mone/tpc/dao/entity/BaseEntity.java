package com.xiaomi.mone.tpc.dao.entity;

import lombok.Data;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.springframework.util.StringUtils;

import java.util.Date;

/**
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/3/3 9:53
 */
@Data
public abstract class BaseEntity {

    @Id
    private Long id;

    @Column(value = "type", wrap = true)
    private Integer type;

    @Column(value = "status", wrap = true)
    private Integer status;

    @Column(value = "desc", wrap = true)
    private String desc;

    @Column
    private String content;

    @Column("creater_id")
    private Long createrId;

    @Column("creater_acc")
    private String createrAcc;

    @Column("creater_type")
    private Integer createrType;

    @Column("updater_id")
    private Long updaterId;

    @Column("updater_acc")
    private String updaterAcc;

    @Column("updater_type")
    private Integer updaterType;

    @Column("create_time")
    private Date createTime;

    @Column("update_time")
    private Date updateTime;

    @Column
    private Integer deleted;

    public void insertInit() {
        Date now = new Date();
        if (createTime == null) {
            createTime = now;
        }
        if (updateTime == null) {
            updateTime = now;
        }
        if (type == null) {
            type = 0;
        }
        if (status == null) {
            status = 0;
        }
        if (createrId == null) {
            createrId = 0L;
        }
        if (StringUtils.isEmpty(createrAcc)) {
            createrAcc = "";
        }
        if (createrType == null) {
            createrType = 0;
        }
        if (updaterId == null) {
            updaterId = 0L;
        }
        if (StringUtils.isEmpty(updaterAcc)) {
            updaterAcc = "";
        }
        if (updaterType == null) {
            updaterType = 0;
        }
        if (deleted == null) {
            deleted = 0;
        }
    }

    public void updateInit() {
        updateTime = new Date();
    }

}
