package com.xiaomi.mone.app.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/10/29 17:01
 */
@Data
@TableName(value = "hera_app_excess_info", autoResultMap = true)

public class HeraAppExcessInfo {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer appBaseId;

    @TableField(value = "tree_ids", typeHandler = JacksonTypeHandler.class)
    private List<Integer> treeIds;

    @TableField(value = "node_ips", typeHandler = JacksonTypeHandler.class)
    private LinkedHashMap<String, List<String>> nodeIPs;

    @TableField(value = "managers", typeHandler = JacksonTypeHandler.class)
    private List<String> managers;

    private Date createTime;

    private Date updateTime;
}
