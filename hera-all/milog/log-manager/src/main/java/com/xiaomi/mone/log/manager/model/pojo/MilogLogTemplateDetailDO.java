package com.xiaomi.mone.log.manager.model.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * milog日志模板详细
 * </p>
 *
 * @author wanghaoyang
 * @since 2021-09-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("milog_log_template_detail")
public class MilogLogTemplateDetailDO implements Serializable {

    private static final long serialVersionUID = 1L;

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
     * 日志模板ID
     */
    private String templateId;

    /**
     * 日志模板属性名
     */
    private String propertiesKey;

    /**
     * 日志模板属性类型
     */
    private String propertiesType;


}
