package run.mone.m78.api.bo.bot;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author zhangping17
 * @description:
 * @date 2024-03-05 17:01
 */

@Data
public class ImTypeDTO {

    private Integer id;

    private String name;

    private String deleted;

    private LocalDateTime createTime;
}
