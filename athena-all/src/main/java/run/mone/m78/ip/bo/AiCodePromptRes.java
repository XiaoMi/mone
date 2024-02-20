package run.mone.m78.ip.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author caobaoyu
 * @description:
 * @date 2023-06-06 11:15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiCodePromptRes {
    private String promptName;
    private String desc;
    private String meta;
    private String type;
    private String showDialog;
    private String prefix = "";
    private String suffix = "";
    private String src;
}
