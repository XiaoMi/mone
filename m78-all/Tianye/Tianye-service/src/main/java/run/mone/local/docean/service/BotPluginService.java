package run.mone.local.docean.service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.xiaomi.youpin.docean.anno.Service;
import com.xiaomi.youpin.docean.plugin.dubbo.anno.Reference;
import lombok.extern.slf4j.Slf4j;
import run.mone.local.docean.fsm.bo.PluginInfo;
import run.mone.m78.api.BotPluginProvider;
import run.mone.m78.api.bo.plugins.BotPluginDTO;

import java.util.Map;
import java.util.Optional;

/**
 * @author HawickMason@xiaomi.com
 * @date 3/5/24 16:08
 */
@Service
@Slf4j
public class BotPluginService {

    private final Gson gson = new Gson();

    @Reference(interfaceClass = BotPluginProvider.class, group = "${dubbo.group}", version = "${dubbo.version}", timeout = 30000, check = false)
    private BotPluginProvider botPluginProvider;


    public PluginInfo getPluginInfoById(Long id) {
        // mason TODO
        BotPluginDTO botPluginById = botPluginProvider.getBotPluginById(id);
        String apiUrl = botPluginById.getApiUrl();
        String meta = botPluginById.getMeta();
        JsonObject metaJson = gson.fromJson(meta, JsonObject.class);

        String display = Optional.ofNullable(metaJson.get("display")).map(it -> it.getAsString()).orElse("");

        JsonArray input = metaJson.get("input").getAsJsonArray();
        JsonArray output = metaJson.get("output").getAsJsonArray();

        Map<String, String> headersMap = gson.fromJson(metaJson.get("http_headers").getAsJsonObject(), new TypeToken<Map<String, Object>>() {
        }.getType());
        return PluginInfo.builder()
                .display(display)
                .url(apiUrl)
                .input(input)
                .name(botPluginById.getName())
                .output(output)
                .method(metaJson.get("http_method").getAsString())
                .headers(headersMap)
                .build();
    }

    public PluginInfo convertBotPluginDTOToPluginInfo(BotPluginDTO dto){
        String apiUrl = dto.getApiUrl();
        String meta = dto.getMeta();
        JsonObject metaJson = gson.fromJson(meta, JsonObject.class);

        String display = Optional.ofNullable(metaJson.get("display")).map(it -> it.getAsString()).orElse("");

        JsonArray input = metaJson.get("input").getAsJsonArray();
        JsonArray output = metaJson.get("output").getAsJsonArray();

        Map<String, String> headersMap = gson.fromJson(metaJson.get("http_headers").getAsJsonObject(), new TypeToken<Map<String, Object>>() {
        }.getType());
        return PluginInfo.builder()
                .display(display)
                .url(apiUrl)
                .input(input)
                .name(dto.getName())
                .output(output)
                .method(metaJson.get("http_method").getAsString())
                .headers(headersMap)
                .build();
    }

    public BotPluginDTO getPluginDtoById(Long id) {
        return botPluginProvider.getBotPluginById(id);
    }
}
