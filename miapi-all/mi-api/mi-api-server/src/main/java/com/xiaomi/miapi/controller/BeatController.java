package com.xiaomi.miapi.controller;

import com.xiaomi.miapi.api.service.bo.BeatInfo;
import com.xiaomi.miapi.service.BeatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * @author dongzhenxing
 * @date 2023/02/08
 * deal with heart beat request
 */
@Controller
@RequestMapping("/OpenApi/Beat")
@Slf4j
public class BeatController {

    @Autowired
    private BeatService beatService;

    /**
     * business application will send their heart beat info to here
     */
    @RequestMapping(value = "/beat", method = RequestMethod.POST)
    @ResponseBody
    public void beat(@RequestBody BeatInfo beatInfo) {
        try {
            beatService.beat(beatInfo);
        } catch (Exception e) {
            log.error("do beat failed,module name:{},address:{},cause:{}",beatInfo.getModuleNames(),beatInfo.getAddress(),e);
        }
    }

}
