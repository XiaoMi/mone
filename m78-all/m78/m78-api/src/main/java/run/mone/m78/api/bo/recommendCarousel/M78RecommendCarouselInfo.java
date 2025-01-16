package run.mone.m78.api.bo.recommendCarousel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class M78RecommendCarouselInfo {

    private Long id;

    private String title;

    private List<String> recommendReasons;

    private Integer type;

    private Integer displayStatus;

    private String backgroundUrl;

    private Long botId;

    private String botName;

    private String botCuser;

    private String botAvatar;

    /**
     * 开放权限0-私有 1-公开
     */
    private Integer botPermissions;

    private Long ctime;

    private Long utime;
}
