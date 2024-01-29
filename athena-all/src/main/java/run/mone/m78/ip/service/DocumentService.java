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
import org.apache.commons.lang3.tuple.Pair;

/**
 * @Author goodjava@qq.com
 * @Date 2021/11/6 12:08
 */
public class DocumentService {

    private static final Logger log = Logger.getInstance(DocumentService.class);

    /**
     * 用ide打开文件
     *
     * @param name
     * @param text
     */
    public void open(String name, String text) {
        log.info("open file");
    }

    /**
     * 获取文件内容和文件名(content,fileName)
     *
     * @param anActionEvent
     * @return
     */
    public Pair<String, String> getContent(AnActionEvent anActionEvent) {
        log.info("get content");
        return null;
    }

}
