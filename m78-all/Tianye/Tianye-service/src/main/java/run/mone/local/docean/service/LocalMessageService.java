package run.mone.local.docean.service;

import com.xiaomi.youpin.docean.anno.Service;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import run.mone.local.docean.po.Message;

import java.util.*;

/**
 * @author goodjava@qq.com
 * @date 2024/2/28 13:55
 */
@Service
@Data
@Slf4j
public class LocalMessageService {

    private Map<String, List<Message>> messagesMap = new HashMap<>();

    public void add(String topicId, Message message) {
        messagesMap.compute(topicId, (k, v) -> {
            if (v == null) {
                List<Message> list = new ArrayList<>();
                list.add(message);
                return list;
            } else {
                v.add(message);
                //如果v的长度超过10,则只保留后3条
                if (v.size() > 10) {
                    v = v.subList(v.size() - 3, v.size());
                    return v;
                }
                return v;
            }
        });
    }

    public void clearMessage(String topcId) {
        List<Message> messageList = messagesMap.get(topcId);
        if (null != messageList) {
            messageList.clear();
        }
    }


    public List<Message> getMessagesMap(String topicId) {
        List<Message> messages = messagesMap.getOrDefault(topicId, new ArrayList<>());
        //获取messageList,如果messageList超过 30条,则截断获取最后的30条
        messages = messages.size() > 30 ? messages.subList(messages.size() - 30, messages.size()) : messages;
        messagesMap.put(topicId, messages);
        return messages;
    }

}
