package com.xiaomi.mone.log.manager.model.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
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
 * @since 2022-01-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("milog_log_count")
public class LogCountDO implements Serializable {

    private static final long serialVersionUID = 1L;
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * tail的ID
     */
    private Long tailId;

    /**
     * es索引名
     */
    private String esIndex;

    /**
     * 日志数据产生日yyyy-MM-dd
     */
    private String day;

    /**
     * 日志条数
     */
    private Long number;


}
