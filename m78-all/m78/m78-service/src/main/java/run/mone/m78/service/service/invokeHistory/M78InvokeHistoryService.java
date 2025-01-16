package run.mone.m78.service.service.invokeHistory;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.xiaomi.data.push.redis.Redis;
import com.xiaomi.youpin.infra.rpc.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import run.mone.m78.api.bo.ListResult;
import run.mone.m78.api.bo.invokeHistory.*;
import run.mone.m78.service.dao.entity.*;
import run.mone.m78.service.dao.mapper.*;
import run.mone.m78.service.service.fileserver.manager.impl.FdsServer;

import javax.annotation.Resource;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static run.mone.m78.api.bo.invokeHistory.InvokeTypeEnum.*;
import static run.mone.m78.service.exceptions.ExCodes.STATUS_BAD_REQUEST;
import static run.mone.m78.service.exceptions.ExCodes.STATUS_NOT_FOUND;
import static run.mone.m78.service.service.invokeHistory.RedisUtils.ONE_DAY_SECONDS;
import static run.mone.m78.service.service.invokeHistory.RedisUtils.getCurrentDateTimestamp;

@Service
@Slf4j

/**
 * M78InvokeHistoryService类提供了对调用历史记录的管理和统计功能。
 * <p>
 * 该类继承自ServiceImpl，主要职责包括：
 * <ul>
 *     <li>定时任务：每天零点五分触发的初始化方法，用于删除过期的历史记录并统计前一天的调用情况。</li>
 *     <li>记录调用历史：提供记录插件、流程和机器人调用历史的功能。</li>
 *     <li>查询功能：提供查询调用历史详情和每日调用统计信息的接口。</li>
 *     <li>数据修复：提供修复头像URL的功能。</li>
 * </ul>
 * <p>
 * 该类依赖于多个Mapper和Redis工具类，通过线程池异步执行部分操作，确保高效的并发处理能力。
 * <p>
 * 注：所有方法均遵循JavaDoc标准进行注释，确保代码的可读性和可维护性。
 */

public class M78InvokeHistoryService extends ServiceImpl<M78InvokeHistoryDetailMapper, M78InvokeHistoryDetailPo> {

    private static final Integer HISTORY_DETAILS_MAX_DAY = 30;

    private static final String LOCK_KEY = "M78InvokeHistoryServiceLock";

    @Autowired
    private Redis redis;

    @Resource
    private M78InvokeSummaryPerdayMapper m78InvokeSummaryPerdayMapper;

    @Resource
    private M78BotMapper botMapper;

    @Resource
    private M78BotPluginOrgMapper pluginOrgMapper;

    @Resource
    private M78WorkspaceMapper workspaceMapper;

    @Resource
    private M78FlowBaseMapper m78FlowBaseMapper;

    @Resource
    private M78BotPluginMapper m78BotPluginMapper;

    private static ThreadPoolExecutor historyDetailExecutor = new ThreadPoolExecutor(10, 100, TimeUnit.SECONDS.toMillis(30), TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(300));

    /**
     * 每天零点五分触发的初始化方法
     * <p>
     * 该方法在每天的00:05被定时调用，首先尝试获取分布式锁以确保方法的唯一性。
     * 如果获取锁失败，则记录日志并返回。成功获取锁后，执行以下操作：
     * 1. 删除历史记录中超过最大天数的记录。
     * 2. 统计前一天的调用情况，包括机器人调用和流程调用。
     *
     * @throws Exception 可能抛出异常，具体异常信息会被记录到日志中
     */
    //写一个init方法，每天零点5分的时候触发调用
    @Scheduled(cron = "0 5 0 * * ?")
//    @PostConstruct
    public void init() {
        if (!RedisUtils.acquireLock(redis, LOCK_KEY)) {
            log.info("ignore at 00:05");
            return;
        }
        log.info("Init method triggered at 00:05 every day");

        //删除HISTORY_DETAILS_MAX_DAY前的记录
        try {
            deleteOldHistoryDetails();
        } catch (Exception e) {
            log.error("[M78InvokeHistoryService], failed to deleteOldHistoryDetails, error: {}", e.getMessage());
        }
        try {
            deleteOldSummaryPerday();
        } catch (Exception e) {
            log.error("[M78InvokeHistoryService], failed to deleteOldSummaryPerday, error: {}", e.getMessage());
        }

        //统计前一天的调用情况
        try {
            summarizeBotInvokePerday(1);
        } catch (Exception e) {
            log.error("[M78InvokeHistoryService], failed to summarizeBotInvokePerday, error: {}", e.getMessage());
        }
        try {
            summarizeFlowInvokePerday(1);
        } catch (Exception e) {
            log.error("[M78InvokeHistoryService], failed to summarizeFlowInvokePerday, error: {}", e.getMessage());
        }
    }

