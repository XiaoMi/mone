package com.xiaomi.mone.log.manager.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaomi.mone.log.api.enums.LogTypeEnum;
import com.xiaomi.mone.log.common.Result;
import com.xiaomi.mone.log.manager.model.convert.MilogLogTemplateConvert;
import com.xiaomi.mone.log.manager.model.convert.MilogLongTemplateDetailConvert;
import com.xiaomi.mone.log.manager.mapper.MilogLogTemplateDetailMapper;
import com.xiaomi.mone.log.manager.mapper.MilogLogTemplateMapper;
import com.xiaomi.mone.log.manager.model.dto.LogTemplateDTO;
import com.xiaomi.mone.log.manager.model.dto.LogTemplateDetailDTO;
import com.xiaomi.mone.log.manager.model.pojo.MilogLogTemplateDO;
import com.xiaomi.mone.log.manager.model.pojo.MilogLogTemplateDetailDO;
import com.xiaomi.mone.log.manager.service.LogTemplateService;
import com.xiaomi.youpin.docean.anno.Service;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Resource;
import java.util.List;

@Service
@Slf4j
public class LogTemplateServiceImpl extends ServiceImpl<MilogLogTemplateMapper, MilogLogTemplateDO> implements LogTemplateService {

    @Resource
    private MilogLogTemplateMapper milogLogTemplateMapper;

    @Resource
    private MilogLogTemplateDetailMapper templateDetailMapper;

    /**
     * 日志模板列表
     *
     * @return
     */
    @Override
    public Result<List<LogTemplateDTO>> getLogTemplateList(String area) {
        List<MilogLogTemplateDO> logTemplateDOList = milogLogTemplateMapper.selectSupportedTemplate(area);
        List<LogTemplateDTO> logTemplateDTOList = MilogLogTemplateConvert.INSTANCE.fromDOList(logTemplateDOList);
        assembleLogTemplateDetail(logTemplateDTOList);
        return Result.success(logTemplateDTOList);
    }

    private void assembleLogTemplateDetail(List<LogTemplateDTO> logTemplateDTOList) {
        if (CollectionUtils.isNotEmpty(logTemplateDTOList)) {
            logTemplateDTOList.forEach(logTemplateDTO -> {
                logTemplateDTO.setLogTemplateDetailDTOList(getLogTemplateById(logTemplateDTO.getValue()).getData());
                LogTypeEnum logTypeEnum = LogTypeEnum.type2enum(logTemplateDTO.getType());
                logTemplateDTO.setDescribe(null != logTypeEnum ? logTypeEnum.getDescribe() : StringUtils.EMPTY);
            });
        }
    }


    /**
     * 获取日志模板
     *
     * @param logTemplateId
     * @return
     */
    @Override
    public Result<LogTemplateDetailDTO> getLogTemplateById(long logTemplateId) {
        MilogLogTemplateDO logTemplate = milogLogTemplateMapper.selectById(logTemplateId);
        MilogLogTemplateDetailDO tmplateDetail = templateDetailMapper.getByTemplateId(logTemplateId);
        if (tmplateDetail == null) {
            return Result.success(null);
        }
        LogTemplateDetailDTO dto = MilogLongTemplateDetailConvert.INSTANCE.fromDO(logTemplate, tmplateDetail);
        return Result.success(dto);
    }

}
