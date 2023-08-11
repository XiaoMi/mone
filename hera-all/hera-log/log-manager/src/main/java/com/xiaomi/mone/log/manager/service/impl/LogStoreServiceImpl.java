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
package com.xiaomi.mone.log.manager.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Pair;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import com.xiaomi.mone.log.api.enums.LogStructureEnum;
import com.xiaomi.mone.log.api.enums.LogTypeEnum;
import com.xiaomi.mone.log.api.enums.OperateEnum;
import com.xiaomi.mone.log.common.Result;
import com.xiaomi.mone.log.exception.CommonError;
import com.xiaomi.mone.log.manager.common.context.MoneUserContext;
import com.xiaomi.mone.log.manager.common.validation.StoreValidation;
import com.xiaomi.mone.log.manager.dao.MilogLogTailDao;
import com.xiaomi.mone.log.manager.dao.MilogLogstoreDao;
import com.xiaomi.mone.log.manager.domain.EsIndexTemplate;
import com.xiaomi.mone.log.manager.domain.LogTail;
import com.xiaomi.mone.log.manager.mapper.MilogEsIndexMapper;
import com.xiaomi.mone.log.manager.model.convert.MilogLogstoreConvert;
import com.xiaomi.mone.log.manager.model.dto.EsInfoDTO;
import com.xiaomi.mone.log.manager.model.dto.LogStoreDTO;
import com.xiaomi.mone.log.manager.model.dto.MapDTO;
import com.xiaomi.mone.log.manager.model.dto.MenuDTO;
import com.xiaomi.mone.log.manager.model.pojo.MilogEsIndexDO;
import com.xiaomi.mone.log.manager.model.pojo.MilogLogStoreDO;
import com.xiaomi.mone.log.manager.model.pojo.MilogLogTailDo;
import com.xiaomi.mone.log.manager.model.pojo.MilogMiddlewareConfig;
import com.xiaomi.mone.log.manager.model.vo.LogStoreParam;
import com.xiaomi.mone.log.manager.service.BaseService;
import com.xiaomi.mone.log.manager.service.LogStoreService;
import com.xiaomi.mone.log.manager.service.extension.common.CommonExtensionService;
import com.xiaomi.mone.log.manager.service.extension.common.CommonExtensionServiceFactory;
import com.xiaomi.mone.log.manager.service.extension.resource.ResourceExtensionService;
import com.xiaomi.mone.log.manager.service.extension.resource.ResourceExtensionServiceFactory;
import com.xiaomi.mone.log.manager.service.extension.store.StoreExtensionService;
import com.xiaomi.mone.log.manager.service.extension.store.StoreExtensionServiceFactory;
import com.xiaomi.youpin.docean.anno.Service;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static com.xiaomi.mone.log.common.Constant.SUCCESS_MESSAGE;
import static com.xiaomi.mone.log.manager.service.impl.EsDataServiceImpl.requiredFields;

/**
 * @author milog
 */
@Service
@Slf4j
public class LogStoreServiceImpl extends BaseService implements LogStoreService {

    @Resource
    private MilogLogstoreDao logStoreDao;

    @Resource
    private MilogLogTailDao milogLogtailDao;

    @Resource
    private EsIndexTemplate esIndexTemplate;

    @Resource
    private StoreValidation storeValidation;

    @Resource
    private MilogMiddlewareConfigServiceImpl milogMiddlewareConfigService;

    @Resource
    private MilogEsIndexMapper milogEsIndexMapper;

    @Resource
    private LogTail logTail;

    @Resource
    private LogTailServiceImpl logTailService;

    private StoreExtensionService storeExtensionService;

    private ResourceExtensionService resourceExtensionService;

    private CommonExtensionService commonExtensionService;

    /**
     * init method
     */
    public void init() {
        storeExtensionService = StoreExtensionServiceFactory.getStoreExtensionService();
        resourceExtensionService = ResourceExtensionServiceFactory.getResourceExtensionService();
        commonExtensionService = CommonExtensionServiceFactory.getCommonExtensionService();
    }

