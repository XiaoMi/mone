package com.xiaomi.mone.log.manager.model.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhangjuan
 * @Description matrix 返回 deploySpaceId 下的日志配置
 * @date 2022-06-17
 */
@Data
public class MatrixLogCollectionsDTO {
    private int code;
    private String message;
    private String level;
    private List<MatrixLogCollection> data;

    public MatrixLogCollectionsDTO() {
        this.data = new ArrayList<>();
    }

    @Data
    public static class MatrixLogCollection {
        private Long id;
        private List<MatrixLogCollectionJob> Jobs;

        public MatrixLogCollection() {
            this.Jobs = new ArrayList<>();
        }
    }

    @Data
    public static class MatrixLogCollectionJob {
        private String cluster;
        private String active;
        private boolean jobStatus;
    }
}