package run.mone.ai.minimax.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequestBodyContent {

    @Builder.Default
    private String model = ModelEnum.Speech01.modelName;

    @Builder.Default
    private String voice_id = VoiceIdEnum.male_qn_qingse.voiceId;

    private String text;

    /**
     * 范围[0.5, 2]，取值越大，语速越快
     */
    @Builder.Default
    private double speed = 1.0;

    /**
     * 范围(0, 10],取值越大，音量越高
     */
    @Builder.Default
    private double vol = 1.0;

    /**
     * 默认值为mp3，可选范围：mp3、wav、pcm、flac、aac
     */
    @Builder.Default
    private String output_format = "mp3";
}
