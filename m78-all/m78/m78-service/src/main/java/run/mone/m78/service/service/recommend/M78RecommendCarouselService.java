package run.mone.m78.service.service.recommend;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.xiaomi.youpin.infra.rpc.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import run.mone.m78.api.bo.ListResult;
import run.mone.m78.api.bo.recommendCarousel.ListQryReq;
import run.mone.m78.api.bo.recommendCarousel.M78RecommendCarouselInfo;
import run.mone.m78.service.common.GsonUtils;
import run.mone.m78.service.dao.entity.M78Bot;
import run.mone.m78.service.dao.entity.M78RecommendCarouselPo;
import run.mone.m78.service.dao.mapper.M78RecommendCarouselMapper;
import run.mone.m78.service.service.bot.BotService;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j

/**
 * M78RecommendCarouselService类提供了对推荐轮播信息的管理功能。
 * 该类继承自ServiceImpl，并使用了Spring的@Service注解和Lombok的@Slf4j注解。
 *
 * 主要功能包括：
 * - 新增推荐轮播信息
 * - 更新推荐轮播信息
 * - 根据查询条件分页查询推荐轮播信息列表
 * - 更新推荐轮播图的显示状态
 *
 * 该类依赖于BotService来获取与轮播图相关的机器人信息。
 */

public class M78RecommendCarouselService extends ServiceImpl<M78RecommendCarouselMapper, M78RecommendCarouselPo> {

    private static int DISPLAY_STATUS_ON = 1;
    private static Gson gson = GsonUtils.gson;

    @Resource
    private BotService botService;

    /**
     * 新增推荐轮播信息
     *
     * @param info 推荐轮播信息对象
     * @return 操作结果，包含布尔值，表示是否成功
     */
    //新增, 入参是M78RecommendCarouselInfo
    public Result<Boolean> addCarousel(M78RecommendCarouselInfo info) {
        M78RecommendCarouselPo po = M78RecommendCarouselPo.builder()
                .title(info.getTitle())
                .recommendReasons(gson.toJson(info.getRecommendReasons()))
                .type(info.getType())
                .displayStatus(DISPLAY_STATUS_ON)
                .backgroundUrl(info.getBackgroundUrl())
                .botId(info.getBotId())
                .ctime(System.currentTimeMillis())
                .utime(System.currentTimeMillis())
                .build();
        return Result.success(save(po));
    }

    /**
     * 更新推荐轮播信息
     *
     * @param info 包含推荐轮播信息的对象
     * @return 更新操作的结果，成功返回true，失败返回false
     */
    //更新，入参是M78RecommendCarouselInfo
    public Result<Boolean> updateCarousel(M78RecommendCarouselInfo info) {

        M78RecommendCarouselPo po = M78RecommendCarouselPo.builder()
                .id(info.getId())
                .title(info.getTitle())
                .recommendReasons(gson.toJson(info.getRecommendReasons()))
                .type(info.getType())
                .displayStatus(info.getDisplayStatus())
                .backgroundUrl(info.getBackgroundUrl())
                .botId(info.getBotId())
                .utime(System.currentTimeMillis())
                .build();
        return Result.success(updateById(po));
    }

    /**
     * 根据请求参数中的类型和显示状态分页查询推荐轮播信息列表
     *
     * @param req 包含查询条件的请求参数
     * @return 包含推荐轮播信息列表的结果对象
     */
    //列表，入参是ListQryReq req，需要根据req里面的type，分页查询，返回Result<ListResult<M78RecommendCarouselInfo>>
    public Result<ListResult<M78RecommendCarouselInfo>> listCarousels(ListQryReq req) {

        QueryWrapper queryWrapper = new QueryWrapper();
        if (req.getType() != null) {
            queryWrapper.eq("type", req.getType());
        }
        if (req.getDisplayStatus() != null) {
            queryWrapper.eq("display_status", req.getDisplayStatus());
        }
        if (req.getTitle() != null && !req.getTitle().isEmpty()) {
            queryWrapper.like("title", req.getTitle());
        }


        Page<M78RecommendCarouselPo> page = new Page<>(req.getPageNum(), req.getPageSize());
        Page<M78RecommendCarouselPo> resultPage = page(page, queryWrapper);

        List<M78Bot> bots = botService.getBotsByIds(resultPage.getRecords().stream().map(M78RecommendCarouselPo::getBotId).collect(Collectors.toList()));
        Map<Long, M78Bot> botMap = bots.stream()
                .collect(Collectors.toMap(M78Bot::getId, bot -> bot));

        List<M78RecommendCarouselInfo> carouselInfoList = resultPage.getRecords().stream()
                .map(po -> {
                    M78RecommendCarouselInfo info = new M78RecommendCarouselInfo();
                    info.setId(po.getId());
                    info.setTitle(po.getTitle());
                    info.setRecommendReasons(gson.fromJson(po.getRecommendReasons(), new TypeToken<List<String>>() {
                    }.getType()));
                    info.setType(po.getType());
                    info.setDisplayStatus(po.getDisplayStatus());
                    info.setBackgroundUrl(po.getBackgroundUrl());
                    info.setBotId(po.getBotId());
                    info.setCtime(po.getCtime());
                    info.setUtime(po.getUtime());
                    info.setBotName(botMap.get(po.getBotId()).getName());
                    info.setBotCuser(botMap.get(po.getBotId()).getCreator());
                    info.setBotAvatar(botMap.get(po.getBotId()).getAvatarUrl());
                    info.setBotPermissions(botMap.get(po.getBotId()).getPermissions());
                    return info;
                })
                .collect(Collectors.toList());

        ListResult<M78RecommendCarouselInfo> listResult = new ListResult<>();
        listResult.setTotalPage(resultPage.getTotalPage());
        listResult.setList(carouselInfoList);

        return Result.success(listResult);
    }

    /**
     * 更新推荐轮播图的显示状态
     *
     * @param info 包含轮播图信息的对象
     * @return 更新操作的结果，成功返回true
     */
    public Result<Boolean> updateDisplayStatus(M78RecommendCarouselInfo info) {
        M78RecommendCarouselPo po = M78RecommendCarouselPo.builder()
                .id(info.getId())
                .displayStatus(info.getDisplayStatus())
                .build();
        updateById(po, true);
        return Result.success(true);
    }

}