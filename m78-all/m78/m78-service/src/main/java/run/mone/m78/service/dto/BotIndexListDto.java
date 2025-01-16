package run.mone.m78.service.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author caobaoyu
 * @description:
 * @date 2024-03-05 20:20
 */
@Data
public class BotIndexListDto implements Serializable {

    private List<BotBaseInfoDto> botList;

    private List<BotBaseInfoDto> pluginList;
}
