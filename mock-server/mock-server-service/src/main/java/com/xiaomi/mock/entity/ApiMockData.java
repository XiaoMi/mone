package com.xiaomi.mock.entity;

import lombok.Data;
import lombok.ToString;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;

/**
 * @Description TODO
 * @Author zhenxing.dong
 * @Date 2021/8/9 11:13
 */
@Data
@ToString
@Table("api_mock_data")
public class ApiMockData {
    @Id
    private Long id;

    @Column("url")
    private String url;

    @Column("api_mock_result")
    private String apiMockData;

    @Column("params_md5")
    private String paramsMd5;

    @Column("enable")
    private Boolean enable;

    @Column("mock_expect_id")
    private Integer mockExpID;

    @Column("mock_proxy_url")
    private String mockProxyUrl;

    @Column("use_mock_script")
    private Boolean useMockScript;

    @Column("mock_script")
    private String mockScript;
}
