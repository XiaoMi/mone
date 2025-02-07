package run.mone.m78.service.service.plugins;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.gson.*;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.util.UpdateEntity;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.xiaomi.data.push.client.HttpClientV5;
import com.xiaomi.youpin.infra.rpc.Result;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.common.Constants;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.config.utils.ReferenceConfigCache;
import org.apache.dubbo.rpc.RpcContext;
import org.apache.dubbo.rpc.service.GenericService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import run.mone.m78.api.bo.plugins.*;
import run.mone.m78.api.constant.FlowConstant;
import run.mone.m78.api.enums.InputValueTypeEnum;
import run.mone.m78.service.bo.plugin.PluginInfo;
import run.mone.m78.service.common.GsonUtils;
import run.mone.m78.service.common.HttpClient;
import run.mone.m78.service.common.IOCUtils;
import run.mone.m78.service.common.enums.UserCollectType;
import run.mone.m78.service.dao.entity.*;
import run.mone.m78.service.dao.mapper.M78BotPluginMapper;
import run.mone.m78.service.dao.mapper.M78BotPluginOrgMapper;
import run.mone.m78.service.dao.mapper.M78UserCollectMapper;
import run.mone.m78.service.dto.BotBaseInfoDto;
import run.mone.m78.service.exceptions.ExCodes;
import run.mone.m78.service.service.categoty.CategoryPluginRelService;
import run.mone.m78.service.utils.ValueTypeUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.mybatisflex.core.query.QueryMethods.noCondition;
import static run.mone.m78.api.constant.CommonConstant.PLUGIN_CALL_TIMEOUT;
import static run.mone.m78.service.dao.entity.table.M78BotPluginOrgTableDef.M78_BOT_PLUGIN_ORG;
import static run.mone.m78.service.dao.entity.table.M78BotPluginTableDef.M78_BOT_PLUGIN;
import static run.mone.m78.service.dao.entity.table.M78CategoryTableDef.M78_CATEGORY;
import static run.mone.m78.service.dao.entity.table.M78UserCollectTableDef.M78_USER_COLLECT;
import static run.mone.m78.service.exceptions.ExCodes.STATUS_INTERNAL_ERROR;

/**
 * @author HawickMason@xiaomi.com
 * @date 3/2/24 2:37 PM
 */

@Slf4j
@Service
public class BotPluginService extends ServiceImpl<M78BotPluginMapper, M78BotPlugin> {

    @Resource
    private M78BotPluginOrgMapper m78BotPluginOrgMapper;

    @Autowired
    private BotPluginRelService relService;

    @Autowired
    private CategoryPluginRelService categoryPluginRelService;

    @Resource
    private M78UserCollectMapper m78UserCollectMapper;


    /**
     * 保存或更新M78BotPlugin
     *
     * @param username 用户名，不能为空
     * @param plugin   M78BotPlugin对象，不能为空
     * @return 包含插件ID的Result对象，如果失败则包含错误信息
     */
    // 保存或更新M78BotPlugin (class)
    public Result<Long> saveOrUpdateM78BotPlugin(String username, M78BotPlugin plugin) {
        Preconditions.checkArgument(StringUtils.isNotBlank(username), "username can not be null!");
        if (plugin == null) {
            return Result.fail(ExCodes.STATUS_BAD_REQUEST, "plugin can not be null!");
        }
        try {
            // Assume there is a method in the context to save the project
            plugin.setUserName(username);
            boolean isSaved = super.saveOrUpdate(plugin);
            log.info("create bot plugin success: {}", isSaved);
            return Result.success(plugin.getId());
        } catch (Exception e) {
            // Log the exception, assume there is a logger available
            log.error("Error saving M78BotPlugin: {}", e.getMessage());
            return Result.fail(STATUS_INTERNAL_ERROR, "Error saving project: " + e.getMessage());
        }
    }

