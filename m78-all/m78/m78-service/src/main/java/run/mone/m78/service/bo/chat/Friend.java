package run.mone.m78.service.bo.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author goodjava@qq.com
 * @date 2024/5/9 11:20
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Friend implements Serializable {

    private String id;

    private String name;

}
