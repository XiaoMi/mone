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

package com.xiaomi.youpin.tesla.dev.bootstrap;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xiaomi.youpin.tesla.bo.GetDsParam;
import com.xiaomi.youpin.tesla.bo.Result;
import com.xiaomi.youpin.tesla.plug.TeslaPlugin;
import com.xiaomi.youpin.tesla.plug.ioc.IocInit;
import lombok.extern.slf4j.Slf4j;
import org.nutz.http.Header;
import org.nutz.http.Http;
import org.nutz.http.Request;
import org.nutz.http.Response;
import org.nutz.http.sender.PostSender;
import org.nutz.ioc.Ioc;
import org.pf4j.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author goodjava@qq.com
 */
@Slf4j
public class PluginInit {


    public void getDsConfig(String address, String url, String filePath, String userName, String token) {
        Request req = Request.create("http://" + address + "/open/plugin/dsconfig", Request.METHOD.POST);
        Map<String, String> mh = new HashMap<>();
        mh.put("Content-Type", "application/json");
        Header header = Header.create(mh);
        req.setHeader(header);

        GetDsParam param = new GetDsParam();
        param.setUrl(url);
        param.setUserName(userName);
        param.setToken(token);


        req.setData(new Gson().toJson(param));
        PostSender ps = new PostSender(req);
        Response res = ps.setTimeout(3000).send();
        log.info(res.getContent());

        Gson gson = new Gson();
        Result<String> r = gson.fromJson(res.getContent(), new TypeToken<Result<String>>() {
        }.getType());

        PluginContext pluginContext = new PluginContext();
        Map<String, Object> m = new HashMap<>(1);
        m.put("dataSourceMap", r.getData());
        pluginContext.setAttachment(m);

        try {
            Files.write(Paths.get(filePath), new Gson().toJson(pluginContext).getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public Ioc init(String pluginId, String path) throws PluginException {
        PluginDescriptor pluginDescriptor = new DefaultPluginDescriptor(pluginId, "", "", "", "", "", "");
        PluginWrapper wrapper = new PluginWrapper(null, pluginDescriptor, null, null);
        TeslaPlugin teslaPlugin = new TeslaPlugin(wrapper, path);
        teslaPlugin.start();
        return IocInit.ins().getIoc();
    }
}
