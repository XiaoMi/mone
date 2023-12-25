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
package run.mone.mimeter.engine.agent.bo.data;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by dongzhenxing on 2023/3/6 11:22 AM
 */
@Data
public class ApiX5InfoDTO implements Serializable {

    private boolean enableX5;

    private String appId;

    private String appKey;

    private String x5Method;

    private String x5Version;
}
