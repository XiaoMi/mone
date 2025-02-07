package run.mone.m78.api.bo.multiModal;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author wmin
 * @date 2024/2/29
 */
@Data
@Builder
public class MultiModalStatusStreamRsp implements Serializable {

    private Long id;

    private int taskStatus;

    private String message;

    private List<String> resultUrl;
}
