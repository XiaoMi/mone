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

import com.google.protobuf.Descriptors;
import com.google.protobuf.DynamicMessage;
import com.google.protobuf.TypeRegistry;
import com.google.protobuf.util.JsonFormat;
import io.grpc.*;
import io.grpc.protobuf.ProtoUtils;
import io.grpc.stub.ClientCalls;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Triple;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author dongzhenxing
 * @date 2023/02/08
 */
@Slf4j
public class GrpcReflectionCall {


    private static ConcurrentHashMap<String, Triple<Descriptors.FileDescriptor, Descriptors.ServiceDescriptor, Descriptors.MethodDescriptor>> m = new ConcurrentHashMap<>();

    private static final ConcurrentHashMap<String, ManagedChannel> channelMap = new ConcurrentHashMap<>();

    private static final int DEFAULT_TIMEOUT = 3000;

    public ManagedChannel channel(String ip, int port) throws Exception {
        log.info("create channel {}:{}", ip, port);
        try {
            return ManagedChannelBuilder.forAddress(ip, port).usePlaintext().build();
        } catch (Exception e) {
            throw new Exception("");
        }
    }

    @SneakyThrows
    public String call(String addr, String service, String request, int timeout){
        String[] ipAndPort = addr.split(":");
        ManagedChannel channel = channelMap.computeIfAbsent(addr, (k) -> {
            try {
                return channel(ipAndPort[0], Integer.parseInt(ipAndPort[1]));
            } catch (Exception e) {
                log.error(e.getMessage());
            }
            return null;
        });
        Triple<Descriptors.FileDescriptor, Descriptors.ServiceDescriptor, Descriptors.MethodDescriptor> info = m.computeIfAbsent(service, k -> {
            ServiceInfoCall serviceInfoCall = new ServiceInfoCall();
            Triple<Descriptors.FileDescriptor, Descriptors.ServiceDescriptor, Descriptors.MethodDescriptor> triple = null;
            try {
                triple = serviceInfoCall.info(channel, service);
            } catch (Exception ignored) {
                log.error(ignored.getMessage());
            }
            return triple;
        });
        return callService(channel, request, info, timeout);
    }

    private static String callService(ManagedChannel channel, String requestContent, Triple<Descriptors.FileDescriptor, Descriptors.ServiceDescriptor, Descriptors.MethodDescriptor> t, int timeout) {
        try {
            return executeCall(channel, t.getLeft(), t.getRight(), requestContent, timeout);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    private static String executeCall(ManagedChannel channel,
                                      Descriptors.FileDescriptor fileDescriptor,
                                      Descriptors.MethodDescriptor originMethodDescriptor,
                                      String requestContent, int timeout) throws Exception {

        // re gen MethodDescriptor
        MethodDescriptor<DynamicMessage, DynamicMessage> methodDescriptor = generateMethodDescriptor(originMethodDescriptor);
        TypeRegistry registry = TypeRegistry.newBuilder()
                .add(fileDescriptor.getMessageTypes())
                .build();
        JsonFormat.Parser parser = JsonFormat.parser().usingTypeRegistry(registry);
        DynamicMessage.Builder messageBuilder = DynamicMessage.newBuilder(originMethodDescriptor.getInputType());
        parser.merge(requestContent, messageBuilder);
        DynamicMessage requestMessage = messageBuilder.build();
        CallOptions callOptions = CallOptions.DEFAULT;
        callOptions = callOptions.withDeadline(Deadline.after(timeout == 0 ? DEFAULT_TIMEOUT : timeout, TimeUnit.MILLISECONDS));
        DynamicMessage response = ClientCalls.blockingUnaryCall(channel, methodDescriptor, callOptions, requestMessage);

        JsonFormat.Printer printer = JsonFormat.printer()
                .usingTypeRegistry(registry)
                .includingDefaultValueFields();
        return printer.print(response);
    }

    private static MethodDescriptor<DynamicMessage, DynamicMessage> generateMethodDescriptor(Descriptors.MethodDescriptor originMethodDescriptor) {
        // gen method name
        String fullMethodName = MethodDescriptor.generateFullMethodName(originMethodDescriptor.getService().getFullName(), originMethodDescriptor.getName());
        // req and res
        MethodDescriptor.Marshaller<DynamicMessage> inputTypeMarshaller = ProtoUtils.marshaller(DynamicMessage.newBuilder(originMethodDescriptor.getInputType())
                .buildPartial());
        MethodDescriptor.Marshaller<DynamicMessage> outputTypeMarshaller = ProtoUtils.marshaller(DynamicMessage.newBuilder(originMethodDescriptor.getOutputType())
                .buildPartial());

        return MethodDescriptor.<DynamicMessage, DynamicMessage>newBuilder()
                .setFullMethodName(fullMethodName)
                .setRequestMarshaller(inputTypeMarshaller)
                .setResponseMarshaller(outputTypeMarshaller)
                .setType(MethodDescriptor.MethodType.UNKNOWN)
                .build();
    }
}
