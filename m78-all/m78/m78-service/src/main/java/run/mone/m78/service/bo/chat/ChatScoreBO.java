package run.mone.m78.service.bo.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import run.mone.m78.service.dao.entity.ChatInfoPo;

/**
 * @author HawickMason@xiaomi.com
 * @date 1/16/24 16:12
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ChatScoreBO {

    private ChatInfoPo chatInfoPo;

    private double similarity;
}
