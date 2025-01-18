package run.mone.m78.service.bo.chatgpt;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import lombok.*;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

/**
 * @author goodjava@qq.com
 * @date 2023/5/31 10:29
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Msg implements Serializable {

    private String role;

    private String content;

    private String type;

    private ImageUrl image_url;

    private boolean jsonContent;

}
