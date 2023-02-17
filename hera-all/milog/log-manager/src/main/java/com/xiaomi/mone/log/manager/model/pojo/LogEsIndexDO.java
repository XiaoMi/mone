package com.xiaomi.mone.log.manager.model.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaomi.mone.log.api.model.vo.EsIndexVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 
 * </p>
 *
 * @author wanghaoyang
 * @since 2021-09-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("milog_es_index")
public class LogEsIndexDO implements Serializable {

    private static final long serialVersionUID = -4651856748263697198L;
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 所属集群id
     */
    private Long clusterId;

    /**
     * 日志类型
     */
    private Integer logType;

    /**
     * es索引名
     */
    private String indexName;


    public static List<LogEsIndexDO> essIndexVoToIndexDO(Long clusterId, EsIndexVo esIndexVo) {

        return esIndexVo.getEsIndexList().stream().map(esIndex -> {
            LogEsIndexDO milogEsIndexDO = new LogEsIndexDO();
            milogEsIndexDO.setClusterId(clusterId);
            milogEsIndexDO.setLogType(esIndexVo.getLogTypeCode());
            milogEsIndexDO.setIndexName(esIndex);
            return milogEsIndexDO;
        }).collect(Collectors.toList());
    }


}
