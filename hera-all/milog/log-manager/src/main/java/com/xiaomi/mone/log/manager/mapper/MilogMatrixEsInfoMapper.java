package com.xiaomi.mone.log.manager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaomi.mone.log.manager.model.pojo.MilogMatrixEsInfoDO;
import org.nutz.mvc.annotation.Param;

import java.util.List;

/**
 * @author zhangjuan
 * @date 2022-06-17
 */
public interface MilogMatrixEsInfoMapper extends BaseMapper<MilogMatrixEsInfoDO> {

    /**
     * 获取 cluster 对应的 matrix es 配置信息
     * @param cluster
     * @return
     */
    List<MilogMatrixEsInfoDO> selectByCluster(@Param("cluster") String cluster);
}
