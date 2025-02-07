package run.mone.m78.api;

import org.apache.commons.lang3.tuple.Pair;
import run.mone.m78.api.bo.plugins.BotPluginDTO;
import run.mone.m78.api.bo.plugins.PluginReq;

import java.util.List;

/**
 * @author HawickMason@xiaomi.com
 * @date 3/5/24 13:52
 */
public interface BotPluginProvider {

    Pair<Long, List<BotPluginDTO>> getBotPlugins(PluginReq req);

    BotPluginDTO getBotPluginById(Long id);

}
