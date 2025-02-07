package run.mone.m78.service.service.chat;

import com.google.gson.reflect.TypeToken;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.util.UpdateEntity;
import com.xiaomi.youpin.infra.rpc.Result;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import run.mone.m78.api.bo.chat.ChatInfo;
import run.mone.m78.service.bo.chat.ChatScoreBO;
import run.mone.m78.service.common.GsonUtils;
import run.mone.m78.service.common.M78StringUtils;
import run.mone.m78.service.dao.mapper.ChatInfoMapper;
import run.mone.m78.service.dao.entity.ChatInfoPo;
import run.mone.m78.service.database.SqlParseUtil;
import run.mone.m78.service.exceptions.InvalidArgumentException;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static run.mone.m78.api.constant.CommonConstant.SIM_THRESHOLD;
import static run.mone.m78.service.exceptions.ExCodes.STATUS_INTERNAL_ERROR;
import static run.mone.m78.service.exceptions.ExCodes.STATUS_NOT_FOUND;

/**
 * @author HawickMason@xiaomi.com
 * @date 1/15/24 10:33 AM
 */
@Slf4j
@Service
public class ChatInfoService {

    @Resource
    private ChatInfoMapper chatInfoMapper;

    /**
     * 记录聊天信息
     *
     * @param chatInfoPo 聊天信息对象
     * @return 受影响的行数
     */
	public int recordChatInfo(ChatInfoPo chatInfoPo) {
        // sql解析得到conditions
        List<Map<String, Object>> conditions = new ArrayList<>();
        try {
            conditions = SqlParseUtil.getColumnNames(chatInfoPo.getMappingContent());
        } catch (Exception e) {
            log.error("Error while try to get parsed conditions from chatInfo:{}; nested exception is:", chatInfoPo, e);
        }
        chatInfoPo.setConditions(GsonUtils.gson.toJson(conditions));

        int affected = chatInfoMapper.insertSelective(chatInfoPo);
        log.info("insert chatInfo, affected:{}", affected);
        return affected;
    }

    /**
     * 根据ID查询聊天信息
     *
     * @param id 聊天信息的ID
     * @return 包含聊天信息的Result对象，如果未找到则返回失败的Result对象
     */
	public Result<ChatInfo> queryChatInfoById(Long id) {
        ChatInfoPo chatInfoPo = chatInfoMapper.selectOneById(id);
        log.info("query chatInfo by id:{}, res:{}", id, chatInfoPo);
        if (chatInfoPo != null) {
            return Result.success(ChatInfo.builder()
                    .id(chatInfoPo.getId())
                    .content(chatInfoPo.getContent())
                    .mappingContent(chatInfoPo.getMappingContent())
                    .conditions(GsonUtils.gson.fromJson(chatInfoPo.getConditions(), new TypeToken<List<Map<String, Object>>>() {
                    }.getType()))

                    .build());
        } else {
            return Result.fail(STATUS_NOT_FOUND, "Chat info not found with id: " + id);
        }
    }

    /**
     * 根据会话ID查询聊天信息列表
     *
     * @param documentId 会话ID
     * @return 聊天信息列表
     */
	public List<ChatInfoPo> queryChatInfoListBySessionId(String documentId) {
        List<ChatInfoPo> res = chatInfoMapper.selectListByQuery(QueryWrapper.create().eq("session_id", documentId));
        log.info("query chatInfo list by sessionId:{}", documentId);
        return res;
    }

    /**
     * 根据会话ID和用户名获取聊天信息
     *
     * @param documentId 会话ID
     * @param userName 用户名
     * @return 聊天信息列表
     */
	public List<ChatInfoPo> getChatInfoBySessionIdAndUserName(String documentId, String userName) {
        List<ChatInfoPo> res = chatInfoMapper.selectListByQuery(QueryWrapper.create().eq("session_id", documentId).eq("user_name", userName));
        return res;
    }

    /**
     * 更新聊天信息
     *
     * @param chatInfo 要更新的聊天信息对象
     * @return 更新结果，成功返回true，失败返回false
     * @throws InvalidArgumentException 当chatInfo或其ID为空时抛出
     */
	public Result<Boolean> updateChatInfo(ChatInfo chatInfo) {
        if (chatInfo == null || chatInfo.getId() == null) {
            throw new InvalidArgumentException("更新chatInfo无效!");
        }
        Long id = chatInfo.getId();
        ChatInfoPo updatePO = UpdateEntity.of(ChatInfoPo.class, id);
        updatePO.setContent(chatInfo.getContent());
        updatePO.setConditions(GsonUtils.gson.toJson(chatInfo.getConditions()));
        updatePO.setMappingContent(updateMappingContent(chatInfo.getMappingContent(), chatInfo.getConditions()));
        int affected = chatInfoMapper.update(updatePO);
        log.info("update chatInfo at id:{} , with:{}", id, chatInfo);
        if (affected > 0) {
            return Result.success(true);
        } else {
            return Result.fail(STATUS_INTERNAL_ERROR, "failed to update chatInfo at id: " + id);
        }
    }

