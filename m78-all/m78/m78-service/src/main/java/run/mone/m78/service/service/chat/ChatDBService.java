package run.mone.m78.service.service.chat;

import com.google.common.base.Preconditions;
import com.google.gson.reflect.TypeToken;
import com.mybatisflex.core.query.QueryWrapper;
import com.xiaomi.youpin.infra.rpc.Result;
import com.xiaomi.youpin.infra.rpc.errors.GeneralCodes;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import run.mone.m78.api.bo.chat.ChatTopicSearchReq;
import run.mone.m78.service.bo.chat.ChatTopicBo;
import run.mone.m78.service.dao.mapper.ChatMessageMapper;
import run.mone.m78.service.dao.mapper.ChatTopicMapper;
import run.mone.m78.service.dao.entity.ChatMessagePo;
import run.mone.m78.service.dao.entity.ChatTopicPo;
import run.mone.m78.service.vo.ImportChatPo;

import javax.annotation.Resource;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static run.mone.m78.service.exceptions.ExCodes.STATUS_INTERNAL_ERROR;
import static run.mone.m78.service.exceptions.ExCodes.STATUS_NOT_FOUND;

/**
 * @author goodjava@qq.com
 * @date 2024/1/16 14:33
 */
@Service
@Slf4j
public class ChatDBService {


    @Resource
    private ChatTopicMapper chatTopicMapper;

    @Resource
    private ChatService chatService;

    @Resource
    private ChatMessageMapper chatMessageMapper;

    /**
     * 插入一条新的ChatMessage
     *
     * @param chatMessage 要插入的ChatMessage对象
     * @return 包含插入结果的Result对象，如果插入成功则返回成功的Result对象，否则返回失败的Result对象
     */
    //插入一条新的ChatMessage(class)
    public Result<ChatMessagePo> insertNewChatMessage(ChatMessagePo chatMessage) {
        if (chatMessage == null) {
            return Result.fail(STATUS_NOT_FOUND, "Chat message data is null");
        }
        chatMessage.setState(1);
        int insertResult = chatMessageMapper.insert(chatMessage);
        if (insertResult > 0) {
            return Result.success(chatMessage);
        } else {
            return Result.fail(STATUS_INTERNAL_ERROR, "Failed to insert new chat message in the database");
        }
    }

    /**
     * 根据消息ID和用户名删除一条聊天消息
     *
     * @param messageId 消息ID
     * @param userName  用户名
     * @return 删除操作的结果，成功返回Result.success(null)，失败返回Result.fail(STATUS_NOT_FOUND, "Failed to delete chat message with id: " + messageId)
     */
    //删除一条ChatMessage(class)
    public Result<Void> deleteChatMessageById(int messageId, String userName) {
        QueryWrapper queryWrapper = QueryWrapper.create().eq("id", messageId).eq("user_name", userName).eq("state", 1);
        int updateResult = chatMessageMapper.updateByQuery(ChatMessagePo.builder().state(2).build(), queryWrapper);
        if (updateResult > 0) {
            return Result.success(null);
        } else {
            return Result.fail(STATUS_NOT_FOUND, "Failed to delete chat message with id: " + messageId);
        }
    }

    /**
     * 获取某个topic下的所有ChatMessage
     *
     * @param topicId  话题ID
     * @param userName 用户名
     * @return 包含ChatMessagePo列表的Result对象，如果没有找到消息则返回失败的Result对象
     */
    //获取某个topic下的所有ChatMessage(class)
    public Result<List<ChatMessagePo>> getAllChatMessagesByTopicId(int topicId, String userName) {
        //tidb不能默认用id，不够自增
        QueryWrapper queryWrapper = QueryWrapper.create()
                .eq("topic_id", topicId)
                .eq("user_name", userName)
                .eq("state", 1)
                .orderBy("ctime", false)
                .limit(20);
        List<ChatMessagePo> chatMessages = chatMessageMapper.selectListByQuery(queryWrapper);

        if (chatMessages != null && !chatMessages.isEmpty()) {
            chatMessages.sort(Comparator.comparing(ChatMessagePo::getCtime));
            return Result.success(chatMessages);
        } else {
            return Result.fail(STATUS_NOT_FOUND, "No chat messages found for topic id: " + topicId);
        }
    }

