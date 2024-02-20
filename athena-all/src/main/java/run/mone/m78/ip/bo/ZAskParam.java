package run.mone.m78.ip.bo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author caobaoyu
 * @description: 请求Z平台的请求参数
 * @date 2023-04-21 15:43
 */
@Data
public class ZAskParam implements Serializable {
    private String token;
    private String prompt;
    private String type;

    private String name;

    private int num;

//    private boolean glm;

    private boolean chatGPT;

    private List<Integer> types;
}
