package run.mone.local.docean.test;

import com.google.gson.Gson;
import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.infra.rpc.Result;
import org.junit.Test;
import org.springframework.util.CollectionUtils;
import run.mone.local.docean.dto.ExecCommandResult;
import run.mone.local.docean.dubbo.TianyeService;
import run.mone.local.docean.fsm.*;
import run.mone.local.docean.po.Message;
import run.mone.local.docean.service.CommandExecuteService;
import run.mone.local.docean.service.TestConvert;
import run.mone.local.docean.service.tool.TerminalExecutorService;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author goodjava@qq.com
 * @date 2024/2/23 10:39
 */
public class TianyeServiceTest {

    @Test
    public void testExecCommand() {
        TerminalExecutorService terminalExecutor = Ioc.ins().init("run.mone.local.docean.service").getBean(TerminalExecutorService.class);
        String command = "ls -l";
        Result<ExecCommandResult> result = terminalExecutor.execCommand(command, null);
        if (result.getCode() == 0 && result.getData() != null) {

        }
    }

    public void testExecCommand2(){
        CommandExecuteService commandExecuteService = Ioc.ins().init("run.mone.local.docean.service").getBean(CommandExecuteService.class);
//        Result result = commandExecuteService.gitCommitWithPath("/home/gxh/myprojects/Tianye");
        Result result = commandExecuteService.gitCommit();
        System.out.println("git commit result : " + new Gson().toJson(result));
    }

    @Test
    public void testExecCommand3(){
        TerminalExecutorService terminalExecutor = Ioc.ins().init("run.mone.local.docean.service").getBean(TerminalExecutorService.class);
        String command = "pwd";
        String workPath = "";
        Result<ExecCommandResult> result = terminalExecutor.execCommand(command, workPath);
        if(result.getCode() == 0 && result.getData() != null){
            ExecCommandResult data = result.getData();
            System.out.println("command : [ " + command + " ] execute result code : " + data.getResultCode());
            List<String> resultData = data.getResultData();
            if (!CollectionUtils.isEmpty(resultData)) {
                System.out.println("command : [ " + command + " ] execute result data =============> ");
                resultData.forEach(t -> {
                    System.out.println(t);
                });
            }
        } else {
            System.out.println("command : [ " + command + " ] execute fail! result :  " + new Gson().toJson(result));
        }
    }

    @Test
    public void testHi() {
        Ioc.ins().init("run.mone.local.docean.dubbo");
        TianyeService tianyeService = Ioc.ins().getBean(TianyeService.class);
        String expected = "hi";
        String actual = tianyeService.hi();
        assertEquals(expected, actual);
    }

    @Test
    public void testFsm() {
        BotContext context = new BotContext();
        BotState bot1 = new BotState();
        bot1.setRemoteIpPort("1");
        BotState bot3 = new BotState();
        bot3.setRemoteIpPort("3");

        ParallelState parallelBot = new ParallelState();
        BotState bot21 = new BotState();
        bot21.setRemoteIpPort("21");
        BotState bot22 = new BotState();
        bot22.setRemoteIpPort("22");
        parallelBot.setBots(Arrays.asList(bot21, bot22));
        context.setBotList(Arrays.asList(bot1, parallelBot, bot3));
        BotFsm fsm = new BotFsm();
        fsm.init(context, BotReq.builder().build());
        fsm.execute(null);

    }

    @Test
    public void testFsm2() {
        BotContext context = new BotContext();
        BotState bot1 = new BotState();
        bot1.setRemoteIpPort("1");
        BotState bot3 = new BotState();
        bot3.setRemoteIpPort("3");

        context.setBotList(Arrays.asList(bot1, bot3));
        context.setCycle(true);
        BotFsm fsm = new BotFsm();
        fsm.init(context, BotReq.builder().build());
        fsm.execute(null);

    }

    @Test
    public void testMapstruct() {
        System.out.println(TestConvert.INSTANCE.toPo(Message.builder().id(12L).build()));
    }

}
