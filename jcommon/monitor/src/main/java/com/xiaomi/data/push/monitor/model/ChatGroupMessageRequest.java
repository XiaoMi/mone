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

package com.xiaomi.data.push.monitor.model;

import lombok.Data;

/**
 * @author maojinrui
 */
@Data
public class ChatGroupMessageRequest {
    private String chatid;
    private String msgtype;
    private Content text;
    private Integer safe;

    public ChatGroupMessageRequest(String chatid, String content) {
        this.chatid = chatid;
        this.msgtype = "text";
        this.text = new Content(content);
        this.safe = 0;
    }

    private static class Content {
        private String content;

        public Content(String content) {
            this.content = content;
        }
    }
}
