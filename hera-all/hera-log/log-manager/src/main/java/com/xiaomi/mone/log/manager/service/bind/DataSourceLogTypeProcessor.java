package com.xiaomi.mone.log.manager.service.bind;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xiaomi.mone.log.manager.common.exception.MilogManageException;
import com.xiaomi.mone.log.manager.mapper.MilogLogTemplateMapper;
import com.xiaomi.mone.log.manager.model.pojo.MilogLogTemplateDO;

import java.util.Objects;

/**
 * @author wtt
 * @version 1.0
 * @description 读取数据库的方式判断
 * @date 2022/12/23 14:03
 */
@Processor(isDefault = true, order = 1000)
public class DataSourceLogTypeProcessor implements LogTypeProcessor {

    private static final Integer EXIST_STATUS = 1;

    private final MilogLogTemplateMapper milogLogTemplateMapper;

    public DataSourceLogTypeProcessor(MilogLogTemplateMapper milogLogTemplateMapper) {
        this.milogLogTemplateMapper = milogLogTemplateMapper;
    }

    @Override
    public boolean supportedConsume(Integer logTypeCode) {
        QueryWrapper<MilogLogTemplateDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("type", logTypeCode);
        MilogLogTemplateDO templateDO = milogLogTemplateMapper.selectOne(queryWrapper);
        if (null == templateDO) {
            throw new MilogManageException("log template not exist,logtypeType:" + logTypeCode);
        }
        return Objects.equals(EXIST_STATUS, templateDO.getSupportedConsume());
    }
}
