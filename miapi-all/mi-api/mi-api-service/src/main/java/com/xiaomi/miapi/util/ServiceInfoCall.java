/*
 *  Copyright 2020 Xiaomi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.xiaomi.miapi.util;

import com.google.protobuf.ByteString;
import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.Descriptors;
import com.google.protobuf.InvalidProtocolBufferException;
import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.MethodDescriptor;
import io.grpc.protobuf.services.ProtoReflectionService;
import io.grpc.reflection.v1alpha.ServerReflectionRequest;
import io.grpc.reflection.v1alpha.ServerReflectionResponse;
import io.grpc.stub.ClientCalls;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Triple;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
/**
 * @author dongzhenxing
 * @date 2023/02/08
 */
@Slf4j
public class ServiceInfoCall {

    public Triple<Descriptors.FileDescriptor, Descriptors.ServiceDescriptor, Descriptors.MethodDescriptor> info(Channel channel, String service) throws Exception {
        ServerReflectionRequest getFileContainingSymbolRequest = ServerReflectionRequest.newBuilder().setFileContainingSymbol(service).build();
        MethodDescriptor<ServerReflectionRequest, ServerReflectionResponse> methodDescriptor = (MethodDescriptor<ServerReflectionRequest, ServerReflectionResponse>) ProtoReflectionService.newInstance().bindService().getMethod("grpc.reflection.v1alpha.ServerReflection/ServerReflectionInfo").getMethodDescriptor();
        ServerReflectionResponse res = null;
        try {
            res = ClientCalls.blockingUnaryCall(channel, methodDescriptor, CallOptions.DEFAULT, getFileContainingSymbolRequest);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        List<ByteString> fileDescriptorProtoList = res.getFileDescriptorResponse().getFileDescriptorProtoList();
        return t(fileDescriptorProtoList, service);
    }

    private static Triple<Descriptors.FileDescriptor, Descriptors.ServiceDescriptor, Descriptors.MethodDescriptor> t(List<ByteString> fileDescriptorProtoList, String methodFullName) throws Exception {
        String fullServiceName = extraPrefix(methodFullName);
        String methodName = extraSuffix(methodFullName);
        String packageName = extraPrefix(fullServiceName);
        String serviceName = extraSuffix(fullServiceName);
        Descriptors.FileDescriptor fileDescriptor = getFileDescriptor(fileDescriptorProtoList, packageName, serviceName);
        Descriptors.ServiceDescriptor serviceDescriptor = fileDescriptor.getFile().findServiceByName(serviceName);
        Descriptors.MethodDescriptor methodDescriptor = serviceDescriptor.findMethodByName(methodName);
        return Triple.of(fileDescriptor, serviceDescriptor, methodDescriptor);
    }

    private static Descriptors.FileDescriptor[] getDependencies(DescriptorProtos.FileDescriptorProto proto,
                                                                Map<String, DescriptorProtos.FileDescriptorProto> finalDescriptorProtoMap) {
        return proto.getDependencyList()
                .stream()
                .map(finalDescriptorProtoMap::get)
                .map(f -> toFileDescriptor(f, getDependencies(f, finalDescriptorProtoMap)))
                .toArray(Descriptors.FileDescriptor[]::new);
    }

    /**
     * transfer FileDescriptorProto to FileDescriptor
     */
    @SneakyThrows
    private static Descriptors.FileDescriptor toFileDescriptor(DescriptorProtos.FileDescriptorProto fileDescriptorProto,
                                                               Descriptors.FileDescriptor[] dependencies) {
        return Descriptors.FileDescriptor.buildFrom(fileDescriptorProto, dependencies);
    }

    private static DescriptorProtos.FileDescriptorProto findServiceFileDescriptorProto(String packageName,
                                                                                       String serviceName,
                                                                                       Map<String, DescriptorProtos.FileDescriptorProto> fileDescriptorProtoMap) {
        for (DescriptorProtos.FileDescriptorProto proto : fileDescriptorProtoMap.values()) {
            if (proto.getPackage().equals(packageName)) {
                boolean exist = proto.getServiceList()
                        .stream()
                        .anyMatch(s -> serviceName.equals(s.getName()));
                if (exist) {
                    return proto;
                }
            }
        }

        throw new IllegalArgumentException("服务不存在");
    }

    public Map<String, DescriptorProtos.FileDescriptorProto> getFileDescriptorMap(Channel channel, String symbol){
        ServerReflectionRequest getFileContainingSymbolRequest = ServerReflectionRequest.newBuilder().setFileContainingSymbol(symbol).build();
        MethodDescriptor<ServerReflectionRequest, ServerReflectionResponse> methodDescriptor = (MethodDescriptor<ServerReflectionRequest, ServerReflectionResponse>) ProtoReflectionService.newInstance().bindService().getMethod("grpc.reflection.v1alpha.ServerReflection/ServerReflectionInfo").getMethodDescriptor();
        ServerReflectionResponse res = ClientCalls.blockingUnaryCall(channel, methodDescriptor, CallOptions.DEFAULT, getFileContainingSymbolRequest);
        List<ByteString> fileDescriptorProtoList = res.getFileDescriptorResponse().getFileDescriptorProtoList();
        Map<String, DescriptorProtos.FileDescriptorProto> fileDescriptorProtoMap =
                fileDescriptorProtoList.stream()
                        .map(bs -> {
                            try {
                                return DescriptorProtos.FileDescriptorProto.parseFrom(bs);
                            } catch (InvalidProtocolBufferException e) {
                                log.error("InvalidProtocolBufferException",e);
                            }
                            return null;
                        })
                        .filter(Objects::nonNull)
                        .collect(Collectors.toMap(DescriptorProtos.FileDescriptorProto::getName, f -> f));

        if (fileDescriptorProtoMap.isEmpty()) {
            log.error("服务不存在");
            throw new IllegalArgumentException("方法的文件描述不存在");
        }
        return fileDescriptorProtoMap;
    }

    public Map<String, List<String>> getServicesAndMethods(Channel channel, String symbol) {
        Map<String, List<String>> serviceMethodMap = new HashMap<>();
        for (DescriptorProtos.FileDescriptorProto proto : this.getFileDescriptorMap(channel,symbol).values()) {
            String packageName = proto.getPackage();
            proto.getServiceList().forEach(serviceDescriptorProto -> {
                String serviceName = packageName + "." + serviceDescriptorProto.getName();
                List<String> methodList = serviceDescriptorProto.getMethodList().stream().map(DescriptorProtos.MethodDescriptorProto::getName).collect(Collectors.toList());
                if (!serviceMethodMap.containsKey(serviceName)) {
                    serviceMethodMap.put(serviceName, methodList);
                } else {
                    serviceMethodMap.get(serviceName).addAll(methodList);
                }
            });
        }
        return serviceMethodMap;
    }

    private static String extraPrefix(String content) {
        int index = content.lastIndexOf(".");
        return content.substring(0, index);
    }

    private static String extraSuffix(String content) {
        int index = content.lastIndexOf(".");
        return content.substring(index + 1);
    }

    private static Descriptors.FileDescriptor getFileDescriptor(List<ByteString> fileDescriptorProtoList,
                                                                String packageName,
                                                                String serviceName) throws Exception {
        Map<String, DescriptorProtos.FileDescriptorProto> fileDescriptorProtoMap =
                fileDescriptorProtoList.stream()
                        .map(bs -> {
                            try {
                                return DescriptorProtos.FileDescriptorProto.parseFrom(bs);
                            } catch (InvalidProtocolBufferException e) {
                                e.printStackTrace();
                            }
                            return null;
                        })
                        .filter(Objects::nonNull)
                        .collect(Collectors.toMap(DescriptorProtos.FileDescriptorProto::getName, f -> f));


        if (fileDescriptorProtoMap.isEmpty()) {
            log.error("服务不存在");
            throw new IllegalArgumentException("方法的文件描述不存在");
        }
        DescriptorProtos.FileDescriptorProto fileDescriptorProto = findServiceFileDescriptorProto(packageName, serviceName, fileDescriptorProtoMap);
        Descriptors.FileDescriptor[] dependencies = getDependencies(fileDescriptorProto, fileDescriptorProtoMap);
        return Descriptors.FileDescriptor.buildFrom(fileDescriptorProto, dependencies);
    }

}
