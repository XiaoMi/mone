package run.mone.m78.service.service.bot;

import com.mybatisflex.core.query.QueryWrapper;
import com.xiaomi.youpin.infra.rpc.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import run.mone.m78.service.dao.entity.LongTermChatSummaryPo;
import run.mone.m78.service.dao.entity.ShortTermChatSummaryPo;
import run.mone.m78.service.dao.mapper.M78LongTermChatSummaryMapper;
import run.mone.m78.service.dao.mapper.M78ShortTermChatSummaryMapper;
import run.mone.m78.service.dto.ReqChatSummaryListDto;

import javax.annotation.Resource;
import java.util.List;

import static com.mybatisflex.core.query.QueryMethods.noCondition;
import static run.mone.m78.service.dao.entity.table.ShortTermChatSummaryPoTableDef.SHORT_TERM_CHAT_SUMMARY_PO;
import static run.mone.m78.service.dao.entity.table.LongTermChatSummaryPoTableDef.LONG_TERM_CHAT_SUMMARY_PO;

/**
 * @author wmin
 * @date 2024/5/23
 */
@Service
@Slf4j
public class ChatSummaryService {

    @Resource
    private M78LongTermChatSummaryMapper longTermChatSummaryMapper;

    @Resource
    private M78ShortTermChatSummaryMapper shortTermChatSummaryMapper;

    /**
     * 添加或更新长期聊天内容
     *
     * @param summaryPo 长期聊天内容的持久化对象
     * @return 包含操作结果的Result对象，成功时返回true，失败时返回false
     */
	public Result<Boolean> addOrUpdateLongTermChatContent(LongTermChatSummaryPo summaryPo) {
        if (StringUtils.isBlank(summaryPo.getContent())){
            return Result.success(false);
        }
        QueryWrapper queryWrapper = QueryWrapper.create().eq("deleted", 0)
                .and(null == summaryPo.getBotId() ? noCondition() : LONG_TERM_CHAT_SUMMARY_PO.BOT_ID.eq(summaryPo.getBotId()))
                .and(null == summaryPo.getAppId() ? noCondition() : LONG_TERM_CHAT_SUMMARY_PO.APP_ID.eq(summaryPo.getAppId()))
                .and(null == summaryPo.getUsername() ? noCondition() : LONG_TERM_CHAT_SUMMARY_PO.USERNAME.eq(summaryPo.getUsername()));
        List<LongTermChatSummaryPo> pos = longTermChatSummaryMapper.selectListByQuery(queryWrapper);
        int count = 0 ;
        if (CollectionUtils.isEmpty(pos)){
            count = longTermChatSummaryMapper.insert(summaryPo);
        } else {
            LongTermChatSummaryPo dbPo = pos.get(0);
            dbPo.setContent(summaryPo.getContent());
            count = longTermChatSummaryMapper.update(dbPo);
        }
        log.info("addLongTermChatSummaries count:{}", count);
        return Result.success(true);
    }

    /**
     * 添加长期聊天摘要列表
     *
     * @param longTermChatSummaryPos 长期聊天摘要对象列表
     * @return 操作结果，成功时返回true
     */
	public Result<Boolean> addLongTermChatSummaries(List<LongTermChatSummaryPo> longTermChatSummaryPos) {
        if (CollectionUtils.isEmpty(longTermChatSummaryPos)){
            return Result.success(true);
        }
        longTermChatSummaryPos.forEach(i -> i.setCtime(System.currentTimeMillis()));
        int count = longTermChatSummaryMapper.insertBatch(longTermChatSummaryPos);
        log.info("addLongTermChatSummaries count:{}", count);
        return Result.success(true);
    }

    /**
     * 查询长期聊天摘要列表
     *
     * @param username 用户名
     * @param botId 机器人ID
     * @return 包含长期聊天摘要的结果对象
     */
	public Result<List<LongTermChatSummaryPo>> qryLongTermChatSummaries(String username, Integer botId) {
        return Result.success(longTermChatSummaryMapper.selectListByQuery(
                QueryWrapper.create().eq("bot_id", botId).eq("username", username).eq("deleted", 0)));
    }

    /**
     * 添加短期聊天摘要列表
     *
     * @param shortTermChatSummaryPos 短期聊天摘要对象列表
     * @return 操作结果，始终返回成功
     */
	public Result<Boolean> addShortTermChatSummaries(List<ShortTermChatSummaryPo> shortTermChatSummaryPos) {
        if (CollectionUtils.isEmpty(shortTermChatSummaryPos)){
            return Result.success(true);
        }
        shortTermChatSummaryPos.forEach(i -> i.setCtime(System.currentTimeMillis()));
        int count = shortTermChatSummaryMapper.insertBatch(shortTermChatSummaryPos);
        log.info("addShortTermChatSummaries count:{}", count);
        return Result.success(true);
    }

    /**
     * 查询短期聊天摘要列表
     *
     * @param req 请求参数对象，包含筛选条件
     * @return 包含短期聊天摘要列表的结果对象
     */
	public Result<List<ShortTermChatSummaryPo>> qryShortTermChatSummaries(ReqChatSummaryListDto req) {
        QueryWrapper queryWrapper = QueryWrapper.create().eq("deleted", 0)
                .and(null == req.getBotId() ? noCondition() : SHORT_TERM_CHAT_SUMMARY_PO.BOT_ID.eq(req.getBotId()))
                .and(null == req.getUsername() ? noCondition() : SHORT_TERM_CHAT_SUMMARY_PO.USERNAME.eq(req.getUsername()))
                .and(null == req.getPriority() ? noCondition() : SHORT_TERM_CHAT_SUMMARY_PO.PRIORITY.eq(req.getPriority()))
                .and(null == req.getStartTime() ? noCondition() : SHORT_TERM_CHAT_SUMMARY_PO.CTIME.ge(req.getStartTime()));
        return Result.success(shortTermChatSummaryMapper.selectListByQuery(queryWrapper));
    }



}
