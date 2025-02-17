package run.mone.moner.server.mcp;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.mutable.MutableObject;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;

import run.mone.hive.schema.AiMessage;
import run.mone.moner.server.bo.SseReq;

public class MonerMcpClient {

    private SseClient sseClient = new SseClient();
    
    private void send(String m) {
        Stopwatch sw = Stopwatch.createStarted();
        AtomicBoolean cancel = new AtomicBoolean(false);
        StringBuilder sb = new StringBuilder();
        this.sseClient.stream(SseReq.builder().messageList(Lists.newArrayList(run.mone.hive.schema.AiMessage.builder().role("user").content(m).build())).messageHandler((line, msg) -> {
            String type = msg.get("type").getAsString();
            if (type.equals("begin")) {
                // begin();
                // consumer.accept(AiMessage.builder().projectName(projectName).text("").type(AiMessageType.begin).id(id).build());
            }

            if (type.equals("event")) {
                // if (isStopped.get()) {
                //     //先简单的不处理
                //     return;
                // }
                // String message = msg.get("content").getAsString();
                // log.info("message:{}", message);
                // sb.append(message);
                // //如果被取消了,则不再追加内容了
                // if (!cancel.get() || !isStopped.get()) {
                //     consumer.accept(AiMessage.builder().projectName(projectName).code(false).text(message).type(AiMessageType.process).id(id).build());
                // }
            }

            if (type.equals("finish")) {
                // executeTopic(TaskEvent.builder().message("end").time(sw.elapsed(TimeUnit.SECONDS)));
                // String res = sb.toString();

                // log.info("BOT_STREAM_RESULT:{}", res);

                // Mutable<String> toolResMsg = new MutableObject<>();
                // Mutable<Boolean> completion = new MutableObject<>(false);

                // //调用mcp
                // mcpCall(res, toolResMsg, completion);

                // //放到一个全局变量里,方便别的代码使用
                // extractCodeBlock(res);
                // String _res = appendToolResMsgIfCompleted(res, completion, toolResMsg);
                // //放入最后的结果
                // this.req.getRes().set(_res);

                // Safe.run(() -> consumer.accept(AiMessage.builder().projectName(projectName).text(_res).type(AiMessageType.success).messageType("BOT_STREAM_RESULT").id(id).build()));
                // //执行工具的结果(并且没有完成)
                // addChatMessage(toolResMsg, completion);
                // if (null != latch) {
                //     latch.countDown();
                // }
                // return;
            }
            if (type.equals("failure")) {
                // String content = msg.get("content").getAsString();
                // failure(new RuntimeException(content), sw);
                // consumer.accept(AiMessage.builder().projectName(projectName).text(content).type(AiMessageType.failure).id(id).build());
                // if (null != latch) {
                //     latch.countDown();
                // }
            }
        }).build());
    }
}
