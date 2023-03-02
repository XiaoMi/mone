package com.xiaomi.mone.log.manager.mapper;

import com.xiaomi.mone.log.manager.model.pojo.MilogLogTemplateDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * milog日志模板 Mapper 接口
 * </p>
 *
 * @author wanghaoyang
 * @since 2021-09-09
 */
@Mapper
public interface MilogLogTemplateMapper extends BaseMapper<MilogLogTemplateDO> {

    List<MilogLogTemplateDO> selectSupportedTemplate(@Param(value = "area") String area);
}
