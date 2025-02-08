package run.mone.m78.service.bo.user;

import lombok.Data;

import java.io.Serializable;

@Data
public class SpeechToTextReq implements Serializable {

    private byte[] bytes;

    /**
     * base64编码后的字符串
     */
    private String text;

    private String format;
}
