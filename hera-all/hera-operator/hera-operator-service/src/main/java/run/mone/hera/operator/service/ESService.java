/*
 * Copyright 2020 Xiaomi
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package run.mone.hera.operator.service;

import com.xiaomi.youpin.docean.anno.Service;
import org.apache.commons.lang3.StringUtils;
import run.mone.hera.operator.common.ESIndexConst;
import run.mone.hera.operator.common.HttpClientUtil;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description
 * @Author dingtao
 * @Date 2023/2/22 11:29 AM
 */
@Service
public class ESService {

    private Base64.Encoder base64 = Base64.getEncoder();

    public void createESTemplate(String esUrl, String userName, String password) {
        // create header
        Map<String, String> headers = new HashMap<>();
        if (StringUtils.isNotEmpty(userName) && StringUtils.isNotEmpty(password)) {
            headers.put("Authorization", generateOuth2(userName, password));
        }
        headers.put("Content-type", "application/json; charset=UTF-8");

        for (String indexName : ESIndexConst.templates.keySet()) {
            HttpClientUtil.sendPostRequest(generateUrl(esUrl, indexName), ESIndexConst.templates.get(indexName), headers);
        }
    }

    private String generateUrl(String esUrl, String index) {
        return "http://" + esUrl + "/_template/" + index;
    }

    private String generateOuth2(String userName, String password) {
        String oriStr = userName + ":" + password;
        String base64Str = new String(base64.encode(oriStr.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
        return "Basic " + base64Str;
    }
}
