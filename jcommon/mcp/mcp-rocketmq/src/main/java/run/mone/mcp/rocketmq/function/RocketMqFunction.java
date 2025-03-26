package run.mone.mcp.rocketmq.function;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.acl.common.AclClientRPCHook;
import org.apache.rocketmq.acl.common.SessionCredentials;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.AclConfig;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.RPCHook;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.springframework.beans.factory.annotation.Value;
import run.mone.hive.mcp.spec.McpSchema;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Data
@Slf4j
public class RocketMqFunction implements Function<Map<String, Object>, McpSchema.CallToolResult> {

    private String name = "rocketmq-sender";

    private String desc = "Send Message To Rocketmq";

    private String nameSrvAddress;

    private String group;

    private String accessKey;

    private String secureKey;

    private String sqlToolSchema = """
            {
                "type": "object",
                "properties": {
                    "topic": {
                        "type": "string",
                        "description": "message topic"
                     },
                    "message": {
                         "type": "string",
                         "description": "the message to send"
                    }
                },
                "required": ["topic",", "message"]
            }
            """;
    private DefaultMQProducer producer;


    public RocketMqFunction(String nameSrvAddress, String group, String accessKey, String secureKey) {
        this.nameSrvAddress = nameSrvAddress;
        this.group = group;
        this.accessKey = accessKey;
        this.secureKey = secureKey;
    }


    private void ensureProducerInited() {
        if (producer == null) {
            synchronized (this) {
                if (producer == null) {
                    producer = new DefaultMQProducer(group, new AclClientRPCHook(new SessionCredentials(accessKey, secureKey)));
                    producer.setNamesrvAddr(nameSrvAddress);
                    try {
                        producer.start();
                    } catch (MQClientException e) {
                        log.error("Failed to start producer", e);
                    }
                }
            }
        }
    }


    @Override
    public McpSchema.CallToolResult apply(Map<String, Object> args) {
        ensureProducerInited();

        String topic = (String) args.get("topic");
        String message = (String) args.get("message");

        return sendRocketMqMessage(topic, message);
    }

    public McpSchema.CallToolResult sendRocketMqMessage(String topic, String message) {
        try {
            Message msg = new Message(topic, message.getBytes(RemotingHelper.DEFAULT_CHARSET));
            SendResult sendResult = producer.send(msg);
            return new McpSchema.CallToolResult(List.of(new McpSchema.TextContent(sendResult.getMsgId())), false);
        } catch (Exception e) {
            log.error("error", e);
            return new McpSchema.CallToolResult(List.of(new McpSchema.TextContent("Error: " + e.getMessage())), true);
        }
    }
}
