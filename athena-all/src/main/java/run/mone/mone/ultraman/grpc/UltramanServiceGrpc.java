package run.mone.mone.ultraman.grpc;

import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ClientCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ClientCalls.asyncClientStreamingCall;
import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.stub.ServerCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ServerCalls.asyncClientStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.34.1)",
    comments = "Source: service.proto")
public final class UltramanServiceGrpc {

  private UltramanServiceGrpc() {}

  public static final String SERVICE_NAME = "com.xiaomi.mone.ultraman.grpc.UltramanService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<UltramanRequest,
      UltramanResponse> getHelloMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "hello",
      requestType = UltramanRequest.class,
      responseType = UltramanResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<UltramanRequest,
      UltramanResponse> getHelloMethod() {
    io.grpc.MethodDescriptor<UltramanRequest, UltramanResponse> getHelloMethod;
    if ((getHelloMethod = UltramanServiceGrpc.getHelloMethod) == null) {
      synchronized (UltramanServiceGrpc.class) {
        if ((getHelloMethod = UltramanServiceGrpc.getHelloMethod) == null) {
          UltramanServiceGrpc.getHelloMethod = getHelloMethod =
              io.grpc.MethodDescriptor.<UltramanRequest, UltramanResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "hello"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  UltramanRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  UltramanResponse.getDefaultInstance()))
              .setSchemaDescriptor(new UltramanServiceMethodDescriptorSupplier("hello"))
              .build();
        }
      }
    }
    return getHelloMethod;
  }

  private static volatile io.grpc.MethodDescriptor<UltramanRequest,
      UltramanResponse> getStreamMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "stream",
      requestType = UltramanRequest.class,
      responseType = UltramanResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.BIDI_STREAMING)
  public static io.grpc.MethodDescriptor<UltramanRequest,
      UltramanResponse> getStreamMethod() {
    io.grpc.MethodDescriptor<UltramanRequest, UltramanResponse> getStreamMethod;
    if ((getStreamMethod = UltramanServiceGrpc.getStreamMethod) == null) {
      synchronized (UltramanServiceGrpc.class) {
        if ((getStreamMethod = UltramanServiceGrpc.getStreamMethod) == null) {
          UltramanServiceGrpc.getStreamMethod = getStreamMethod =
              io.grpc.MethodDescriptor.<UltramanRequest, UltramanResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.BIDI_STREAMING)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "stream"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  UltramanRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  UltramanResponse.getDefaultInstance()))
              .setSchemaDescriptor(new UltramanServiceMethodDescriptorSupplier("stream"))
              .build();
        }
      }
    }
    return getStreamMethod;
  }

  private static volatile io.grpc.MethodDescriptor<UltramanRequest,
      UltramanResponse> getCallMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "call",
      requestType = UltramanRequest.class,
      responseType = UltramanResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.CLIENT_STREAMING)
  public static io.grpc.MethodDescriptor<UltramanRequest,
      UltramanResponse> getCallMethod() {
    io.grpc.MethodDescriptor<UltramanRequest, UltramanResponse> getCallMethod;
    if ((getCallMethod = UltramanServiceGrpc.getCallMethod) == null) {
      synchronized (UltramanServiceGrpc.class) {
        if ((getCallMethod = UltramanServiceGrpc.getCallMethod) == null) {
          UltramanServiceGrpc.getCallMethod = getCallMethod =
              io.grpc.MethodDescriptor.<UltramanRequest, UltramanResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.CLIENT_STREAMING)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "call"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  UltramanRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  UltramanResponse.getDefaultInstance()))
              .setSchemaDescriptor(new UltramanServiceMethodDescriptorSupplier("call"))
              .build();
        }
      }
    }
    return getCallMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static UltramanServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<UltramanServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<UltramanServiceStub>() {
        @Override
        public UltramanServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new UltramanServiceStub(channel, callOptions);
        }
      };
    return UltramanServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static UltramanServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<UltramanServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<UltramanServiceBlockingStub>() {
        @Override
        public UltramanServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new UltramanServiceBlockingStub(channel, callOptions);
        }
      };
    return UltramanServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static UltramanServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<UltramanServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<UltramanServiceFutureStub>() {
        @Override
        public UltramanServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new UltramanServiceFutureStub(channel, callOptions);
        }
      };
    return UltramanServiceFutureStub.newStub(factory, channel);
  }

  /**
   */
  public static abstract class UltramanServiceImplBase implements io.grpc.BindableService {

    /**
     */
    public void hello(UltramanRequest request,
                      io.grpc.stub.StreamObserver<UltramanResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getHelloMethod(), responseObserver);
    }

    /**
     */
    public io.grpc.stub.StreamObserver<UltramanRequest> stream(
        io.grpc.stub.StreamObserver<UltramanResponse> responseObserver) {
      return asyncUnimplementedStreamingCall(getStreamMethod(), responseObserver);
    }

    /**
     */
    public io.grpc.stub.StreamObserver<UltramanRequest> call(
        io.grpc.stub.StreamObserver<UltramanResponse> responseObserver) {
      return asyncUnimplementedStreamingCall(getCallMethod(), responseObserver);
    }

    @Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getHelloMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                UltramanRequest,
                UltramanResponse>(
                  this, METHODID_HELLO)))
          .addMethod(
            getStreamMethod(),
            asyncBidiStreamingCall(
              new MethodHandlers<
                UltramanRequest,
                UltramanResponse>(
                  this, METHODID_STREAM)))
          .addMethod(
            getCallMethod(),
            asyncClientStreamingCall(
              new MethodHandlers<
                UltramanRequest,
                UltramanResponse>(
                  this, METHODID_CALL)))
          .build();
    }
  }

  /**
   */
  public static final class UltramanServiceStub extends io.grpc.stub.AbstractAsyncStub<UltramanServiceStub> {
    private UltramanServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected UltramanServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new UltramanServiceStub(channel, callOptions);
    }

    /**
     */
    public void hello(UltramanRequest request,
                      io.grpc.stub.StreamObserver<UltramanResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getHelloMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public io.grpc.stub.StreamObserver<UltramanRequest> stream(
        io.grpc.stub.StreamObserver<UltramanResponse> responseObserver) {
      return asyncBidiStreamingCall(
          getChannel().newCall(getStreamMethod(), getCallOptions()), responseObserver);
    }

    /**
     */
    public io.grpc.stub.StreamObserver<UltramanRequest> call(
        io.grpc.stub.StreamObserver<UltramanResponse> responseObserver) {
      return asyncClientStreamingCall(
          getChannel().newCall(getCallMethod(), getCallOptions()), responseObserver);
    }
  }

  /**
   */
  public static final class UltramanServiceBlockingStub extends io.grpc.stub.AbstractBlockingStub<UltramanServiceBlockingStub> {
    private UltramanServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected UltramanServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new UltramanServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public UltramanResponse hello(UltramanRequest request) {
      return blockingUnaryCall(
          getChannel(), getHelloMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class UltramanServiceFutureStub extends io.grpc.stub.AbstractFutureStub<UltramanServiceFutureStub> {
    private UltramanServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected UltramanServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new UltramanServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<UltramanResponse> hello(
        UltramanRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getHelloMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_HELLO = 0;
  private static final int METHODID_STREAM = 1;
  private static final int METHODID_CALL = 2;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final UltramanServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(UltramanServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_HELLO:
          serviceImpl.hello((UltramanRequest) request,
              (io.grpc.stub.StreamObserver<UltramanResponse>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @Override
    @SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_STREAM:
          return (io.grpc.stub.StreamObserver<Req>) serviceImpl.stream(
              (io.grpc.stub.StreamObserver<UltramanResponse>) responseObserver);
        case METHODID_CALL:
          return (io.grpc.stub.StreamObserver<Req>) serviceImpl.call(
              (io.grpc.stub.StreamObserver<UltramanResponse>) responseObserver);
        default:
          throw new AssertionError();
      }
    }
  }

  private static abstract class UltramanServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    UltramanServiceBaseDescriptorSupplier() {}

    @Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return Service.getDescriptor();
    }

    @Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("UltramanService");
    }
  }

  private static final class UltramanServiceFileDescriptorSupplier
      extends UltramanServiceBaseDescriptorSupplier {
    UltramanServiceFileDescriptorSupplier() {}
  }

  private static final class UltramanServiceMethodDescriptorSupplier
      extends UltramanServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    UltramanServiceMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (UltramanServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new UltramanServiceFileDescriptorSupplier())
              .addMethod(getHelloMethod())
              .addMethod(getStreamMethod())
              .addMethod(getCallMethod())
              .build();
        }
      }
    }
    return result;
  }
}
