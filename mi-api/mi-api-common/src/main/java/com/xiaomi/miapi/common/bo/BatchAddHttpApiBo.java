package com.xiaomi.miapi.common.bo;

import lombok.Data;

import java.util.List;

@Data
public class BatchAddHttpApiBo {
    String apiEnv;
    List<BatchImportHttpApiBo> bos;
}
