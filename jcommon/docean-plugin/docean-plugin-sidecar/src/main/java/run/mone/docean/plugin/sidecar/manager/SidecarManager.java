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

package run.mone.docean.plugin.sidecar.manager;

import com.xiaomi.youpin.docean.anno.Service;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import run.mone.docean.plugin.sidecar.bo.SideCarApp;
import run.mone.docean.plugin.sidecar.state.client.PingState;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author goodjava@qq.com
 * @date 2022/6/19
 */
@Data
@Service
@Slf4j
public class SidecarManager {

    private ConcurrentHashMap<String, SideCarApp> apps = new ConcurrentHashMap<>();

    private final long TIME_OUT = PingState.TIME * 3;

    public void init() {
        Executors.newSingleThreadScheduledExecutor().scheduleWithFixedDelay(() -> {
            long now = System.currentTimeMillis();
            apps.values().stream().filter(it -> {
                if (now - it.getUtime() > TIME_OUT) {
                    log.info("app:{} reg timeout remove", it.getApp());
                    return true;
                }
                return false;
            }).map(it -> it.getApp()).collect(Collectors.toList()).forEach(it -> apps.remove(it));
        }, 0, 10, TimeUnit.SECONDS);
    }

    public void putApp(SideCarApp app) {
        log.info("put app:{}", app.getApp());
        app.setUtime(System.currentTimeMillis());
        this.apps.put(app.getApp(), app);
    }

    public void removeApp(SideCarApp app) {
        log.info("remove app:{}", app.getApp());
        this.apps.remove(app.getApp());
    }

    public void updateSideCar(String app) {
        this.apps.computeIfPresent(app, (k, v) -> {
            v.setUtime(System.currentTimeMillis());
            return v;
        });
    }

    public List<SideCarApp> filterApp(Predicate<SideCarApp> predicate) {
        return apps.values().stream().filter(it -> predicate.test(it)).collect(Collectors.toList());
    }

    public List<SideCarApp> filterApp(Predicate<SideCarApp> predicate, Runnable notFoundRunnable) {
        List<SideCarApp> list = filterApp(predicate);
        if (list.size() == 0) {
            notFoundRunnable.run();
        }
        return list;
    }

}
