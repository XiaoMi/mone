package run.mone.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author goodjava@qq.com
 * @date 2024/3/26 15:59
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Message implements Serializable {

    //system user assistant
    private String role;

    private String content;

}
