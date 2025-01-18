package run.mone.m78.api.bo.flow;

import lombok.Data;

import java.io.Serializable;

/**
 * @author wmin
 * @date 2024/2/29
 */
@Data
public class EndSetting implements Serializable {

    //var、content
    private String type;

    private String answerContent;

}
