package run.mone.ai.minimax.bo;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class ExtraInfo {

    @SerializedName(value = "audio_length")
    private Long audioLength;

    @SerializedName(value = "audio_sample_rate")
    private Long audioSampleRate;

    @SerializedName(value = "audio_size")
    private Long audioSize;

    private Long bitrate;

    @SerializedName(value = "word_count")
    private Long wordCount;

    @SerializedName(value = "invisible_character_ratio")
    private Double invisibleCharacterRatio;

    @SerializedName(value = "usage_characters")
    private Long usageCharacters;
}
