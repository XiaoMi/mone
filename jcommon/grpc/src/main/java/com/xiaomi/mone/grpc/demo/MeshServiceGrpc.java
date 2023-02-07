package com.xiaomi.mone.grpc.demo;

import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ClientCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ClientCalls.asyncClientStreamingCall;
import static io.grpc.stub.ClientCalls.asyncServerStreamingCall;
import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.blockingServerStreamingCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.stub.ServerCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ServerCalls.asyncClientStreamingCall;
import static io.grpc.stub.ServerCalls.asyncServerStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.34.1)",
    comments = "Source: service.proto")
public final class MeshServiceGrpc {

  private MeshServiceGrpc() {}

  public static final String SERVICE_NAME = "com.xiaomi.mone.grpc.demo.MeshService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<com.xiaomi.mone.grpc.demo.GrpcMeshRequest,
      com.xiaomi.mone.grpc.demo.GrpcMeshResponse> getCallMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "call",
      requestType = com.xiaomi.mone.grpc.demo.GrpcMeshRequest.class,
      responseType = com.xiaomi.mone.grpc.demo.GrpcMeshResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.xiaomi.mone.grpc.demo.GrpcMeshRequest,
      com.xiaomi.mone.grpc.demo.GrpcMeshResponse> getCallMethod() {
    io.grpc.MethodDescriptor<com.xiaomi.mone.grpc.demo.GrpcMeshRequest, com.xiaomi.mone.grpc.demo.GrpcMeshResponse> getCallMethod;
    if ((getCallMethod = MeshServiceGrpc.getCallMethod) == null) {
      synchronized (MeshServiceGrpc.class) {
        if ((getCallMethod = MeshServiceGrpc.getCallMethod) == null) {
          MeshServiceGrpc.getCallMethod = getCallMethod =
              io.grpc.MethodDescriptor.<com.xiaomi.mone.grpc.demo.GrpcMeshRequest, com.xiaomi.mone.grpc.demo.GrpcMeshResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "call"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.xiaomi.mone.grpc.demo.GrpcMeshRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.xiaomi.mone.grpc.demo.GrpcMeshResponse.getDefaultInstance()))
              .setSchemaDescriptor(new MeshServiceMethodDescriptorSupplier("call"))
              .build();
        }
      }
    }
    return getCallMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.xiaomi.mone.grpc.demo.GrpcMeshRequest,
      com.xiaomi.mone.grpc.demo.PushMsg> getListenMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "listen",
      requestType = com.xiaomi.mone.grpc.demo.GrpcMeshRequest.class,
      responseType = com.xiaomi.mone.grpc.demo.PushMsg.class,
      methodType = io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
  public static io.grpc.MethodDescriptor<com.xiaomi.mone.grpc.demo.GrpcMeshRequest,
      com.xiaomi.mone.grpc.demo.PushMsg> getListenMethod() {
    io.grpc.MethodDescriptor<com.xiaomi.mone.grpc.demo.GrpcMeshRequest, com.xiaomi.mone.grpc.demo.PushMsg> getListenMethod;
    if ((getListenMethod = MeshServiceGrpc.getListenMethod) == null) {
      synchronized (MeshServiceGrpc.class) {
        if ((getListenMethod = MeshServiceGrpc.getListenMethod) == null) {
          MeshServiceGrpc.getListenMethod = getListenMethod =
              io.grpc.MethodDescriptor.<com.xiaomi.mone.grpc.demo.GrpcMeshRequest, com.xiaomi.mone.grpc.demo.PushMsg>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "listen"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.xiaomi.mone.grpc.demo.GrpcMeshRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.xiaomi.mone.grpc.demo.PushMsg.getDefaultInstance()))
              .setSchemaDescriptor(new MeshServiceMethodDescriptorSupplier("listen"))
              .build();
        }
      }
    }
    return getListenMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static MeshServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<MeshServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<MeshServiceStub>() {
        @java.lang.Override
        public MeshServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new MeshServiceStub(channel, callOptions);
        }
      };
    return MeshServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static MeshServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<MeshServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<MeshServiceBlockingStub>() {
        @java.lang.Override
        public MeshServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new MeshServiceBlockingStub(channel, callOptions);
        }
      };
    return MeshServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static MeshServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<MeshServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<MeshServiceFutureStub>() {
        @java.lang.Override
        public MeshServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new MeshServiceFutureStub(channel, callOptions);
        }
      };
    return MeshServiceFutureStub.newStub(factory, channel);
  }

  /**
   */
  public static abstract class MeshServiceImplBase implements io.grpc.BindableService {

    /**
     */
    public void call(com.xiaomi.mone.grpc.demo.GrpcMeshRequest request,
        io.grpc.stub.StreamObserver<com.xiaomi.mone.grpc.demo.GrpcMeshResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getCallMethod(), responseObserver);
    }

    /**
     */
    public void listen(com.xiaomi.mone.grpc.demo.GrpcMeshRequest request,
        io.grpc.stub.StreamObserver<com.xiaomi.mone.grpc.demo.PushMsg> responseObserver) {
      asyncUnimplementedUnaryCall(getListenMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getCallMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                com.xiaomi.mone.grpc.demo.GrpcMeshRequest,
                com.xiaomi.mone.grpc.demo.GrpcMeshResponse>(
                  this, METHODID_CALL)))
          .addMethod(
            getListenMethod(),
            asyncServerStreamingCall(
              new MethodHandlers<
                com.xiaomi.mone.grpc.demo.GrpcMeshRequest,
                com.xiaomi.mone.grpc.demo.PushMsg>(
                  this, METHODID_LISTEN)))
          .build();
    }
  }

  /**
   */
  public static final class MeshServiceStub extends io.grpc.stub.AbstractAsyncStub<MeshServiceStub> {
    private MeshServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected MeshServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new MeshServiceStub(channel, callOptions);
    }

    /**
     */
    public void call(com.xiaomi.mone.grpc.demo.GrpcMeshRequest request,
        io.grpc.stub.StreamObserver<com.xiaomi.mone.grpc.demo.GrpcMeshResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getCallMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void listen(com.xiaomi.mone.grpc.demo.GrpcMeshRequest request,
        io.grpc.stub.StreamObserver<com.xiaomi.mone.grpc.demo.PushMsg> responseObserver) {
      asyncServerStreamingCall(
          getChannel().newCall(getListenMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class MeshServiceBlockingStub extends io.grpc.stub.AbstractBlockingStub<MeshServiceBlockingStub> {
    private MeshServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected MeshServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new MeshServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public com.xiaomi.mone.grpc.demo.GrpcMeshResponse call(com.xiaomi.mone.grpc.demo.GrpcMeshRequest request) {
      return blockingUnaryCall(
          getChannel(), getCallMethod(), getCallOptions(), request);
    }

    /**
     */
    public java.util.Iterator<com.xiaomi.mone.grpc.demo.PushMsg> listen(
        com.xiaomi.mone.grpc.demo.GrpcMeshRequest request) {
      return blockingServerStreamingCall(
          getChannel(), getListenMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class MeshServiceFutureStub extends io.grpc.stub.AbstractFutureStub<MeshServiceFutureStub> {
    private MeshServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected MeshServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new MeshServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.xiaomi.mone.grpc.demo.GrpcMeshResponse> call(
        com.xiaomi.mone.grpc.demo.GrpcMeshRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getCallMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_CALL = 0;
  private static final int METHODID_LISTEN = 1;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final MeshServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(MeshServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_CALL:
          serviceImpl.call((com.xiaomi.mone.grpc.demo.GrpcMeshRequest) request,
              (io.grpc.stub.StreamObserver<com.xiaomi.mone.grpc.demo.GrpcMeshResponse>) responseObserver);
          break;
        case METHODID_LISTEN:
          serviceImpl.listen((com.xiaomi.mone.grpc.demo.GrpcMeshRequest) request,
              (io.grpc.stub.StreamObserver<com.xiaomi.mone.grpc.demo.PushMsg>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  private static abstract class MeshServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    MeshServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.xiaomi.mone.grpc.demo.Service.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("MeshService");
    }
  }

  private static final class MeshServiceFileDescriptorSupplier
      extends MeshServiceBaseDescriptorSupplier {
    MeshServiceFileDescriptorSupplier() {}
  }

  private static final class MeshServiceMethodDescriptorSupplier
      extends MeshServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    MeshServiceMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (MeshServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new MeshServiceFileDescriptorSupplier())
              .addMethod(getCallMethod())
              .addMethod(getListenMethod())
              .build();
        }
      }
    }
    return result;
  }
}