    @Override
    public Result<String> newLogStore(LogStoreParam cmd) {
        if (null != cmd.getId()) {
            return updateLogStore(cmd);
        } else {
            return createLogStore(cmd);
        }
    }

    private Result<String> createLogStore(LogStoreParam cmd) {
        String errorInfos = storeValidation.logStoreParamValid(cmd);
        if (StringUtils.isNotEmpty(errorInfos)) {
            return Result.failParam(errorInfos);
        }
        if (!cmd.getNameSameStatus() && logStoreDao.verifyExistByName(cmd.getLogstoreName(), null)) {
            return new Result<>(CommonError.UnknownError.getCode(), "存在同名storeName", "");
        }
        List<MilogLogStoreDO> logStoreDOS = logStoreDao.queryBySpaceIdNamed(cmd.getSpaceId(), cmd.getLogstoreName());
        if (CollectionUtils.isNotEmpty(logStoreDOS)) {
            return Result.failParam("store名称重复，请重新填写名称");
        }
        MilogLogStoreDO storeDO = MilogLogstoreConvert.INSTANCE.fromCommad(cmd);
        wrapBaseCommon(storeDO, OperateEnum.ADD_OPERATE);
        // 绑定资源
        storeExtensionService.storeResourceBinding(storeDO, cmd, OperateEnum.ADD_OPERATE);
        checkRequiredFieldExist(storeDO);
        // 存储
        boolean res = logStoreDao.newMilogLogStore(storeDO);
        if (res == true) {
            storeExtensionService.postProcessing(storeDO, cmd);
            return new Result<>(CommonError.Success.getCode(), CommonError.Success.getMessage());
        } else {
            log.warn("[MilogLogstoreService.newMilogLogstore] creator MilogLogstore err,logstoreName:{}", cmd.getLogstoreName());
            return new Result<>(CommonError.UnknownError.getCode(), CommonError.UnknownError.getMessage());
        }

    }

    private void checkRequiredFieldExist(MilogLogStoreDO storeDO) {
        String keyList = storeDO.getKeyList();
        String columnTypeList = storeDO.getColumnTypeList();
        for (Pair<String, String> requiredField : requiredFields) {
            if (!keyList.contains(requiredField.getKey())) {
                keyList = String.format("%s,%s:1", keyList, requiredField.getKey());
                columnTypeList = String.format("%s,%s", columnTypeList, requiredField.getValue());
            }
        }
        storeDO.setKeyList(keyList);
        storeDO.setColumnTypeList(columnTypeList);
    }

    @Override
    public MilogLogStoreDO buildLogStoreEsInfo(LogStoreParam storeParam, String creator) {
        // get esIndex
        EsInfoDTO esInfo = esIndexTemplate.getEsInfo(storeParam.getMachineRoom(), storeParam.getLogType());
        storeParam.setEsIndex(esInfo.getIndex());
        MilogLogStoreDO ml = MilogLogstoreConvert.INSTANCE.fromCommad(storeParam);
        ml.setEsClusterId(esInfo.getClusterId());
        wrapBaseCommon(ml, OperateEnum.ADD_OPERATE, creator);
        return ml;
    }

    @Override
    public Result<LogStoreDTO> getLogStoreById(Long id) {
        if (null == id) {
            return Result.failParam("id can not be empty");
        }
        MilogLogStoreDO milogLogStoreDO = logStoreDao.queryById(id);
        LogStoreDTO logStoreDTO = new LogStoreDTO();
        if (null != milogLogStoreDO) {
            BeanUtil.copyProperties(milogLogStoreDO, logStoreDTO);
            if (MoneUserContext.getCurrentUser().getIsAdmin()) {
                logStoreDTO.setSelectCustomIndex(Boolean.TRUE);
            }
            logStoreDTO.setEsResourceId(milogLogStoreDO.getEsClusterId());
            logStoreDTO.setLogTypeName(LogTypeEnum.queryNameByType(milogLogStoreDO.getLogType()));
            logStoreDTO.setMachineRoomName(commonExtensionService.getMachineRoomName(milogLogStoreDO.getMachineRoom()));
        }
        return Result.success(logStoreDTO);
    }

