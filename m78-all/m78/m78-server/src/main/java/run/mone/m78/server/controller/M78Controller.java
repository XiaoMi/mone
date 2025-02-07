package run.mone.m78.server.controller;

import cn.hutool.core.codec.Base64;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.xiaomi.youpin.infra.rpc.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import run.mone.m78.service.bo.chatgpt.Ask;
import run.mone.m78.service.service.base.ChatgptService;
import run.mone.m78.service.service.base.SseService;

import javax.annotation.Resource;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static run.mone.m78.api.constant.CommonConstant.API_PREFIX;
import static run.mone.m78.api.constant.PromptConstant.PROMPT_CSV_CREATE_TABLE_SQL;


/**
 * @author goodjava@qq.com
 * @date 2024/1/9 20:21
 */
@Slf4j
@RestController(value = API_PREFIX + "/m78")
public class M78Controller {


    @Resource
    private ChatgptService chatgptService;

    @Resource
    private SseService sseService;

    private ExecutorService pool = Executors.newFixedThreadPool(1);


    /**
     * 提供一个测试接口，调用chatgptService的call方法以生成并返回一个SELECT SQL语句
     */
    @GetMapping("/test")
    public String test() {
        Result<String> data = chatgptService.call("generateSelectSql", Maps.<String, String>newHashMap(), "sql");
        return data.getData();
    }

    /**
     * Invoke the `call` method of `chatgptService` to convert data in CSV format into SQL statements for table creation, and return the result string.
     */
    @GetMapping("/test2")
    public Result<String> test2(@RequestParam("content") String content) {
        Result<String> data = chatgptService.call(PROMPT_CSV_CREATE_TABLE_SQL, ImmutableMap.of("content", content), "sql");
        return Result.success(data.getData());
    }

    /**
     * 打字机效果测试界面
     * <p>
     */
    @PostMapping(value = {"/ask"}, produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> ask(@RequestBody Ask ask) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("X-Accel-Buffering", "no");
        httpHeaders.setCacheControl(CacheControl.noCache());
        String id = ask.getId();
        SseEmitter emitter = sseService.registerSseEmitter(id);
        pool.submit(() -> {
            chatgptService.ask(ask, (msg) -> {
                if (msg.equals("^quit^")) {
                    sseService.complete(id);
                    log.info("ask quit");
                    return;
                }
                log.info("msg:{}", Base64.decodeStr(msg));
                sseService.sendMessage(id, msg);
            });
        });
        return ResponseEntity.ok().contentType(MediaType.TEXT_EVENT_STREAM).headers(httpHeaders).body(emitter);
    }

}
