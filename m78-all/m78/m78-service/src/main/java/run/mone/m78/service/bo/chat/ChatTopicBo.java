package run.mone.m78.service.bo.chat;

import lombok.Data;
import run.mone.m78.service.bo.knowledge.KnowledgeConfigDetail;
import run.mone.m78.service.dao.entity.ChatTopicPo;

/**
 * @author wmin
 * @date 2024/1/31
 */
@Data
public class ChatTopicBo {

    private ChatTopicPo chatTopic;

    private KnowledgeConfigDetail knowledgeConfigDetail;

}