    /**
     * 根据ID删除某个插件
     *
     * @param username 用户名
     * @param id       插件ID
     * @return 删除操作的结果，包含布尔值表示是否成功
     */
    // 根据id删除某个plugin (class)
    public Result<Boolean> deleteM78BotPluginById(String username, Long id) {
        if (id == null) {
            return Result.fail(ExCodes.STATUS_BAD_REQUEST, "Plugin ID can not be null!");
        }
        try {
            List<M78BotPlugin> m78BotPlugins = mapper.selectListByQuery(QueryWrapper.create().eq("user_name", username).eq("id", id));
            if (CollectionUtils.isEmpty(m78BotPlugins)) {
                return Result.fail(ExCodes.STATUS_NOT_FOUND, "Plugin not found!");
            }
            boolean isDeleted = super.removeById(id);
            return Result.success(isDeleted);
        } catch (Exception e) {
            log.error("Error deleting M78BotPlugin with ID {}: {}", id, e.getMessage());
            return Result.fail(STATUS_INTERNAL_ERROR, "Error deleting plugin: " + e.getMessage());
        }
    }

    /**
     * 根据ID查询某个插件
     *
     * @param id 插件的ID
     * @return 包含插件信息的结果对象，如果ID为空、插件未找到或发生错误，则返回相应的失败信息
     */
    // 根据id查询某个plugin (class)
    public Result<BotPluginDTO> getM78BotPluginById(Long id) {
        if (id == null) {
            return Result.fail(ExCodes.STATUS_BAD_REQUEST, "Plugin ID can not be null!");
        }
        try {
            M78BotPlugin plugin = super.getById(id);
            if (plugin == null) {
                return Result.fail(ExCodes.STATUS_NOT_FOUND, "Plugin not found!");
            }
            return Result.success(plugin.toDTO());
        } catch (Exception e) {
            log.error("Error retrieving M78BotPlugin with ID {}: {}", id, e.getMessage());
            return Result.fail(STATUS_INTERNAL_ERROR, "Error retrieving plugin: " + e.getMessage());
        }
    }


    /**
     * 根据插件ID获取插件信息
     *
     * @param id 插件ID
     * @return 插件信息对象PluginInfo
     */
    // for inner usage only, PluginInfo is a BO
    public PluginInfo getPluginInfoById(Long id) {
        BotPluginDTO botPluginById = getM78BotPluginById(id).getData();
        String apiUrl = botPluginById.getApiUrl();
        String meta = botPluginById.getMeta();
        JsonObject metaJson = GsonUtils.gson.fromJson(meta, JsonObject.class);

        String display = Optional.ofNullable(metaJson.get("display")).map(it -> it.getAsString()).orElse("");

        JsonArray input = metaJson.get("input").getAsJsonArray();
        JsonArray output = metaJson.get("output").getAsJsonArray();

        Map<String, String> headersMap = GsonUtils.gson.fromJson(metaJson.get("http_headers").getAsJsonObject(), new com.google.gson.reflect.TypeToken<Map<String, Object>>() {
        }.getType());
        return PluginInfo.builder()
                .display(display)
                .url(apiUrl)
                .input(input)
                .name(botPluginById.getName())
                .output(output)
                .method(metaJson.get("http_method").getAsString())
                .headers(headersMap)
                .build();
    }

