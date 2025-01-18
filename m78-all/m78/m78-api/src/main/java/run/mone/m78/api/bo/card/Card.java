package run.mone.m78.api.bo.card;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 卡片
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Card {

    private Long id;

    private String name;

    private Long elementId;

    private Long workspaceId;

    private String type;

    private Integer status;

    private Integer official;

    private String description;

    private String creator;

    private String updater;

    private Long ctime;

    private Long utime;
}
