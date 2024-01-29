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
import com.intellij.openapi.diagnostic.Logger;
import run.mone.m78.ip.common.Context;

/**
 * @Author goodjava@qq.com
 * @Date 2021/11/6 20:23
 */
public class ImageService extends AbstractService {

    private static final Logger log = Logger.getInstance(ImageService.class);

    /**
     * 使用ide 打开图片
     */
    public void open(String t) {

    }


    public void openImage(String url) {

    }

    @Override
    public void execute(Context context, AnActionEvent e) {

        this.next(context, e);
    }
}
