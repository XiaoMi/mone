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
 * @since 2022-08-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("milog_analyse_dashboard_graph_ref")
public class MilogAnalyseDashboardGraphRefDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long dashboardId;

    private Long graphId;

    private String point;

    private String privateName;

}
