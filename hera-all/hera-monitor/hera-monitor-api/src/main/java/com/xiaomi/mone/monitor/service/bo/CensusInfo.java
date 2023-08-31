/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xiaomi.mone.monitor.service.bo;

import lombok.Data;

/**
 *
 * @author zhanggaofeng1
 */
@Data
public class CensusInfo {

    private long duration = 30 * 60l;
    private int pageSize = 100;
    private int maxPageNo = 100;
    
}