    public Result<List<MapDTO<String, Long>>> getLogStoreBySpaceId(Long spaceId) {
        List<MilogLogStoreDO> stores = logStoreDao.getMilogLogstoreBySpaceId(spaceId);
        ArrayList<MapDTO<String, Long>> ret = new ArrayList<>();
        for (MilogLogStoreDO s : stores) {
            ret.add(new MapDTO<>(s.getLogstoreName(), s.getId()));
        }
        return new Result<>(CommonError.Success.getCode(), CommonError.Success.getMessage(), ret);
    }

    public Result<Map<String, Object>> getLogStoreByPage(String logstoreName, Long spaceId, int page, int pagesize) {
        Map<String, Object> ret = logStoreDao.getMilogLogstoreByPage(logstoreName, spaceId, page, pagesize);
        return new Result<>(CommonError.Success.getCode(), CommonError.Success.getMessage(), ret);
    }

    public Result<Map<String, Object>> getAllLogStore() {
        Map<String, Object> ret = logStoreDao.getAllMilogLogstore(MoneUserContext.getCurrentUser().getZone());
        return new Result<>(CommonError.Success.getCode(), CommonError.Success.getMessage(), ret);
    }

    public Result<List<MilogLogStoreDO>> getLogStoreByIds(List<Long> ids) {
        List<MilogLogStoreDO> ret = logStoreDao.getMilogLogstore(ids);
        return new Result<>(CommonError.Success.getCode(), CommonError.Success.getMessage(), ret);
    }

    public Result<String> updateLogStore(LogStoreParam param) {
        MilogLogStoreDO milogLogstoreDO = logStoreDao.queryById(param.getId());
        if (null == milogLogstoreDO) {
            return new Result<>(CommonError.ParamsError.getCode(), "logstore 不存在");
        }
        if (!param.getLogType().equals(milogLogstoreDO.getLogType()) && 0 != milogLogtailDao.getTailCount(param.getId())) {
            return new Result<>(CommonError.ParamsError.getCode(), "logstore 下已经创建 logtail，不允许修改日志类型");
        }
        String errorInfos = storeValidation.logStoreParamValid(param);
        if (StringUtils.isNotEmpty(errorInfos)) {
            return Result.failParam(errorInfos);
        }
        if (logStoreDao.verifyExistByName(param.getLogstoreName(), param.getId())) {
            return new Result(CommonError.UnknownError.getCode(), "存在同名storeName", "");
        }

        MilogLogStoreDO ml = MilogLogstoreConvert.INSTANCE.fromCommad(param);
        ml.setEsClusterId(milogLogstoreDO.getEsClusterId());
        ml.setEsIndex(milogLogstoreDO.getEsIndex());
        ml.setCtime(milogLogstoreDO.getCtime());
        ml.setCreator(milogLogstoreDO.getCreator());
        // 选择对应的索引
        storeExtensionService.storeResourceBinding(ml, param, OperateEnum.UPDATE_OPERATE);
        wrapBaseCommon(ml, OperateEnum.UPDATE_OPERATE);
        checkRequiredFieldExist(ml);
        boolean updateRes = storeExtensionService.updateLogStore(ml);
        if (updateRes && storeExtensionService.sendConfigSwitch(param)) {
            //查看是否有tail 如果有重新发送配置信息（nacos 和 agent）
            logTail.handleStoreTail(milogLogstoreDO.getId());
            return new Result<>(CommonError.Success.getCode(), CommonError.Success.getMessage());
        }
        return Result.success(SUCCESS_MESSAGE);
    }