    /**
     * 根据会话ID删除聊天信息
     *
     * @param documentId 会话ID
     * @return 受影响的行数
     */
	public int removeChatInfoBySessionId(String documentId) {
        int affected = chatInfoMapper.deleteByQuery(QueryWrapper.create().eq("session_id", documentId));
        log.info("delete chatInfo with sessionId:{}", documentId);
        return affected;
    }

    /**
     * 根据ID删除聊天信息
     *
     * @param chatInfoId 聊天信息的ID
     * @return 受影响的行数
     */
	public int removeById(Long chatInfoId) {
        int affected = chatInfoMapper.deleteById(chatInfoId);
        log.info("delete chatInfo at id:{}", chatInfoId);
        return affected;
    }

    /**
     * 根据ID和用户名删除聊天信息
     *
     * @param id 聊天信息的ID
     * @param userName 用户名
     * @return 受影响的行数
     */
	//删除chatinfo 按 id 和 user_name(class)
    public int removeChatInfoByIdAndUserName(Long id, String userName) {
        int affected = chatInfoMapper.deleteByQuery(QueryWrapper.create().eq("id", id).eq("user_name", userName));
        log.info("delete chatInfo with id:{} and userName:{}", id, userName);
        return affected;
    }

    /**
     * 根据文档ID和聊天内容查询现有的聊天信息
     *
     * @param documentId 文档ID
     * @param chatContent 聊天内容
     * @return 匹配的ChatInfoPo对象，如果没有匹配的记录则返回null
     */
	public ChatInfoPo getExistingSqlByDocumentIdAndChatContent(String documentId, String chatContent) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("session_id", documentId);
        queryWrapper.eq("content", chatContent);
        List<ChatInfoPo> chatInfoPos = chatInfoMapper.selectListByQuery(queryWrapper);
        if (chatInfoPos.size() == 0) {
            return null;
        }
        return chatInfoPos.get(0);
    }

    // 从chatInfo表中, 根据documentId何chatContent获取是否已经有对应的sql记录(class)
    public Result<String> getExistingSqlByDocumentIdAndChatContent(String documentId, String chatContent, boolean fuzzy) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("session_id", documentId);
        if (!fuzzy) {
            queryWrapper.eq("content", chatContent);
        }
        List<ChatInfoPo> chatInfoPos = chatInfoMapper.selectListByQuery(queryWrapper);
        if (!fuzzy) {
            if (CollectionUtils.isNotEmpty(chatInfoPos)) {
                if (chatInfoPos.size() > 1) {
                    log.warn("get multiple chat info same as chatContent, use the first one!");
                }
                ChatInfoPo chatInfoPo = chatInfoPos.get(0);
                return Result.success(chatInfoPo.getMappingContent());
            } else {
                return Result.fail(STATUS_NOT_FOUND, "No existing SQL record found for the given documentId and chatContent");
            }
        } else {
            Optional<ChatScoreBO> sim = chatInfoPos.stream()
                    .map(po -> ChatScoreBO.builder()
                            .chatInfoPo(po)
                            .similarity(M78StringUtils.getStrEditDistanceSimilarity(chatContent, po.getContent()))
                            .build())
                    .filter(chatScoreBO -> chatScoreBO.getSimilarity() > SIM_THRESHOLD)
                    .findFirst();
            if (sim.isPresent()) {
                return Result.success(sim.get().getChatInfoPo().getMappingContent());
            } else {
                return Result.fail(STATUS_NOT_FOUND, "No existing SQL record found for the given documentId and chatContent");
            }
        }
    }

    private String updateMappingContent(String mappingContent, List<Map<String, Object>> conditions) {
        if (CollectionUtils.isEmpty(conditions)) {
            return mappingContent;
        }
        Map<String, Object> newConditions = conditions.stream()
                .flatMap(c -> c.entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (o1, o2) -> o2));
        try {
            return SqlParseUtil.updateSqlWhereParts(mappingContent, newConditions);
        } catch (JSQLParserException e) {
            log.error("Error while try to update sql where parts, nested exception is:", e);
            return mappingContent;
        }
    }


}

