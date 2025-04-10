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
package run.mone.mcp.terminal.service;

import com.google.gson.JsonObject;
import lombok.SneakyThrows;
import run.mone.mcp.terminal.config.Const;
import run.mone.mcp.terminal.http.HttpClient;

public class AthenaService {

    private static final String IDEA_PORT = System.getenv("IDEA_PORT");

    @SneakyThrows
    public static void openTerminal() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("cmd", "terminal");
        jsonObject.addProperty("action", "open");
        new HttpClient().post("http://" + Const.IP + ":" + IDEA_PORT + "/tianye", jsonObject);
    }

    @SneakyThrows
    public static void openTerminalInDirectory(String workingDir) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("cmd", "terminal");
        jsonObject.addProperty("action", "openInDir");
        jsonObject.addProperty("workingDir", workingDir);
        new HttpClient().post("http://" + Const.IP + ":" + IDEA_PORT + "/tianye", jsonObject);
    }

    @SneakyThrows
    public static void openTerminalWithCommand(String command) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("cmd", "terminal");
        jsonObject.addProperty("action", "execute");
        jsonObject.addProperty("command", command);
        new HttpClient().post("http://" + Const.IP + ":" + IDEA_PORT + "/tianye", jsonObject);
    }

    @SneakyThrows
    public static String executeCommandAndGetResult(String command, int timeout) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("cmd", "terminal");
        jsonObject.addProperty("action", "executeAndGetResult");
        jsonObject.addProperty("command", command);
        jsonObject.addProperty("timeout", timeout);
        JsonObject post = new HttpClient().post("http://" + Const.IP + ":" + IDEA_PORT + "/tianye", jsonObject);
        return post.get("result").getAsString();
    }
}
