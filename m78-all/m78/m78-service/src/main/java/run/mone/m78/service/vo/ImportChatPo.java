package run.mone.m78.service.vo;

import lombok.Data;
import run.mone.m78.service.dao.entity.ChatMessagePo;
import run.mone.m78.service.dao.entity.ChatTopicPo;

import java.util.List;

@Data
public class ImportChatPo {
    private ChatTopicPo topic;
    private List<ChatMessagePo> messagePos;
    private String userName;
}