    /**
     * 记录插件的调用历史详情
     *
     * @param pluginId 插件的唯一标识
     * @param userName 调用插件的用户名
     * @param inputs   插件调用时的输入参数
     * @param outputs  插件调用后的输出结果
     * @param from     调用来源标识
     */
    public void pluginHistoryDetail(Long pluginId, String userName, String inputs, String outputs, int from) {
        historyDetailExecutor.execute(() -> {
            M78InvokeHistoryDetailInfo info = M78InvokeHistoryDetailInfo.builder()
                    .relateId(pluginId)
                    .invokeUserName(userName)
                    .type(PLUGIN.getCode())
                    .inputs(inputs)
                    .outputs(outputs)
                    .invokeWay(from)
                    .invokeTime(System.currentTimeMillis())
                    .build();
            newHistoryDetail(info);
        });
    }

    /**
     * 记录流程历史详情
     *
     * @param flowId   流程ID
     * @param userName 调用用户名
     * @param inputs   调用输入参数
     * @param outputs  调用输出结果
     * @param from     调用来源标识
     * @throws Throwable 可能抛出异常
     */
    public void flowHistoryDetail(Long flowId, String userName, String inputs, String outputs, int from) {
        try {
            historyDetailExecutor.execute(() -> {
                M78InvokeHistoryDetailInfo info = M78InvokeHistoryDetailInfo.builder()
                        .relateId(flowId)
                        .invokeUserName(userName)
                        .type(FLOW.getCode())
                        .inputs(inputs)
                        .outputs(outputs)
                        .invokeWay(from)
                        .invokeTime(System.currentTimeMillis())
                        .build();
                newHistoryDetail(info);
            });
        } catch (Throwable e) {
            log.error("failed to submit to flowHistoryDetail, error: {}", e.getMessage());
        }
    }

    /**
     * 记录机器人调用历史的详细信息
     *
     * @param botId    机器人的唯一标识
     * @param userName 调用机器人的用户名
     * @param inputs   调用时的输入内容
     * @param outputs  调用后的输出内容
     * @param from     调用来源标识
     *                 <p>
     *                 该方法会异步执行记录操作，并缓存当天的调用次数以供热度统计。
     *                 如果调用次数为1，则设置缓存的过期时间为一天。
     */
    public void probotHistoryDetail(Long botId, String userName, String inputs, String outputs, int from) {
        try {
            historyDetailExecutor.execute(() -> {
                M78InvokeHistoryDetailInfo info = M78InvokeHistoryDetailInfo.builder()
                        .relateId(botId)
                        .invokeUserName(userName)
                        .type(PROBOT.getCode())
                        .inputs(inputs)
                        .outputs(outputs)
                        .invokeWay(from)
                        .invokeTime(System.currentTimeMillis())
                        .build();
                newHistoryDetail(info);

                //缓存当天的调用，给热度用
                String key = RedisUtils.getProbotHotKey(botId);
                long value = redis.incr(key);
                if (value == 1) {
                    redis.expire(key, ONE_DAY_SECONDS);
                }
            });
        } catch (Throwable e) {
            log.error("failed to submit to probotHistoryDetail, error: {}", e.getMessage());
        }
    }

    /**
     * 查看某个bot之前n天的调用记录，并组装InvokePerdayListReq后调用listPerdayInfo方法
     *
     * @param req 包含请求参数的对象，包含相关的bot ID和查询天数
     * @return 返回包含调用记录的结果，类型为ListResult<M78InvokeSummaryPerdayInfo>
     */
    //查看某个bot之前n天的的调用记录，组装InvokePerdayListReq后调用listPerdayInfo
    public Result<ListResult<M78InvokeSummaryPerdayInfo>> listPerdayInfoByBotId(InvokePerdayListByBotIdReq req) {
        if (req.getDaysAgo() == null || req.getDaysAgo() == 0) {
            req.setDaysAgo(7);
        }

        if (req.getDaysAgo() > HISTORY_DETAILS_MAX_DAY) {
            return Result.fail(STATUS_BAD_REQUEST, "history exists only " + HISTORY_DETAILS_MAX_DAY + " days");
        }

        long startOfDay = getCurrentDateTimestamp() - TimeUnit.DAYS.toMillis(req.getDaysAgo());
        long endOfDay = getCurrentDateTimestamp() - 1;

        InvokePerdayListReq listReq = InvokePerdayListReq.builder()
                .relateId(req.getRelateId())
                .type(PROBOT.getCode())
                .invokeTimeBegin(startOfDay)
                .invokeTimeEnd(endOfDay)
                .page(1)
                .pageSize(HISTORY_DETAILS_MAX_DAY)
                .build();

        return listPerdayInfo(listReq);
    }

