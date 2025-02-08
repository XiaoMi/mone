package run.mone.m78.service.bo.chatgpt;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import run.mone.m78.service.common.Config;
import run.mone.z.proxy.api.dto.ModelInfo;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author goodjava@qq.com
 * @date 2023/5/25 14:16
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Ask implements Serializable {

    private String id;

    private String token;

    @Builder.Default
    private String zzToken = Config.zToken;

    private String userName;

    private int userType;

    private String promptName;

    private String[] params;

    private ChatCompletion chatCompletion;

    private Map<String, String> paramMap;

    /**
     * 0 是单条的,并且传过来是promptName
     * 1 多条,就是正常的多轮问答
     */
    @Builder.Default
    private int type = 0;

    private List<Msg> msgList;


    /**
     * 来源
     */
    private String from;

    @Builder.Default
    private String model = Config.model;

    private boolean debug;

    /**
     * 聊天的时候可以设定温度
     */
    private double temperature;

    /**
     * 是否流式返回，默认为true
     */
    private Boolean stream;

    private String suffix;


    private boolean hasImage;

    private List<VisionImage> imageBo;

    @ToString.Exclude
    private ModelInfo modelInfo;

    private Long relationId;

}
