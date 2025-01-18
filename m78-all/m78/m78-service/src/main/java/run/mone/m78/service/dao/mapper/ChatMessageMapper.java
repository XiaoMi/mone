package run.mone.m78.service.dao.mapper;

import com.mybatisflex.core.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import run.mone.m78.service.dao.entity.ChatMessagePo;

/**
 * @author goodjava@qq.com
 */
public interface ChatMessageMapper extends BaseMapper<ChatMessagePo> {
    // do nothing here

    @Update("update m78_chat_messages set state = 2 where topic_id = #{topicId} and user_name = #{userName} and state = 1")
    int softDelete(@Param("topicId") Integer topicId, @Param("userName") String userName);
}
