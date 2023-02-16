package com.xiaomi.mone.log.manager.service;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.xiaomi.mone.log.api.enums.MachineRegionEnum;
import com.xiaomi.mone.log.manager.model.dto.PodDTO;
import com.xiaomi.mone.log.manager.model.dto.RegionDTO;
import com.xiaomi.mone.log.manager.service.impl.LogSpaceServiceImpl;
import com.xiaomi.mone.log.manager.service.impl.LogTailServiceImpl;
import com.xiaomi.mone.log.manager.service.impl.MilogAppMiddlewareRelServiceImpl;
import com.xiaomi.mone.log.manager.service.impl.NeoAppInfoServiceImpl;
import com.xiaomi.youpin.docean.Ioc;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class MilogSpaceServiceTest {
    private LogSpaceServiceImpl minilogSpaceService;
    private Gson gson = new Gson();

    @Before
    public void buildBean() {
        Ioc.ins().init("com.xiaomi");
        minilogSpaceService = Ioc.ins().getBean(LogSpaceServiceImpl.class);
    }

    @Test
    public void refreshSpaceDeptId() {
        minilogSpaceService.refreshSpaceDeptId();
    }

    @Test
    public void test() {
        Ioc.ins().init("com.xiaomi");
        NeoAppInfoServiceImpl neoAppInfoService = Ioc.ins().getBean(NeoAppInfoServiceImpl.class);
        LogTailServiceImpl milogLogtailService = Ioc.ins().getBean(LogTailServiceImpl.class);
        List<String> treeIds = Lists.newArrayList("4541", "5068", "5530", "5845", "5846", "10000368");
        List<RegionDTO> neoAppInfos = neoAppInfoService.getNeoAppInfo(treeIds);
        List<PodDTO> podDTOS = milogLogtailService.regionDTOTransferSimpleAppDTOs(neoAppInfos, MachineRegionEnum.CN_MACHINE);
        System.out.println("result:" + gson.toJson(podDTOS));
    }

    @Test
    public void testMiddlewareRelService() {
        Ioc.ins().init("com.xiaomi");
        MilogAppMiddlewareRelServiceImpl middlewareRelService = Ioc.ins().getBean(MilogAppMiddlewareRelServiceImpl.class);
        Long tailId = 554L;
        Long milogAppId = 83L;
        Long middleWareId = 2L;
        middlewareRelService.defaultBindingAppTailConfigRel(
                tailId, milogAppId, middleWareId, "", null);
    }
}