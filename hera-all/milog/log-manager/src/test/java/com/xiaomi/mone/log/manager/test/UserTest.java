package com.xiaomi.mone.log.manager.test;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.xiaomi.mone.log.common.Constant;
import com.xiaomi.mone.log.manager.dao.MilogAppMiddlewareRelDao;
import com.xiaomi.mone.log.manager.dao.MilogRegionAvailableZoneDao;
import com.xiaomi.mone.log.manager.model.pojo.MilogAppMiddlewareRel;
import com.xiaomi.mone.log.manager.model.pojo.MilogRegionAvailableZoneDO;
import com.xiaomi.mone.log.manager.user.*;
import com.xiaomi.youpin.docean.Ioc;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.time.Instant;
import java.util.List;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2021/9/9 17:44
 */
@Slf4j
public class UserTest {

    private Gson gson = new Gson();

    @Test
    public void queryUserByPhoneIdTest() {
        Ioc.ins().init("com.xiaomi");
        String phone = "12345678911";
        IdmMoneUserDetailService userService = Ioc.ins().getBean(IdmMoneUserDetailService.class);
//        String uId = userService.queryUserUIdByPhone(phone);
        String uId = "test";
        UseDetailInfo useDetailInfo = userService.queryUser(uId);
        log.info("UseDetailInfo:{},phone:{}", gson.toJson(useDetailInfo), phone);

        MilogAppMiddlewareRelDao milogAppMiddlewareRelDao = Ioc.ins().getBean(MilogAppMiddlewareRelDao.class);

        List<MilogAppMiddlewareRel> milogAppMiddlewareRels = milogAppMiddlewareRelDao.queryByCondition(null, 1L, 304L);
        log.info("result:{}", milogAppMiddlewareRels);
    }

    @Test
    public void userIdByPhoneTest() {
        Ioc.ins().init("com.xiaomi");
        String phone = "543535435";
        IdmMoneUserDetailService userService = Ioc.ins().getBean(IdmMoneUserDetailService.class);
        String uId = userService.queryUserUIdByPhone(phone);
        log.info("userId:{},phone:{}", uId, phone);
    }

    @Test
    public void userIdByEmpIdTest() {
        Ioc.ins().init("com.xiaomi");
        String empId = "52016test";
        IdmMoneUserDetailService userService = Ioc.ins().getBean(IdmMoneUserDetailService.class);
        String uId = userService.queryUserUIdByEmpId(empId);
        log.info("userId:{},phone:{}", uId, empId);
    }

    @Test
    public void userIdByUserNameTest() {
        Ioc.ins().init("com.xiaomi");
        String userName = "zhangsan";
        IdmMoneUserDetailService userService = Ioc.ins().getBean(IdmMoneUserDetailService.class);
        String uId = userService.queryUserUIdByUserName(userName);
        log.info("userId:{},userName:{}", uId, userName);
    }

    @Test
    public void testUser() {
        Ioc.ins().init("com.xiaomi");
        String empId = "52016test";
        MoneUser moneUId = MoneUtil.findMoneUserByUId("65464656");
        System.out.println(moneUId);

    }

    @Test
    public void test1() {
        Ioc.ins().init("com.xiaomi");
        String headerData = "stsetest32432432";
        MoneUser moneUser = MoneUtil.getUserInfo(headerData);
        log.info("登陆人的信息：{}", new Gson().toJson(moneUser));

    }

    @Test
    public void queryChildDept() {
        Ioc.ins().init("com.xiaomi");
        IdmMoneUserDetailService userService = Ioc.ins().getBean(IdmMoneUserDetailService.class);
        JsonArray dept = userService.queryChildDept("MI");
        log.info("dept:{}", dept);
    }

    @Test
    public void testInsert() {
        Ioc.ins().init("com.xiaomi");
        String jsonStr = "";
        RegionZoneBO regionZoneBO = gson.fromJson(jsonStr, RegionZoneBO.class);
        MilogRegionAvailableZoneDao regionAvailableZoneDao = Ioc.ins().getBean(MilogRegionAvailableZoneDao.class.getCanonicalName());
        regionZoneBO.getData().forEach(innerClass -> {
            if (innerClass.getIs_used()) {
                MilogRegionAvailableZoneDO milogRegionAvailableZoneDO = new MilogRegionAvailableZoneDO();
                milogRegionAvailableZoneDO.setRegionNameEN(innerClass.getRegion_en());
                milogRegionAvailableZoneDO.setRegionNameCN(innerClass.getRegion_cn());
                milogRegionAvailableZoneDO.setZoneNameCN(innerClass.getZone_name_cn());
                milogRegionAvailableZoneDO.setZoneNameEN(innerClass.getZone_name_en());
                milogRegionAvailableZoneDO.setCtime(Instant.now().toEpochMilli());
                milogRegionAvailableZoneDO.setUtime(Instant.now().toEpochMilli());
                milogRegionAvailableZoneDO.setCreator(Constant.DEFAULT_OPERATOR);
                milogRegionAvailableZoneDO.setUpdater(Constant.DEFAULT_OPERATOR);
                regionAvailableZoneDao.insert(milogRegionAvailableZoneDO);
            }
        });
        System.out.println(regionZoneBO);
    }
}
