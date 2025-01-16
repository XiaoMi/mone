package run.mone.m78.service.agent.multiagent.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author goodjava@qq.com
 * @date 2024/9/10 19:10
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SysMessage implements Serializable, Message {

    private SystemMessage systemMessage;

    private Map<String, String> data = new HashMap<>();

    public SysMessage(SystemMessage systemMessage) {
        this.systemMessage = systemMessage;
    }

    public String getParameter(String key) {
        return data.get(key);
    }

    public void addParameter(String key, String value) {
        data.put(key, value);
    }

}
