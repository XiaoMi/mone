package run.mone.mcp.rocketmq.function;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.acl.common.AclClientRPCHook;
import org.apache.rocketmq.acl.common.SessionCredentials;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import reactor.core.publisher.Flux;
import run.mone.hive.mcp.spec.McpSchema;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Data
@Slf4j
public class RocketMqFunction implements Function<Map<String, Object>, Flux<McpSchema.CallToolResult>> {

    private String name = "stream_rocketmq_sender";

    private String desc = "Send Message To Rocketmq";

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
                "required": ["topic", "message"]
            }
            """;
    private DefaultMQProducer producer;


    @Override
    public Flux<McpSchema.CallToolResult> apply(Map<String, Object> args) {
        return Flux.defer(() -> {
            try {
                ensureProducerInited();

                String topic = (String) args.get("topic");
                String message = (String) args.get("message");
                String msgId = sendRocketMqMessage(topic, message);
                log.info("send mq success， topic: {}, msgId: {}", topic, msgId);
                return createSuccessFlux(msgId);
            } catch (Exception e) {
                log.error("send mq fail", e);
                return Flux.just(new McpSchema.CallToolResult(
                        List.of(new McpSchema.TextContent("操作失败：" + e.getMessage())), true));
            }
        });
    }

    public RocketMqFunction() {
    }

    private void ensureProducerInited() throws MQClientException {
        if (producer == null) {
            synchronized (this) {
                if (producer == null) {
                    String nameSrvAddress = System.getenv().getOrDefault("NAMESRV_ADDR", "");
                    String group = System.getenv().getOrDefault("GROUP", "");
                    String accessKey = System.getenv().getOrDefault("ACCESS_KEY", "");
                    String secureKey = System.getenv().getOrDefault("SECURE_KEY", "");
                    producer = new DefaultMQProducer(group, new AclClientRPCHook(new SessionCredentials(accessKey, secureKey)));
                    producer.setNamesrvAddr(nameSrvAddress);
                    try {
                        producer.start();
                    } catch (MQClientException e) {
                        log.error("Failed to start producer", e);
                        throw  e;
                    }
                }
            }
        }
    }

    public String sendRocketMqMessage(String topic, String message) {
        try {
            Message msg = new Message(topic, message.getBytes(RemotingHelper.DEFAULT_CHARSET));
            SendResult sendResult = producer.send(msg);
            if (sendResult == null || sendResult.getSendStatus() != SendStatus.SEND_OK) {
                log.error("send message fail, result:{}", sendResult);
                throw new Exception("msg send fail");
            }
            return sendResult.getMsgId();
        } catch (Exception e) {
            log.error("error", e);
            throw new RuntimeException(e);
        }
    }

    private Flux<McpSchema.CallToolResult> createSuccessFlux(String result) {
        return Flux.just(
                new McpSchema.CallToolResult(List.of(new McpSchema.TextContent(result)), false),
                new McpSchema.CallToolResult(List.of(new McpSchema.TextContent("[DONE]")), false)
        );
    }
}
