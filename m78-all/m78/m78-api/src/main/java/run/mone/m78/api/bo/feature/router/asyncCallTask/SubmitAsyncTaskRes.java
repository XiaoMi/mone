package run.mone.m78.api.bo.feature.router.asyncCallTask;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class SubmitAsyncTaskRes implements Serializable {

    private String taskId;

    private String taskStatus;

    private String message;

}
