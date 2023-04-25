package com.xiaomi.mone.app.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import com.xiaomi.mone.app.api.model.HeraSimpleEnv;
import com.xiaomi.mone.app.api.service.HeraAppEnvOutwardService;
import com.xiaomi.mone.app.dao.HeraAppEnvMapper;
import com.xiaomi.mone.app.model.HeraAppEnv;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.dubbo.config.annotation.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/11/12 11:39
 */
@Slf4j
@Service(interfaceClass = HeraAppEnvOutwardService.class, group = "${dubbo.group}")
@org.springframework.stereotype.Service
public class HeraAppEnvOutwardServiceImpl implements HeraAppEnvOutwardService {

    private final HeraAppEnvMapper heraAppEnvMapper;

    public HeraAppEnvOutwardServiceImpl(HeraAppEnvMapper heraAppEnvMapper) {
        this.heraAppEnvMapper = heraAppEnvMapper;
    }

    @Override
    public List<HeraSimpleEnv> querySimpleEnvAppBaseInfoId(Integer id) {
        QueryWrapper<HeraAppEnv> queryWrapper = new QueryWrapper<HeraAppEnv>().eq("hera_app_id", id);
        List<HeraAppEnv> heraAppEnvs = heraAppEnvMapper.selectList(queryWrapper);
        if (CollectionUtils.isNotEmpty(heraAppEnvs)) {
            return heraAppEnvs.stream().map(HeraAppEnv::toHeraSimpleEnv).collect(Collectors.toList());
        }
        return Lists.newArrayList();
    }
}
