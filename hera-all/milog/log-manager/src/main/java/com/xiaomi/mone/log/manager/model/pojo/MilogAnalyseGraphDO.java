package com.xiaomi.mone.log.manager.model.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author wanghaoyang
 * @since 2022-08-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("milog_analyse_graph")
public class MilogAnalyseGraphDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String name;

    private String fieldName;

    private Integer graphType;

    private String graphParam;

    private Long spaceId;

    private Long storeId;

    private String creator;

    private String updater;

    private Long createTime;

    private Long updateTime;


}
