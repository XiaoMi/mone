package run.mone.m78.service.dao.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;

import java.io.Serializable;

import com.mybatisflex.core.handler.Fastjson2TypeHandler;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import run.mone.m78.service.bo.code.Code;

/**
 * 实体类。
 *
 * @author zhangzhiyong
 * @since 2024-03-09
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(value = "m78_code")
public class M78Code implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Long id;

    private Long utime;

    private Long ctime;

    @Column(value = "code", typeHandler = Fastjson2TypeHandler.class)
    private Code code;

    private String creator;

    private Integer type;

    private String model;

    private String name;

    private String desc;

}
