package com.xiaomi.mone.monitor.service;


import com.xiaomi.mone.monitor.service.bo.AlertGroupQryInfo;

import java.util.List;

public interface AlertGorupFacade {

    List<AlertGroupQryInfo> query(String account, String likeName);

    List<AlertGroupQryInfo> queryByIds(String account, List<Long> ids);
}
