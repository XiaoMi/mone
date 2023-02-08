package com.xiaomi.youpin.gwdash.controller;

import com.xiaomi.youpin.gwdash.service.impl.TransferToolService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author goodjava@qq.com
 * @date 2022/10/2 22:02
 */
@Slf4j
@RestController
@RequestMapping("/tool")
public class ToolController {

    @Resource
    private TransferToolService toolService;

    @SneakyThrows
    @RequestMapping(value = "/transfer", method = RequestMethod.GET)
    public String transfer(@RequestParam("num")Integer num, @RequestParam("tenement")String tenement) {
        new Thread(() -> {
            toolService.transferData(num,tenement);
        }).start();
        return "ok";
    }
}
