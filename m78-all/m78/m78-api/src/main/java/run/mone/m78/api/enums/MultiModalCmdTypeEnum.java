package run.mone.m78.api.enums;


/**
 * @author wmin
 * @date 2024/2/29
 */
public enum MultiModalCmdTypeEnum {

    IMAGE_UNDERSTAND(1, "imageUnderstand"),
    ART_WORD(2, "artWord"),
    BACKGROUND_GEN(3, "backgroundGen"),
    SKETCH_TO_IMAGE(4, "sketchToImage"),
    TEXT_AND_IMAGE(5, "textAndImage"),
    STYLE_REPAINT(6, "styleRepaint"),
    TEXT_TO_IMAGE(7, "textToImage"),
    WORK_CHART(8, "workChart"),

    AUDIO_TO_TEXT(9, "audioToText"),
    TEXT_TO_AUDIO(10, "textToAudio");

    private final int code;
    private final String name;

    MultiModalCmdTypeEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getCode() {
        return code;
    }
}
