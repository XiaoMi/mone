package run.mone.m78.service.bo.file.moonshot;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MoonshotContentRes implements Serializable {

    @SerializedName("content")
    private String content;

    @SerializedName("file_type")
    private String fileType;

    @SerializedName("filename")
    private String filename;

    @SerializedName("title")
    private String title;

    @SerializedName("type")
    private String type;

}