    /**
     * 查询M78InvokeHistoryDetailPo的历史记录
     *
     * @param req 查询请求参数，包括分页信息和筛选条件
     * @return 查询结果，包含分页信息和历史记录列表
     */
    //写一个列表查询的接口，查询M78InvokeHistoryDetailPo。入参是InvokeHistoryListReq。
    //按入参是InvokeHistoryListReq里面的page和pageSize分页，按type筛选，如果relateId传了并大于0，按relateId筛选。
    //如果invokeTimeBegin传了并大于0，则invoke_time大于等于invokeTimeBegin，如果invokeTimeEnd传了并大于0，则invoke_time小于于等于invokeTimeEnd
    //如果传了orderBy并不为空，则添加排序，否则按invoke_day逆序
    //返回结果是Result<ListResult<M78InvokeHistoryDetailInfo>>
    public Result<ListResult<M78InvokeHistoryDetailInfo>> listHistoryDetails(InvokeHistoryListReq req) {
        QueryWrapper queryWrapper = new QueryWrapper();
        if (req.getType() != null) {
            queryWrapper.eq("type", req.getType());
        }
        if (req.getRelateId() != null && req.getRelateId() > 0) {
            queryWrapper.eq("relate_id", req.getRelateId());
        }
        if (req.getInvokeTimeBegin() != null && req.getInvokeTimeBegin() > 0) {
            queryWrapper.ge("invoke_time", req.getInvokeTimeBegin());
        }
        if (req.getInvokeTimeEnd() != null && req.getInvokeTimeEnd() > 0) {
            queryWrapper.le("invoke_time", req.getInvokeTimeEnd());
        }
        if (StringUtils.isNotEmpty(req.getInvokeUserName())) {
            queryWrapper.like("invoke_user_name", req.getInvokeUserName());
        }
        if (req.getOrderBy() != null && !req.getOrderBy().isEmpty()) {
            queryWrapper.orderBy(req.getOrderBy(), req.isAsc());
        } else {
            queryWrapper.orderBy("invoke_time", false);
        }

        Page<M78InvokeHistoryDetailPo> page = new Page<>(req.getPage(), req.getPageSize());
        Page<M78InvokeHistoryDetailPo> resultPage = page(page, queryWrapper);
        List<M78InvokeHistoryDetailInfo> infoList = resultPage.getRecords().stream()
                .map(po -> M78InvokeHistoryDetailInfo.builder()
                        .id(po.getId())
                        .type(po.getType())
                        .relateId(po.getRelateId())
                        .inputs(po.getInputs())
                        .outputs(po.getOutputs())
                        .invokeTime(po.getInvokeTime())
                        .invokeWay(po.getInvokeWay())
                        .invokeUserName(po.getInvokeUserName())
                        .build())
                .collect(Collectors.toList());

        ListResult<M78InvokeHistoryDetailInfo> listResult = new ListResult<>();
        listResult.setTotalPage(resultPage.getTotalPage());
        listResult.setList(infoList);
        listResult.setPage(req.getPage());
        listResult.setPageSize(req.getPageSize());

        return Result.success(listResult);
    }


