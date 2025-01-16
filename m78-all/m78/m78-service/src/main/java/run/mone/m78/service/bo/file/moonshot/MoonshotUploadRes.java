package run.mone.m78.service.bo.file.moonshot;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import run.mone.m78.service.bo.code.Param;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MoonshotUploadRes implements Serializable {

    private String id;

    private String object;

    private int bytes;

    @SerializedName("create_at")
    private long createdAt;

    private String filename;

    private String purpose;

    private String status;

    @SerializedName("status_details")
    private String statusDetails;

}
