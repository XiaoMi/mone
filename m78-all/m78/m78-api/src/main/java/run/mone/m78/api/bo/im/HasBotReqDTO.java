package run.mone.m78.api.bo.im;

import lombok.Data;

import java.io.Serializable;

/**
 * @author caobaoyu
 * @description:
 * @date 2024-03-25 16:37
 */
@Data
public class HasBotReqDTO implements Serializable {

    private String user;
    private Integer imType;

}
