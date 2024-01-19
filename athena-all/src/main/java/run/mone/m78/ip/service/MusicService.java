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

package run.mone.m78.ip.service;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import run.mone.m78.ip.action.ActionEnum;
import run.mone.m78.ip.common.ApiCall;
import run.mone.m78.ip.common.Context;
import run.mone.m78.ip.common.Mp3PlayerV2;
import run.mone.m78.ip.common.NotificationCenter;
import run.mone.m78.ip.common.Safe;
import org.apache.commons.lang.StringUtils;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @Author goodjava@qq.com
 * @Date 2021/11/6 11:46
 */
public class MusicService extends AbstractService {

    private List<String> list = new ArrayList<>();

    private int index;

    private String musicUrl;

    public void setList(List<String> list) {
        this.list = list;
    }

    private static final class LazyHolder {
        private static MusicService ins = new MusicService();
    }

    public static MusicService ins() {
        return LazyHolder.ins;
    }

    private MusicService() {
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            if (StringUtils.isNotEmpty(musicUrl)) {
                if (Mp3PlayerV2.ins().isComplete()) {
                    play(musicUrl);
                }
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    public synchronized void play(String t) {
        musicUrl = t;
        ApplicationManager.getApplication().invokeLater(() -> {
            Safe.run(() -> {
                if (StringUtils.startsWith(t, ActionEnum.diga.name() + ":")) {
                    String url = new ApiCall().callIt(ApiCall.MUSIC_API, t.replaceFirst(ActionEnum.diga.name() + ":", "") + ".mp3");
                    InputStream input = new URL(url).openStream();
                    Mp3PlayerV2.ins().play(input);
                    return;
                }
                String url = new ApiCall().callOne(ApiCall.MUSIC_API);
                NotificationCenter.notice("music:" + url);
                InputStream input = new URL(url).openStream();
                //这里会启用一个新的线程
                Mp3PlayerV2.ins().play(input);
            });
        });
    }

    public void playWithUrl(String url) {
        Safe.run(() -> {
            InputStream input = new URL(url).openStream();
            Mp3PlayerV2.ins().play(input);
        });
    }

    public synchronized void stop() {
        this.musicUrl = "";
        Safe.run(() -> Mp3PlayerV2.ins().close());
    }

    @Override
    public void execute(Context context, AnActionEvent e) {
        String content = context.getContent();
        if (content.equals(ActionEnum.diga.name()) || StringUtils.startsWith(content, ActionEnum.diga.name() + ":")) {
            play(content);
            return;
        }

        if (content.equals(ActionEnum.diga_stop.name())) {
            stop();
            return;
        }

        this.next(context, e);
    }
}
