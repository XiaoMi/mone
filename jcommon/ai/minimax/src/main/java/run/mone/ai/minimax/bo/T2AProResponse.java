package run.mone.ai.minimax.bo;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class T2AProResponse {

    /**
     * 合成的音频下载链接
     */
    @SerializedName(value = "audio_file")
    private String audioFile;

    /**
     * 合成的字幕下载链接
     */
    @SerializedName(value = "subtitle_file")
    private String subtitleFile;

    @SerializedName(value = "extra_info")
    private ExtraInfo extraInfo;

    @SerializedName(value = "base_resp")
    private BaseResponse baseResponse;
}
