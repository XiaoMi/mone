package com.xiaomi.miapi.service;

import com.xiaomi.miapi.common.Result;

import java.util.List;
import java.util.Map;

public interface PartnerService {
	Map<String,List<Map<Integer,String>>> getPartnerList(Integer projectID);

	Map<String,List<Map<Integer,String>>> getGroupPartnerList(Integer groupID);

	Result<List<Map<Integer, String>>> getAllPartnerList();
}
