package com.xiaomi.mone.log.manager.model.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 * milog日志模板
 * </p>
 *
 * @author wanghaoyang
 * @since 2021-09-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("milog_log_template")
public class MilogLogTemplateDO implements Serializable {

    /**
     * 主键Id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 创建时间
     */
    private Long ctime;

    /**
     * 更新时间
     */
    private Long utime;

    /**
     * 日志模板名称
     */
    private String templateName;

    /**
     * 日志模板类型0-自定义日志;1-app;2-nginx
     */
    private Integer type;

    private String supportArea;

    /**
     * 排序
     */
    private Integer orderCol;
    /**
     * 是否自持消费，1.支持
     */
    private Integer supportedConsume;

}
