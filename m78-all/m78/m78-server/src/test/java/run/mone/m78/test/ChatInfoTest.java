package run.mone.m78.test;

import com.mybatisflex.core.query.QueryWrapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import run.mone.m78.service.dao.entity.ChatInfoPo;
import run.mone.m78.service.dao.mapper.ChatInfoMapper;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author goodjava@qq.com
 * @date 2024/1/15 10:59
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = run.mone.m78.server.M78Bootstrap.class)
public class ChatInfoTest {

    @Resource
    private ChatInfoMapper chatInfoMapper;


    @Test
    public void insertChatInfoPoTest() {
        ChatInfoPo po = ChatInfoPo.builder().user("name").build();
        chatInfoMapper.insertSelective(po);
    }

    @Test
    public void printChatInfoBySessionId() {
        List<ChatInfoPo> list = chatInfoMapper.selectListByQuery(QueryWrapper.create().eq("session_id", "1"));
        System.out.println(list);
    }


}
