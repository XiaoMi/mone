package run.mone.m78.service.bo;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author goodjava@qq.com
 * @date 2024/2/21 16:58
 */
@Data
@Builder
public class ApiResult implements Serializable {

    private int code;

    private String message;

    private JsonElement data;

}
