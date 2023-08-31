package run.mone.openai.listener;

import lombok.SneakyThrows;

import java.util.concurrent.CountDownLatch;

/**
 * @author goodjava@qq.com
 * @date 2023/5/19 09:15
 */
public class AskListener  {

    private CountDownLatch latch = new CountDownLatch(1);

    private String answer;


    public void end(String res) {
        this.answer = res;
        latch.countDown();
    }

    @SneakyThrows
    public String getAnswer() {
        latch.await();
        return answer;
    }
}
