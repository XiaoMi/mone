package run.mone.local.docean.fsm;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * @author wmin
 * @date 2024/2/23
 */
@Slf4j
@Data
public class ParallelState extends BotState {

    private List<BotState> bots;

    private ExecutorService pool = Executors.newCachedThreadPool();

    public BotRes execute(BotReq req, BotContext context) {
        try {
            List<Future<BotRes>> futureList = pool.invokeAll(bots.stream().map(bot -> (Callable<BotRes>) () -> {
                log.info(" ParallelBot start enter {}", bot.getRemoteIpPort());
                bot.enter(context);
                BotRes res = bot.execute(req, context);
                bot.exit();
                return res;
            }).collect(Collectors.toList()));

            List<BotRes> resList = futureList.stream().map(it -> {
                try {
                    log.info("ParallelBot res:{}", it.get());
                    return it.get();
                } catch (InterruptedException e) {
                    log.error("ParallelBot.execute InterruptedException{}", e);
                } catch (ExecutionException e) {
                    log.error("ParallelBot.execute ExecutionException{}", e);
                }
                return null;
            }).collect(Collectors.toList());

            if (resList.stream().allMatch(it -> it.getCode() == BotRes.SUCCESS)) {
                return BotRes.success("ok");
            }
        } catch (Exception e){
            log.error("ParallelBot execute error,", e);
            return BotRes.failure("");
        }
        return BotRes.failure("ParallelBot failure");
    }
}