    /**
     * 根据PluginReq查询plugin列表
     *
     * @param req        PluginReq对象，包含查询条件
     * @param workspaces 当前权限内的工作空间列表
     * @return 包含BotPluginDTO对象的分页结果
     */
    // 根据PluginReq查询plugin列表 (project)
    public Result<Page<BotPluginDTO>> listM78BotPluginsByRequest(PluginReq req, List<M78Workspace> workspaces) {
        if (req == null) {
            return Result.fail(ExCodes.STATUS_BAD_REQUEST, "Request can not be null!");
        }

        try {
            // 增加当前权限内的空间条件
            List<Long> botPluginOrgIds = null;
            if (workspaces != null && !workspaces.isEmpty()) {
                List<Long> workspaceIds = workspaces.stream()
                        .map(M78Workspace::getId)
                        .collect(Collectors.toList());
                QueryWrapper queryWrapper = QueryWrapper.create()
                        .select("id")
                        .where(M78_BOT_PLUGIN_ORG.WORKSPACE_ID.in(workspaceIds));

                botPluginOrgIds = m78BotPluginOrgMapper.selectListByQuery(queryWrapper)
                        .stream()
                        .map(M78BotPluginOrg::getId)
                        .collect(Collectors.toList());
            }

            QueryWrapper queryWrapper = QueryWrapper.create()
                    .select(M78_BOT_PLUGIN.DEFAULT_COLUMNS, M78_CATEGORY.NAME)
                    .from(M78_BOT_PLUGIN)
                    .leftJoin(M78_CATEGORY)
                    .on(M78_BOT_PLUGIN.CATEGORY_ID.eq(M78_CATEGORY.ID))
                    .where(req.getId() != null ? M78_BOT_PLUGIN.ID.eq(req.getId()) : noCondition())
                    .and(req.getStatus() != null ? M78_BOT_PLUGIN.STATUS.eq(req.getStatus()) : noCondition())
                    .and(req.getOrgId() != null ? M78_BOT_PLUGIN.ORG_ID.eq(req.getOrgId()) : noCondition())
                    .and(StringUtils.isNotBlank(req.getCategoryName()) ? M78_CATEGORY.NAME.eq(req.getCategoryName()) : noCondition())
                    .and(req.getCategoryId() != null ? M78_CATEGORY.ID.eq(req.getCategoryId()) : noCondition())
                    .and(req.getWorkspaceId() != null ? M78_BOT_PLUGIN.WORKSPACE_ID.eq(req.getWorkspaceId()) : noCondition())
                    .and(StringUtils.isNotBlank(req.getUserName()) ? M78_BOT_PLUGIN.USER_NAME.eq(req.getUserName()) : noCondition())
                    .and(req.getType() != null ? M78_BOT_PLUGIN.TYPE.eq(req.getType()) : noCondition())
                    .and(StringUtils.isNoneBlank(req.getName()) ? M78_BOT_PLUGIN.NAME.like(req.getName()) : noCondition())
                    .and(botPluginOrgIds != null && !botPluginOrgIds.isEmpty() ? M78_BOT_PLUGIN.ORG_ID.in(botPluginOrgIds) : noCondition())
                    .orderBy(M78_BOT_PLUGIN.CREATE_TIME.desc());
            if (req.getPageNum() == null || req.getPageSize() == null) {
                req.setPageNum(1);
                req.setPageSize(Integer.MAX_VALUE);
            }
            Page<M78BotPlugin> page = super.page(Page.of(req.getPageNum(), req.getPageSize()), queryWrapper);
            Page<BotPluginDTO> resPage = new Page<>();
            List<M78BotPlugin> records = page.getRecords();
            if (CollectionUtils.isNotEmpty(records)) {
                List<BotPluginDTO> dtoList = records.stream()
                        .map(M78BotPlugin::toDTO)
                        .collect(Collectors.toList());
                resPage.setPageNumber(page.getPageNumber());
                resPage.setPageSize(page.getPageSize());
                resPage.setTotalPage(page.getTotalPage());
                resPage.setTotalRow(page.getTotalRow());
                resPage.setRecords(dtoList);
            }
            return Result.success(resPage);
        } catch (Exception e) {
            log.error("Error listing M78BotPlugins: {}", e.getMessage());
            return Result.fail(STATUS_INTERNAL_ERROR, "Error listing plugins: " + e.getMessage());
        }
    }

