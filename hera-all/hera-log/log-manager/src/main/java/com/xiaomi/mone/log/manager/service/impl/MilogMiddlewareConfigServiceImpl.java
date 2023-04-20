package com.xiaomi.mone.log.manager.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.google.common.collect.Lists;
import com.xiaomi.mone.log.api.enums.LogTypeEnum;
import com.xiaomi.mone.log.api.enums.MiddlewareEnum;
import com.xiaomi.mone.log.api.enums.OperateEnum;
import com.xiaomi.mone.log.api.model.bo.MiLogResource;
import com.xiaomi.mone.log.api.model.bo.ResourcePage;
import com.xiaomi.mone.log.api.model.vo.EsIndexVo;
import com.xiaomi.mone.log.api.model.vo.ResourceInfo;
import com.xiaomi.mone.log.api.model.vo.ResourceUserSimple;
import com.xiaomi.mone.log.api.model.vo.ValueKeyObj;
import com.xiaomi.mone.log.common.Result;
import com.xiaomi.mone.log.manager.bootstrap.EsPlugin;
import com.xiaomi.mone.log.manager.common.context.MoneUserContext;
import com.xiaomi.mone.log.manager.common.exception.MilogManageException;
import com.xiaomi.mone.log.manager.common.validation.ResourceValidation;
import com.xiaomi.mone.log.manager.dao.MilogAppMiddlewareRelDao;
import com.xiaomi.mone.log.manager.dao.MilogLogstoreDao;
import com.xiaomi.mone.log.manager.dao.MilogMiddlewareConfigDao;
import com.xiaomi.mone.log.manager.domain.LogTail;
import com.xiaomi.mone.log.manager.mapper.MilogEsClusterMapper;
import com.xiaomi.mone.log.manager.mapper.MilogEsIndexMapper;
import com.xiaomi.mone.log.manager.model.bo.MiddlewareAddParam;
import com.xiaomi.mone.log.manager.model.bo.MiddlewareQueryParam;
import com.xiaomi.mone.log.manager.model.bo.MiddlewareUpdateParam;
import com.xiaomi.mone.log.manager.model.dto.MiddlewareConfigDTO;
import com.xiaomi.mone.log.manager.model.page.PageInfo;
import com.xiaomi.mone.log.manager.model.pojo.*;
import com.xiaomi.mone.log.manager.service.BaseService;
import com.xiaomi.mone.log.manager.service.MilogMiddlewareConfigService;
import com.xiaomi.mone.log.manager.service.extension.resource.ResourceExtensionService;
import com.xiaomi.mone.log.manager.service.extension.resource.ResourceExtensionServiceFactory;
import com.xiaomi.youpin.docean.anno.Service;
import com.xiaomi.youpin.docean.common.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.nutz.dao.Cnd;
import org.nutz.dao.Condition;
import org.nutz.dao.pager.Pager;

import javax.annotation.Resource;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.xiaomi.mone.log.common.Constant.*;
import static com.xiaomi.mone.log.manager.common.ManagerConstant.*;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2021/9/22 11:42
 */
@Service
@Slf4j
public class MilogMiddlewareConfigServiceImpl extends BaseService implements MilogMiddlewareConfigService {

    @Resource
    private MilogMiddlewareConfigDao milogMiddlewareConfigDao;
    @Resource
    private MilogAppMiddlewareRelDao milogAppMiddlewareRelDao;
    @Resource
    private ResourceValidation resourceValidation;
    @Resource
    private MilogEsClusterMapper milogEsClusterMapper;
    @Resource
    private MilogEsIndexMapper milogEsIndexMapper;
    @Resource
    private MilogLogstoreDao logstoreDao;
    @Resource
    private EsPlugin esPlugin;
    @Resource
    private LogTail logTail;

    private ResourceExtensionService resourceExtensionService;

    List<Integer> accurateTypes = Arrays.asList(MiddlewareEnum.ROCKETMQ.getCode());

    public void init() {
        resourceExtensionService = ResourceExtensionServiceFactory.getResourceExtensionService();
    }

    @Override
    public PageInfo<MilogMiddlewareConfig> queryMiddlewareConfigPage(MiddlewareQueryParam middlewareQueryParam) {
        Condition cnd = handleParamToCondition(middlewareQueryParam);
        List<MilogMiddlewareConfig> milogMiddlewareConfigs = milogMiddlewareConfigDao.queryMiddlewareConfigByCondition(cnd, new Pager(middlewareQueryParam.getPage(), middlewareQueryParam.getPageSize()));
        Integer count = milogMiddlewareConfigDao.queryMiddlewareConfigCountByCondition(cnd);
        return new PageInfo<>(middlewareQueryParam.getPage(), middlewareQueryParam.getPageSize(), count, milogMiddlewareConfigs);
    }

    @Override
    public Result addMiddlewareConfig(MiddlewareAddParam middlewareAddParam) {
        List<String> checkMsg = preCheckMiddlewareConfig(middlewareAddParam);
        if (CollectionUtils.isNotEmpty(checkMsg)) {
            return Result.failParam(checkMsg.stream().collect(Collectors.joining(",")));
        }
        List<MilogMiddlewareConfig> milogMiddlewareConfigs = milogMiddlewareConfigDao.queryMiddlewareConfigByCondition(Cnd.where("alias", "=", middlewareAddParam.getAlias()), null);
        if (CollectionUtils.isNotEmpty(milogMiddlewareConfigs)) {
            return Result.failParam("别名已经存在了，请修改后保存");
        }
        //默认值只会有一个
        if (accurateTypes.contains(middlewareAddParam.getType())) {
            MilogMiddlewareConfig config = milogMiddlewareConfigDao.queryDefaultMiddlewareConfig();
            if (null != config && middlewareAddParam.getIsDefault() == 1) {
                return Result.failParam("默认值只会有一个,请把其它的默认的默认值关掉");
            }
        }
        milogMiddlewareConfigDao.addMiddlewareConfig(AddParamToAddEntity(middlewareAddParam));
        return Result.success();
    }