    /**
     * 查询每日调用信息的列表
     *
     * @param req 查询请求参数，包括分页信息和筛选条件
     * @return 查询结果，包含分页信息和每日调用信息列表
     */
    //写一个列表查询的接口，入参是InvokePerdayListReq，按里面的page和pageSize分页，按type筛选，如果relateId传了并大于0，按relateId筛选
    //如果invokeTimeBegin传了并大于0，则invoke_day大于等于invokeTimeBegin，如果invokeTimeEnd传了并大于0，则invoke_day小于于等于invokeTimeEnd
    //如果传了orderBy并不为空，则添加排序，否则按invoke_day逆序
    public Result<ListResult<M78InvokeSummaryPerdayInfo>> listPerdayInfo(InvokePerdayListReq req) {
        QueryWrapper queryWrapper = new QueryWrapper();
        if (req.getType() != null) {
            queryWrapper.eq("type", req.getType());
        }
        if (req.getRelateId() != null && req.getRelateId() > 0) {
            queryWrapper.eq("relate_id", req.getRelateId());
        }
        if (req.getInvokeTimeBegin() != null && req.getInvokeTimeBegin() > 0) {
            queryWrapper.ge("invoke_day", req.getInvokeTimeBegin());
        }
        if (req.getInvokeTimeEnd() != null && req.getInvokeTimeEnd() > 0) {
            queryWrapper.le("invoke_day", req.getInvokeTimeEnd());
        }
        if (req.getOrderBy() != null && !req.getOrderBy().isEmpty()) {
            queryWrapper.orderBy(req.getOrderBy(), req.isAsc());
        } else {
            queryWrapper.orderBy("invoke_day", true);
        }

        Page<M78InvokeSummaryPerdayPo> page = new Page<>(req.getPage(), req.getPageSize());
        Page<M78InvokeSummaryPerdayPo> resultPage = m78InvokeSummaryPerdayMapper.paginate(page, queryWrapper);
        List<M78InvokeSummaryPerdayInfo> infoList = resultPage.getRecords().stream()
                .map(po -> M78InvokeSummaryPerdayInfo.builder()
                        .id(po.getId())
                        .type(po.getType())
                        .relateId(po.getRelateId())
                        .invokeCounts(po.getInvokeCounts())
                        .invokeUsers(po.getInvokeUsers())
                        .invokeDay(po.getInvokeDay())
                        .build())
                .collect(Collectors.toList());

        ListResult<M78InvokeSummaryPerdayInfo> listResult = new ListResult<>();
        listResult.setTotalPage(resultPage.getTotalPage());
        listResult.setList(infoList);
        listResult.setPage(req.getPage());
        listResult.setPageSize(req.getPageSize());

        return Result.success(listResult);
    }


    private void newHistoryDetail(M78InvokeHistoryDetailInfo info) {
        try {
            M78InvokeHistoryDetailPo po = M78InvokeHistoryDetailPo.builder()
                    .type(info.getType())
                    .relateId(info.getRelateId())
                    .inputs(info.getInputs())
                    .outputs(info.getOutputs())
                    .invokeTime(info.getInvokeTime())
                    .invokeWay(info.getInvokeWay())
                    .invokeUserName(info.getInvokeUserName())
                    .build();
            save(po);
        } catch (Throwable e) {
            log.error("[M78InvokeHistoryService.newHistoryDetail], save error: {}", e.getMessage());
        }
    }

    //删除HISTORY_DETAILS_MAX_DAY天之前的M78InvokeHistoryDetail
    private void deleteOldHistoryDetails() {
        long daysAgo = getCurrentDateTimestamp() - TimeUnit.DAYS.toMillis(HISTORY_DETAILS_MAX_DAY);
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.lt("invoke_time", daysAgo);
        try {
            remove(queryWrapper);
            log.info("Deleted history details older than HISTORY_DETAILS_MAX_DAY days");
        } catch (Exception e) {
            log.error("Error deleting old history details: {}", e.getMessage());
        }
    }

    //删除HISTORY_DETAILS_MAX_DAY天前的M78InvokeSummaryPerdayPo，调用m78InvokeSummaryPerdayMapper
    private void deleteOldSummaryPerday() {
        long daysAgo = getCurrentDateTimestamp() - TimeUnit.DAYS.toMillis(HISTORY_DETAILS_MAX_DAY);
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.lt("invoke_day", daysAgo);
        try {
            m78InvokeSummaryPerdayMapper.deleteByQuery(queryWrapper);
            log.info("Deleted summary per day records older than HISTORY_DETAILS_MAX_DAY days");
        } catch (Exception e) {
            log.error("Error deleting old summary per day records: {}", e.getMessage());
        }
    }

