package com.xiaomi.mone.log.manager.common.validation;

import com.google.common.collect.Lists;
import com.xiaomi.mone.log.api.model.vo.ResourceUserSimple;
import com.xiaomi.mone.log.manager.common.context.MoneUserContext;
import com.xiaomi.mone.log.manager.common.exception.MilogManageException;
import com.xiaomi.mone.log.manager.model.vo.CreateOrUpdateLogStoreCmd;
import com.xiaomi.mone.log.manager.model.vo.LogStoreParam;
import com.xiaomi.mone.log.manager.service.impl.MilogMiddlewareConfigServiceImpl;
import com.xiaomi.youpin.docean.anno.Component;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

import static com.xiaomi.mone.log.common.Constant.SYMBOL_COMMA;

/**
 * @author: wtt
 * @date: 2022/5/12 17:10
 * @description: store 中参数校验
 */
@Slf4j
@Component
public class StoreValidation {

    @Resource
    private MilogMiddlewareConfigServiceImpl milogMiddlewareConfigService;

    public String logStoreParamValid(LogStoreParam param) {
        if (null == MoneUserContext.getCurrentUser()) {
            throw new MilogManageException("please go to login");
        }
        List<String> errorInfos = Lists.newArrayList();
        if (null == param.getSpaceId()) {
            errorInfos.add("space信息 不能为空");
        }
        if (null == param || StringUtils.isBlank(param.getLogstoreName())) {
            errorInfos.add("logStore 不能为空");
        }
        if (StringUtils.isEmpty(param.getMachineRoom())) {
            errorInfos.add("机房信息 不能为空");
            return errorInfos.stream().collect(Collectors.joining(SYMBOL_COMMA));
        }
        if (null == param.getLogType()) {
            errorInfos.add("日志类型 不能为空");
        }
        if (StringUtils.isEmpty(param.getKeyList())) {
            errorInfos.add("索引列 不能为空");
        }
        //校验当前用户所属部门是否初始化资源
        ResourceUserSimple resourceUserSimple = milogMiddlewareConfigService.userResourceList(param.getMachineRoom(), param.getLogType());
        if (!resourceUserSimple.getInitializedFlag()) {
            errorInfos.add(resourceUserSimple.getNotInitializedMsg());
        }
        boolean resourceChosen = null == param.getMqResourceId() || null == param.getEsResourceId();
        if (resourceUserSimple.getInitializedFlag() &&
                resourceUserSimple.getShowFlag() && resourceChosen) {
            errorInfos.add("请先选择所需要的资源信息");
        }
        return errorInfos.stream().collect(Collectors.joining(SYMBOL_COMMA));
    }
}
