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
package run.mone.mcp.idea.composer.service;

import com.google.gson.JsonObject;
import lombok.SneakyThrows;
import run.mone.mcp.idea.composer.config.Const;
import run.mone.hive.http.HttpClient;

public class ComposerService {

    private static final String IDEA_PORT = System.getenv("IDEA_PORT");

    @SneakyThrows
    public static JsonObject getProjectReportAndUserQuery(JsonObject jsonObject) {
        jsonObject.addProperty("cmd", "getProjectReport");
        JsonObject post = new HttpClient().post("http://" + jsonObject.get("athenaPluginHost").getAsString() + "/tianye", jsonObject);
        return post.get("content").getAsJsonObject();
    }

    @SneakyThrows
    public static String getFileByPath(String  filePath) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("filePath", filePath);
        jsonObject.addProperty("cmd", "read_code");
        JsonObject post = new HttpClient().post("http://" + Const.IP + ":" + IDEA_PORT + "/tianye", jsonObject);
        return post.get("fileContent").getAsString();
    }

    @SneakyThrows
    public static boolean fileExist(String  filePath) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("filePath", filePath);
        jsonObject.addProperty("cmd", "file_exist");
        JsonObject post = new HttpClient().post("http://" + Const.IP + ":" + IDEA_PORT + "/tianye", jsonObject);
        return post.get("result").getAsBoolean();
    }
}
