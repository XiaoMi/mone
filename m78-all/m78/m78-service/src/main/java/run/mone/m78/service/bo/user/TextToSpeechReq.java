package run.mone.m78.service.bo.user;

import lombok.Data;

import java.io.Serializable;

@Data
public class TextToSpeechReq implements Serializable {

    private String text;

    private String voiceId;
}
