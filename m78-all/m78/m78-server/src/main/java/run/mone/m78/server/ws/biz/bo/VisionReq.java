package run.mone.m78.server.ws.biz.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author goodjava@qq.com
 * @date 2023/5/25 14:16
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VisionReq implements Serializable {

    private String zzToken;

    private String userName;

    private int userType;

    private String model;

    private List<VisionMsg> msgs;

    @Builder.Default
    private double temperature = 0.2;
}
