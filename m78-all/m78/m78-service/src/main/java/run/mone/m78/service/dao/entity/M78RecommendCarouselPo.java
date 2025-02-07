package run.mone.m78.service.dao.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static run.mone.m78.api.constant.TableConstant.RECOMMEND_CAROUSEL_TABLE;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Table(RECOMMEND_CAROUSEL_TABLE)
public class M78RecommendCarouselPo {

    @Id(keyType = KeyType.Auto)
    private Long id;

    @Column
    private String title;

    @Column("recommend_reasons")
    private String recommendReasons;

    @Column
    private Integer type;

    @Column("display_status")
    private Integer displayStatus;

    @Column("background_url")
    private String backgroundUrl;

    @Column("bot_id")
    private Long botId;

    @Column
    private Long ctime;

    @Column
    private Long utime;
}
