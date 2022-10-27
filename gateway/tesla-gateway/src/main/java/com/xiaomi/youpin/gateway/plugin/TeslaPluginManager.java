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

package com.xiaomi.youpin.gateway.plugin;


import com.google.common.collect.Maps;
import com.xiaomi.data.push.redis.Redis;
import com.xiaomi.youpin.infra.rpc.errors.GeneralCodes;
import com.youpin.xiaomi.tesla.plugin.bo.Request;
import com.youpin.xiaomi.tesla.plugin.bo.RequestContext;
import com.youpin.xiaomi.tesla.plugin.bo.Response;
import com.youpin.xiaomi.tesla.plugin.common.DsUtils;
import com.youpin.xiaomi.tesla.plugin.handler.IHandler;
import com.xiaomi.youpin.gateway.common.Keys;
import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.extern.slf4j.Slf4j;
import org.pf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author goodjava@qq.com
 * 插件管理容器
 */
@Slf4j
@Component
public class TeslaPluginManager {

    private PluginManager pluginManager;

    private ConcurrentHashMap<String, IHandler> handlerMap = new ConcurrentHashMap<>();


    @Autowired
    private Redis redis;

    @Value("${plugin.path}")
    private String pluginPath;

    /**
     * 处理落盘后的考虑是:
     * 这样就不用每次重启服务器后再次拉取plugin.jar
     */
    @PostConstruct
    public void init() {
        Map<String, Object> attachments = new HashMap<>(1);
        attachments.put(Const.ContextPath, pluginPath);
        pluginManager = new DefaultPluginManager(Paths.get(pluginPath), attachments);
        load();
        try {
            createPluginPath();
            watchPluginFile();
        } catch (Throwable ex) {
            log.error(ex.getMessage(), ex);
        }
    }

    private void createPluginPath() {
        if (!Files.exists(Paths.get(pluginPath))) {
            try {
                Files.createDirectories(Paths.get(pluginPath));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void watchPluginFile() {
        new Thread(() -> {
            try {
                WatchService watchService = FileSystems.getDefault().newWatchService();
                String filePath = pluginPath;
                Paths.get(filePath).register(watchService, StandardWatchEventKinds.ENTRY_CREATE,
                        StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE);
                while (true) {
                    WatchKey key = watchService.take();
                    List<WatchEvent<?>> watchEvents = key.pollEvents();
                    for (WatchEvent<?> event : watchEvents) {
                        //创建文件
                        if (StandardWatchEventKinds.ENTRY_CREATE == event.kind()) {
                            log.info("create：[" + filePath + "/" + event.context() + "]");
                        }
                        //文件发生修改
                        if (StandardWatchEventKinds.ENTRY_MODIFY == event.kind()) {
                            log.info("modify：[" + filePath + "/" + event.context() + "]");
                        }
                        //文件被删除
                        if (StandardWatchEventKinds.ENTRY_DELETE == event.kind()) {
                            log.info("delete：[" + filePath + "/" + event.context() + "]");
                        }

                    }
                    key.reset();
                }
            } catch (Throwable ex) {
                log.error(ex.getMessage(), ex);
            }
        }).start();
    }

    /**
     * 加载本地的插件
     * 启动的时候会执行
     */
    public void load() {
        try {
            pluginManager.loadPlugins();
            pluginManager.startPlugins();

            List<IHandler> handlerList = pluginManager.getExtensions(IHandler.class);
            handlerList.stream().filter(it -> null != it).forEach(it -> handlerMap.put(it.url(), it));

            pluginManager.getPlugins().stream().forEach(it -> log.info("load plugin:{}", it.getPluginId()));
        } catch (Throwable ex) {
            log.error("load plugin error:" + ex.getMessage(), ex);
        }
    }


    /**
     * 启动插件
     *
     * @param path
     * @param pluginId
     */
    public boolean startPlugin(String path, String pluginId, String dsIds) {
        try {
            log.info("startPlugin path:{} pluginId:{} dsIds:{}", path, pluginId, dsIds);
            pluginManager.loadPlugin(Paths.get(path));
            org.pf4j.PluginContext context = new org.pf4j.PluginContext();
            Map<String, Object> m = Maps.newHashMap();
            m.put("createTime", new Date().toString());
            m.put("dataSourceMap", getDatasourceMap(pluginId, dsIds));
            context.setAttachment(m);
            PluginState state = pluginManager.startPlugin(pluginId, context);

            log.info("start plugin:{} state:{}", pluginId, state);


            if (state.equals(PluginState.DISABLED) || state.equals(PluginState.STOPPED)) {
                //启动失败
                return false;
            }

            List<IHandler> list = pluginManager.getExtensions(IHandler.class, pluginId);

            log.info("handler list size:{}", list.size());

            list.stream().filter(it -> null != it).forEach(it -> {
                log.info("url:{}", it.url());
                handlerMap.put(it.url(), it);
            });
            return true;
        } catch (Throwable ex) {
            log.error(ex.getMessage(), ex);
            return false;
        }
    }


    /**
     * 获取数据源
     * 这个以后要从数据库中取
     * <p>
     * 这里需要从数据库里取
     *
     * @param pluginId
     * @param dsIds    数据源id列表
     * @return
     */
    private String getDatasourceMap(String pluginId, String dsIds) {
        return DsUtils.dsToString(dsIds, pluginId, it -> redis.get(Keys.dsKey(Long.valueOf(it))));
    }


    /**
     * 获取插件列表
     *
     * @return
     */
    public List<String> pluginList() {
        if (null != pluginManager) {
            return pluginManager.getPlugins().stream().map(it -> it.getPluginId()).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }


    public boolean startPluginByName(String fileName, String pluginId, String dsIds) {
        return startPlugin(pluginPath + File.separator + fileName, pluginId, dsIds);
    }

    public void stopPlugin(String pluginId, String url) {
        try {
            handlerMap.remove(url);

            PluginState state = pluginManager.stopPlugin(pluginId);
            log.info("stop plugin:{} state:{}", pluginId, state);

            pluginManager.unloadPlugin(pluginId);
        } catch (Throwable ex) {
            log.error(ex.getMessage(), ex);
        }

    }


    public Response call(String uri, RequestContext context, Request request) {
        IHandler h = this.handlerMap.get(uri);
        if (h != null) {
            return h.execute(context, request);
        }
        return new Response(GeneralCodes.NotFound.getCode(), HttpResponseStatus.NOT_FOUND.reasonPhrase(), "call uri:" + uri);
    }


}
