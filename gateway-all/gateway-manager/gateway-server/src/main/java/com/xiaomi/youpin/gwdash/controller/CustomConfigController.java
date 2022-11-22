package com.xiaomi.youpin.gwdash.controller;


import com.xiaomi.youpin.gwdash.common.Result;
import com.xiaomi.youpin.gwdash.dao.model.TCustomeConfig;
import com.xiaomi.youpin.gwdash.service.CustomConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/api/custom/config")
public class CustomConfigController {

    @Autowired
    private CustomConfigService customConfigService;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public Result<Map<String, Object>> list(HttpServletRequest request,
                                            @RequestParam(value = "page", defaultValue = "1") int page,
                                            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        return Result.success(customConfigService.list(page, pageSize));
    }

    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public Result<TCustomeConfig> get(@RequestParam("key") String key) {
        return Result.success(customConfigService.get(key));
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public Result<Boolean> create(HttpServletRequest request,
                                  @RequestBody TCustomeConfig tCustomeConfig) {
        return Result.success(customConfigService.create(tCustomeConfig));
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public Result<Boolean> update(HttpServletRequest request,
                                  @RequestBody TCustomeConfig tCustomeConfig) {
        return Result.success(customConfigService.update(tCustomeConfig));
    }

    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    public Result<Integer> delete(HttpServletRequest request,
                                  @RequestParam(value = "id", required = true) long id) {
        return Result.success(customConfigService.delete(id));
    }
}
