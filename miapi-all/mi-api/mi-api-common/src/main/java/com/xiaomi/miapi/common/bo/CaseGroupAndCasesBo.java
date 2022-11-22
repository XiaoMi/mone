package com.xiaomi.miapi.common.bo;

import com.xiaomi.miapi.common.pojo.ApiTestCase;
import lombok.Data;

import java.util.List;

@Data
public class CaseGroupAndCasesBo {
    private int caseGroupId;
    private String caseGroupName;
    private List<ApiTestCase> caseList;
}
