package com.xiaomi.mone.log.manager.user;

import cn.hutool.core.codec.Base64;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.xiaomi.mone.log.common.Config;
import com.xiaomi.youpin.docean.anno.Service;
import com.xiaomi.youpin.docean.common.StringUtils;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.IOException;
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

    public final static String BASE_IDM_URL = Config.ins().get("idm_url", "");

    public final static String IDM_APP_ID = Config.ins().get("idm_app_id", "");

    public final static String IDM_APP_KEY = Config.ins().get("idm_app_key", "");

    private String BASE_IDaas_URL = Config.ins().get("base_IDaas_url", "");

    public String IDaas_APP_ID = Config.ins().get("IDaas_app_id", "");

    public String IDaas_APP_KEY = Config.ins().get("IDaas_app_key", "");

    private OkHttpClient httpClient = new OkHttpClient();

    private Map<String, String> userNameCache = Maps.newConcurrentMap();

    private String queryIdmSignData(Map<String, Object> paramMap) {
        String body = GSON.toJson(paramMap);
        String sign = DigestUtils.md5Hex(IDM_APP_ID + body + IDM_APP_KEY).toUpperCase();
        JsonObject json = new JsonObject();
        JsonObject header = new JsonObject();
        header.addProperty("appid", IDM_APP_ID);
        header.addProperty("sign", sign);
        json.add("header", header);
        json.addProperty("body", body);
        return Base64.encode(json.toString().getBytes());
    }

    @Override
    public UseDetailInfo queryUser(String uId) {
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
                    log.error("IDM query user detail error,please contact them,return value:{},uId:{}", rstJson, uId);
                }
                UseDetailInfo detailInfo = rst.getData();
                if (detailInfo == null) {
                    return null;
                }
                List<UseDetailInfo.DeptDescriptor> deptDescriptors = GSON.fromJson(detailInfo.getFullDeptDesc(), new TypeToken<ArrayList<UseDetailInfo.DeptDescriptor>>() {
                }.getType());
                detailInfo.setFullDeptDescriptorList(null == deptDescriptors ? Collections.emptyList() : deptDescriptors);
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
    public UseDetailInfo queryUserByUserName(String userName) {
        String uId = queryUserUIdByUserName(userName);
        return queryUser(uId);
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
    public String queryUserUIdByUserName(String userName) {
        if (StringUtils.isEmpty(userName)) {
            return null;
        }
        String userTemp = userNameCache.get(userName);
        if (StringUtils.isNotBlank(userTemp)) {
            return userTemp;
        }
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("userName", userName);
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
                log.info("IDM get Uid by userName:{}", rstJson);
                userNameCache.put(userName, rst.getData());
                return rst.getData();
            }
            log.info("IDM return data by userName:{}", response.body().string());
        } catch (Exception e) {
            log.error(String.format("IDM query userDetail exception,url:%s,userName:%s,data:%s", url, userName, data), e);
        }
        log.error("IDM query userId by userName error,contact us");
        return null;
    }

    @Override
    public JsonArray queryChildDept(String deptId) {
        if (StringUtils.isEmpty(deptId)) {
            return null;
        }
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("deptId", deptId);
        String data = queryIdmSignData(paramMap);
        String url = BASE_IDM_URL + "/api/department/batch/queryChildrenByDeptId";
        try {
            Request request = new Request.Builder()
                    .url(url)
                    .post(new FormBody.Builder().add("data", data).build())
                    .build();
            Response response = httpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                String rstJson = response.body().string();
                IdmResponse<JsonArray> rst = GSON.fromJson(rstJson, new TypeToken<IdmResponse<JsonArray>>() {
                }.getType());
                return rst.getData();
            }
        } catch (Exception e) {
            log.error(String.format("IDM queryChildrenByDeptId exception,url:%s,deptId:%s,data:%s", url, deptId, data), e);
        }
        log.error("IDM queryChildrenByDeptId error,contact us");
        return null;
    }

    public List<UseDetailInfo> queryDeptPerson(String deptId) {
        List<String> personIdList = queryDeptPersonIds(deptId);
        if (personIdList == null || personIdList.isEmpty()) {
            return null;
        }
        List<UseDetailInfo> res = new ArrayList<>();
        for (String personId : personIdList) {
            res.add(queryUser(personId));
        }
        return res;
    }

    @Override
    public List<String> queryDeptPersonIds(String deptId) {
        if (StringUtils.isEmpty(deptId)) {
            return null;
        }
        Map<String, String> headParams = new HashMap<>();
        headParams.put("token", getIDaasToken());
        Request request = new Request.Builder()
                .url(BASE_IDaas_URL + "/api/v1/orgs/" + deptId + "/users")
                .addHeader("Cookie", "token=" + getIDaasToken())
                .build();
        Response response = null;
        try {
            response = httpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                String rstJson = response.body().string();
                IdmResponse<JsonObject> rst = new Gson().fromJson(rstJson, new TypeToken<IdmResponse<JsonObject>>() {
                }.getType());
                JsonObject data = rst.getData();
                JsonArray personList = data.getAsJsonArray("list");
                List<String> res = new ArrayList<>();
                for (JsonElement person : personList) {
                    res.add(person.getAsJsonObject().get("personId").getAsString());
                }
                return res;
            }
        } catch (IOException e) {
            log.error(String.format("IDaas queryDeptPersons exception,{}"), e);
        }
        log.error("IDaas queryDeptPersons error,contact us");
        return null;
    }

    public String getIDaasToken() {
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\"appId\":\"" + IDaas_APP_ID + "\",\"appSecret\":\"" + IDaas_APP_KEY + "\"}");
        Request request = new Request.Builder()
                .url(BASE_IDaas_URL + "/api/v1/auth/open")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .build();
        Response response = null;
        try {
            response = httpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                String rstJson = response.body().string();
                IdmResponse<JsonObject> rst = new Gson().fromJson(rstJson, new TypeToken<IdmResponse<JsonObject>>() {
                }.getType());
                String token = rst.getData().get("token").toString();
                return token.substring(0, token.length() - 1);
            }
        } catch (IOException e) {
            log.error(String.format("IDaas getIDaasToken exception,{}"), e);
        }
        log.error("IDaas getIDaasToken error,contact us");
        return null;
    }


}
