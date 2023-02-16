package com.xiaomi.mone.log.manager.domain;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.xiaomi.mone.log.manager.model.cache.IDMDeptCache;
import com.xiaomi.mone.log.manager.user.IdmMoneUserDetailService;
import com.xiaomi.youpin.docean.anno.Service;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class IDMDept {

    @Resource
    IdmMoneUserDetailService userService;

    private IDMDeptCache deptCache;

    private String[] needDeptArray1;

    {
        needDeptArray1 = new String[]{"XX", "XT"};
    }

    public IDMDeptCache getDeptCache() {
        return this.deptCache;
    }

    public void refreshDeptCache() {
        deptCache = new IDMDeptCache("XX", "XX公司", 0, null);
        IDMDeptCache in = new IDMDeptCache("XT", "test部门", 1, null);
        List<IDMDeptCache> deptList1 = Arrays.asList(in);
        for (IDMDeptCache dept1 : deptList1) {
            JsonArray deptJsonArray2 = userService.queryChildDept(dept1.getDeptId());
            List<IDMDeptCache> deptList2 = parseDept(deptJsonArray2);
            dept1.setChildren(deptList2);
            for (IDMDeptCache dept2 : deptList2) {
                JsonArray deptJsonArray3 = userService.queryChildDept(dept2.getDeptId());
                List<IDMDeptCache> deptList3 = parseDept(deptJsonArray3);
                dept2.setChildren(deptList3);
            }
        }
        deptCache.setChildren(deptList1);
    }

    public List<IDMDeptCache> parseDept(JsonArray deptArray) {
        List<IDMDeptCache> res = new ArrayList<>();
        Gson gson = new Gson();
        for (JsonElement dept : deptArray) {
            res.add(gson.fromJson(dept, IDMDeptCache.class));
        }
        return res;
    }


}
