package run.mone.m78.service.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author caobaoyu
 * @description:
 * @date 2024-03-27 09:53
 */
@Data
public class RoleDto implements Serializable {

    private Integer roleCode;
    private String role;

}
