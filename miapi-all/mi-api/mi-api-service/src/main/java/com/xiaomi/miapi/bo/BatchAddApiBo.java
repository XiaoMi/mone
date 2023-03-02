package com.xiaomi.miapi.bo;

import lombok.Data;

import java.util.List;
/**
 * @author dongzhenxing
 * @date 2023/02/08
 */
@Data
public class BatchAddApiBo {
    String apiEnv;
    List<BatchImportApiBo> bos;
}
