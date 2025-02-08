package run.mone.m78.api.bo.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wmin
 * @date 2024/1/25
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ModelRes {

    private String name;

    private String cname;

    private String info;

    private String vendors;

    private String description;

    private String imageUrl;

}
