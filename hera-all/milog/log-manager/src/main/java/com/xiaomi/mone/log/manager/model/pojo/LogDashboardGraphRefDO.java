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
@TableName("milog_analyse_dashboard_graph_ref")
public class LogDashboardGraphRefDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long dashboardId;

    private Long graphId;

    private String point;

    private String privateName;

}
