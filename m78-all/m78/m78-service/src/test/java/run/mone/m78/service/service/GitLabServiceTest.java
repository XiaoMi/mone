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

package run.mone.m78.service.service;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import run.mone.m78.api.bo.gitlab.GitTreeItem;
import run.mone.m78.service.service.git.impl.InnerGitLabServiceImpl;

import java.util.List;

/**
 * @author shanwb
 * @date 2024-02-26
 */
public class GitLabServiceTest {

    private static Gson gson  = new Gson();

    @Test
    public void testGetProjectStructureTree() {
        // Arrange
        String branch = "master";
        String projectId = "83504"; // 替换为你的项目ID
        String gitToken = ""; // 替换为你的访问令牌
        String gitDomain = "test.com";

        InnerGitLabServiceImpl service = new InnerGitLabServiceImpl();


        List<GitTreeItem> list = service.getProjectStructureTree(branch, gitDomain, gitToken, projectId);
        System.out.println(gson.toJson(list));

    }

}
