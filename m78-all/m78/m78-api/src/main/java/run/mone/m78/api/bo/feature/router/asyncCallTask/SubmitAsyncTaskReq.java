package run.mone.m78.api.bo.feature.router.asyncCallTask;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class SubmitAsyncTaskReq {

    private Integer type;

    private Long relateId;

    private Object inputs;

    private String callbackUrl;

    private String invokeUserName;

}
