package run.mone.mesh.bo;

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
    comments = "Source: sidecar.proto")
public final class SideCarServiceGrpc {

  private SideCarServiceGrpc() {}

  public static final String SERVICE_NAME = "run.mone.mesh.bo.SideCarService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<run.mone.mesh.bo.SideCarRequest,
      run.mone.mesh.bo.SideCarResponse> getCallMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "call",
      requestType = run.mone.mesh.bo.SideCarRequest.class,
      responseType = run.mone.mesh.bo.SideCarResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<run.mone.mesh.bo.SideCarRequest,
      run.mone.mesh.bo.SideCarResponse> getCallMethod() {
    io.grpc.MethodDescriptor<run.mone.mesh.bo.SideCarRequest, run.mone.mesh.bo.SideCarResponse> getCallMethod;
    if ((getCallMethod = SideCarServiceGrpc.getCallMethod) == null) {
      synchronized (SideCarServiceGrpc.class) {
        if ((getCallMethod = SideCarServiceGrpc.getCallMethod) == null) {
          SideCarServiceGrpc.getCallMethod = getCallMethod =
              io.grpc.MethodDescriptor.<run.mone.mesh.bo.SideCarRequest, run.mone.mesh.bo.SideCarResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "call"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  run.mone.mesh.bo.SideCarRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  run.mone.mesh.bo.SideCarResponse.getDefaultInstance()))
              .setSchemaDescriptor(new SideCarServiceMethodDescriptorSupplier("call"))
              .build();
        }
      }
    }
    return getCallMethod;
  }

  private static volatile io.grpc.MethodDescriptor<run.mone.mesh.bo.SideCarRequest,
      run.mone.mesh.bo.SideCarPushMsg> getListenMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "listen",
      requestType = run.mone.mesh.bo.SideCarRequest.class,
      responseType = run.mone.mesh.bo.SideCarPushMsg.class,
      methodType = io.grpc.MethodDescriptor.MethodType.BIDI_STREAMING)
  public static io.grpc.MethodDescriptor<run.mone.mesh.bo.SideCarRequest,
      run.mone.mesh.bo.SideCarPushMsg> getListenMethod() {
    io.grpc.MethodDescriptor<run.mone.mesh.bo.SideCarRequest, run.mone.mesh.bo.SideCarPushMsg> getListenMethod;
    if ((getListenMethod = SideCarServiceGrpc.getListenMethod) == null) {
      synchronized (SideCarServiceGrpc.class) {
        if ((getListenMethod = SideCarServiceGrpc.getListenMethod) == null) {
          SideCarServiceGrpc.getListenMethod = getListenMethod =
              io.grpc.MethodDescriptor.<run.mone.mesh.bo.SideCarRequest, run.mone.mesh.bo.SideCarPushMsg>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.BIDI_STREAMING)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "listen"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  run.mone.mesh.bo.SideCarRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  run.mone.mesh.bo.SideCarPushMsg.getDefaultInstance()))
              .setSchemaDescriptor(new SideCarServiceMethodDescriptorSupplier("listen"))
              .build();
        }
      }
    }
    return getListenMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static SideCarServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<SideCarServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<SideCarServiceStub>() {
        @java.lang.Override
        public SideCarServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new SideCarServiceStub(channel, callOptions);
        }
      };
    return SideCarServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static SideCarServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<SideCarServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<SideCarServiceBlockingStub>() {
        @java.lang.Override
        public SideCarServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new SideCarServiceBlockingStub(channel, callOptions);
        }
      };
    return SideCarServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static SideCarServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<SideCarServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<SideCarServiceFutureStub>() {
        @java.lang.Override
        public SideCarServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new SideCarServiceFutureStub(channel, callOptions);
        }
      };
    return SideCarServiceFutureStub.newStub(factory, channel);
  }

  /**
   */
  public static abstract class SideCarServiceImplBase implements io.grpc.BindableService {

    /**
     */
    public void call(run.mone.mesh.bo.SideCarRequest request,
        io.grpc.stub.StreamObserver<run.mone.mesh.bo.SideCarResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getCallMethod(), responseObserver);
    }

    /**
     */
    public io.grpc.stub.StreamObserver<run.mone.mesh.bo.SideCarRequest> listen(
        io.grpc.stub.StreamObserver<run.mone.mesh.bo.SideCarPushMsg> responseObserver) {
      return asyncUnimplementedStreamingCall(getListenMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getCallMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                run.mone.mesh.bo.SideCarRequest,
                run.mone.mesh.bo.SideCarResponse>(
                  this, METHODID_CALL)))
          .addMethod(
            getListenMethod(),
            asyncBidiStreamingCall(
              new MethodHandlers<
                run.mone.mesh.bo.SideCarRequest,
                run.mone.mesh.bo.SideCarPushMsg>(
                  this, METHODID_LISTEN)))
          .build();
    }
  }

  /**
   */
  public static final class SideCarServiceStub extends io.grpc.stub.AbstractAsyncStub<SideCarServiceStub> {
    private SideCarServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected SideCarServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new SideCarServiceStub(channel, callOptions);
    }

    /**
     */
    public void call(run.mone.mesh.bo.SideCarRequest request,
        io.grpc.stub.StreamObserver<run.mone.mesh.bo.SideCarResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getCallMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public io.grpc.stub.StreamObserver<run.mone.mesh.bo.SideCarRequest> listen(
        io.grpc.stub.StreamObserver<run.mone.mesh.bo.SideCarPushMsg> responseObserver) {
      return asyncBidiStreamingCall(
          getChannel().newCall(getListenMethod(), getCallOptions()), responseObserver);
    }
  }

  /**
   */
  public static final class SideCarServiceBlockingStub extends io.grpc.stub.AbstractBlockingStub<SideCarServiceBlockingStub> {
    private SideCarServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected SideCarServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new SideCarServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public run.mone.mesh.bo.SideCarResponse call(run.mone.mesh.bo.SideCarRequest request) {
      return blockingUnaryCall(
          getChannel(), getCallMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class SideCarServiceFutureStub extends io.grpc.stub.AbstractFutureStub<SideCarServiceFutureStub> {
    private SideCarServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected SideCarServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new SideCarServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<run.mone.mesh.bo.SideCarResponse> call(
        run.mone.mesh.bo.SideCarRequest request) {
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
    private final SideCarServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(SideCarServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_CALL:
          serviceImpl.call((run.mone.mesh.bo.SideCarRequest) request,
              (io.grpc.stub.StreamObserver<run.mone.mesh.bo.SideCarResponse>) responseObserver);
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
        case METHODID_LISTEN:
          return (io.grpc.stub.StreamObserver<Req>) serviceImpl.listen(
              (io.grpc.stub.StreamObserver<run.mone.mesh.bo.SideCarPushMsg>) responseObserver);
        default:
          throw new AssertionError();
      }
    }
  }

  private static abstract class SideCarServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    SideCarServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return run.mone.mesh.bo.Sidecar.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("SideCarService");
    }
  }

  private static final class SideCarServiceFileDescriptorSupplier
      extends SideCarServiceBaseDescriptorSupplier {
    SideCarServiceFileDescriptorSupplier() {}
  }

  private static final class SideCarServiceMethodDescriptorSupplier
      extends SideCarServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    SideCarServiceMethodDescriptorSupplier(String methodName) {
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
      synchronized (SideCarServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new SideCarServiceFileDescriptorSupplier())
              .addMethod(getCallMethod())
              .addMethod(getListenMethod())
              .build();
        }
      }
    }
    return result;
  }
}
