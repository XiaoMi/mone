package com.xiaomi.mone.monitor.service.user;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;

import java.util.*;

/**
 * @author wtt
 * @version 1.0
 * @description 从idm 系统中获取用户信息
 * @date 2021/9/7 10:37
 */
@Slf4j
@Service
public class IdmMoneUserDetailService implements MoneUserDetailService {

    @Value("${idm.url:noconfig}")
    private String BASE_IDM_URL;
    @Value("${idm.app.id}")
    private String IDM_APP_ID;
    @NacosValue("${idm.app.key:noconfig}")
    private String IDM_APP_KEY;

    @NacosValue(value = "${hera.access.member.white.list:noconfig}", autoRefreshed = true)
    private String accessWhiteList;

    @NacosValue(value = "${hera.access.member.black.list:noconfig}", autoRefreshed = true)
    private String accessBlackList;

    @NacosValue(value = "${hera.access.dept.black.list:noconfig}", autoRefreshed = true)
    private String accessDeptBlackList;

    @NacosValue(value = "${hera.admin.member.list:noconfig}", autoRefreshed = true)
    private String adminMemberList;

    private OkHttpClient httpClient = new OkHttpClient();

    private String queryIdmSignData(Map<String, Object> paramMap) {
        String body = GSON.toJson(paramMap);
        String sign = DigestUtils.md5Hex(IDM_APP_ID + body + IDM_APP_KEY).toUpperCase();
        JsonObject json = new JsonObject();
        JsonObject header = new JsonObject();
        header.addProperty("appid", IDM_APP_ID);
        header.addProperty("sign", sign);
        json.add("header", header);
        json.addProperty("body", body);
        return Base64Utils.encodeToString(json.toString().getBytes());
    }

    @Override
    public UseDetailInfo queryUser(String uId) {
        if (StringUtils.isBlank(uId)) {
            return null;
        }
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("uid", uId);
        String data = queryIdmSignData(paramMap);
        String url = BASE_IDM_URL + "/api/user/findUserBaseInfoByUid";
        try {
            Request request = new Request.Builder()
                    .url(url)
                    .post(new FormBody.Builder().add("data", data).build())
                    .build();
            Response response = httpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                String rstJson = response.body().string();
                IdmResponse<UseDetailInfo> rst = GSON.fromJson(rstJson, new TypeToken<IdmResponse<UseDetailInfo>>() {
                }.getType());
                if (!rst.getCode().equals(0)) {
                    log.error("IDM query user detail error,please contact them,return value:{}", rstJson);
                }
                UseDetailInfo detailInfo = rst.getData();
                List<UseDetailInfo.DeptDescr> deptDescrs = GSON.fromJson(detailInfo.getFullDeptDescr(), new TypeToken<ArrayList<UseDetailInfo.DeptDescr>>() {
                }.getType());
                detailInfo.setFullDeptDescrList(deptDescrs);
                log.info("IDM get userDetail:{}", GSON.toJson(rst));
                return detailInfo;
            }
            log.info("IDM return data:{}", response.body().string());
        } catch (Exception e) {
            log.error(String.format("IDM query userDetail exception,url:%s,uId:%s,data:{}", url, uId, data), e);
        }
        log.error("IDM query user error,contact us");
        return null;
    }

    @Override
    public String queryUserUIdByPhone(String phone) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("mobile", phone);
        String data = queryIdmSignData(paramMap);
        String url = BASE_IDM_URL + "/api/account/findUidByMobile";
        try {
            Request request = new Request.Builder()
                    .url(url)
                    .post(new FormBody.Builder().add("data", data).build())
                    .build();
            Response response = httpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                String rstJson = response.body().string();
                IdmResponse<String> rst = GSON.fromJson(rstJson, new TypeToken<IdmResponse<String>>() {
                }.getType());
                log.info("IDM get Uid by phone:{}", rst);
                return rst.getData();
            }
            log.info("IDM return data by phone:{}", response.body().string());
        } catch (Exception e) {
            log.error(String.format("IDM query userDetail exception,url:%s,phone:%s,data:%s", url, phone, data), e);
        }
        log.error("IDM query userId by phone error,contact us");
        return null;
    }

    @Override
    public String queryUserUIdByEmpId(String empId) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("personId", empId);
        String data = queryIdmSignData(paramMap);
        String url = BASE_IDM_URL + "/api/user/findUidByPersonId";
        try {
            Request request = new Request.Builder()
                    .url(url)
                    .post(new FormBody.Builder().add("data", data).build())
                    .build();
            Response response = httpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                String rstJson = response.body().string();
                IdmResponse<String> rst = GSON.fromJson(rstJson, new TypeToken<IdmResponse<String>>() {
                }.getType());
                log.info("IDM get Uid by empId:{}", rstJson);
                return rst.getData();
            }
            log.info("IDM return data by empId:{}", response.body().string());
        } catch (Exception e) {
            log.error(String.format("IDM query userDetail exception,url:%s,empId:%s,data:%s", url, empId, data), e);
        }
        log.error("IDM query userId by empId error,contact us");
        return null;
    }

    @Override
    public String queryUserUIdByUsername(String username) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("userName", username);
        String data = queryIdmSignData(paramMap);
        String url = BASE_IDM_URL + "/api/account/findUidByUserName";
        try {
            Request request = new Request.Builder()
                    .url(url)
                    .post(new FormBody.Builder().add("data", data).build())
                    .build();
            Response response = httpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                String rstJson = response.body().string();
                IdmResponse<String> rst = GSON.fromJson(rstJson, new TypeToken<IdmResponse<String>>() {
                }.getType());
                log.info("IDM get Uid by username:{}", rstJson);
                return rst.getData();
            }
            log.info("IDM return data by username:{}", response.body().string());
        } catch (Exception e) {
            log.error(String.format("IDM query userDetail exception,url:%s,username:%s,data:%s", url, username, data), e);
        }
        log.error("IDM query userId by username error,contact us");
        return null;
    }

    @Override
    public List<String> getWhiteList() {

        if(StringUtils.isBlank(accessWhiteList)){
            return Lists.newArrayList();
        }
        String[] members = accessWhiteList.split(",");

        return Arrays.asList(members);
    }

    @Override
    public List<String> getBlackList() {
        if(StringUtils.isBlank(accessBlackList)){
            return Lists.newArrayList();
        }
        String[] members = accessBlackList.split(",");

        return Arrays.asList(members);

    }

    @Override
    public List<String> getDeptBlackList() {
        if(StringUtils.isBlank(accessDeptBlackList)){
            return Lists.newArrayList();
        }
        String[] depts = accessDeptBlackList.split(",");

        return Arrays.asList(depts);
    }

    @Override
    public List<String> getAdminUserList() {
        if(StringUtils.isBlank(adminMemberList)){
            return Lists.newArrayList();
        }
        String[] admins = adminMemberList.split(",");

        return Arrays.asList(admins);
    }
}
