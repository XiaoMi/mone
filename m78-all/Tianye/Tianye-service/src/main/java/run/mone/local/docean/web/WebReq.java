package run.mone.local.docean.web;

import lombok.Builder;
import lombok.Data;
import run.mone.local.docean.fsm.bo.FlowData;
import run.mone.local.docean.fsm.bo.NodeEdge;

import java.util.List;

/**
 * @author liuchuankang
 * @Type WebReq.java
 * @Desc
 * @date 2024/3/22 20:20
 */
@Data
public class WebReq {
    private String openUrl;
    private String cookies;
}
