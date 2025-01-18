package run.mone.m78.test;

import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import run.mone.m78.service.dao.mapper.ChatMessageMapper;
import run.mone.m78.service.dao.mapper.ChatTopicMapper;
import run.mone.m78.service.dao.entity.ChatMessagePo;
import run.mone.m78.service.dao.entity.ChatTopicPo;

import javax.annotation.Resource;

/**
 * @author goodjava@qq.com
 * @date 2024/1/16 14:29
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = run.mone.m78.server.M78Bootstrap.class)
public class ChatTest {


    @Resource
    private ChatTopicMapper chatTopicMapper;

    @Resource
    private ChatMessageMapper chatMessageMapper;


    @Test
    public void insertChatTopicTest() {
        chatTopicMapper.insert(ChatTopicPo.builder().state(1).title("gogo").ctime(System.currentTimeMillis()).utime(System.currentTimeMillis()).build());
    }

    @Test
    public void insertChatMessageTest() {
        chatMessageMapper.insert(ChatMessagePo.builder().topicId(1)
                .message("123")
                .ctime(System.currentTimeMillis())
                .utime(System.currentTimeMillis())
                .state(1)
                .meta(ImmutableMap.of("name", "test", "id", "1")).build());
    }

}
