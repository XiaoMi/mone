package run.mone.m78.service.service.plugins.dubbo;

import com.google.common.base.Preconditions;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.xiaomi.youpin.infra.rpc.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.dubbo.config.annotation.DubboService;
import run.mone.m78.api.BotPluginProvider;
import run.mone.m78.api.bo.plugins.BotPluginDTO;
import run.mone.m78.api.bo.plugins.PluginReq;
import run.mone.m78.api.constant.CommonConstant;
import run.mone.m78.service.dao.entity.M78BotPlugin;
import run.mone.m78.service.service.plugins.BotPluginService;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.mybatisflex.core.query.QueryMethods.noCondition;
import static run.mone.m78.service.dao.entity.table.M78BotPluginTableDef.M78_BOT_PLUGIN;

/**
 * @author HawickMason@xiaomi.com
 * @date 3/5/24 14:28
 */
@DubboService(timeout = CommonConstant.DEF_DUBBO_TIMEOUT, group = "${dubbo.group}", version = "1.0")
@Slf4j
public class BotPluginProviderImpl implements BotPluginProvider {

    @Resource
    private BotPluginService botPluginService;

    @Override
    public Pair<Long, List<BotPluginDTO>> getBotPlugins(PluginReq req) {
        log.info("listing plugins with req:{}", req);
        QueryWrapper queryWrapper = QueryWrapper.create()
                .select(M78_BOT_PLUGIN.DEFAULT_COLUMNS)
                .from(M78_BOT_PLUGIN)
                .where(req.getId() != null ? M78_BOT_PLUGIN.ID.eq(req.getId()) : noCondition())
                .and(StringUtils.isNotBlank(req.getName()) ? M78_BOT_PLUGIN.NAME.eq(req.getName()) : noCondition())
                .and(StringUtils.isNotBlank(req.getUserName()) ? M78_BOT_PLUGIN.USER_NAME.eq(req.getUserName()) : noCondition());
        List<BotPluginDTO> res = new ArrayList<>();
        if (req.getPageNum() != null && req.getPageSize() != null) {
            // 分页查询
            Result<Page<BotPluginDTO>> pageDataResult = botPluginService.listM78BotPluginsByRequest(req, null);
            if (pageDataResult != null && pageDataResult.getData() != null && pageDataResult.getData().hasRecords()) {
                res = new ArrayList<>(pageDataResult.getData().getRecords());
                log.info("get plugin list by page: {}, with rows:{}", req, res.size());
                return Pair.of(pageDataResult.getData().getTotalRow(), res);
            }
        } else {
            // 全量查询
            List<M78BotPlugin> list = botPluginService.list(queryWrapper);
            res = list.stream()
                    .map(M78BotPlugin::toDTO)
                    .collect(Collectors.toList());
        }
        log.info("get plugin list : {}, with rows:{}", req, res.size());
        return Pair.of((long) res.size(), res);
    }

    @Override
    public BotPluginDTO getBotPluginById(Long id) {
        log.info("getBotPluginById:{}", id);
        Preconditions.checkArgument(id != null, "须传递plugin的id");
        M78BotPlugin pById = botPluginService.getById(id);
        BotPluginDTO res = pById.toDTO();
        log.info("getBotPluginById, res:{}", res);
        return res;
    }
}
