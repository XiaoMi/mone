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

package com.xiaomi.youpin.tesla.plug.ioc;

import com.xiaomi.youpin.tesla.plug.common.PluginCommonVersion;
import com.xiaomi.youpin.tesla.plug.config.TeslaConfig;
import com.xiaomi.youpin.tesla.plug.nutz.DubboIocLoader;
import com.xiaomi.youpin.tesla.plug.nutz.DubboManager;
import com.xiaomi.youpin.tesla.plug.nutz.ResourceLoader;
import com.youpin.xiaomi.tesla.plugin.bo.PluginDatasource;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.NutIoc;
import org.nutz.ioc.loader.annotation.AnnotationIocLoader;
import org.nutz.ioc.loader.combo.ComboIocLoader;
import org.nutz.resource.Scans;
import org.nutz.resource.impl.JarResourceLocation;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author goodjava@qq.com
 */
@Data
@Slf4j
public class IocInit {

    private Ioc ioc;

    private CountDownLatch destoryLatch = new CountDownLatch(1);

    private IocInit() {
        log.info("iocInit:{}", new PluginCommonVersion());
        try {
            PluginDatasource pluginDatasource = TeslaConfig.ins().getPluginDatasource();
            //容错
            if (null == pluginDatasource) {
                pluginDatasource = new PluginDatasource();
                pluginDatasource.setIocPackage("com.xiaomi.youpin.tesla");
                pluginDatasource.setJarPath("/tmp/plugin/" + TeslaConfig.ins().getPluginId() + ".jar");
            }
            Thread.currentThread().setContextClassLoader(IocInit.class.getClassLoader());
            log.info("jar path:{} package:{} classLoader:{}", pluginDatasource.getJarPath(), pluginDatasource.getIocPackage(), Thread.currentThread().getContextClassLoader());

            if (Files.exists(Paths.get(pluginDatasource.getJarPath()))) {
                Scans.me().addResourceLocation(new JarResourceLocation(pluginDatasource.getJarPath()));
            } else {
                log.warn("plugin jar not exist");
            }

            List<Class<?>> clazzList = Scans.me().scanPackage(pluginDatasource.getIocPackage());

            boolean diffPak = false;
            //加载一些容器的类
            if (!pluginDatasource.getIocPackage().equals("com.xiaomi.youpin.tesla")) {
                clazzList.addAll(Scans.me().scanPackage("com.xiaomi.youpin.tesla"));
                diffPak = true;
            }
            String[] pks = null;
            if (diffPak) {
                pks = new String[]{"com.xiaomi.youpin.tesla", pluginDatasource.getIocPackage()};
            } else {
                pks = new String[]{pluginDatasource.getIocPackage()};
            }

            clazzList.stream().forEach(it -> log.info(it.getName()));
            AnnotationIocLoader al = new AnnotationIocLoader(pks);
            DubboIocLoader dubboIocLoader = new DubboIocLoader(pks);
            ResourceLoader resourceLoader = new ResourceLoader();
            ioc = new NutIoc(new ComboIocLoader(resourceLoader, al, dubboIocLoader));
            log.info("ioc names:{}", ioc.getNames());
            //会调用init
            ioc.get(DubboManager.class);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    }


    private static class IocInitLazyHolder {
        private static IocInit ins = new IocInit();
    }


    public static IocInit ins() {
        return IocInitLazyHolder.ins;
    }


    public <T> T get(Class<T> c) {
        return ioc.get(c);
    }

    public <T> T get(Class<T> c, String name) {
        return ioc.get(c, name);
    }

    /**
     * 销毁
     */
    public void destory() {
        ioc.depose();
        //制造堵塞
        try {
            destoryLatch.await(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 销毁完毕
     */
    public void destoryFinish() {
        destoryLatch.countDown();
    }
}
