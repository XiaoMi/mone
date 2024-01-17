/*
 * Copyright 2020 XiaoMi.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the following link.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package run.mone.mimeter.dashboard.bo.sceneapi;

import com.xiaomi.mone.http.docs.annotations.HttpApiDocClassDefine;
import lombok.Data;

import java.io.Serializable;

/**
 * Created by dongzhenxing on 2023/3/6 10:43 AM
 */
@Data
public class ApiX5Info implements Serializable {
    @HttpApiDocClassDefine(value = "enableX5", description = "是否使用x5协议", defaultValue = "false")
    private boolean enableX5;

    @HttpApiDocClassDefine(value = "appID", description = "应用id", defaultValue = "121")
    private String appId;

    @HttpApiDocClassDefine(value = "appKey", description = "应用密钥", defaultValue = "password")
    private String appKey;

    @HttpApiDocClassDefine(value = "x5Method", description = "方法", defaultValue = "get")
    private String x5Method;

    @HttpApiDocClassDefine(value = "x5Version", description = "x5的版本", defaultValue = "china")
    private String x5Version;

    public ApiX5Info() {
    }

    public ApiX5Info(boolean enableX5) {
        this.enableX5 = enableX5;
    }
}
