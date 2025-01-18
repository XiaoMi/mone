package run.mone.m78.service.service.plugins;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.util.UpdateEntity;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.xiaomi.youpin.infra.rpc.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import run.mone.m78.api.bo.plugins.BotPluginOrgDTO;
import run.mone.m78.api.bo.plugins.OfficialByAdminReq;
import run.mone.m78.api.bo.plugins.PluginReq;
import run.mone.m78.service.dao.entity.M78BotPlugin;
import run.mone.m78.service.dao.entity.M78BotPluginOrg;
import run.mone.m78.service.dao.entity.M78CategoryPluginRel;
import run.mone.m78.service.dao.entity.PluginOrgPubStatusEnum;
import run.mone.m78.service.dao.mapper.M78BotPluginOrgMapper;
import run.mone.m78.service.dao.mapper.M78CategoryPluginRelMapper;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static run.mone.m78.service.exceptions.ExCodes.*;

/**
 * @author HawickMason@xiaomi.com
 * @date 3/13/24 2:55 PM
 */
@Service
@Slf4j
public class BotPluginOrgService extends ServiceImpl<M78BotPluginOrgMapper, M78BotPluginOrg> {

    @Resource
    private BotPluginService botPluginService;
    @Autowired
    private M78CategoryPluginRelMapper categoryPluginRelMapper;

    /**
     * 导入M78BotPluginOrg，注意需要先插入pluginOrg，生成了Id，替换了plugins字段里面的org_id，再插入plugins
     *
     * @param username  用户名
     * @param pluginOrg 插件组织对象
     * @return 导入结果，成功返回true，失败返回相应的错误信息
     */
    // 导入M78BotPluginOrg，注意需要先插入pluginOrg，生成了Id，替换了plugins字段里面的org_id，再插入plugins
    public Result<Boolean> importM78BotPluginOrg(String username, M78BotPluginOrg pluginOrg) {
        if (pluginOrg == null) {
            log.error("PluginOrg object is null");
            return Result.fail(STATUS_BAD_REQUEST, "PluginOrg object is null");
        }
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        // 前端如果带过来，把它去掉，自动生成一个新的orgId
        pluginOrg.setId(null);
        pluginOrg.setUserName(username);
        pluginOrg.setModifier(username);
        pluginOrg.setCreateTime(now);
        pluginOrg.setModifyTime(now);

        try {
            // 插入pluginOrg，生成Id
            boolean isOrgSaved = super.save(pluginOrg);
            if (!isOrgSaved) {
                return Result.fail(STATUS_INTERNAL_ERROR, "Failed to save PluginOrg");
            }

            // 替换plugins字段里面的org_id
            Long orgId = pluginOrg.getId();
            List<M78BotPlugin> plugins = pluginOrg.getPlugins();
            if (plugins != null && !plugins.isEmpty()) {
                plugins.forEach((plugin) -> {
                    plugin.setOrgId(orgId);
                    // 前端如果带过来pulgin Id，把它去掉，自动生成一个新的Id
                    plugin.setId(null);
                    plugin.setUserName(username);
                    plugin.setModifier(username);
                    plugin.setCreateTime(now);
                    plugin.setModifyTime(now);
                });
                boolean isPluginsSaved = botPluginService.saveBatch(plugins);
                if (!isPluginsSaved) {
                    // 回滚插件生成
                    boolean isDeleted = super.removeById(orgId);
                    if (!isDeleted) {
                        log.error("Failed to import M78BotPluginOrg, plugin save failed, plugin org roolback failed, plugin_org_id:{}", orgId);
                    }

                    return Result.fail(STATUS_INTERNAL_ERROR, "Failed to import M78BotPluginOrg due to an exception");
                }
            }

            return Result.success(true);
        } catch (Exception e) {
            log.error("Failed to import M78BotPluginOrg", e);
            return Result.fail(STATUS_INTERNAL_ERROR, "Failed to import M78BotPluginOrg due to an exception");
        }
    }

    /**
     * 保存或更新M78BotPluginOrg对象
     *
     * @param username         用户名
     * @param pluginOrg        M78BotPluginOrg对象
     * @param pluginCategories 插件类别列表
     * @return 包含操作结果的Result对象，成功时返回插件ID，失败时返回错误信息
     */
    // 保存或更新M78BotPluginOrg (project)
    public Result<Long> saveOrUpdateM78BotPluginOrg(String username, M78BotPluginOrg pluginOrg, List<String> pluginCategories) {
        if (pluginOrg == null) {
            log.error("PluginOrg object is null");
            return Result.fail(STATUS_BAD_REQUEST, "PluginOrg object is null");
        }
        try {
            if (StringUtils.isBlank(pluginOrg.getUserName()) && pluginOrg.getId() == null) {
                pluginOrg.setUserName(username);
            }
            if (StringUtils.isBlank(pluginOrg.getModifier())) {
                pluginOrg.setModifier(username);
            }

            if (pluginOrg.getId() == null) {
                pluginOrg.setStatus(PluginOrgPubStatusEnum.NOT_PUB.getCode());
            }
            boolean isSaved = super.saveOrUpdate(pluginOrg);

            if (isSaved) {
                // insert从database获取，update从参数获取
                Long pluginId = pluginOrg.getId();
                return Result.success(pluginId);
            }
            return Result.fail(STATUS_INTERNAL_ERROR, "Failed to save or update M78BotPluginOrg due to an exception");
        } catch (Exception e) {
            log.error("Failed to save or update M78BotPluginOrg", e);
            return Result.fail(STATUS_INTERNAL_ERROR, "Failed to save or update M78BotPluginOrg due to an exception");
        }

    }

