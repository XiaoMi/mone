package com.xiaomi.mone.log.manager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaomi.mone.log.manager.model.pojo.MilogLogTemplateDetailDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * milog日志模板详细 Mapper 接口
 * </p>
 *
 * @author wanghaoyang
 * @since 2021-09-09
 */

@Mapper
public interface MilogLogTemplateDetailMapper extends BaseMapper<MilogLogTemplateDetailDO> {

    /**
     * 查询模板的detail
     * @param templateId
     * @return
     */
    MilogLogTemplateDetailDO getByTemplateId(@Param("templateId") long templateId);
}
