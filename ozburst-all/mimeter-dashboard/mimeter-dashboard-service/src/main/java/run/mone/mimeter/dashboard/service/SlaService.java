package run.mone.mimeter.dashboard.service;

import run.mone.mimeter.dashboard.bo.common.Result;
import run.mone.mimeter.dashboard.bo.sla.GetSlaListReq;
import run.mone.mimeter.dashboard.bo.sla.SlaDto;
import run.mone.mimeter.dashboard.bo.sla.SlaList;

import java.util.List;

public interface SlaService {

    /**
     * sla
     */
    Result<Integer> newSla(SlaDto param);

    Result<Boolean> updateSla(SlaDto param);

    Result<SlaList> getSlaList(GetSlaListReq req);

    Result<SlaDto> getSlaById(int id);

    Result<Boolean> delSla(int id);

    Result<Boolean> multiDelSla(List<Integer> ids);

}
