package run.mone.m78.service.bo.category;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * @author caobaoyu
 * @description:
 * @date 2024-03-02 09:58
 */
@Data
@AllArgsConstructor
public class CategoryVo implements Serializable {

    private Long id;

    private String name;

    /**
     * CategoryTypeEnum
     */
    private Integer type;

}
