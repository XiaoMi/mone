package run.mone.knowledge.server.controller;

import com.xiaomi.data.push.redis.Redis;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(value = "/api")
public class TestController {


    @Autowired
    private Redis redis;

    /**
     * 进程心跳调用
     *
     * @return
     */
    @RequestMapping(value = "/isOk")
    public String isOk() {
        return "ok";
    }

    //从redis中读取指定key的值
    @RequestMapping(value = "/getValueFromRedis", method = RequestMethod.GET)
    public ResponseEntity<String> getValueFromRedis(@RequestParam("key") String key) {
        String value = redis.get(key);
        return value != null ? ResponseEntity.ok(value) : ResponseEntity.notFound().build();
    }

    //按指定key value写入redis
    @RequestMapping(value = "/setValueToRedis", method = RequestMethod.POST)
    public ResponseEntity<Void> setValueToRedis(@RequestParam("key") String key, @RequestParam("value") String value) {
        redis.set(key, value);
        return ResponseEntity.ok().build();
    }


}