    /**
     * 汇总指定天数前的机器人调用情况
     *
     * @param daysAgo 指定的天数，表示要汇总的日期为当前日期减去该天数
     *                <p>
     *                该方法从botService获取所有未被删除的机器人，遍历每个机器人的ID，根据ID从M78InvokeHistoryDetail获取前一天0点到24点的调用次数。如果没有找到调用记录，则调用次数默认为0。最后，生成一条记录并插入到m78InvokeSummaryPerday中。
     */
    //从botService获取所有没被删除的bot，遍历botId，根据botId从M78InvokeHistoryDetail获取前一天0点到24点的调用次数和，查不到的话则调用次数为0，最终产生一条记录插入m78InvokeSummaryPerday
    public void summarizeBotInvokePerday(int daysAgo) {
        List<M78Bot> bots = botMapper.selectListByQuery(new QueryWrapper().eq("deleted", 0));
        long startOfDay = getCurrentDateTimestamp() - TimeUnit.DAYS.toMillis(daysAgo);
        long endOfDay = startOfDay + TimeUnit.DAYS.toMillis(1) - 1;

        for (M78Bot bot : bots) {
            Long botId = bot.getId();
            long invokeCount = count(new QueryWrapper()
                    .eq("relate_id", botId)
                    .eq("type", PROBOT.getCode()) // 1 for bot type
                    .between("invoke_time", startOfDay, endOfDay));

            long activeUserCount = 0;
            if (invokeCount > 0) {
                // 统计活跃用户数，按用户名去重
                List<M78InvokeHistoryDetailPo> list = list(new QueryWrapper()
                        .select("DISTINCT invoke_user_name")
                        .eq("relate_id", botId)
                        .eq("type", PROBOT.getCode()) // 1 for bot type
                        .between("invoke_time", startOfDay, endOfDay));
                activeUserCount = list.size();
            }

            M78InvokeSummaryPerdayPo summary = M78InvokeSummaryPerdayPo.builder()
                    .type(PROBOT.getCode()) // 1 for bot type
                    .relateId(botId)
                    .invokeCounts(invokeCount)
                    .invokeUsers(activeUserCount)
                    .invokeDay(startOfDay)
                    .build();

            m78InvokeSummaryPerdayMapper.insert(summary);
        }
    }

    /**
     * 统计指定天数前的流量调用情况
     *
     * @param daysAgo 指定的天数，表示从当前日期向前推算的天数
     *                <p>
     *                该方法查询状态为0的流量记录，并统计在指定日期范围内的调用次数和活跃用户数。
     *                首先计算出指定日期的开始和结束时间，然后遍历所有流量记录，统计每个流量的调用次数。
     *                如果调用次数大于0，则进一步统计活跃用户数，最后将统计结果插入到每日调用汇总表中。
     */
    public void summarizeFlowInvokePerday(int daysAgo) {
        List<FlowBasePo> flows = m78FlowBaseMapper.selectListByQuery(new QueryWrapper().eq("state", 0));
        long startOfDay = getCurrentDateTimestamp() - TimeUnit.DAYS.toMillis(daysAgo);
        long endOfDay = startOfDay + TimeUnit.DAYS.toMillis(1) - 1;

        for (FlowBasePo flow : flows) {
            Integer flowId = flow.getId();
            long invokeCount = count(new QueryWrapper()
                    .eq("relate_id", flowId)
                    .eq("type", FLOW.getCode())
                    .between("invoke_time", startOfDay, endOfDay));

            long activeUserCount = 0;
            if (invokeCount > 0) {
                // 统计活跃用户数，按用户名去重
                List<M78InvokeHistoryDetailPo> list = list(new QueryWrapper()
                        .select("DISTINCT invoke_user_name")
                        .eq("relate_id", flowId)
                        .eq("type", FLOW.getCode())
                        .between("invoke_time", startOfDay, endOfDay));
                activeUserCount = list.size();
            }

            M78InvokeSummaryPerdayPo summary = M78InvokeSummaryPerdayPo.builder()
                    .type(FLOW.getCode())
                    .relateId(flowId.longValue())
                    .invokeCounts(invokeCount)
                    .invokeUsers(activeUserCount)
                    .invokeDay(startOfDay)
                    .build();

            m78InvokeSummaryPerdayMapper.insert(summary);
        }
    }