    /**
     * 返回发布的组件列表或者是自己空间下的组件列表
     *
     * @param req PluginReq对象，包含查询条件
     * @return 包含BotPluginDTO对象的分页结果
     */
    // 根据PluginReq查询plugin列表 (project)
    public Result<Page<BotPluginDTO>> listPublishOrOwnM78BotPlugins(PluginReq req, List<M78Workspace> workspaces) {
        if (req == null) {
            return Result.fail(ExCodes.STATUS_BAD_REQUEST, "Request can not be null!");
        }

        try {
            // 可以查询的pulgin
            // 1.已上架的插件
            QueryWrapper pulginOrgQueryWrapper = QueryWrapper.create()
                    .select("id")
                    .where(M78_BOT_PLUGIN_ORG.STATUS.eq(PluginOrgPubStatusEnum.PUB.getCode()));
            List<Long> publishedBotPluginOrgIds = m78BotPluginOrgMapper.selectListByQuery(pulginOrgQueryWrapper)
                    .stream()
                    .map(M78BotPluginOrg::getId)
                    .collect(Collectors.toList());

            // 2. 有空间权限的plugin_org
            List<Long> ownBotPluginOrgIds = null;
            if (workspaces != null && !workspaces.isEmpty()) {
                List<Long> workspaceIds = workspaces.stream()
                        .map(M78Workspace::getId)
                        .collect(Collectors.toList());
                QueryWrapper queryWrapper = QueryWrapper.create()
                        .select("id")
                        .where(M78_BOT_PLUGIN_ORG.WORKSPACE_ID.in(workspaceIds));

                ownBotPluginOrgIds = m78BotPluginOrgMapper.selectListByQuery(queryWrapper)
                        .stream()
                        .map(M78BotPluginOrg::getId)
                        .collect(Collectors.toList());
            }

            QueryWrapper queryWrapper = QueryWrapper.create()
                    .select(M78_BOT_PLUGIN.DEFAULT_COLUMNS)
                    .from(M78_BOT_PLUGIN)
                    .where(req.getId() != null ? M78_BOT_PLUGIN.ID.eq(req.getId()) : noCondition())
                    .and(StringUtils.isNoneBlank(req.getName()) ? M78_BOT_PLUGIN.NAME.like(req.getName()) : noCondition())
                    .and(ownBotPluginOrgIds != null && !ownBotPluginOrgIds.isEmpty() ?
                            M78_BOT_PLUGIN.ORG_ID.in(ownBotPluginOrgIds)
                                    .or(M78_BOT_PLUGIN.ORG_ID.in(publishedBotPluginOrgIds).and(M78_BOT_PLUGIN.STATUS.eq(PluginOrgPubStatusEnum.PUB.getCode())))
                            : M78_BOT_PLUGIN.ORG_ID.in(publishedBotPluginOrgIds).and(M78_BOT_PLUGIN.STATUS.eq(PluginOrgPubStatusEnum.PUB.getCode())))
                    .orderBy(M78_BOT_PLUGIN.CREATE_TIME.desc());
            if (req.getPageNum() == null || req.getPageSize() == null) {
                req.setPageNum(1);
                req.setPageSize(Integer.MAX_VALUE);
            }
            Page<M78BotPlugin> page = super.page(Page.of(req.getPageNum(), req.getPageSize()), queryWrapper);
            Page<BotPluginDTO> resPage = new Page<>();
            List<M78BotPlugin> records = page.getRecords();
            if (CollectionUtils.isNotEmpty(records)) {
                List<BotPluginDTO> dtoList = records.stream()
                        .map(M78BotPlugin::toDTO)
                        .collect(Collectors.toList());
                resPage.setPageNumber(page.getPageNumber());
                resPage.setPageSize(page.getPageSize());
                resPage.setTotalPage(page.getTotalPage());
                resPage.setTotalRow(page.getTotalRow());
                resPage.setRecords(dtoList);
            }
            return Result.success(resPage);
        } catch (Exception e) {
            log.error("Error listing M78BotPlugins: {}", e.getMessage());
            return Result.fail(STATUS_INTERNAL_ERROR, "Error listing plugins: " + e.getMessage());
        }
    }

