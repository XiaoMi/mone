package com.xiaomi.data.push.aliyun;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.ecs.model.v20140526.*;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;

/**
 * @author Zheng Xu zheng.xucn@outlook.com
 */

public class Aliyun {

    private DefaultProfile profile;

    public Aliyun(String regionId, String accessKey, String secret) {
        profile = DefaultProfile.getProfile(regionId, accessKey, secret);
    }

    public RunInstancesResponse createInstance(RunInstancesRequest request) throws ClientException {
        IAcsClient client = new DefaultAcsClient(profile);
        RunInstancesResponse response = client.getAcsResponse(request);
        return response;
    }

    public DeleteInstanceResponse deleteInstance(String instanceId) throws ClientException {
        IAcsClient client = new DefaultAcsClient(profile);
        DeleteInstanceRequest request = new DeleteInstanceRequest();
        request.setInstanceId(instanceId);
        DeleteInstanceResponse response = client.getAcsResponse(request);
        return response;
    }

    public StartInstanceResponse startInstance(String instanceId) throws ClientException {
        IAcsClient client = new DefaultAcsClient(profile);
        StartInstanceRequest request = new StartInstanceRequest();
        request.setInstanceId(instanceId);
        StartInstanceResponse response = client.getAcsResponse(request);
        return response;
    }

    public StopInstanceResponse stopInstance(String instanceId) throws ClientException {
        IAcsClient client = new DefaultAcsClient(profile);
        StopInstanceRequest request = new StopInstanceRequest();
        request.setInstanceId(instanceId);
        StopInstanceResponse response = client.getAcsResponse(request);
        return response;
    }

    public DescribeInstancesResponse getAllInstances() throws ClientException {
        IAcsClient client = new DefaultAcsClient(profile);
        DescribeInstancesRequest request = new DescribeInstancesRequest();
        DescribeInstancesResponse response = client.getAcsResponse(request);
        return response;
    }
}
