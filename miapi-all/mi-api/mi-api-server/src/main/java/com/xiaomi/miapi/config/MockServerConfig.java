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
package com.xiaomi.miapi.config;

import com.xiaomi.miapi.bo.MockServerInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by dongzhenxing on 2023/2/10 3:06 PM
 */
@Configuration
public class MockServerConfig {
    /**
     * mock server addr
     * this is the mock server address,you need to update
     * this after you start your mock server
     */
    @Value("${MiApi.mockAddr:http://127.0.0.1:8080}")
    private String mockUrlPrefix;

    @Bean
    public MockServerInfo mockServerInfo(){
        return new MockServerInfo(mockUrlPrefix);
    }

}