    /**
     * 列出M78Bot插件组织信息
     *
     * @param req 插件组织请求参数
     * @return 包含Bot插件组织DTO的分页结果
     */
    public Result<Page<BotPluginOrgDTO>> listM78BotPluginsOrg(PluginOrgReq req, String username) {
        if (req == null) {
            return Result.fail(ExCodes.STATUS_BAD_REQUEST, "Request can not be null!");
        }
        //查询我创建的plugin
        if (req.getScope()!=null && !req.getScope().isEmpty() && req.getScope().equals("mine")){
            req.setUserName(username);
        }
        try {
            QueryWrapper queryWrapper = QueryWrapper.create()
                    .where(req.getId() != null ? M78_BOT_PLUGIN_ORG.ID.eq(req.getId()) : noCondition())
                    .and(req.getWorkspaceId() != null ? M78_BOT_PLUGIN_ORG.WORKSPACE_ID.eq(req.getWorkspaceId()) : noCondition())
                    .and(StringUtils.isNotBlank(req.getUserName()) ? M78_BOT_PLUGIN_ORG.USER_NAME.eq(req.getUserName()) : noCondition())
                    .and(StringUtils.isNotBlank(req.getName()) ? M78_BOT_PLUGIN_ORG.NAME.like(req.getName()) : noCondition())
                    .and(req.getStatus() != null ? M78_BOT_PLUGIN_ORG.STATUS.eq(req.getStatus()) : noCondition())
                    .and(req.getOfficial() != null ? M78_BOT_PLUGIN_ORG.OFFICIAL.eq(req.getOfficial()) : noCondition())
                    //     .orderBy(StringUtils.isNotBlank(req.getOrder()) ? req.getOrder() : "create_time", StringUtils.isNotBlank(req.getOrder()) ? false : null);
                    .orderBy("official", false)
                    .orderBy("plugin_avg_star", false);
            //如果是我收藏的,收藏的表为m78_user_collect，但是表里边的type全为0，plugin对应的type为1，需要和前端说明修改一下
            if (req.getScope()!=null && !req.getScope().isEmpty() && req.getScope().equals("favorite")){
                QueryWrapper userCollectWrapper = QueryWrapper.create()
                        .and(M78_USER_COLLECT.USERNAME.eq(username))
                        .and(M78_USER_COLLECT.TYPE.eq(UserCollectType.PLUGIN.getCode()));
                List<Long> favoritePluginsList = m78UserCollectMapper.selectListByQuery(userCollectWrapper).stream().map(M78UserCollect::getCollectId).toList();
                if (favoritePluginsList == null || favoritePluginsList.isEmpty()){
                    //我收藏中没有任何的插件,
                    return Result.success(new Page<>());
                }
                queryWrapper.in("id", favoritePluginsList);
            }
            // 如果关联了分类
            if (StringUtils.isNotEmpty(req.getCategoryId())) {
                Long categoryId = Long.parseLong(req.getCategoryId());
                List<M78CategoryPluginRel> categoryIdsBotRelList = categoryPluginRelService.getByCatId(categoryId);
                if (categoryIdsBotRelList == null || categoryIdsBotRelList.isEmpty()) {
                    //该分类未有任何bot，直接返回不判断后面
                    return Result.success(new Page<>());
                }
                queryWrapper.in("id", categoryIdsBotRelList.stream().map(M78CategoryPluginRel::getPluginId).toList());
            }
            // TODO : orderBy filter by scale
            if (req.getPageNum() == null || req.getPageSize() == null) {
                req.setPageNum(1);
                req.setPageSize(Integer.MAX_VALUE);
            }
            Page<M78BotPluginOrg> page = m78BotPluginOrgMapper.paginate(Page.of(req.getPageNum(), req.getPageSize()), queryWrapper);
            List<M78BotPluginOrg> records = page.getRecords();
            Page<BotPluginOrgDTO> resPage = new Page<>();
            if (page.hasRecords()) {
                resPage.setPageNumber(page.getPageNumber());
                resPage.setPageSize(page.getPageSize());
                resPage.setTotalPage(page.getTotalPage());
                resPage.setTotalRow(page.getTotalRow());
                resPage.setRecords(records.stream()
                        .map(M78BotPluginOrg::toDTO)
                        .collect(Collectors.toList()));
                // 添加插件引用次数
                relService.setOrgRelCount(resPage.getRecords());
                // 添加插件分类
                categoryPluginRelService.setCategoryPluginRel(resPage.getRecords());
                if (!req.isOrgOnly()) {
                    resPage.getRecords()
                            .forEach(r -> {
                                r.setPlugins(listM78BotPluginsByRequest(PluginReq.builder()
                                        .orgId(r.getId())
                                        .status(req.getSubStatus())
                                        .build(), null)
                                        .getData()
                                        .getRecords());
                                r.setPluginCnt(r.getPlugins().size());
                            });
                }
            }
            return Result.success(resPage);
        } catch (Exception e) {
            log.error("Error listing M78BotPlugins: {}", e.getMessage());
            return Result.fail(STATUS_INTERNAL_ERROR, "Error listing plugins: " + e.getMessage());
        }
    }

