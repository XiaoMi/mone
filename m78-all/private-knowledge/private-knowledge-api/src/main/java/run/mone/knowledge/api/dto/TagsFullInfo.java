package run.mone.knowledge.api.dto;

import lombok.Data;

import java.util.List;

/**
 * @author wmin
 * @date 2024/2/22
 */
@Data
public class TagsFullInfo {

    private TagsInfo tagsInfo;

    private List<String> tags;

    private String groupTag;

    private String leafTag;
}
