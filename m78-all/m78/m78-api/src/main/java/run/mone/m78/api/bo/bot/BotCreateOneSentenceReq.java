package run.mone.m78.api.bo.bot;

import lombok.Data;

import java.io.Serializable;
@Data
public class BotCreateOneSentenceReq implements Serializable {

    private String sentence;
}