    /**
     * 测试Dubbo插件
     *
     * @param dubboTestReq 包含测试请求信息的对象
     * @return 测试结果的封装对象
     */
    public Result<Object> testDubboPlugin(DubboTestReq dubboTestReq) {
        log.info("testing dubbo, dubboTestReq:{}", dubboTestReq);
        M78BotPlugin pluginInfo = super.getById(dubboTestReq.getPluginId());
        BotPluginDTO dto = pluginInfo.toDTO();
        BotPluginDTO.BotPluginMeta meta = dto.getBotPluginMeta();
        String key = ReferenceConfigCache.getKey(meta.getDubboServiceName(), meta.getDubboServiceGroup(), meta.getDubboServiceVersion());
        GenericService genericService = ReferenceConfigCache.getCache().get(key);
        if (null == genericService) {
            ReferenceConfig<GenericService> reference = new ReferenceConfig<>();
            ApplicationConfig applicationConfig = (ApplicationConfig) IOCUtils.getBean("applicationConfig");
            RegistryConfig registryConfig = (RegistryConfig) IOCUtils.getBean("registryConfig");
            reference.setApplication(applicationConfig);
            reference.setRegistry(registryConfig);
            reference.setInterface(meta.getDubboServiceName());
            reference.setGeneric(true);
            reference.setCheck(false);
            reference.setGroup(meta.getDubboServiceGroup());
            reference.setVersion(meta.getDubboServiceVersion());
            reference.setTimeout(meta.getTimeout());
            ReferenceConfigCache cache = ReferenceConfigCache.getCache();
            genericService = cache.get(reference);
        }
        RpcContext.getContext().setAttachment(Constants.TIMEOUT_KEY, String.valueOf(meta.getTimeout()));
        if (meta.getRpcContext() != null && meta.getRpcContext().size() > 0) {
            meta.getRpcContext().entrySet().stream().forEach(entry -> RpcContext.getContext().setAttachment(entry.getKey(), entry.getValue()));
        }
        Object res = genericService.$invoke(meta.getDubboMethodName(), meta.getDubboMethodParamtypes().toArray(new String[0]), dubboTestReq.getParams().toArray());

        return Result.success(res);
    }

