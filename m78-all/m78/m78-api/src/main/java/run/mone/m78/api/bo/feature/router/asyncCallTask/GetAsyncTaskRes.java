package run.mone.m78.api.bo.feature.router.asyncCallTask;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class GetAsyncTaskRes implements Serializable {

    private String taskId;

    private String taskStatus;

    private String message;

    private String output;

}
