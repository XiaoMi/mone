package run.mone.m78.api.bo.plugins;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author HawickMason@xiaomi.com
 * @date 4/17/24 11:15 AM
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class BotPluginResTypeDTO {

    private int code;

    private String name;

}
