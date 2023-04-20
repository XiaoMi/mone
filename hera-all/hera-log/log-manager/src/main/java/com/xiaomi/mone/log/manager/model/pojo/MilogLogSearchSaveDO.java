package com.xiaomi.mone.log.manager.model.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author wanghaoyang
 * @since 2022-03-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("milog_log_search_save")
public class MilogLogSearchSaveDO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String name;

    private Long spaceId;

    private Long storeId;

    private Long tailId;

    private String queryText;

    /**
     * 1-保存了时间参数；0-没有保存
     */
    private Integer isFixTime;

    private Long startTime;

    private Long endTime;

    /**
     * 备注
     */
    private String common;

    private Integer sort;

    private Integer orderNum;

    private String creator;

    private String updater;

    private Long createTime;

    private Long updateTime;

}
