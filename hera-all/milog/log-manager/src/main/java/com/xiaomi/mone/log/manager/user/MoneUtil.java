package com.xiaomi.mone.log.manager.user;

import cn.hutool.core.bean.BeanUtil;
import com.google.gson.Gson;
import com.xiaomi.mone.log.api.enums.ProjectSourceEnum;
import com.xiaomi.mone.log.common.Config;
import com.xiaomi.youpin.docean.Ioc;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2021/9/6 10:32
 */
@Slf4j
public class MoneUtil {

    /**
     * 数据签名+用户数据header key
     */
    public final static String HEADER_KEY_SIGN_AND_USER_DATA = "x-proxy-userdetail";

    public final static String HEADER_KEY_SIGN_AND_USER_DATA_EXAM = "X-Proxy-UserDetail";

    public final static String MONE_USER_INFO = "x-proxy-mone-user";

    public final static String MONE_USER_INFO_EXAM = "X-Proxy-Mone-User";

    public static Gson gson = new Gson();

    public static String ADMIN_DEPT_NAME = "效能组";
    public static String YOU_ZONE = "kaiyuan";
    public static String YOU_ZONE_CN = "kaiyuan";

    /**
     * 获取用户信息
     */
    public static MoneUser getUserInfo(String data) {
        UserIDService identifyService = Ioc.ins().getBean(MiDunUserIDService.class);
        return formatUserInfo(identifyService.findUserId(data));
    }

    @SneakyThrows
    public static MoneUser formatUserInfo(String userJson) {
        MoneUser moneUser;
        if (StringUtils.isNotBlank(userJson)) {
            moneUser = gson.fromJson(userJson, MoneUser.class);
            log.info("userJson:{},moneUser:{}", userJson, moneUser);
            handleUserBelongInfo(moneUser);
        } else {
            // 可能没有走米盾--走默认用户信息
            MoneUserDetailService userDetailService = Ioc.ins().getBean(IdmMoneUserDetailService.class);
            String uId = userDetailService.queryUserUIdByEmpId(Config.ins().get("default_login_user_emp_id", "52016"));
            UseDetailInfo useDetailInfo = userDetailService.queryUser(uId);
            // 可以模拟用户
//            UseDetailInfo useDetailInfo = userDetailService.queryUserByUserName("testse32");
            moneUser = new MoneUser();
            BeanUtil.copyProperties(useDetailInfo, moneUser, true);
            handleUserOtherInfo(moneUser, useDetailInfo);
        }

        return moneUser;
    }

    private static void handleUserBelongInfo(MoneUser moneUser) {
        MoneUserDetailService moneUserDetailService = Ioc.ins().getBean(IdmMoneUserDetailService.class);
        // http接口获取
        UseDetailInfo detailInfo = moneUserDetailService.queryUser(moneUser.getUID());
        handleUserOtherInfo(moneUser, detailInfo);
    }

    private static void handleUserOtherInfo(MoneUser moneUser, UseDetailInfo detailInfo) {
        if (null != detailInfo) {
            moneUser.setUser(detailInfo.getUserName());
            moneUser.setDeptId(detailInfo.getDeptId());
            moneUser.setCompany(detailInfo.getCompanyDesc());
            moneUser.setDepartmentName(detailInfo.getDeptDesc());
            moneUser.setAvatar(detailInfo.getHeadUrl());
            moneUser.setUser(detailInfo.getUserName());
            moneUser.setUID(detailInfo.getUid());
            List<UseDetailInfo.DeptDescriptor> fullDeptDescrList = detailInfo.getFullDeptDescriptorList();
            String deptNames = fullDeptDescrList.stream().map(UseDetailInfo.DeptDescriptor::getDeptName).collect(Collectors.joining(","));
            String zoneSource = ProjectSourceEnum.ONE_SOURCE.getSource();
            try {
                if (deptNames.contains(YOU_ZONE_CN)) {
                    zoneSource = ProjectSourceEnum.TWO_SOURCE.getSource();
                } else {
                    zoneSource = ProjectSourceEnum.querySourceByDesc(fullDeptDescrList.get(fullDeptDescrList.size() - 2).getDeptName());
                }
            } catch (Exception e) {
                log.error("query organizational structure error,", e);
            }
            moneUser.setZone(zoneSource);
            if (StringUtils.isNotBlank(detailInfo.getDeptDesc()) && detailInfo.getDeptDesc().contains(ADMIN_DEPT_NAME) && !YOU_ZONE.equals(zoneSource)) {
                moneUser.setIsAdmin(true);
            } else {
                moneUser.setIsAdmin(false);
            }
        }
    }

    public static MoneUser findMoneUserByUId(String uId) {
        MoneUser moneUser = new MoneUser();
        MoneUserDetailService moneUserDetailService = Ioc.ins().getBean(IdmMoneUserDetailService.class);
        UseDetailInfo detailInfo = moneUserDetailService.queryUser(uId);
        BeanUtil.copyProperties(detailInfo, moneUser);
        handleUserOtherInfo(moneUser, detailInfo);
        return moneUser;
    }

}