    /**
     * 查询每日调用统计信息
     *
     * @param req 查询条件，包括开始和结束时间
     * @return 每日调用统计信息列表，如果没有数据则返回失败状态
     */
    public Result<List<AllM78InvokeSummaryPerdayInfo>> listAllPerdayInfos(InvokePerdayListByAdminReq req) {
        QueryWrapper queryWrapper = new QueryWrapper();
        if (req.getInvokeTimeBegin() != null && req.getInvokeTimeBegin() > 0) {
            queryWrapper.ge("invoke_day", req.getInvokeTimeBegin());
        }
        if (req.getInvokeTimeEnd() != null && req.getInvokeTimeEnd() > 0) {
            queryWrapper.le("invoke_day", req.getInvokeTimeEnd());
        }
        //bot的调用统计
        List<M78InvokeSummaryPerdayPo> m78InvokeSummaryPerdayPoList = m78InvokeSummaryPerdayMapper.selectListByQuery(queryWrapper);
        if (m78InvokeSummaryPerdayPoList == null || m78InvokeSummaryPerdayPoList.isEmpty()) {
            return Result.fail(STATUS_NOT_FOUND, "no data");
        }
        List<AllM78InvokeSummaryPerdayInfo> M78InvokeSummaryPerdayInfoList = m78InvokeSummaryPerdayPoList.stream().collect(Collectors.groupingBy(M78InvokeSummaryPerdayPo::getType))
                .entrySet().stream().map(entry -> {
                    Integer type = entry.getKey();
                    List<M78InvokeSummaryPerdayPo> list = entry.getValue();
                    long allCounts = list.stream().mapToLong(po -> po.getInvokeCounts() != null ? po.getInvokeCounts() : 0L).sum();
                    long allUsers = list.stream().mapToLong(po -> po.getInvokeUsers() != null ? po.getInvokeUsers() : 0L).sum();
                    return AllM78InvokeSummaryPerdayInfo.builder().type(type).allInvokeCounts(allCounts).allInvokeUsers(allUsers).build();
                }).toList();

        return Result.success(M78InvokeSummaryPerdayInfoList);
    }

    /**
     * 根据管理员请求列出每日调用信息
     *
     * @param req 包含查询条件的请求对象
     * @return 查询结果，包含每日调用信息的列表和分页信息
     */
    public Result<ListResult<M78InvokeSummaryPerdayInfo>> listPerdayInfosByAdmin(InvokePerdayListReq req) {
        QueryWrapper queryWrapper = new QueryWrapper();
        if (req.getType() != null) {
            queryWrapper.eq("type", req.getType());
        }
        if (req.getInvokeTimeBegin() != null && req.getInvokeTimeBegin() > 0) {
            queryWrapper.ge("invoke_day", req.getInvokeTimeBegin());
        }
        if (req.getInvokeTimeEnd() != null && req.getInvokeTimeEnd() > 0) {
            queryWrapper.le("invoke_day", req.getInvokeTimeEnd());
        }
        if (req.getRelateName() != null && !req.getRelateName().isEmpty()) {
            //按照名称模糊查询出bot表、flow表、plugin表中的数据
            QueryWrapper queryWrapperByBotName = new QueryWrapper();
            queryWrapperByBotName.like("name", req.getRelateName());
            List<Long> botIdList = botMapper.selectListByQuery(queryWrapperByBotName).stream().filter(bot -> bot.getDeleted() != 1).map(M78Bot::getId).toList();
            QueryWrapper queryWrapperByFLowName = new QueryWrapper();
            queryWrapperByFLowName.like("name", req.getRelateName());
            List<Long> flowIdList = m78FlowBaseMapper.selectListByQuery(queryWrapperByFLowName).stream().map(FlowBasePo::getId).map(Integer::longValue).toList();
            QueryWrapper queryWrapperByPluginName = new QueryWrapper();
            queryWrapperByPluginName.like("name", req.getRelateName());
            List<Long> pluginIdList = m78BotPluginMapper.selectListByQuery(queryWrapperByPluginName).stream().map(M78BotPlugin::getId).toList();
            if (req.getType() != null) {
                switch (req.getType()) {
                    case 1:
                        queryWrapper.in("relate_id", botIdList);
                        break;
                    case 2:
                        queryWrapper.in("relate_id", flowIdList);
                        break;
                    case 3:
                        queryWrapper.in("relate_id", pluginIdList);
                        break;
                }
            } else {
                List<Long> allIdList = Stream.of(botIdList, flowIdList, pluginIdList)
                        .filter(list -> list != null && !list.isEmpty())
                        .flatMap(List::stream)
                        .toList();
                if (!allIdList.isEmpty()) {
                    queryWrapper.in("relate_id", allIdList);
                }
            }
        }
        if (req.getOrderBy() != null && !req.getOrderBy().isEmpty()) {
            queryWrapper.orderBy(req.getOrderBy(), req.isAsc());
        } else {
            queryWrapper.orderBy("id", req.isAsc());
        }
        Page<M78InvokeSummaryPerdayPo> page = new Page<>(req.getPage(), req.getPageSize());
        Page<M78InvokeSummaryPerdayPo> resultPage = m78InvokeSummaryPerdayMapper.paginate(page, queryWrapper);
        if (resultPage == null) {
            return Result.fail(STATUS_NOT_FOUND, "no data");
        }

        List<M78InvokeSummaryPerdayInfo> m78InvokeSummaryPerdayInfoList = resultPage.getRecords().stream().map(this::M78InvokeSummaryPerdayPoToInfo).toList();

        ListResult<M78InvokeSummaryPerdayInfo> listResult = new ListResult<>();
        listResult.setTotalPage(resultPage.getTotalPage());
        listResult.setList(m78InvokeSummaryPerdayInfoList);
        listResult.setPage(req.getPage());
        listResult.setPageSize(req.getPageSize());
        return Result.success(listResult);
    }

