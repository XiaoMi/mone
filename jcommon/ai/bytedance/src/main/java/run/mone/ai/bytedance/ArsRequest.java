package run.mone.ai.bytedance;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ArsRequest {

    private String appId;

    private String token;

    private String cluster;

    @Builder.Default
    private String audio_format = "mp3";

    private byte[] audio;
}
