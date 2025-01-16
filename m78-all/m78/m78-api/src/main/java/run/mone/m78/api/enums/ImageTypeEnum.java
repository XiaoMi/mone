package run.mone.m78.api.enums;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author HawickMason@xiaomi.com
 * @date 3/21/24 11:09
 */
public enum ImageTypeEnum {

    AVATAR(0, "avatar"),
    NORMAL_IMAGE(2, "normal_image"),

    PDF(3,"pdf");

    private final int code;
    private final String desc;

    private static final Map<Integer, ImageTypeEnum> valMap = Arrays.stream(values()).collect(Collectors.toMap(ImageTypeEnum::getCode, Function.identity()));

    private static final Map<String, ImageTypeEnum> nameMap = Arrays.stream(values()).collect(Collectors.toMap(ImageTypeEnum::getDesc, Function.identity()));

    ImageTypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static ImageTypeEnum getTypeEnumByCode(int code) {
        return valMap.getOrDefault(code, AVATAR);
    }

    public static ImageTypeEnum getTypeEnumByName(String name) {
        return nameMap.getOrDefault(name, AVATAR);
    }
}
