package com.xiaomi.miapi.dto;

import lombok.Data;

/**
 * @author dongzhenxing
 * @date 2023/02/08
 */
@Data
public class DocumentDTO {
    private Integer projectID;
    private Integer documentID;
    private String groupName;
    private String updateTime;
    private Integer contentType;
    private String contentRaw;
    private String content;
    private String title;
    private Integer userID;
    private String createUserName;
}
