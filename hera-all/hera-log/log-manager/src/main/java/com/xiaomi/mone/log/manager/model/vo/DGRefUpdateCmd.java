package com.xiaomi.mone.log.manager.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class DGRefUpdateCmd implements Serializable {

    private Long dashboardId;

    private List<DGRefDetailUpdateCmd> graphList;

    @Data
    public class DGRefDetailUpdateCmd  implements Serializable {

        private Long graphId;

        private String point;

        private String graphPrivateName ;

    }

}


