package run.mone.m78.api.bo.flow;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author wmin
 * @date 2024/3/5
 */
@Data
@Builder
public class EndFlowRes implements Serializable {

    private String answerContent;

    private List<OutputDataDetail> outputDataDetails;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OutputDataDetail {
        private String name;
        private String value;
        //string、array，用于前端展示区分
        private String valueType;
    }
}