    /**
     * 根据ID删除某个PluginOrg
     *
     * @param id PluginOrg的ID
     * @return 包含删除操作结果的Result对象
     */
    // 根据id删除某个pluginOrg (class)
    public Result<Boolean> deletePluginOrgById(Long id) {
        if (id == null) {
            log.error("PluginOrg id is null");
            return Result.fail(STATUS_BAD_REQUEST, "PluginOrg id is null");
        }
        try {
            boolean isDeleted = super.removeById(id);
            return Result.success(isDeleted);
        } catch (Exception e) {
            log.error("Failed to delete M78BotPluginOrg with id: " + id, e);
            return Result.fail(STATUS_INTERNAL_ERROR, "Failed to delete M78BotPluginOrg due to an exception");
        }
    }

    /**
     * 根据id查询某个pluginOrg
     *
     * @param id 插件组织的唯一标识
     * @return 包含插件组织信息的结果对象，如果id为空或查询失败则返回相应的错误信息
     */
    // 根据id查询某个pluginOrg (class)
    public Result<BotPluginOrgDTO> getPluginOrgById(Long id) {
        if (id == null) {
            log.error("PluginOrg id is null");
            return Result.fail(STATUS_BAD_REQUEST, "PluginOrg id is null");
        }
        try {
            M78BotPluginOrg pluginOrg = super.getById(id);
            BotPluginOrgDTO res = pluginOrg.toDTO();
            res.setPlugins(botPluginService.listM78BotPluginsByRequest(PluginReq.builder().orgId(id).build(), null).getData().getRecords());
            return res != null ? Result.success(res) : Result.fail(STATUS_NOT_FOUND, "PluginOrg not found");
        } catch (Exception e) {
            log.error("Failed to retrieve M78BotPluginOrg with id: " + id, e);
            return Result.fail(STATUS_INTERNAL_ERROR, "Failed to retrieve M78BotPluginOrg due to an exception");
        }
    }

    /**
     * 发布或取消发布M78BotPluginOrg
     *
     * @param id               插件组织的ID
     * @param pub              是否发布，true表示发布，false表示取消发布
     * @param pluginCategories 插件分类ID列表
     * @return 操作结果，true表示成功
     */
    // 发布或取消发布M78BotPluginOrg
    @Transactional
    public Result<Boolean> pubOrCancelPluginOrg(Long id, boolean pub, List<Long> pluginCategories) {
        M78BotPluginOrg update = UpdateEntity.of(M78BotPluginOrg.class, id);
        if (pub) {
            update.setStatus(PluginOrgPubStatusEnum.PUB.getCode());
            update.setReleaseTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")));
        } else {
            update.setStatus(PluginOrgPubStatusEnum.NOT_PUB.getCode());
        }
        super.saveOrUpdate(update);

        // 新增或修改插件分类
        if (pluginCategories != null && pluginCategories.size() > 0) {
            Date date = new Date();
            List<M78CategoryPluginRel> existsRels = categoryPluginRelMapper.selectListByQuery(QueryWrapper.create().eq("plugin_id", id).eq("deleted", 0));
            List<Long> existsRelCatIds = existsRels.stream().map(M78CategoryPluginRel::getCatId).collect(Collectors.toList());
            List<Long> addRelCatIds = pluginCategories.stream().filter(it -> !existsRelCatIds.contains(it)).collect(Collectors.toList());
            List<Long> deletedCatIds = existsRelCatIds.stream().filter(it -> !pluginCategories.contains(it)).collect(Collectors.toList());

            List<M78CategoryPluginRel> insertRels = addRelCatIds.stream().map(it ->
                    M78CategoryPluginRel.builder().pluginId(id).catId(it).deleted(0).createTime(date).build()).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(insertRels)) {
                categoryPluginRelMapper.insertBatch(insertRels);
            }

            existsRels.stream().forEach(it -> {
                if (deletedCatIds.contains(it.getCatId())) {
                    it.setDeleted(1);
                    it.setUpdateTime(date);
                    categoryPluginRelMapper.update(it);
                }
            });

        }
        return Result.success(true);
    }

    /**
     * 管理员标记官方状态
     *
     * @param officialByAdminReq 包含要标记的官方信息的请求对象
     * @return 操作结果，返回一个包含布尔值的结果对象，表示更新是否成功
     */
    public Result<Boolean> markOfficialByAdmin(OfficialByAdminReq officialByAdminReq) {
        M78BotPluginOrg m78BotPluginOrg = getById(officialByAdminReq.getId());
        m78BotPluginOrg.setOfficial(officialByAdminReq.getOfficial());
        return Result.success(updateById(m78BotPluginOrg, true));
    }


    // TODO: 根据PluginOrgReq查询pluginOrg列表 (project);   后续按需添加，目前在BotPluginService#listM78BotPluginsOrg
}
