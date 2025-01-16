package run.mone.m78.api.bo.multiModal.audio;


import com.xiaomi.youpin.infra.rpc.Result;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AudioToTextCallProxyRes {

    private int code;

    private String msg;

    private String asrPlatformTaskId;

    private String modalTaskId;

}
