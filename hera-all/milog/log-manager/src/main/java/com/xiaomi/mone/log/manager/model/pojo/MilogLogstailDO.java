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
 * @since 2022-01-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("milog_logstail")
public class MilogLogstailDO implements Serializable {

    private static final long serialVersionUID = 1L;

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
     * 创建人	
     */
    private String creator;

    /**
     * 更新人	
     */
    private String updater;

    /**
     * spaceId
     */
    private Long spaceId;

    /**
     * storeId
     */
    private Long storeId;

    /**
     * milog表主键
     */
    private Long milogAppId;

    /**
     * 应用id	
     */
    private Long appId;

    /**
     * 应用名	
     */
    private String appName;

    /**
     * 0.mione项目 1.mis项目
     */
    private Integer appType;

    /**
     * mis应用 机器类型 0.容器 1.物理机
     */
    private Integer machineType;

    /**
     * 环境Id	
     */
    private Integer envId;

    /**
     * 环境名称	
     */
    private String envName;

    /**
     * 日志解析类型：1:服务应用日志，2.分隔符，3：单行，4：多行，5：自定义
     */
    private Integer parseType;

    /**
     * 对于分隔符，该字段指定分隔符；对于自定义，该字段指定日志读取脚本	
     */
    private String parseScript;

    /**
     * 逗号分割，多个日志文件路径,e.g.:/home/work/log/xxx/server.log	
     */
    private String logPath;

    /**
     * value列表，多个用逗号分隔	
     */
    private String valueList;

    /**
     * ip列表	
     */
    private String ips;

    /**
     * mis 应用机房信息
     */
    private String motorRooms;

    /**
     * 应用别名	
     */
    private String tail;

    /**
     * filter配置	
     */
    private String filter;

    /**
     * mis应用索引配置
     */
    private String enEsIndex;


}
