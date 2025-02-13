package run.mone.moner.server.bo;

import lombok.Data;

/**
 * @author wmin
 * @date 2024/1/15
 */
@Data
public class Text2AudioParam {
    private String text;
    private String dialect;
    private String voice;
}