    private M78InvokeSummaryPerdayInfo M78InvokeSummaryPerdayPoToInfo(M78InvokeSummaryPerdayPo po) {
        String name = "";
        String avatarUrl = "";
        switch (po.getType()) {
            case 1:
                M78Bot m78Bot = botMapper.selectOneById(po.getRelateId());
                if (m78Bot != null) {
                    name = m78Bot.getName();
                    avatarUrl = m78Bot.getAvatarUrl();
                }
            case 2:
                FlowBasePo flowBasePo = m78FlowBaseMapper.selectOneById(po.getRelateId());
                if (flowBasePo != null) {
                    name = flowBasePo.getName();
                    avatarUrl = flowBasePo.getAvatarUrl();
                }
            case 3:
                M78BotPlugin m78BotPlugin = m78BotPluginMapper.selectOneById(po.getRelateId());
                if (m78BotPlugin != null) {
                    name = m78BotPlugin.getName();
                    avatarUrl = m78BotPlugin.getAvatarUrl();
                }
        }
        return M78InvokeSummaryPerdayInfo.builder().id(po.getRelateId()).relateId(po.getRelateId())
                .invokeDay(po.getInvokeDay()).invokeCounts(po.getInvokeCounts())
                .invokeUsers(po.getInvokeUsers()).type(po.getType()).relateName(name).avatarUrl(avatarUrl).build();
    }

    /**
     * 修复机器人头像的URL
     * <p>
     * 此方法从数据库中查询所有头像URL包含"fds"的机器人，并检查其头像URL的过期时间。
     * 如果头像URL的过期时间小于指定值，则生成新的URL并更新数据库中的头像URL。
     *
     * @return 无返回值
     */
    public void fixBotImageUrl() {
        List<M78Bot> bots = botMapper.selectListByQuery(new QueryWrapper().like("avatar_url", "fds"));
        bots.stream().forEach(it -> {
            String url = it.getAvatarUrl();
            try {
                URL urlObj = new URL(url);
                // 提取域名后的第一个字段
                String path = urlObj.getPath();
                String bucketName = path.split("/")[1];
                String key = path.split("/")[2];

                // 提取Expires的值
                String query = urlObj.getQuery();
                String[] pairs = query.split("&");
                String time = "0";
                for (String pair : pairs) {
                    int idx = pair.indexOf("=");
                    String k = URLDecoder.decode(pair.substring(0, idx), StandardCharsets.UTF_8);
                    String v = URLDecoder.decode(pair.substring(idx + 1), StandardCharsets.UTF_8);
                    if ("Expires".equals(k)) {
                        time = v;  // 输出: 1729651922841
                        break;
                    }
                }

                if (Long.valueOf(time) < 2729651922841l) {
                    String newUrl = FdsServer.expireUrl(url, bucketName, key);
                    if (StringUtils.isNotEmpty(newUrl)) {
                        it.setAvatarUrl(newUrl);
                        botMapper.update(it);
                    }
                }

            } catch (Exception e) {
                log.error("fixImageUrl, url: {}, msg: {}", url, e.getMessage());
            }
        });

    }

