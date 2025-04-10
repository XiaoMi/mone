package run.mone.mcp.chat.server;

import lombok.SneakyThrows;
import run.mone.hive.roles.Role;
import run.mone.hive.schema.ActionContext;
import run.mone.hive.schema.Message;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author goodjava@qq.com
 * @date 2025/4/9 09:50
 */
public class MinZai extends Role {

    private boolean firstMsg = true;

    public MinZai() {
    }

    @Override
    protected int think() {
        return observe();
    }


    @SneakyThrows
    @Override
    protected int observe() {
        Message msg = this.rc.getNews().poll(3, TimeUnit.MINUTES);
        if (null == msg) {
            return -1;
        }

        if (firstMsg) {
            String firstMsg = msg.getContent();
            this.getRc().getMemory().add(Message.builder().role("user").content(firstMsg).build());
        }

        // 获取memory中最后一条消息
        Message lastMsg = this.getRc().getMemory().getStorage().get(this.getRc().getMemory().getStorage().size() - 1);
        String lastMsgContent = lastMsg.getContent();

        List<Result> tools = new MultiXmlParser().parse(lastMsgContent);
        //结束 或者 ai有问题 都需要退出整个的执行，
        // 在微信回复场景下， 其实ask和chat的内容都应该发送给客户，所以这如果是ask_followup_question或者chat，并不会停止
        int attemptCompletion = tools.stream().anyMatch(it ->
                it.getTag().trim().equals("attempt_completion")
        ) ? -1 : 1;
        if (firstMsg) {
            firstMsg = false;
        }
        if (attemptCompletion == 1) {
            this.getRc().getNews().add(msg);
        }
        if (countDownLatch != null && attemptCompletion == -1) {
            countDownLatch.countDown();
        }
        return attemptCompletion;

        return super.observe();
    }

    @Override
    protected CompletableFuture<Message> act(ActionContext context) {
        return super.act(context);
    }
}
