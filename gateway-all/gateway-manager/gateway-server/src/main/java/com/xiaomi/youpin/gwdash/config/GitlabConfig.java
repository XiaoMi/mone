/*
 *  Copyright 2020 Xiaomi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.xiaomi.youpin.gwdash.config;

import com.xiaomi.youpin.gitlab.Gitlab;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.xiaomi.youpin.gitlab.GitlabConst.GIT_API_URL;

@Configuration
public class GitlabConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(GitlabConfig.class);


    @Bean(name = "gitlabHandler")
    public Gitlab getGitlabHandler() {
        LOGGER.info("[GitlabConfig.getGitlabHandler] gitlab base url: {}", GIT_API_URL);
        Gitlab gitlab = new Gitlab(GIT_API_URL);
        return gitlab;
    }

}