    @Override
    public Result updateMiddlewareConfig(MiddlewareUpdateParam middlewareUpdateParam) {
        List<String> checkMsg = preCheckUpdateMiddlewareConfig(middlewareUpdateParam);
        if (CollectionUtils.isNotEmpty(checkMsg)) {
            return Result.failParam(checkMsg.stream().collect(Collectors.joining(",")));
        }
        MilogMiddlewareConfig milogMiddlewareConfig = milogMiddlewareConfigDao.queryById(middlewareUpdateParam.getId());
        if (null == milogMiddlewareConfig) {
            return Result.failParam("修改的对象不存在，请确保Id正确");
        }
        List<MilogMiddlewareConfig> milogMiddlewareConfigs = milogMiddlewareConfigDao.queryMiddlewareConfigByCondition(Cnd.where("alias", "=", middlewareUpdateParam.getAlias()), null);
        if (CollectionUtils.isNotEmpty(milogMiddlewareConfigs)) {
            List<MilogMiddlewareConfig> middlewareConfigs = milogMiddlewareConfigs.stream().filter(config -> config.getId().longValue() != middlewareUpdateParam.getId().longValue()).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(middlewareConfigs)) {
                return Result.failParam("别名已经存在了，请修改后保存");
            }
        }
        //默认值只会有一个
        if (accurateTypes.contains(middlewareUpdateParam.getType())) {
            MilogMiddlewareConfig config = milogMiddlewareConfigDao.queryDefaultMiddlewareConfig();
            if (null != config && middlewareUpdateParam.getIsDefault() == 1 && !config.getId().equals(middlewareUpdateParam.getId())) {
                return Result.failParam("默认值只会有一个,请把其它的默认值关掉后修改");
            }
        }
        milogMiddlewareConfigDao.updateMiddlewareConfig(updateParamToUpdateEntity(middlewareUpdateParam, milogMiddlewareConfig));
        return Result.success();
    }

    @Override
    public Result deleteMiddlewareConfig(Long id) {
        if (null == id) {
            return Result.failParam("id can not null");
        }
        MilogMiddlewareConfig milogMiddlewareConfig = milogMiddlewareConfigDao.queryById(id);
        if (null == milogMiddlewareConfig) {
            return Result.failParam("删除的对象不存在，请确保Id正确");
        }
        List<MilogAppMiddlewareRel> milogAppMiddlewareRels = milogAppMiddlewareRelDao.queryByCondition(null, id, null);
        if (CollectionUtils.isNotEmpty(milogAppMiddlewareRels)) {
            return Result.failParam("关联的tail存在，请确保tail已变更");
        }
        milogMiddlewareConfigDao.deleteMiddlewareConfig(id);
        return Result.success();
    }

    @Override
    public List<MilogMiddlewareConfig> queryMiddlewareConfigList() {
        return milogMiddlewareConfigDao.queryAll();
    }

    @Override
    public Result<MilogMiddlewareConfig> queryMiddlewareConfigById(Long id) {
        if (null == id) {
            return Result.failParam("id can not null");
        }
        MilogMiddlewareConfig config = milogMiddlewareConfigDao.queryById(id);
        MiddlewareConfigDTO middlewareConfigDTO = new MiddlewareConfigDTO();
        BeanUtil.copyProperties(config, middlewareConfigDTO);
        middlewareConfigDTO.setTypes(Arrays.asList(config.getType(), config.getRegionEn()));
        return Result.success(middlewareConfigDTO);
    }

    /**
     * @param resourcePage
     * @return
     */
    @Override
    public PageInfo<ResourceInfo> queryResourceWithTab(ResourcePage resourcePage) {
        if (null == resourcePage.getResourceCode()) {
            return PageInfo.emptyPageInfo();
        }
        Cnd mqCnd = generateMqQueryCnd(resourcePage);

        Pager pager = new Pager(resourcePage.getPage(), resourcePage.getPageSize());

        List<MilogMiddlewareConfig> milogMiddlewareConfigs = milogMiddlewareConfigDao
                .queryMiddlewareConfigByCondition(mqCnd, pager);
        Integer mqResourceTotal = milogMiddlewareConfigDao.queryMiddlewareConfigCountByCondition(mqCnd);

        Long esResourceTotal = queryEsResource(milogMiddlewareConfigs, resourcePage);

//        milogMiddlewareConfigs = userShowAuthority(milogMiddlewareConfigs);
        milogMiddlewareConfigs = resourceExtensionService.userShowAuthority(milogMiddlewareConfigs);
        List<ResourceInfo> resourceInfos = milogMiddlewareConfigs.stream()
                .map(MilogMiddlewareConfig::configToResourceVO)
                .collect(Collectors.toList());

        return new PageInfo(resourcePage.getPage(), resourcePage.getPageSize(),
                mqResourceTotal + esResourceTotal.intValue(), resourceInfos);
    }

    private Cnd generateMqQueryCnd(ResourcePage resourcePage) {
        Cnd cnd = Cnd.NEW();
        if (null != resourcePage.getResourceCode()) {
            cnd.andEX("type", EQUAL_OPERATE, resourcePage.getResourceCode());
        }
        if (null != resourcePage.getRegionEnCode()) {
            cnd.andEX("region_en", EQUAL_OPERATE, resourcePage.getRegionEnCode());
        }
        if (StringUtils.isNotBlank(resourcePage.getAliasName())) {
            cnd.andEX("alias", LIKE_OPERATE, String.format("%s%s%s", "%", resourcePage.getAliasName(), "%"));
        }
        return cnd;
    }

    /**
     * 处理es资源，由于历史原理，es资源在单独的表中，我们需要合并数据
     */
    private Long queryEsResource(List<MilogMiddlewareConfig> milogMiddlewareConfigs,
                                 ResourcePage resourcePage) {
        Long count = 0L;
        if (MiddlewareEnum.ELASTICSEARCH.getCode().equals(resourcePage.getResourceCode())) {
            PageDTO page = new PageDTO<MilogEsClusterDO>(resourcePage.getPage(), resourcePage.getPageSize());
            Wrapper queryWrapper = generateEsQueryWrapper(resourcePage);

            PageDTO<MilogEsClusterDO> esClusterPage = milogEsClusterMapper.selectPage(
                    page, queryWrapper);
            List<MilogMiddlewareConfig> configEsList = esClusterPage.getRecords().stream()
                    .map(MilogEsClusterDO::miLogEsResourceToConfig)
                    .collect(Collectors.toList());
            milogMiddlewareConfigs.addAll(configEsList);

            count = milogEsClusterMapper.selectCount(queryWrapper);
        }
        return count;
    }

    private Wrapper generateEsQueryWrapper(ResourcePage resourcePage) {
        QueryWrapper queryWrapper = new QueryWrapper<MilogEsClusterDO>();
        if (null != resourcePage.getRegionEnCode()) {
            queryWrapper.eq("area", resourcePage.getRegionEnCode());
        }
        if (StringUtils.isNotBlank(resourcePage.getAliasName())) {
            queryWrapper.like("name", resourcePage.getAliasName());
        }
        resourceExtensionService.filterEsQueryWrapper(queryWrapper);
        return queryWrapper;
    }

    @Override
    public Result<String> resourceOperate(MiLogResource miLogResource) {
        String errInfos = resourceValidation.resourceOperateValid(miLogResource);
        if (StringUtils.isNotBlank(errInfos)) {
            return Result.failParam(errInfos);
        }
        checkAlias(miLogResource, OperateEnum.queryByCode(miLogResource.getOperateCode()));
        if (OperateEnum.ADD_OPERATE == OperateEnum.queryByCode(miLogResource.getOperateCode())) {
            addResource(miLogResource);
        }
        if (OperateEnum.UPDATE_OPERATE == OperateEnum.queryByCode(miLogResource.getOperateCode())) {
            updateResource(miLogResource);
        }
        if (OperateEnum.DELETE_OPERATE == OperateEnum.queryByCode(miLogResource.getOperateCode())) {
            deleteResource(miLogResource.getResourceCode(), miLogResource.getId());
        }
        return Result.success(SUCCESS_MESSAGE);
    }

    private void checkEsAddressPortOperate(MiLogResource miLogResource) {
        String serviceUrl = miLogResource.getServiceUrl();
        String portStr = serviceUrl.substring(serviceUrl.lastIndexOf(":") + 1);
        if (StringUtils.isBlank(portStr)) {
            serviceUrl = String.format("%s:%s", serviceUrl, "80");
        }
        miLogResource.setServiceUrl(serviceUrl);
    }

    private void deleteResource(Integer resourceCode, Long id) {
        if (MiddlewareEnum.ELASTICSEARCH.getCode().equals(resourceCode)) {
            milogEsClusterMapper.deleteById(id);
            deleteEsIndex(id);
            return;
        }
        deleteMiddlewareConfig(id);
    }

    @Override
    public String synchronousResourceLabel(Long id) {
        synchronousMqResourceLabel(id);
        synchronousEsResourceLabel(id);
        return SUCCESS_MESSAGE;
    }

    private void synchronousMqResourceLabel(Long id) {
        List<MilogMiddlewareConfig> milogMiddlewareConfigs = Lists.newArrayList();
        if (null != id) {
            milogMiddlewareConfigs.add(milogMiddlewareConfigDao.queryById(id));
        }
        if (CollectionUtils.isEmpty(milogMiddlewareConfigs)) {
            milogMiddlewareConfigs = milogMiddlewareConfigDao.queryAll();
        }
        milogMiddlewareConfigs.forEach(milogMiddlewareConfig -> {
            updateMqResourceLabel(milogMiddlewareConfig);
        });
    }

    private void synchronousEsResourceLabel(Long id) {
        List<MilogEsClusterDO> esClusterDOS = Lists.newArrayList();
        if (null != id) {
            esClusterDOS.add(milogEsClusterMapper.selectById(id));
        }
        if (CollectionUtils.isEmpty(esClusterDOS)) {
            esClusterDOS = milogEsClusterMapper.selectAll();
        }
        esClusterDOS.forEach(clusterDO -> {
            updateEsResourceLabel(clusterDO);
        });
    }

    /**
     * 当前用户的资源信息
     * 1.是否初始化
     * 2.是否展示
     * 3.资源列表
     *
     * @return
     */
    @Override
    public ResourceUserSimple userResourceList(String regionCode, Integer logTypeCode) {
        if (StringUtils.isBlank(regionCode)) {
            throw new MilogManageException("regionCode不能为空");
        }
        if (null == logTypeCode) {
            throw new MilogManageException("logTypeCode不能为空");
        }
        ResourceUserSimple resourceUserSimple = new ResourceUserSimple();

        if (resourceExtensionService.userResourceListPre(logTypeCode)) {
            resourceUserSimple.setInitializedFlag(Boolean.TRUE);
            resourceUserSimple.setShowFlag(Boolean.FALSE);
            return resourceUserSimple;
        }

        final Boolean initializedStatus = queryResourceInitialized(resourceUserSimple, regionCode, logTypeCode);
        final boolean showStatus = resourceExtensionService.resourceShowStatusFlag(resourceUserSimple);

        if (initializedStatus && showStatus) {

            List<MilogMiddlewareConfig> milogMiddlewareConfigs = milogMiddlewareConfigDao.queryByResourceCode(MiddlewareEnum.ROCKETMQ.getCode(), regionCode);
            queryCurrentUserResourceList(resourceUserSimple, milogMiddlewareConfigs, MiddlewareEnum.ROCKETMQ);

            List<MilogMiddlewareConfig> middlewareConfigEss = getESConfigs(regionCode);
            queryCurrentUserResourceList(resourceUserSimple, middlewareConfigEss, MiddlewareEnum.ELASTICSEARCH);
        }
        return resourceUserSimple;
    }

    @NotNull
    public List<MilogMiddlewareConfig> getESConfigs(String regionCode) {
        QueryWrapper queryWrapper = new QueryWrapper<>().eq("area", regionCode);
        List<MilogEsClusterDO> esClusterDOS = milogEsClusterMapper
                .selectList(queryWrapper);
        List<MilogMiddlewareConfig> middlewareConfigEss = esClusterDOS.stream()
                .map(MilogEsClusterDO::miLogEsResourceToConfig)
                .collect(Collectors.toList());
        return middlewareConfigEss;
    }

    /**
     * 查询当前用户下的资源是否已经初始化
     */
    private Boolean queryResourceInitialized(ResourceUserSimple configResource, String regionCode,
                                             Integer logTypeCode) {
        List<MilogMiddlewareConfig> milogMiddlewareConfigs = milogMiddlewareConfigDao.queryByResourceCode(MiddlewareEnum.ROCKETMQ.getCode(), regionCode);
        List<MilogMiddlewareConfig> middlewareMqConfigs = resourceExtensionService.currentUserConfigFilter(milogMiddlewareConfigs);

        List<MilogEsClusterDO> esClusterDOS = milogEsClusterMapper.selectList(Wrappers.lambdaQuery());
        List<MilogMiddlewareConfig> middlewareConfigEss = esClusterDOS.stream()
                .map(logEsClusterDO -> {
                    MilogMiddlewareConfig milogMiddlewareConfig = MilogEsClusterDO.miLogEsResourceToConfig(logEsClusterDO);
                    return milogMiddlewareConfig;
                })
                .collect(Collectors.toList());

        List<MilogMiddlewareConfig> middlewareEsConfigs = resourceExtensionService.currentUserConfigFilter(middlewareConfigEss);

        List<MilogEsIndexDO> milogEsIndexDOS = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(middlewareEsConfigs)) {
            milogEsIndexDOS = queryMilogEsIndex(middlewareEsConfigs.stream().map(MilogMiddlewareConfig::getId).collect(Collectors.toList()), logTypeCode);
        }

        if (resourceExtensionService.resourceNotRequiredInit(logTypeCode, middlewareMqConfigs, middlewareEsConfigs, milogEsIndexDOS)) {
            configResource.setInitializedFlag(Boolean.TRUE);
            return Boolean.TRUE;
        }
        String msg = "";
        if (CollectionUtils.isEmpty(middlewareMqConfigs)) {
            msg = "mq资源没有初始化";
        }
        if (CollectionUtils.isEmpty(middlewareEsConfigs) || CollectionUtils.isEmpty(milogEsIndexDOS)) {
            msg = "ES资源没有初始化或者当前类型的日志索引没有初始化";
        }
        configResource.setInitializedFlag(Boolean.FALSE);
        configResource.setNotInitializedMsg(String.format(
                "%s%s", msg, RESOURCE_NOT_INITIALIZED_MESSAGE));
        return Boolean.FALSE;
    }

    private List<MilogEsIndexDO> queryMilogEsIndex(List<Long> clusterIds, Integer logTyCode) {
        QueryWrapper queryWrapper = new QueryWrapper<>();
        queryWrapper.in("cluster_id", clusterIds);
        queryWrapper.eq("log_type", logTyCode);
        return milogEsIndexMapper.selectList(queryWrapper);
    }

    /**
     * 查询当前用户的三级部门下的资源列表
     *
     * @param configResource
     */
    private void queryCurrentUserResourceList(ResourceUserSimple configResource,
                                              List<MilogMiddlewareConfig> milogMiddlewareConfigs, MiddlewareEnum middlewareEnum) {
        milogMiddlewareConfigs = resourceExtensionService.currentUserConfigFilter(milogMiddlewareConfigs);
        List valueKeyObjs = milogMiddlewareConfigs.stream()
                .map(milogMiddlewareConfig -> new ValueKeyObj(
                        milogMiddlewareConfig.getId(), milogMiddlewareConfig.getAlias()))
                .collect(Collectors.toList());
        if (MiddlewareEnum.ELASTICSEARCH == middlewareEnum) {
            configResource.setEsResourceList(valueKeyObjs);
            return;
        }
        configResource.setMqResourceList(valueKeyObjs);
    }

    @Override
    public ResourceInfo resourceDetail(Integer resourceCode, Long id) {
        if (MiddlewareEnum.ELASTICSEARCH.getCode().equals(resourceCode)) {
            return wrapEsResourceInfo(id);
        }
        MilogMiddlewareConfig milogMiddlewareConfig = milogMiddlewareConfigDao.queryById(id);
        if (null != milogMiddlewareConfig) {
            return milogMiddlewareConfig.configToResourceVO();
        }
        return ResourceInfo.builder().build();
    }

    @Override
    public MilogMiddlewareConfig queryMiddlewareConfigDefault(String regionCode) {
        List<MilogMiddlewareConfig> milogMiddlewareConfigs = milogMiddlewareConfigDao.queryByResourceCode(MiddlewareEnum.ROCKETMQ.getCode(), regionCode);
        List<MilogMiddlewareConfig> middlewareMqConfigs = resourceExtensionService.currentUserConfigFilter(milogMiddlewareConfigs);

        if (CollectionUtils.isNotEmpty(middlewareMqConfigs)) {
            return middlewareMqConfigs.get(middlewareMqConfigs.size() - 1);
        }
        return null;
    }

    private ResourceInfo wrapEsResourceInfo(Long id) {
        MilogEsClusterDO esClusterDO = milogEsClusterMapper.selectById(id);
        if (null != esClusterDO) {
            List<MilogEsIndexDO> milogEsIndexDOS = milogEsIndexMapper.selectList(new QueryWrapper<MilogEsIndexDO>().eq("cluster_id", esClusterDO.getId()));
            List<EsIndexVo> multipleEsIndex = milogEsIndexDOS.stream()
                    .map(MilogEsIndexDO::getLogType)
                    .distinct()
                    .sorted(Integer::compareTo)
                    .map(getIntegerEsIndexVoFunction(milogEsIndexDOS))
                    .collect(Collectors.toList());

            return esClusterDO.configToResourceVO(multipleEsIndex);
        }
        return ResourceInfo.builder().build();
    }

    @NotNull
    private Function<Integer, EsIndexVo> getIntegerEsIndexVoFunction(List<MilogEsIndexDO> milogEsIndexDOS) {
        return logTypeCode -> {
            EsIndexVo esIndexVo = new EsIndexVo();
            esIndexVo.setLogTypeCode(logTypeCode);
            esIndexVo.setLogTypeName(LogTypeEnum.queryNameByType(logTypeCode));
            List<String> esIndexList = milogEsIndexDOS.stream()
                    .filter(milogEsIndexDO -> Objects.equals(
                            logTypeCode, milogEsIndexDO.getLogType()))
                    .map(MilogEsIndexDO::getIndexName)
                    .collect(Collectors.toList());
            esIndexVo.setEsIndexList(esIndexList);
            return esIndexVo;
        };
    }

    /**
     * 更新每一列资源的标签
     *
     * @param milogMiddlewareConfig
     */
    private void updateMqResourceLabel(MilogMiddlewareConfig milogMiddlewareConfig) {
        List<String> labels = milogMiddlewareConfig.getLabels();
        List<String> newLabels = generateResourceLabels(milogMiddlewareConfig.getUpdater(), labels);
        milogMiddlewareConfig.setLabels(newLabels);
        milogMiddlewareConfigDao.updateMiddlewareConfig(milogMiddlewareConfig);
    }

    private void updateEsResourceLabel(MilogEsClusterDO clusterDO) {
        List<String> labels = clusterDO.getLabels();
        if (StringUtils.isBlank(clusterDO.getUpdater()) &&
                StringUtils.isBlank(clusterDO.getCreator())) {
            return;
        }
        List<String> newLabels = generateResourceLabels(
                StringUtils.isBlank(clusterDO.getUpdater()) ?
                        clusterDO.getCreator() : clusterDO.getUpdater(),
                labels);
        clusterDO.setLabels(newLabels);
        milogEsClusterMapper.updateById(clusterDO);
    }

    private List<String> generateResourceLabels(String updaterUId, List<String> existLabels) {
        List<String> resourceDeptLabels = resourceExtensionService.generateResourceLabels(updaterUId);
        if (CollectionUtils.isNotEmpty(existLabels)) {
            List<String> userLabelList = existLabels.stream().filter(label ->
                            !label.startsWith(DEPT_LEVEL_PREFIX) || !label.contains(DEPT_NAME_PREFIX))
                    .collect(Collectors.toList());
            resourceDeptLabels.addAll(userLabelList);
        }
        return resourceDeptLabels;
    }

    /**
     * 添加资源
     * 1.如果是mq创建3个公共topic且开始标签过滤
     * 2.标签处理
     * 2.信息入库
     *
     * @param miLogResource
     */
    private void addResource(MiLogResource miLogResource) {
        List<String> resourceDeptLabels = resourceExtensionService.generateResourceLabels(MoneUserContext.getCurrentUser().getUser());
        resourceDeptLabels.addAll(miLogResource.getLabels());
        miLogResource.setLabels(resourceDeptLabels);
        resourceExtensionService.addResourcePreProcessing(resourceDeptLabels, miLogResource);

        if (MiddlewareEnum.ELASTICSEARCH.getCode().equals(miLogResource.getResourceCode())) {
            checkEsAddressPortOperate(miLogResource);
            addEsResourceInfo(miLogResource);
            return;
        }

        resourceExtensionService.addResourceMiddleProcessing(miLogResource);

        MilogMiddlewareConfig milogMiddlewareConfig = MilogMiddlewareConfig
                .miLogMqResourceToConfig(miLogResource);

        wrapBaseCommon(milogMiddlewareConfig, OperateEnum.ADD_OPERATE);

        resourceExtensionService.addResourcePostProcessing(milogMiddlewareConfig);
        milogMiddlewareConfigDao.addMiddlewareConfig(milogMiddlewareConfig);
    }

    private void addEsResourceInfo(MiLogResource miLogResource) {
        MilogEsClusterDO esClusterDO = MilogEsClusterDO.miLogEsResourceToConfig(miLogResource);
        wrapBaseCommon(esClusterDO, OperateEnum.ADD_OPERATE);
        resourceExtensionService.addEsResourcePreProcessing(esClusterDO);
        milogEsClusterMapper.insert(esClusterDO);

        addEsIndex(esClusterDO.getId(), miLogResource.getMultipleEsIndex());
        //添加es客户端
        esPlugin.buildEsClient(esClusterDO);
    }

    private void checkAlias(MiLogResource resource, OperateEnum operateEnum) {
        if (MiddlewareEnum.ELASTICSEARCH.getCode().equals(resource.getResourceCode())) {
            List<MilogEsClusterDO> logEsClusterDOS = milogEsClusterMapper.selectByAlias(resource.getAlias());
            if (operateEnum == OperateEnum.ADD_OPERATE && CollectionUtils.isNotEmpty(logEsClusterDOS)) {
                throw new MilogManageException("alias has exists,please refill");
            }
            if (operateEnum == OperateEnum.UPDATE_OPERATE && CollectionUtils.isNotEmpty(logEsClusterDOS)) {
                MilogEsClusterDO logEsClusterDO = logEsClusterDOS.get(logEsClusterDOS.size() - 1);
                if (!Objects.equals(resource.getId(), logEsClusterDO.getId())) {
                    throw new MilogManageException("alias has exists,please refill");
                }
            }
        }

        if (MiddlewareEnum.ROCKETMQ.getCode().equals(resource.getResourceCode())) {
            List<MilogMiddlewareConfig> middlewareConfigs = milogMiddlewareConfigDao.selectByAlias(resource.getAlias());
            if (operateEnum == OperateEnum.ADD_OPERATE && CollectionUtils.isNotEmpty(middlewareConfigs)) {
                throw new MilogManageException("alias has exists,please refill");
            }
            if (operateEnum == OperateEnum.UPDATE_OPERATE && CollectionUtils.isNotEmpty(middlewareConfigs)) {
                MilogMiddlewareConfig middlewareConfig = middlewareConfigs.get(middlewareConfigs.size() - 1);
                if (!Objects.equals(resource.getId(), middlewareConfig.getId())) {
                    throw new MilogManageException("alias has exists,please refill");
                }
            }
        }
    }

    private void addEsIndex(Long esClusterId, List<EsIndexVo> multipleEsIndex) {
        List<MilogEsIndexDO> milogEsIndexDOS = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(multipleEsIndex)) {
            for (EsIndexVo esIndexVo : multipleEsIndex) {
                milogEsIndexDOS.addAll(
                        MilogEsIndexDO.essIndexVoToIndexDO(esClusterId, esIndexVo));
            }
        }
        for (MilogEsIndexDO milogEsIndexDO : milogEsIndexDOS) {
            milogEsIndexMapper.insert(milogEsIndexDO);
        }
    }

    /**
     * 修改资源
     * 1.比较talos的信息是否变化，如果变化，则需要创建3个公共topic且开始标签过滤
     * 2.信息修改入库
     *
     * @param miLogResource
     */
    private void updateResource(MiLogResource miLogResource) {

        if (MiddlewareEnum.ELASTICSEARCH.getCode().equals(miLogResource.getResourceCode())) {
            boolean changed = authenticationInChanged(milogEsClusterMapper.selectById(miLogResource.getId()), miLogResource);
            checkEsAddressPortOperate(miLogResource);
            MilogEsClusterDO esClusterDO = MilogEsClusterDO.miLogEsResourceToConfig(miLogResource);
            esClusterDO.setId(miLogResource.getId());
            resourceExtensionService.addEsResourcePreProcessing(esClusterDO);
            milogEsClusterMapper.updateById(esClusterDO);
            handleEsIndexStore(miLogResource, esClusterDO.getId(), changed);
            //修改es客户端信息
            esPlugin.buildEsClient(esClusterDO);
            return;
        }

        updateCompareMqInfo(miLogResource);

        MilogMiddlewareConfig milogMiddlewareConfig = MilogMiddlewareConfig
                .miLogMqResourceToConfig(miLogResource);

        buildCommonAttr(miLogResource.getId(), milogMiddlewareConfig);

        milogMiddlewareConfigDao.updateMiddlewareConfig(milogMiddlewareConfig);
    }

    private boolean authenticationInChanged(MilogEsClusterDO clusterDO, MiLogResource miLogResource) {
        if (!Objects.equals(clusterDO.getConWay(), miLogResource.getConWay())) {
            return true;
        }
        if (Objects.equals(ES_CONWAY_PWD, miLogResource.getConWay())) {
            return !Objects.equals(clusterDO.getUser(), miLogResource.getAk()) ||
                    !Objects.equals(clusterDO.getPwd(), miLogResource.getSk());
        }
        if (Objects.equals(ES_CONWAY_TOKEN, miLogResource.getConWay())) {
            return !Objects.equals(clusterDO.getToken(), miLogResource.getEsToken()) ||
                    !Objects.equals(clusterDO.getDtCatalog(), miLogResource.getCatalog()) ||
                    !Objects.equals(clusterDO.getDtDatabase(), miLogResource.getDatabase());
        }
        return false;
    }

    /**
     * 1.查询该es是不是已经被使用了
     * 2.如果使用了，查看使用的索引是否变化了
     * 3.如果变化了，则修改
     * 4.否则，，删掉新增
     *
     * @param miLogResource
     */
    private void handleEsIndexStore(MiLogResource miLogResource, Long esClusterId, boolean changed) {
        //删除旧的es索引，新增新的es索引(比较如果已经存在的有，进来的没有，删除，已经存在的没有，进来的有新增)
        handleEsIndex(esClusterId, miLogResource.getMultipleEsIndex());
        if (changed) {
            List<MilogLogStoreDO> storeDOS = logstoreDao.queryByEsInfo(miLogResource.getRegionEn(), esClusterId);
            if (changed && CollectionUtils.isNotEmpty(storeDOS)) {
                //同步到stream
                for (MilogLogStoreDO storeDO : storeDOS) {
                    logTail.handleStoreTail(storeDO.getId());
                }
            }
        }
    }

    private void handleEsIndex(Long esClusterId, List<EsIndexVo> multipleEsIndex) {
        for (EsIndexVo esIndex : multipleEsIndex) {
            doHandleEsIndexByLogType(esClusterId, esIndex.getLogTypeCode(), esIndex.getEsIndexList());
        }
    }

    private void doHandleEsIndexByLogType(Long esClusterId, Integer logTypeCode, List<String> esIndexList) {
        List<MilogEsIndexDO> addExIndexDo;
        List<MilogEsIndexDO> delExIndexDo = Lists.newArrayList();
        QueryWrapper queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("cluster_id", esClusterId);
        queryWrapper.eq("log_type", logTypeCode);
        List<MilogEsIndexDO> existEsIndexDO = milogEsIndexMapper.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(existEsIndexDO)) {
            addExIndexDo = esIndexList.stream()
                    .map(indexName -> getMilogEsIndexDO(esClusterId, logTypeCode, indexName))
                    .collect(Collectors.toList());
        } else {
            for (MilogEsIndexDO milogEsIndexDO : existEsIndexDO) {
                if (!esIndexList.contains(milogEsIndexDO.getIndexName())) {
                    delExIndexDo.add(milogEsIndexDO);
                }
            }
            List<String> existIndexes = existEsIndexDO.stream().map(MilogEsIndexDO::getIndexName).collect(Collectors.toList());
            addExIndexDo = esIndexList.stream().filter(indexName -> !existIndexes.contains(indexName))
                    .map(indexName -> getMilogEsIndexDO(esClusterId, logTypeCode, indexName))
                    .collect(Collectors.toList());
        }
        if (CollectionUtils.isNotEmpty(delExIndexDo)) {
            milogEsIndexMapper.deleteBatchIds(delExIndexDo.stream().map(MilogEsIndexDO::getId).collect(Collectors.toList()));
        }
        addExIndex(addExIndexDo);
    }

    private void addExIndex(List<MilogEsIndexDO> addExIndexDo) {
        for (MilogEsIndexDO milogEsIndexDO : addExIndexDo) {
            milogEsIndexMapper.insert(milogEsIndexDO);
        }
    }

    @NotNull
    private MilogEsIndexDO getMilogEsIndexDO(Long esClusterId, Integer logTypeCode, String indexName) {
        MilogEsIndexDO milogEsIndexDO = new MilogEsIndexDO();
        milogEsIndexDO.setClusterId(esClusterId);
        milogEsIndexDO.setLogType(logTypeCode);
        milogEsIndexDO.setIndexName(indexName);
        return milogEsIndexDO;
    }

    private void buildCommonAttr(Long resourceId, MilogMiddlewareConfig milogMiddlewareConfig) {
        MilogMiddlewareConfig existConfig = milogMiddlewareConfigDao.queryById(resourceId);
        milogMiddlewareConfig.setCtime(existConfig.getCtime());
        milogMiddlewareConfig.setCreator(existConfig.getCreator());
        wrapBaseCommon(milogMiddlewareConfig, OperateEnum.UPDATE_OPERATE);
        resourceExtensionService.addResourcePostProcessing(milogMiddlewareConfig);
        milogMiddlewareConfig.setId(resourceId);
    }

    private void deleteEsIndex(Long esClusterId) {
        QueryWrapper<MilogEsIndexDO> deleteWrapper = new QueryWrapper<>();
        deleteWrapper.eq("cluster_id", esClusterId);
        milogEsIndexMapper.delete(deleteWrapper);
    }

    /**
     * 比较mq的信息是否变化
     * 变化后需要修改公共topic的信息
     *
     * @param miLogResource
     */
    private void updateCompareMqInfo(MiLogResource miLogResource) {
        MilogMiddlewareConfig config = milogMiddlewareConfigDao.queryById(miLogResource.getId());
        if (!Objects.equals(miLogResource.getAk(), config.getAk()) ||
                !Objects.equals(miLogResource.getSk(), config.getSk()) ||
                !Objects.equals(miLogResource.getServiceUrl(), config.getServiceUrl()) ||
                !Objects.equals(miLogResource.getOrgId(), config.getOrgId()) ||
                !Objects.equals(miLogResource.getTeamId(), config.getTeamId())) {
            resourceExtensionService.addResourceMiddleProcessing(miLogResource);
        }
    }

    private void baseMiddlewareConfigAssemble(MiddlewareAddParam param, MilogMiddlewareConfig middlewareConfig) {
        middlewareConfig.setType(param.getType());
        middlewareConfig.setRegionEn(param.getRegionEn());
        middlewareConfig.setAlias(param.getAlias());
        middlewareConfig.setNameServer(param.getNameServer());
        middlewareConfig.setServiceUrl(param.getServiceUrl());
        middlewareConfig.setAk(param.getAk());
        middlewareConfig.setSk(param.getSk());
        middlewareConfig.setAuthorization(param.getAuthorization());
        middlewareConfig.setOrgId(param.getOrgId());
        middlewareConfig.setTeamId(param.getTeamId());
        middlewareConfig.setIsDefault(param.getIsDefault());
    }

    private MilogMiddlewareConfig AddParamToAddEntity(MiddlewareAddParam addParam) {
        MilogMiddlewareConfig middlewareConfig = new MilogMiddlewareConfig();
        baseMiddlewareConfigAssemble(addParam, middlewareConfig);
        middlewareConfig.setCtime(Instant.now().toEpochMilli());
        middlewareConfig.setUtime(Instant.now().toEpochMilli());
        middlewareConfig.setCreator(MoneUserContext.getCurrentUser().getUser());
        middlewareConfig.setUpdater(MoneUserContext.getCurrentUser().getUser());
        return middlewareConfig;
    }

    private MilogMiddlewareConfig updateParamToUpdateEntity(MiddlewareUpdateParam updateParam, MilogMiddlewareConfig middlewareConfig) {
        baseMiddlewareConfigAssemble(updateParam, middlewareConfig);
        middlewareConfig.setUtime(Instant.now().toEpochMilli());
        middlewareConfig.setUpdater(MoneUserContext.getCurrentUser().getUser());
        return middlewareConfig;
    }


    private List<String> preCheckMiddlewareConfig(MiddlewareAddParam param) {
        List<String> msg = Lists.newArrayList();
        if (null == param) {
            msg.add("addParam can not be null");
            return msg;
        }
        List<?> types = param.getTypes();
        if (CollectionUtils.isEmpty(types) || types.size() != 2) {
            msg.add("types can not be null");
            return msg;
        }
        param.setType(((Double) types.get(0)).intValue());
        param.setRegionEn((String) types.get(1));
        if (null == param.getType()) {
            msg.add("region can not be null");
        }
        if (StringUtils.isEmpty(param.getNameServer())) {
            msg.add("nameServer can not be null");
        }
        if (StringUtils.isEmpty(param.getServiceUrl()) && accurateTypes.contains(param.getType())) {
            msg.add("serviceUrl can not be null");
        }
        if (StringUtils.isEmpty(param.getAk()) && accurateTypes.contains(param.getType())) {
            msg.add("ak can not be null");
        }
        if (StringUtils.isEmpty(param.getSk()) && accurateTypes.contains(param.getType())) {
            msg.add("sk can not be null");
        }
        if (MiddlewareEnum.ROCKETMQ.getCode().equals(param.getType()) && StringUtils.isEmpty(param.getAuthorization())) {
            msg.add("authorization can not be null");
        }
        if (StringUtils.isEmpty(param.getOrgId()) && accurateTypes.contains(param.getType())) {
            msg.add("orgId can not be null");
        }
        if (MiddlewareEnum.ROCKETMQ.getCode().equals(param.getType()) && StringUtils.isEmpty(param.getTeamId())) {
            msg.add("teamId can not be null");
        }
        return msg;
    }

    private List<String> preCheckUpdateMiddlewareConfig(MiddlewareUpdateParam updateParam) {
        List<String> checkMsg = preCheckMiddlewareConfig(updateParam);
        if (null == updateParam.getId()) {
            checkMsg.add("ID can not null");
        }
        return checkMsg;
    }


    private Condition handleParamToCondition(MiddlewareQueryParam param) {
        Cnd cnd = Cnd.NEW();
        int size = 2;
        List<?> types = param.getTypes();
        if (CollectionUtils.isNotEmpty(param.getTypes()) && null != types.get(0)) {
            cnd.and("type", EQUAL_OPERATE, ((Double) types.get(0)).intValue());
        }
        if (CollectionUtils.isNotEmpty(param.getTypes()) && types.size() == size && null != types.get(1)) {
            cnd.and("region_en", EQUAL_OPERATE, types.get(1));
        }
        if (StringUtils.isNotEmpty(param.getAlias())) {
            cnd.and("alias", "like", "%" + param.getAlias() + "%");
        }
        cnd.orderBy("ctime", "desc");
        return cnd;
    }
}