    /**
     * 修复插件的头像URL
     * <p>
     * 该方法查询所有头像URL中包含"fds"的插件，并检查其URL的过期时间。如果过期时间小于指定值，则更新为新的URL。
     *
     * @return 无返回值
     */
    public void fixPluginImageUrl() {
        List<M78BotPluginOrg> bots = pluginOrgMapper.selectListByQuery(new QueryWrapper().like("avatar_url", "fds"));
        bots.stream().forEach(it -> {
            String url = it.getAvatarUrl();
            try {
                URL urlObj = new URL(url);
                // 提取域名后的第一个字段
                String path = urlObj.getPath();
                String bucketName = path.split("/")[1];
                String key = path.split("/")[2];

                // 提取Expires的值
                String query = urlObj.getQuery();
                String[] pairs = query.split("&");
                String time = "0";
                for (String pair : pairs) {
                    int idx = pair.indexOf("=");
                    String k = URLDecoder.decode(pair.substring(0, idx), StandardCharsets.UTF_8);
                    String v = URLDecoder.decode(pair.substring(idx + 1), StandardCharsets.UTF_8);
                    if ("Expires".equals(k)) {
                        time = v;  // 输出: 1729651922841
                        break;
                    }
                }

                if (Long.valueOf(time) < 2729651922841l) {
                    String newUrl = FdsServer.expireUrl(url, bucketName, key);
                    if (StringUtils.isNotEmpty(newUrl)) {
                        it.setAvatarUrl(newUrl);
                        pluginOrgMapper.update(it);
                    }
                }

            } catch (Exception e) {
                log.error("fixImageUrl, url: {}, msg: {}", url, e.getMessage());
            }
        });

    }

    /**
     * 修复工作区的头像图片URL
     * <p>
     * 该方法查询所有头像URL包含"fds"的工作区，并检查其过期时间。如果头像URL的过期时间小于指定值，则更新为新的URL。
     *
     * @return 无返回值
     */
    public void fixWorkspaceImageUrl() {
        List<M78Workspace> bots = workspaceMapper.selectListByQuery(new QueryWrapper().like("avatar_url", "fds"));
        bots.stream().forEach(it -> {
            String url = it.getAvatarUrl();
            try {
                URL urlObj = new URL(url);
                // 提取域名后的第一个字段
                String path = urlObj.getPath();
                String bucketName = path.split("/")[1];
                String key = path.split("/")[2];

                // 提取Expires的值
                String query = urlObj.getQuery();
                String[] pairs = query.split("&");
                String time = "0";
                for (String pair : pairs) {
                    int idx = pair.indexOf("=");
                    String k = URLDecoder.decode(pair.substring(0, idx), StandardCharsets.UTF_8);
                    String v = URLDecoder.decode(pair.substring(idx + 1), StandardCharsets.UTF_8);
                    if ("Expires".equals(k)) {
                        time = v;  // 输出: 1729651922841
                        break;
                    }
                }

                if (Long.valueOf(time) < 2729651922841l) {
                    String newUrl = FdsServer.expireUrl(url, bucketName, key);
                    if (StringUtils.isNotEmpty(newUrl)) {
                        it.setAvatarUrl(newUrl);
                        workspaceMapper.update(it);
                    }
                }

            } catch (Exception e) {
                log.error("fixImageUrl, url: {}, msg: {}", url, e.getMessage());
            }
        });

    }

    /**
     * 修复流图像的URL
     * <p>
     * 该方法从数据库中查询所有包含特定字符串“fds”的头像URL，并检查这些URL的过期时间。
     * 如果过期时间小于指定值，则生成新的URL并更新数据库中的记录。
     *
     * @return 无返回值
     */
    public void fixFlowImageUrl() {
        List<FlowBasePo> bots = m78FlowBaseMapper.selectListByQuery(new QueryWrapper().like("avatar_url", "fds"));
        bots.stream().forEach(it -> {
            String url = it.getAvatarUrl();
            try {
                URL urlObj = new URL(url);
                // 提取域名后的第一个字段
                String path = urlObj.getPath();
                String bucketName = path.split("/")[1];
                String key = path.split("/")[2];

                // 提取Expires的值
                String query = urlObj.getQuery();
                String[] pairs = query.split("&");
                String time = "0";
                for (String pair : pairs) {
                    int idx = pair.indexOf("=");
                    String k = URLDecoder.decode(pair.substring(0, idx), StandardCharsets.UTF_8);
                    String v = URLDecoder.decode(pair.substring(idx + 1), StandardCharsets.UTF_8);
                    if ("Expires".equals(k)) {
                        time = v;  // 输出: 1729651922841
                        break;
                    }
                }

                if (Long.valueOf(time) < 2729651922841l) {
                    String newUrl = FdsServer.expireUrl(url, bucketName, key);
                    if (StringUtils.isNotEmpty(newUrl)) {
                        it.setAvatarUrl(newUrl);
                        m78FlowBaseMapper.update(it);
                    }
                }

            } catch (Exception e) {
                log.error("fixImageUrl, url: {}, msg: {}", url, e.getMessage());
            }
        });
    }

}
