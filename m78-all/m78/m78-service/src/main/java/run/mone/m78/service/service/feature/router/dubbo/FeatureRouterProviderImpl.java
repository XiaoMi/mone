package run.mone.m78.service.service.feature.router.dubbo;

import com.google.common.base.Preconditions;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.util.UpdateEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.dubbo.config.annotation.DubboService;
import run.mone.m78.api.FeatureRouterProvider;
import run.mone.m78.api.bo.feature.router.FeatureRouterDTO;
import run.mone.m78.api.bo.feature.router.FeatureRouterReq;
import run.mone.m78.api.constant.CommonConstant;
import run.mone.m78.service.common.MappingUtils;
import run.mone.m78.service.dao.entity.ChatInfoPo;
import run.mone.m78.service.dao.entity.FeatureRouter;
import run.mone.m78.service.dao.mapper.ChatInfoMapper;
import run.mone.m78.service.service.feature.router.FeatureRouterService;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.mybatisflex.core.query.QueryMethods.noCondition;
import static run.mone.m78.api.constant.FeatureRouterConstant.*;
import static run.mone.m78.api.constant.FeatureRouterConstant.ROUTER_META_STORE;
import static run.mone.m78.service.dao.entity.table.ChatInfoPoTableDef.CHAT_INFO_PO;
import static run.mone.m78.service.dao.entity.table.FeatureRouterTableDef.FEATURE_ROUTER;

/**
 * @author HawickMason@xiaomi.com
 * @date 2/29/24 11:15 AM
 */
@DubboService(timeout = CommonConstant.DEF_DUBBO_TIMEOUT, group = "${dubbo.group}", version = "1.0")
@Slf4j
public class FeatureRouterProviderImpl implements FeatureRouterProvider {

    @Resource
    private FeatureRouterService featureRouterService;

    @Resource
    private ChatInfoMapper chatInfoMapper;

    @Override
    public Pair<Long, List<FeatureRouterDTO>> listAllFeatureRouter(FeatureRouterReq req) {
        log.info("listing feature router with req:{}", req);
        QueryWrapper queryWrapper = QueryWrapper.create()
                .select(FEATURE_ROUTER.DEFAULT_COLUMNS, CHAT_INFO_PO.TYPE, CHAT_INFO_PO.MAPPING_CONTENT)
                .from(FEATURE_ROUTER)
                .leftJoin(CHAT_INFO_PO)
                .on(FEATURE_ROUTER.LABEL_ID.eq(CHAT_INFO_PO.ID))
                .where(req.getId() != null ? FEATURE_ROUTER.ID.eq(req.getId()) : noCondition())
                .and(req.getLabelId() != null ? FEATURE_ROUTER.LABEL_ID.eq(req.getLabelId()) : noCondition())
                .and(req.getType() != null ? CHAT_INFO_PO.TYPE.eq(req.getType()) : noCondition())
                .and(StringUtils.isNotBlank(req.getName()) ? FEATURE_ROUTER.NAME.eq(req.getName()) : noCondition())
                .and(StringUtils.isNotBlank(req.getUserName()) ? FEATURE_ROUTER.USER_NAME.eq(req.getUserName()) : noCondition())
                .orderBy(FEATURE_ROUTER.CREATE_TIME.desc());
        List<FeatureRouterDTO> res = new ArrayList<>();
        if (req.getPage() != null && req.getPageSize() != null) {
            // 分页查询
            Page<FeatureRouter> page = featureRouterService.page(Page.of(req.getPage(), req.getPageSize()), queryWrapper);
            if (page != null && page.hasRecords()) {
                res = page.getRecords().stream()
                        .map(this::getFeatureRouterDTO)
                        .collect(Collectors.toList());
                log.info("get feature router list by page: {}, with rows:{}", req, res.size());
                return Pair.of(page.getTotalRow(), res);
            }
        } else {
            // 全量查询
            List<FeatureRouter> list = featureRouterService.list(queryWrapper);
            res = list.stream()
                    .map(this::getFeatureRouterDTO)
                    .collect(Collectors.toList());
        }
        log.info("get feature router list : {}, with rows:{}", req, res.size());
        return Pair.of((long) res.size(), res);
    }

    private FeatureRouterDTO getFeatureRouterDTO(FeatureRouter r) {
        FeatureRouterDTO dto = MappingUtils.map(r, FeatureRouterDTO.class);
        // HINT: nasty
        if (dto.getRouterMeta() != null) {
            dto.getRouterMeta().remove(ROUTER_META_TYPE_MARK);
            dto.getRouterMeta().remove(ROUTER_META_SQL);
            dto.getRouterMeta().remove(ROUTER_META_TYPE);
            dto.getRouterMeta().remove(ROUTER_META_STORE);
        }
        if (r.getChatInfoPo() != null) {
            dto.setContent(r.getChatInfoPo().getMappingContent());
        }
        return dto;
    }

    @Override
    public FeatureRouterDTO getFeatureRouterDetailById(Long id) {
        log.info("getFeatureRouterDetailById:{}", id);
        Preconditions.checkArgument(id != null, "须传递FeatureRouter的id");
        QueryWrapper queryWrapper = QueryWrapper.create()
                .select(FEATURE_ROUTER.DEFAULT_COLUMNS, CHAT_INFO_PO.TYPE, CHAT_INFO_PO.MAPPING_CONTENT)
                .from(FEATURE_ROUTER)
                .leftJoin(CHAT_INFO_PO)
                .on(FEATURE_ROUTER.LABEL_ID.eq(CHAT_INFO_PO.ID))
                .where(FEATURE_ROUTER.ID.eq(id));
        FeatureRouter r = featureRouterService.getOne(queryWrapper);
        FeatureRouterDTO res = getFeatureRouterDTO(r);
        log.info("getFeatureRouterDetailById, res:{}", res);
        return res;
    }

    /**
     * 更新特性路由映射内容。
     * 根据传入的FeatureRouterReq对象，更新对应的特性路由映射。
     * 首先校验请求中的id和content不为空，然后根据id获取FeatureRouter对象。
     * 如果FeatureRouter对象的labelId为空，则返回false。
     * 根据labelId创建一个待更新的ChatInfoPo对象，并设置其mappingContent。
     * 调用chatInfoMapper的update方法进行更新操作。
     * 如果更新成功（影响行数为1），则返回true，否则返回false。
     */
    public boolean updateFeatureRouterMappingContent(FeatureRouterReq req) {
        // get id & content to be updated
        Long id = req.getId();
        String content = req.getContent();
        Preconditions.checkArgument(id != null, "须传递更新id");
        Preconditions.checkArgument(content != null, "须传递更新的content");
        FeatureRouter featureRouter = featureRouterService.getById(id);
        if (featureRouter.getLabelId() == null) {
            return false;
        }
        ChatInfoPo toBeUpdate = UpdateEntity.of(ChatInfoPo.class, featureRouter.getLabelId());
        toBeUpdate.setMappingContent(content);
        int update = chatInfoMapper.update(toBeUpdate);
        return update == 1;
    }
}
