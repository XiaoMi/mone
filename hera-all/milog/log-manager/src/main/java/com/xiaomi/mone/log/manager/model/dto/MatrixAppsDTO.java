package com.xiaomi.mone.log.manager.model.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhangjuan
 * @Description matrix 返回 iam-tree-id 下的 app 信息
 * @date 2022-06-17
 */
@Data
public class MatrixAppsDTO {
    private int code;
    private String message;
    private String level;
    private List<MatrixAppData> data;

    public MatrixAppsDTO() {
        this.data = new ArrayList<>();
    }

    @Data
    public static class MatrixAppData {
        private Long id;
        private String name;
        private List<MatrixDeploySpace> deploySpaces;

        public MatrixAppData() {
            this.deploySpaces = new ArrayList<>();
        }
    }



    @Data
    public static class MatrixDeploySpace {
        private Long id;
        private String name;
    }
}
