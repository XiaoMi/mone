package run.mone.m78.service.bo.bot;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author caobaoyu
 * @description:
 * @date 2024-06-21 18:55
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BotExecuteRecordBo implements Serializable {

    private Long botId;

    private String username;

}
