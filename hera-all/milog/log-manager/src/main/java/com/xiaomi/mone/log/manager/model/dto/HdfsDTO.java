package com.xiaomi.mone.log.manager.model.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class HdfsDTO implements Serializable {

    private List<HdfsDetailDto> list;

}