    public Result<Void> deleteLogStore(Long id) {
        MilogLogStoreDO logStore = logStoreDao.queryById(id);
        if (null == logStore) {
            return new Result<>(CommonError.ParamsError.getCode(), "logstore 不存在");
        }
        List<MilogLogTailDo> tails = milogLogtailDao.getMilogLogtailByStoreId(id);
        if (tails != null && tails.size() != 0) {
            return new Result<>(CommonError.ParamsError.getCode(), "该 log store 下存在tail，无法删除");
        }
        if (logStoreDao.deleteMilogSpace(id)) {
            //删除nacos中的配置
            logTailService.deleteConfigRemote(logStore.getSpaceId(), id, logStore.getMachineRoom(), LogStructureEnum.STORE);
            storeExtensionService.deleteStorePostProcessing(logStore);
            return new Result<>(CommonError.Success.getCode(), CommonError.Success.getMessage());
        } else {
            log.warn("[MilogLogstoreService.deleteMilogLogstore] delete Milogstore err,spaceId:{}", id);
            return new Result<>(CommonError.UnknownError.getCode(), CommonError.UnknownError.getMessage());
        }
    }


    public Result<List<Map<String, String>>> getStoreIps(Long storeId) {
        if (storeId == null) {
            return Result.failParam("参数不能为空");
        }
        final List<MilogLogTailDo> logtailDoList = milogLogtailDao.getMilogLogtailByStoreId(storeId);
        List<Map<String, String>> res = new ArrayList<>();
        Map<String, String> ferry;
        Set<String> existIpSet = new HashSet<>();
        for (MilogLogTailDo tail : logtailDoList) {
            List<String> ipList = tail.getIps();
            if (CollectionUtils.isNotEmpty(ipList)) {
                for (String ip : ipList) {
                    if (existIpSet.contains(ip) || StringUtils.isEmpty(ip)) {
                        continue;
                    }
                    ferry = new HashMap<>(2);
                    ferry.put("label", ip);
                    ferry.put("value", ip);
                    res.add(ferry);
                    existIpSet.add(ip);
                }
            }
        }
        return Result.success(res);
    }

    public Result<List<MenuDTO<Long, String>>> queryDeptExIndexList(String regionCode, Integer logTypeCode) {
        if (StringUtils.isBlank(regionCode) || null == logTypeCode) {
            return Result.failParam("regionCode or logTypeCode can not empty");
        }
        //查询当前用户所属的部门下的es信息
        List<MilogMiddlewareConfig> middlewareConfigEs = milogMiddlewareConfigService.getESConfigs(regionCode);
        middlewareConfigEs = resourceExtensionService.currentUserConfigFilter(middlewareConfigEs);
        List<MenuDTO<Long, String>> menuDTOS = middlewareConfigEs.stream().map(config -> {
            MenuDTO<Long, String> menuDTO = new MenuDTO<>();
            menuDTO.setKey(config.getId());
            menuDTO.setLabel(config.getAlias());
            menuDTO.setChildren(getExIndexByLogType(config.getId(), logTypeCode));
            return menuDTO;
        }).collect(Collectors.toList());
        return Result.success(menuDTOS);
    }

    private List<MenuDTO<Long, String>> getExIndexByLogType(Long clusterId, Integer logTypeCode) {
        QueryWrapper queryWrapper = new QueryWrapper<>().eq("cluster_id", clusterId).eq("log_type", logTypeCode);
        List<MilogEsIndexDO> esIndexDOS = milogEsIndexMapper.selectList(queryWrapper);
        if (CollectionUtils.isNotEmpty(esIndexDOS)) {
            return esIndexDOS.stream().map(indexDO -> {
                MenuDTO<Long, String> menuDTO = new MenuDTO<>();
                menuDTO.setKey(indexDO.getId());
                menuDTO.setLabel(indexDO.getIndexName());
                return menuDTO;
            }).collect(Collectors.toList());
        }
        return Lists.newArrayList();
    }
}
