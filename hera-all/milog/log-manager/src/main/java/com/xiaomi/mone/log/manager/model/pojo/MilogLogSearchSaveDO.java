package com.xiaomi.mone.log.manager.model.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

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

    private Long storeId;

    private String name;

    private String param;

    /**
     * 1-保存了时间参数；0-没有保存
     */
    private Integer isFixTime;

    /**
     * 备注
     */
    private String common;

    private String creator;

    private String updater;

    private Long createTime;

    private Long updateTime;

    private Long startTime;

    private Long endTime;

}
