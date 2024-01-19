package run.mone.m78.ip.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author caobaoyu
 * @description:
 * @date 2023-05-30 14:25
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiCodeRes implements Serializable {
    private String msg;
    private Long tagId;
    private String tagName;
    private Boolean selected;
    private List<AiCodePromptRes> promptInfoList;

}
