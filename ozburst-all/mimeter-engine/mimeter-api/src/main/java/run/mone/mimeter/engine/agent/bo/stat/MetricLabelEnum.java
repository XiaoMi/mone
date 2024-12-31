package run.mone.mimeter.engine.agent.bo.stat;

import java.util.Arrays;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * @author Xirui Yang (yangxirui@xiaomi.com)
 * @version 1.0
 * @since 2022/8/26
 *
 * 注意：只许往后加枚举值
 */
public enum MetricLabelEnum {

    SCENE_ID(0, "sceneid"),
    REPORT_ID(1, "reportid"),
    SERIAL_ID(2, "serialid"),
    TYPE(3, "type"),
    CODE(4, "code"),
    URI(5, "uri"),
    METHOD(6, "method"),
    API_ID(7, "apiid"),
    SUCCESS(8, "success");

    private final int index;

    private final String name;

    private static final String[] labelNames = new String[MetricLabelEnum.values().length];

    private static final String[] labelApiRpsNames = new String[8];

    static {
        int i = 0;
        for (MetricLabelEnum label : MetricLabelEnum.values()) {
            labelNames[i++] = label.getName();
        }
        labelApiRpsNames[0] = SCENE_ID.name;
        labelApiRpsNames[1] = REPORT_ID.name;
        labelApiRpsNames[2] = SERIAL_ID.name;
        labelApiRpsNames[3] = TYPE.name;
        labelApiRpsNames[4] = URI.name;
        labelApiRpsNames[5] = METHOD.name;
        labelApiRpsNames[6] = API_ID.name;
        labelApiRpsNames[7] = SUCCESS.name;
    }

    public static String[] getLabelNames() {
        return labelNames;
    }

    public static String[] getLabelApiRpsNames() {
        return labelApiRpsNames;
    }

    public static String[] labelNameSlice(int len) {
        checkArgument(len > 0 && len <= labelNames.length, "MetricLabelEnum labelNames invalid len " + len);
        return Arrays.asList(labelNames).subList(0, len).toArray(new String[]{});
    }

    MetricLabelEnum(int index, String name) {
        this.index = index;
        this.name = name;
    }

    public int getIndex() {
        return index;
    }

    public String getName() {
        return name;
    }
}