    /**
     * 测试HTTP插件
     *
     * @param pluginId 插件ID
     * @param inputMap 输入参数的映射
     * @return 测试结果，成功时返回插件的响应内容，失败时返回错误信息
     */
    public Result<String> testHttpPlugin(Long pluginId, Map<String, String> inputMap) {
        log.info("testing http plugin id:{}, input:{}", pluginId, inputMap);
        try {
            //从m78获取plugin信息
            M78BotPlugin pluginInfo = super.getById(pluginId);
            BotPluginDTO dto = pluginInfo.toDTO();
            String urlStr = dto.getApiUrl();
            String method = dto.getBotPluginMeta().getHttp_method();
            Map<String, String> headersMap = dto.getBotPluginMeta().getHttp_headers();

            HashMap<String, InputValueTypeEnum> inputInfoTypes = new HashMap<>();
            for (BotPluginDTO.BotPluginMetaParam input : dto.getBotPluginMeta().getInput()) {
                // 兼容历史数据，历史数据valueType没有
                if (StringUtils.isBlank(input.getValueType())) {
                    inputInfoTypes.put(input.getName(), InputValueTypeEnum.STRING);
                }
                InputValueTypeEnum inputValueTypeEnum = InputValueTypeEnum.getEnumByName(input.getValueType());
                if (inputValueTypeEnum != null) {
                    inputInfoTypes.put(input.getName(), inputValueTypeEnum);
                }
            }

            JsonObject param = new JsonObject();
            Map<String, Object> paramMap = Maps.newHashMap();

            for (Map.Entry<String, String> entry : inputMap.entrySet()) {
                InputValueTypeEnum typeEnum = inputInfoTypes.get(entry.getKey());
                if (typeEnum == null) {
                    typeEnum = InputValueTypeEnum.STRING;
                }
                JsonElement convertValue = ValueTypeUtils.convertValueByTypeToJsonElement(entry.getValue(), typeEnum);
                param.add(entry.getKey(), convertValue);
                if (convertValue.isJsonPrimitive()) {
                    paramMap.put(entry.getKey(), convertValue.getAsString());
                }
            }

            String reqUrl = "GET".equalsIgnoreCase(method)
                    ? HttpClient.buildUrlWithParameters(urlStr, paramMap)
                    : urlStr;
            //todo 页面设置或者plugin元数据带过来
            String jsonStr = HttpClient.proxy(reqUrl, method, headersMap, param, PLUGIN_CALL_TIMEOUT);
            log.info("test plugin id:{}, with res:{}", pluginInfo, jsonStr);
            // update debug status
            M78BotPlugin update = UpdateEntity.of(M78BotPlugin.class, pluginId);
            update.setDebugStatus(PluginDebugStatusEnum.TEST_SUCCESS.getCode());
            super.saveOrUpdate(update);
            return Result.success(jsonStr);
        } catch (Exception e) {
            log.error("failed to test plugin:{}, nested exception is:", pluginId, e);
            // update debug status
            M78BotPlugin update = UpdateEntity.of(M78BotPlugin.class, pluginId);
            update.setDebugStatus(PluginDebugStatusEnum.TEST_FAILED.getCode());
            super.saveOrUpdate(update);
            return Result.fail(STATUS_INTERNAL_ERROR, "test plugin failed!");
        }
    }

    /**
     * 启用或禁用插件
     *
     * @param pluginId 插件ID
     * @param enable   是否启用插件，true表示启用，false表示禁用
     * @return 操作结果，包含一个布尔值，表示操作是否成功
     */
    public Result<Boolean> enableOrDisablePlugin(Long pluginId, boolean enable) {
        M78BotPlugin update = UpdateEntity.of(M78BotPlugin.class, pluginId);
        if (enable) {
            update.setStatus(PluginEnableStatusEnum.ENABLED.getCode());
            update.setReleaseTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")));
        } else {
            update.setStatus(PluginEnableStatusEnum.DISABLED.getCode());
        }
        return Result.success(super.saveOrUpdate(update));
    }

