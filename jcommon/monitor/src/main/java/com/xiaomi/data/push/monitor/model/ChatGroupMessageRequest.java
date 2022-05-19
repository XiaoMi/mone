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
