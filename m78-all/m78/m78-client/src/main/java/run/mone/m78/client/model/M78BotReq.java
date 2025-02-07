package run.mone.m78.client.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author HawickMason@xiaomi.com
 * @date 8/22/24 15:24
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class M78BotReq {

    private String botId;

    private String userName;

    private String input;

    private String token;
}
