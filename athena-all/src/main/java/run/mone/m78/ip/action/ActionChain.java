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

package run.mone.m78.ip.action;

import com.intellij.openapi.actionSystem.AnActionEvent;
import run.mone.m78.ip.common.Context;
import run.mone.m78.ip.service.*;

/**
 * @Author goodjava@qq.com
 * @Date 2021/11/7 21:00
 */
public class ActionChain {


    private AbstractService testService;

    public void init() {
        testService = new TestService();

        ChatGptService chatGptService = new ChatGptService();
        testService.setNext(chatGptService);

        CodeService codeService = new CodeService();
        chatGptService.setNext(codeService);

        MusicService musicService = MusicService.ins();
        codeService.setNext(musicService);

        ImageService imageService = new ImageService();
        musicService.setNext(imageService);

        UserService userService = new UserService();
        imageService.setNext(userService);

    }


    public void execute(Context context, AnActionEvent e) {
        testService.execute(context, e);
    }

}
