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
import lombok.extern.slf4j.Slf4j;
import run.mone.m78.ip.common.Context;

/**
 * @Author goodjava@qq.com
 * @Date 2021/11/7 21:43
 */
@Slf4j
public class TestService extends AbstractService {
    @Override
    public void execute(Context context, AnActionEvent e) {
        String content = context.getContent();
        if (content.equals("test")) {
        }
        this.next(context, e);
    }

}
