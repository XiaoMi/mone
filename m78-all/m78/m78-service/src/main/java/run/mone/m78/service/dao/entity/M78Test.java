package run.mone.m78.service.dao.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import java.io.Serializable;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

import com.mybatisflex.core.handler.Fastjson2TypeHandler;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *  实体类。
 *
 * @author zhangzhiyong
 * @since 2024-02-02
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(value = "m78_test")
public class M78Test implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @Id(keyType = KeyType.Auto)
    private BigInteger id;

    /**
     * 创建时间
     */
    private LocalDateTime gmtCreate;

    /**
     * 创建者
     */
    private String creator;

    /**
     * 原文本
     */
    private String textBefore;

    /**
     * 脱敏后文本
     */
    private String textAfter;

    /**
     * 状态, 0: 失败, 1：成功
     */
    private Integer status;


    @Column(value = "t", typeHandler = Fastjson2TypeHandler.class)
    private List<String> t;

}
