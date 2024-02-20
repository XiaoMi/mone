package run.mone.m78.ip.common;

import run.mone.m78.ip.bo.AiCodeRes;
import run.mone.m78.ip.bo.Tag;
import lombok.Data;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author caobaoyu
 * @description: 从Z获取一些基础配置 对应 -> 68 Athena の cfg center
 * @date 2023-05-30 11:02
 */
@Data
public class ConfigCenter {


    private static Map<String, AiCodeRes> copyrightingMap = new HashMap<>();

    @Getter
    private static List<String> guide = new ArrayList<>();

    private static List<Tag> menuTag = new ArrayList<>();

    public static Map<String, Tag> getMenuTag() {
        return menuTag.stream().collect(Collectors.toMap(Tag::getName, Function.identity()));
    }

    public static void init(String zAddr) {
        guide.clear();
        menuTag.clear();
        buildCopyrighting(zAddr);
    }

    private static void buildCopyrighting(String zAddr) {


    }

    public static AiCodeRes build(String type) {
        return copyrightingMap.get(type);
    }


}
