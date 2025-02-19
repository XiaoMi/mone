package run.mone.local.docean.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VisionReq implements Serializable {

    private String zzToken;

    private String userName;

    private int userType;

    private String cmd;

    private String model;

    private String promptName;

    private Map<String, String> params;

    private List<VisionMsg> msgs;

    @Builder.Default
    private double temperature = 0.2;
}