    /**
     * 获取使用次数最多的前三个插件信息列表
     *
     * @return 包含使用次数最多的前三个插件信息的列表
     */
    public List<BotBaseInfoDto> mostUsedList() {
        List<M78BotPlugin> m78Bots = mapper.selectListByQuery(QueryWrapper.create().orderBy(M78_BOT_PLUGIN.PLUGIN_USE_TIMES, false).limit(3));
        return m78Bots.stream().map(plugin -> {
            BotBaseInfoDto dto = BotBaseInfoDto.builder()
                    .id(plugin.getId().longValue())
                    .name(plugin.getName())
                    .avatarUrl("")
                    .creator(plugin.getUserName())
                    .remark(plugin.getDescription())
                    .useTimes(plugin.getPluginUseTimes())
                    .build();
            return dto;
        }).toList();
    }

    /**
     * 调用指定插件并返回其响应结果
     *
     * @param pluginId 插件的唯一标识符
     * @param param    调用插件时传递的参数
     * @return 插件的响应结果，包含插件名称、类型和显示信息
     */
    @SneakyThrows
    public JsonObject callPlugin(String pluginId, JsonObject param, String userName) {
        PluginInfo plugin = getPluginInfoById(Long.valueOf(pluginId));
        String url = plugin.getUrl();
        String method = plugin.getMethod();
        Map<String, String> headers = plugin.getHeaders();
        JsonElement res = new JsonPrimitive("");
        JsonArray array = plugin.getInput();
        handlePluginParam(param, userName);
        if ("GET".equalsIgnoreCase(method)) {
            Map<String, Object> paraMap = new HashMap<>();
            array.forEach(it -> {
                        JsonObject jsonObj = it.getAsJsonObject();
                        if (jsonObj == null) {
                            return;
                        }
                        JsonElement nameJ = jsonObj.get("name");
                        if (nameJ == null) {
                            return;
                        }
                        String name = nameJ.getAsString();
                        JsonElement valueJ = param.get(name);
                        if (valueJ == null) {
                            return;
                        }
                        String value = param.get(name).getAsString();
                        paraMap.put(name, value);
                    }
            );
            paraMap.put(FlowConstant.TY_USERNAME_MARK, param.getAsJsonPrimitive(FlowConstant.TY_USERNAME_MARK));
            String callRes = HttpClientV5.get(HttpClient.buildUrlWithParameters(url, paraMap), headers, 180000);
            try {
                res = new JsonParser().parse(callRes);
            } catch (Exception e) {
                res = new JsonPrimitive(callRes);
            }
        } else {
            JsonObject pluginReq = new JsonObject();
            array.forEach(it -> {
                JsonObject jsonObj = it.getAsJsonObject();
                String name = jsonObj.get("name").getAsString();
                JsonElement value = param.get(name);
                pluginReq.add(name, value);
            });
            pluginReq.add(FlowConstant.TY_USERNAME_MARK, param.getAsJsonPrimitive(FlowConstant.TY_USERNAME_MARK));
            res = JsonParser.parseString(HttpClient.post(url, GsonUtils.gson.toJson(pluginReq)));
        }

        log.info("call plugin res:{}", res);
        res.getAsJsonObject().addProperty("call_plugin", plugin.getName());
        res.getAsJsonObject().addProperty("type", "plugin");
        res.getAsJsonObject().addProperty("display", plugin.getDisplay());
        return res.getAsJsonObject();
    }


    // 如果param中包含key为FlowConstant.TY_USERNAME_MARK，并且userName不为空
    private void handlePluginParam(JsonObject param, String userName) {
        // 特殊处理$$TY_USERNAME$$
        if (param.has(FlowConstant.TY_USERNAME_MARK) && StringUtils.isNotEmpty(userName)) {
            String user = userName.contains("_") ? userName.split("_")[1] : userName;
            param.addProperty(FlowConstant.TY_USERNAME_MARK, user);
        }
    }

}
