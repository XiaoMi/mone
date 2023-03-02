package com.xiaomi.miapi.bo;

import com.xiaomi.miapi.pojo.ApiTestCase;
import lombok.Data;

import java.util.List;
/**
 * @author dongzhenxing
 * @date 2023/02/08
 */
@Data
public class CaseGroupAndCasesBo {
    private int caseGroupId;
    private String caseGroupName;
    private List<ApiTestCase> caseList;
}
