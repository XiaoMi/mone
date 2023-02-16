package com.xiaomi.mone.monitor.service.user;

import com.google.gson.Gson;

import java.util.List;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2021/9/7 10:36
 */
public interface MoneUserDetailService {

    Gson GSON = new Gson();

    /**
     * 查询用户详情
     *
     * @param uId
     * @return
     */
    UseDetailInfo queryUser(String uId);

    /**
     * 根据手机查询用户唯一Id
     *
     * @param phone
     * @return
     */
    String queryUserUIdByPhone(String phone);


    /**
     * 根据员工号查询用户唯一Id
     *
     * @param empId
     * @return
     */
    String queryUserUIdByEmpId(String empId);
    String queryUserUIdByUsername(String empId);


    List<String> getWhiteList();

    List<String> getBlackList();

    List<String> getDeptBlackList();

    List<String> getAdminUserList();

}
