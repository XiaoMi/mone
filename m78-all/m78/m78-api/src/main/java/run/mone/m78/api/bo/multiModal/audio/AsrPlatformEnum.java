package run.mone.m78.api.bo.multiModal.audio;

import java.util.ArrayList;
import java.util.List;

public enum AsrPlatformEnum {
    TENCENT_ASR("tencent_asr"),
    ALI_ASR("ali_asr");

    private String asrPlatform;

    AsrPlatformEnum(String asrPlatform) {
        this.asrPlatform = asrPlatform;
    }

    public static List<String> getAll() {
        List<String> list = new ArrayList<>();
        for (AsrPlatformEnum platform : AsrPlatformEnum.values()) {
            list.add(platform.getAsrPlatform());
        }
        return list;
    }

    public static Boolean have(String asrPlatform) {
        for (AsrPlatformEnum platform : AsrPlatformEnum.values()) {
            if (platform.getAsrPlatform().equals(asrPlatform)) {
                return true;
            }
        }
        return false;
    }

    public String getAsrPlatform() {
        return this.asrPlatform;
    }
}