    /**
     * 获取某个topic下的所有ChatMessage，按创建时间倒序排列，最多返回20条
     *
     * @param topicId  话题ID
     * @param userName 用户名
     * @return 包含ChatMessagePo列表的Result对象，如果没有找到消息则返回失败的Result对象
     */
    //获取某个topic下的所有ChatMessage(class)
    public Result<List<ChatMessagePo>> getAllChatMessagesByTopicIdDesc(int topicId, String userName) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .eq("topic_id", topicId)
                .eq("user_name", userName)
                .eq("state", 1)
                .orderBy("ctime", false)
                .limit(20);
        List<ChatMessagePo> chatMessages = chatMessageMapper.selectListByQuery(queryWrapper);
        if (chatMessages != null && !chatMessages.isEmpty()) {
            return Result.success(chatMessages);
        } else {
            return Result.fail(STATUS_NOT_FOUND, "No chat messages found for topic id: " + topicId);
        }
    }

    /**
     * 根据用户名获取所有聊天主题信息
     *
     * @param userName 用户名
     * @param type     聊天主题类型
     * @return 包含聊天主题信息的结果对象，如果未找到则返回失败信息
     */
    //根据userName,获取所有ChatTopic信息(class)
    public Result<List<ChatTopicPo>> getAllChatTopicsByUserName(String userName, int type) {
        QueryWrapper queryWrapper = new QueryWrapper()
                .eq("type", type)
                .eq("user_name", userName)
                .orderBy("ctime", false)
                .limit(20);
        List<ChatTopicPo> chatTopics = chatTopicMapper.selectListByQuery(queryWrapper);
        if (chatTopics != null && !chatTopics.isEmpty()) {
            return Result.success(chatTopics);
        } else {
            return Result.fail(STATUS_NOT_FOUND, "No chat topics found for user: " + userName);
        }
    }

    /**
     * 根据ChatTopicPo的title和userName获取所有的ChatTopic
     *
     * @param title    聊天主题的标题
     * @param userName 用户名
     * @return 包含所有匹配的ChatTopicPo对象的Result对象，如果没有找到匹配的聊天主题则返回失败的Result对象
     */
    // 根据ChatTopicPo的title和userName获取所有的ChatTopic(class)
    public Result<List<ChatTopicPo>> getChatTopicsByTitleAndUserName(String title, String userName) {
        QueryWrapper queryWrapper = new QueryWrapper().eq("title", title).eq("user_name", userName);
        List<ChatTopicPo> chatTopics = chatTopicMapper.selectListByQuery(queryWrapper);
        if (chatTopics != null && !chatTopics.isEmpty()) {
            return Result.success(chatTopics);
        } else {
            return Result.fail(STATUS_NOT_FOUND, "No chat topics found with title: " + title + " for user: " + userName);
        }
    }

    /**
     * 获取聊天主题的详细信息
     *
     * @param topicId  聊天主题的ID
     * @param userName 用户名
     * @return 包含聊天主题详细信息的结果对象，如果未找到对应的聊天主题则返回失败结果
     */
    public Result<ChatTopicBo> chatTopicDetail(int topicId, String userName) {
        QueryWrapper queryWrapper = new QueryWrapper().eq("id", topicId).eq("user_name", userName);
        ChatTopicPo chatTopicPo = chatTopicMapper.selectOneByQuery(queryWrapper);
        if (chatTopicPo == null) {
            return Result.fail(STATUS_NOT_FOUND, "Chat topic not found with id: " + topicId);
        }
        ChatTopicBo bo = new ChatTopicBo();
        bo.setChatTopic(chatTopicPo);
        bo.setKnowledgeConfigDetail(chatService.qryKnowledgeConfigDetail(chatTopicPo));
        return Result.success(bo);
    }


    /**
     * 创建聊天主题，并更新到数据库
     *
     * @param chatTopic 聊天主题对象
     * @param userName  用户名
     * @return 包含创建结果的Result对象
     */
    //创建chatTopic,并更新到数据库(class)
    public Result<ChatTopicPo> createChatTopic(ChatTopicPo chatTopic, String userName) {
        if (chatTopic == null) {
            return Result.fail(STATUS_NOT_FOUND, "Chat topic data is null");
        }
        long now = System.currentTimeMillis();
        chatTopic.setUserName(userName);
        chatTopic.setUtime(now);
        chatTopic.setCtime(now);
        chatTopic.setState(1);
        int insertResult = chatTopicMapper.insert(chatTopic);
        if (insertResult > 0) {
            return Result.success(chatTopic);
        } else {
            return Result.fail(STATUS_INTERNAL_ERROR, "Failed to create chat topic in the database");
        }
    }

    /**
     * 根据ID删除聊天主题
     *
     * @param topicId  聊天主题的ID
     * @param userName 用户名
     * @return 操作结果
     */
    //按id删除chatTopic(class)
    public Result<Void> deleteChatTopicById(int topicId, String userName) {
        QueryWrapper queryWrapper = QueryWrapper.create().eq("topic_id", topicId).eq("user_name", userName).eq("state", 1);
        List<ChatMessagePo> chatMessages = chatMessageMapper.selectListByQuery(queryWrapper);
        if (CollectionUtils.isNotEmpty(chatMessages)) {
            //删除这个topic下的所有消息
            int deleteResult = chatMessageMapper.deleteByQuery(
                    QueryWrapper.create().eq("topic_id", topicId).eq("user_name", userName));
            log.info("delete message by topicId:{} sum:{}", topicId, deleteResult);
        }

        int deleteResult = chatTopicMapper.deleteById(topicId);
        log.info("delete topic by topicId:{} sum:{}", topicId, deleteResult);

        return Result.success(null);
    }


    /**
     * 更新聊天主题
     *
     * @param chatTopic 聊天主题对象
     * @return 更新结果，成功返回更新后的聊天主题对象，失败返回错误信息
     */
    //更新chatTopic(class)
    public Result<ChatTopicPo> updateChatTopic(ChatTopicPo chatTopic) {
        if (chatTopic == null) {
            return Result.fail(STATUS_NOT_FOUND, "Chat topic data is null");
        }
        int updateResult = chatTopicMapper.update(chatTopic);
        if (updateResult > 0) {
            return Result.success(chatTopic);
        } else {
            return Result.fail(STATUS_INTERNAL_ERROR, "Failed to update chat topic in the database");
        }
    }

    /**
     * 查找或创建聊天主题
     *
     * @param searchReq 聊天主题搜索请求，包含主题类型、机器人ID、应用ID、用户名等信息
     * @return 包含聊天主题列表的结果，如果创建失败则返回错误信息
     */
    public Result<List<ChatTopicPo>> findOrCreateChatTopics(ChatTopicSearchReq searchReq) {
        Preconditions.checkArgument(searchReq.getTopicType() != null && searchReq.getBotId() != null, "searchReq is invalid");
        String topicTitle = (searchReq.getAppId() == null ? "" : searchReq.getAppId() + "_")
                + searchReq.getBotId() + "_" + searchReq.getUsername();

        QueryWrapper queryWrapper = new QueryWrapper()
                .eq("type", searchReq.getTopicType())
                .like("title", topicTitle);
        if (searchReq.getAppId() != null) {
            queryWrapper.eq("app_id", searchReq.getAppId());
        }
        List<ChatTopicPo> chatTopics = chatTopicMapper.selectListByQuery(queryWrapper);
        if (CollectionUtils.isNotEmpty(chatTopics)) {
            return Result.success(chatTopics);
        }
        if (!searchReq.isCreateIfNotExist()) {
            return Result.success(Collections.emptyList());
        }
        ChatTopicPo chatTopicPo = new ChatTopicPo();
        long now = System.currentTimeMillis();
        chatTopicPo.setAppId(searchReq.getAppId());
        chatTopicPo.setTitle(topicTitle);
        chatTopicPo.setType(searchReq.getTopicType());
        chatTopicPo.setUserName(searchReq.getUsername());
        chatTopicPo.setUtime(now);
        chatTopicPo.setCtime(now);
        chatTopicPo.setState(1);
        int insertResult = chatTopicMapper.insert(chatTopicPo);
        if (insertResult > 0) {
            return Result.success(List.of(chatTopicPo));
        } else {
            return Result.fail(STATUS_INTERNAL_ERROR, "Failed to create chat topic in the database");
        }
    }


    /**
     * 清空指定 chatTopic id 的所有 chatMessage，需要验证 userName
     *
     * @param topicId  话题的唯一标识
     * @param userName 用户名，用于验证操作权限
     * @return 操作结果，如果成功返回成功结果，否则返回失败信息
     */
    //清空指定chatTopic id 的所有chatMessage,需要验证userName(class)
    public Result<Void> clearChatMessagesByTopicId(int topicId, String userName) {
        int updateResult = chatMessageMapper.softDelete(topicId, userName);
        if (updateResult > 0) {
            return Result.success(null);
        } else {
            return Result.fail(STATUS_NOT_FOUND, "No chat messages found to clear for topic id: " + topicId);
        }
    }


    /**
     * 解析JSON，导入topic和chatMessage，并设置ctime和utime为当前时间，设置userName为传入的值
     *
     * @param po 包含topic和chatMessage的导入数据对象
     * @return 导入结果，成功返回成功信息，失败返回错误信息
     */
    // 解析JSON，导入topic和chatMessage，并设置ctime和utime为当前时间，设置userName为传入的(class)
    @Transactional
    public Result<Void> importTopicAndChat(ImportChatPo po) {
        ChatTopicPo chatTopic = po.getTopic();
        if (chatTopic == null || StringUtils.isEmpty(po.getUserName())) {
            return Result.fail(GeneralCodes.ParamError, "chatTopic or user name is null");
        }
        Result<ChatTopicPo> topicResult = createChatTopic(chatTopic, po.getUserName());
        if (topicResult.getCode() != 0) {
            return Result.fail(GeneralCodes.InternalError, "Failed to insert chat topic");
        }

        List<ChatMessagePo> messages = po.getMessagePos();
        if (messages != null && !messages.isEmpty()) {
            for (ChatMessagePo chatMessage : messages) {
                chatMessage.setTopicId(topicResult.getData().getId());
                chatMessage.setUserName(po.getUserName());
                int insertMessageResult = chatMessageMapper.insert(chatMessage);
                if (insertMessageResult <= 0) {
                    return Result.fail(STATUS_INTERNAL_ERROR, "Failed to insert chat message");
                }
            }
        }
        return Result.success(null);
    }



}
