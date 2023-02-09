package com.xiaomi.miapi.common.bo;

import lombok.Data;

import java.util.List;

@Data
public class BatchAddDubboApiBo {
    String apiEnv;
    List<BatchImportDubboApiBo> bos;
}
