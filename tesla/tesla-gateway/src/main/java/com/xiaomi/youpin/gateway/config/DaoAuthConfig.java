package com.xiaomi.youpin.gateway.config;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xiaomi.youpin.gateway.common.Utils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Configuration;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author shanwenbang@xiaomi.com
 * @date 2021/4/20
 */
@Configuration
public class DaoAuthConfig {
    public static final String SOURCE_SPLIT_CHAR = "|";

    private static String dacMd5;
    //json数组格式 [{},{}]
    @NacosValue(value = "${serverless.daoAuth.config:}", autoRefreshed = true)
    private String daoAuthConfigStr;

    private static Map<String, List<AuthItem>> daoAuthConfig;

    public synchronized Map<String, List<AuthItem>> getDaoAuthConfig() {
        if (StringUtils.isBlank(daoAuthConfigStr)) {
            return Maps.newHashMap();
        }

        String md5 = Utils.md5(daoAuthConfigStr);
        if (!md5.equals(dacMd5)) {
            List<AuthItem> authItemList = new Gson().fromJson(daoAuthConfigStr, new TypeToken<List<AuthItem>>(){}.getType());
            daoAuthConfig = authItemList.stream()
                    .collect(Collectors.groupingBy(a -> String.valueOf(a.getApiId()), Collectors.mapping(a -> a, Collectors.toList())));

            dacMd5 = md5;
        }

        return daoAuthConfig;
    }

    public static final class AuthItem implements Serializable {
        private Long apiId;

        /**
         * 授权实体
         * authType=0, authTarget = tableName
         * authType=1, authTarget = tableName|columnName
         */
        private String authTarget;

        // R|W|RW (read|write|rw)
        private String authCode;

        // 授权类型 0-表；1-字段
        private Integer authType;

        public AuthItem() {
        }

        public String getSplitTarget(int index) {
            String[] targetArr = authTarget.split(SOURCE_SPLIT_CHAR);
            if (index < targetArr.length) {
                return targetArr[index];
            } else {
                return null;
            }
        }

        public Long getApiId() {
            return apiId;
        }

        public void setApiId(Long apiId) {
            this.apiId = apiId;
        }

        public String getAuthTarget() {
            return authTarget;
        }

        public void setAuthTarget(String authTarget) {
            this.authTarget = authTarget;
        }

        public String getAuthCode() {
            return authCode;
        }

        public void setAuthCode(String authCode) {
            this.authCode = authCode;
        }

        public Integer getAuthType() {
            return authType;
        }

        public void setAuthType(Integer authType) {
            this.authType = authType;
        }

        @Override
        public String toString() {
            return "AuthItem{" +
                    "apiId=" + apiId +
                    ", authTarget='" + authTarget + '\'' +
                    ", authCode='" + authCode + '\'' +
                    ", authType=" + authType +
                    '}';
        }
    }


}
