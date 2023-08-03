/*
 * Copyright 2020 Xiaomi
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.xiaomi.mone.log.manager.model.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaomi.mone.log.api.model.vo.EsIndexVo;
import com.xiaomi.mone.log.manager.domain.ClusterIndexVO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.nutz.dao.entity.annotation.Column;

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
public class MilogEsIndexDO implements Serializable {

    private static final long serialVersionUID = -4651856748263697198L;
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    @Column(value = "id")
    private Long id;

    /**
     * 所属集群id
     */
    @Column(value = "cluster_id")
    private Long clusterId;

    /**
     * 日志类型
     */
    @Column(value = "log_type")
    private Integer logType;

    /**
     * es索引名
     */
    @Column(value = "index_name")
    private String indexName;


    public static List<MilogEsIndexDO> essIndexVoToIndexDO(Long clusterId, EsIndexVo esIndexVo) {

        return esIndexVo.getEsIndexList().stream().map(esIndex -> {
            MilogEsIndexDO milogEsIndexDO = new MilogEsIndexDO();
            milogEsIndexDO.setClusterId(clusterId);
            milogEsIndexDO.setLogType(esIndexVo.getLogTypeCode());
            milogEsIndexDO.setIndexName(esIndex);
            return milogEsIndexDO;
        }).collect(Collectors.toList());
    }

    public ClusterIndexVO toClusterIndexVO() {
        return ClusterIndexVO.builder().clusterId(this.getClusterId()).indexName(this.getIndexName()).build();
    }


}
