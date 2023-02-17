package com.xiaomi.mone.log.manager.model.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain=true)
@TableName("milog_log_process")
public class MilogLogProcessDOMybatis {
    @TableId(type= IdType.AUTO)
    private Long id;
    private Long agentId;
    private String ip;
    private Integer fileRowNumber;
    private Long pointer;
    private Long collectTime;
    private Long ctime;
    private Long utime;
}
