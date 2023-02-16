package com.xiaomi.mone.log.manager.controller;

import com.xiaomi.mone.log.common.Result;
import com.xiaomi.mone.log.manager.model.dto.RegionDTO;
import com.xiaomi.mone.log.manager.service.impl.NeoAppInfoServiceImpl;
import com.xiaomi.mone.log.manager.service.impl.RegionAvailableZoneServiceImpl;
import com.xiaomi.youpin.docean.anno.Controller;
import com.xiaomi.youpin.docean.anno.RequestMapping;
import com.xiaomi.youpin.docean.anno.RequestParam;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Controller
public class RegionZoneController {

    @Resource
    private RegionAvailableZoneServiceImpl regionAvailableZoneService;

    @Resource
    private NeoAppInfoServiceImpl neoAppInfoService;

    @RequestMapping(path = "/milog/regionzone/process", method = "get")
    public Result<Void> getRegion(@RequestParam(value = "open") String open) {
        //regionAvailableZoneService.clear();
        return Result.success();
    }

    @RequestMapping(path = "/milog/neo/process", method = "get")
    public Result<List<RegionDTO>> getNeo(@RequestParam(value = "treeId") String treeId) {
        List<String> list = new ArrayList<>();
        list.add(treeId);
        List<RegionDTO> regionDTOList = neoAppInfoService.getNeoAppInfo(list);
        return Result.success(regionDTOList);
    }
}
