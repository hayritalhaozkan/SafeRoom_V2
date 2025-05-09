package com.saferoom.grpc;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.58.0)",
    comments = "Source: stun.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class UDPHoleGrpc {

  private UDPHoleGrpc() {}

  public static final java.lang.String SERVICE_NAME = "UDPHole";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<com.saferoom.grpc.SafeRoomProto.Stun_Info,
      com.saferoom.grpc.SafeRoomProto.Status> getRegisterClientMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "RegisterClient",
      requestType = com.saferoom.grpc.SafeRoomProto.Stun_Info.class,
      responseType = com.saferoom.grpc.SafeRoomProto.Status.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.saferoom.grpc.SafeRoomProto.Stun_Info,
      com.saferoom.grpc.SafeRoomProto.Status> getRegisterClientMethod() {
    io.grpc.MethodDescriptor<com.saferoom.grpc.SafeRoomProto.Stun_Info, com.saferoom.grpc.SafeRoomProto.Status> getRegisterClientMethod;
    if ((getRegisterClientMethod = UDPHoleGrpc.getRegisterClientMethod) == null) {
      synchronized (UDPHoleGrpc.class) {
        if ((getRegisterClientMethod = UDPHoleGrpc.getRegisterClientMethod) == null) {
          UDPHoleGrpc.getRegisterClientMethod = getRegisterClientMethod =
              io.grpc.MethodDescriptor.<com.saferoom.grpc.SafeRoomProto.Stun_Info, com.saferoom.grpc.SafeRoomProto.Status>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "RegisterClient"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.saferoom.grpc.SafeRoomProto.Stun_Info.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.saferoom.grpc.SafeRoomProto.Status.getDefaultInstance()))
              .setSchemaDescriptor(new UDPHoleMethodDescriptorSupplier("RegisterClient"))
              .build();
        }
      }
    }
    return getRegisterClientMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.saferoom.grpc.SafeRoomProto.Request_Client,
      com.saferoom.grpc.SafeRoomProto.Stun_Info> getGetStunInfoMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetStunInfo",
      requestType = com.saferoom.grpc.SafeRoomProto.Request_Client.class,
      responseType = com.saferoom.grpc.SafeRoomProto.Stun_Info.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.saferoom.grpc.SafeRoomProto.Request_Client,
      com.saferoom.grpc.SafeRoomProto.Stun_Info> getGetStunInfoMethod() {
    io.grpc.MethodDescriptor<com.saferoom.grpc.SafeRoomProto.Request_Client, com.saferoom.grpc.SafeRoomProto.Stun_Info> getGetStunInfoMethod;
    if ((getGetStunInfoMethod = UDPHoleGrpc.getGetStunInfoMethod) == null) {
      synchronized (UDPHoleGrpc.class) {
        if ((getGetStunInfoMethod = UDPHoleGrpc.getGetStunInfoMethod) == null) {
          UDPHoleGrpc.getGetStunInfoMethod = getGetStunInfoMethod =
              io.grpc.MethodDescriptor.<com.saferoom.grpc.SafeRoomProto.Request_Client, com.saferoom.grpc.SafeRoomProto.Stun_Info>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetStunInfo"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.saferoom.grpc.SafeRoomProto.Request_Client.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.saferoom.grpc.SafeRoomProto.Stun_Info.getDefaultInstance()))
              .setSchemaDescriptor(new UDPHoleMethodDescriptorSupplier("GetStunInfo"))
              .build();
        }
      }
    }
    return getGetStunInfoMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.saferoom.grpc.SafeRoomProto.FromTo,
      com.saferoom.grpc.SafeRoomProto.Status> getPunchTestMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "PunchTest",
      requestType = com.saferoom.grpc.SafeRoomProto.FromTo.class,
      responseType = com.saferoom.grpc.SafeRoomProto.Status.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.saferoom.grpc.SafeRoomProto.FromTo,
      com.saferoom.grpc.SafeRoomProto.Status> getPunchTestMethod() {
    io.grpc.MethodDescriptor<com.saferoom.grpc.SafeRoomProto.FromTo, com.saferoom.grpc.SafeRoomProto.Status> getPunchTestMethod;
    if ((getPunchTestMethod = UDPHoleGrpc.getPunchTestMethod) == null) {
      synchronized (UDPHoleGrpc.class) {
        if ((getPunchTestMethod = UDPHoleGrpc.getPunchTestMethod) == null) {
          UDPHoleGrpc.getPunchTestMethod = getPunchTestMethod =
              io.grpc.MethodDescriptor.<com.saferoom.grpc.SafeRoomProto.FromTo, com.saferoom.grpc.SafeRoomProto.Status>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "PunchTest"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.saferoom.grpc.SafeRoomProto.FromTo.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.saferoom.grpc.SafeRoomProto.Status.getDefaultInstance()))
              .setSchemaDescriptor(new UDPHoleMethodDescriptorSupplier("PunchTest"))
              .build();
        }
      }
    }
    return getPunchTestMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.saferoom.grpc.SafeRoomProto.HandshakeConfirm,
      com.saferoom.grpc.SafeRoomProto.Status> getHandShakeMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "HandShake",
      requestType = com.saferoom.grpc.SafeRoomProto.HandshakeConfirm.class,
      responseType = com.saferoom.grpc.SafeRoomProto.Status.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.saferoom.grpc.SafeRoomProto.HandshakeConfirm,
      com.saferoom.grpc.SafeRoomProto.Status> getHandShakeMethod() {
    io.grpc.MethodDescriptor<com.saferoom.grpc.SafeRoomProto.HandshakeConfirm, com.saferoom.grpc.SafeRoomProto.Status> getHandShakeMethod;
    if ((getHandShakeMethod = UDPHoleGrpc.getHandShakeMethod) == null) {
      synchronized (UDPHoleGrpc.class) {
        if ((getHandShakeMethod = UDPHoleGrpc.getHandShakeMethod) == null) {
          UDPHoleGrpc.getHandShakeMethod = getHandShakeMethod =
              io.grpc.MethodDescriptor.<com.saferoom.grpc.SafeRoomProto.HandshakeConfirm, com.saferoom.grpc.SafeRoomProto.Status>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "HandShake"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.saferoom.grpc.SafeRoomProto.HandshakeConfirm.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.saferoom.grpc.SafeRoomProto.Status.getDefaultInstance()))
              .setSchemaDescriptor(new UDPHoleMethodDescriptorSupplier("HandShake"))
              .build();
        }
      }
    }
    return getHandShakeMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.saferoom.grpc.SafeRoomProto.Stun_Info,
      com.saferoom.grpc.SafeRoomProto.Status> getHeartBeatMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "HeartBeat",
      requestType = com.saferoom.grpc.SafeRoomProto.Stun_Info.class,
      responseType = com.saferoom.grpc.SafeRoomProto.Status.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.saferoom.grpc.SafeRoomProto.Stun_Info,
      com.saferoom.grpc.SafeRoomProto.Status> getHeartBeatMethod() {
    io.grpc.MethodDescriptor<com.saferoom.grpc.SafeRoomProto.Stun_Info, com.saferoom.grpc.SafeRoomProto.Status> getHeartBeatMethod;
    if ((getHeartBeatMethod = UDPHoleGrpc.getHeartBeatMethod) == null) {
      synchronized (UDPHoleGrpc.class) {
        if ((getHeartBeatMethod = UDPHoleGrpc.getHeartBeatMethod) == null) {
          UDPHoleGrpc.getHeartBeatMethod = getHeartBeatMethod =
              io.grpc.MethodDescriptor.<com.saferoom.grpc.SafeRoomProto.Stun_Info, com.saferoom.grpc.SafeRoomProto.Status>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "HeartBeat"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.saferoom.grpc.SafeRoomProto.Stun_Info.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.saferoom.grpc.SafeRoomProto.Status.getDefaultInstance()))
              .setSchemaDescriptor(new UDPHoleMethodDescriptorSupplier("HeartBeat"))
              .build();
        }
      }
    }
    return getHeartBeatMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.saferoom.grpc.SafeRoomProto.Request_Client,
      com.saferoom.grpc.SafeRoomProto.Status> getFinishMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Finish",
      requestType = com.saferoom.grpc.SafeRoomProto.Request_Client.class,
      responseType = com.saferoom.grpc.SafeRoomProto.Status.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.saferoom.grpc.SafeRoomProto.Request_Client,
      com.saferoom.grpc.SafeRoomProto.Status> getFinishMethod() {
    io.grpc.MethodDescriptor<com.saferoom.grpc.SafeRoomProto.Request_Client, com.saferoom.grpc.SafeRoomProto.Status> getFinishMethod;
    if ((getFinishMethod = UDPHoleGrpc.getFinishMethod) == null) {
      synchronized (UDPHoleGrpc.class) {
        if ((getFinishMethod = UDPHoleGrpc.getFinishMethod) == null) {
          UDPHoleGrpc.getFinishMethod = getFinishMethod =
              io.grpc.MethodDescriptor.<com.saferoom.grpc.SafeRoomProto.Request_Client, com.saferoom.grpc.SafeRoomProto.Status>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "Finish"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.saferoom.grpc.SafeRoomProto.Request_Client.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.saferoom.grpc.SafeRoomProto.Status.getDefaultInstance()))
              .setSchemaDescriptor(new UDPHoleMethodDescriptorSupplier("Finish"))
              .build();
        }
      }
    }
    return getFinishMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.saferoom.grpc.SafeRoomProto.Empty,
      com.saferoom.grpc.SafeRoomProto.PublicKeyMessage> getGetServerPublicKeyMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetServerPublicKey",
      requestType = com.saferoom.grpc.SafeRoomProto.Empty.class,
      responseType = com.saferoom.grpc.SafeRoomProto.PublicKeyMessage.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.saferoom.grpc.SafeRoomProto.Empty,
      com.saferoom.grpc.SafeRoomProto.PublicKeyMessage> getGetServerPublicKeyMethod() {
    io.grpc.MethodDescriptor<com.saferoom.grpc.SafeRoomProto.Empty, com.saferoom.grpc.SafeRoomProto.PublicKeyMessage> getGetServerPublicKeyMethod;
    if ((getGetServerPublicKeyMethod = UDPHoleGrpc.getGetServerPublicKeyMethod) == null) {
      synchronized (UDPHoleGrpc.class) {
        if ((getGetServerPublicKeyMethod = UDPHoleGrpc.getGetServerPublicKeyMethod) == null) {
          UDPHoleGrpc.getGetServerPublicKeyMethod = getGetServerPublicKeyMethod =
              io.grpc.MethodDescriptor.<com.saferoom.grpc.SafeRoomProto.Empty, com.saferoom.grpc.SafeRoomProto.PublicKeyMessage>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetServerPublicKey"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.saferoom.grpc.SafeRoomProto.Empty.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.saferoom.grpc.SafeRoomProto.PublicKeyMessage.getDefaultInstance()))
              .setSchemaDescriptor(new UDPHoleMethodDescriptorSupplier("GetServerPublicKey"))
              .build();
        }
      }
    }
    return getGetServerPublicKeyMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.saferoom.grpc.SafeRoomProto.EncryptedAESKeyMessage,
      com.saferoom.grpc.SafeRoomProto.Status> getSendEncryptedAESKeyMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "SendEncryptedAESKey",
      requestType = com.saferoom.grpc.SafeRoomProto.EncryptedAESKeyMessage.class,
      responseType = com.saferoom.grpc.SafeRoomProto.Status.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.saferoom.grpc.SafeRoomProto.EncryptedAESKeyMessage,
      com.saferoom.grpc.SafeRoomProto.Status> getSendEncryptedAESKeyMethod() {
    io.grpc.MethodDescriptor<com.saferoom.grpc.SafeRoomProto.EncryptedAESKeyMessage, com.saferoom.grpc.SafeRoomProto.Status> getSendEncryptedAESKeyMethod;
    if ((getSendEncryptedAESKeyMethod = UDPHoleGrpc.getSendEncryptedAESKeyMethod) == null) {
      synchronized (UDPHoleGrpc.class) {
        if ((getSendEncryptedAESKeyMethod = UDPHoleGrpc.getSendEncryptedAESKeyMethod) == null) {
          UDPHoleGrpc.getSendEncryptedAESKeyMethod = getSendEncryptedAESKeyMethod =
              io.grpc.MethodDescriptor.<com.saferoom.grpc.SafeRoomProto.EncryptedAESKeyMessage, com.saferoom.grpc.SafeRoomProto.Status>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "SendEncryptedAESKey"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.saferoom.grpc.SafeRoomProto.EncryptedAESKeyMessage.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.saferoom.grpc.SafeRoomProto.Status.getDefaultInstance()))
              .setSchemaDescriptor(new UDPHoleMethodDescriptorSupplier("SendEncryptedAESKey"))
              .build();
        }
      }
    }
    return getSendEncryptedAESKeyMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.saferoom.grpc.SafeRoomProto.EncryptedPacket,
      com.saferoom.grpc.SafeRoomProto.Status> getSendEncryptedMessageMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "SendEncryptedMessage",
      requestType = com.saferoom.grpc.SafeRoomProto.EncryptedPacket.class,
      responseType = com.saferoom.grpc.SafeRoomProto.Status.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.saferoom.grpc.SafeRoomProto.EncryptedPacket,
      com.saferoom.grpc.SafeRoomProto.Status> getSendEncryptedMessageMethod() {
    io.grpc.MethodDescriptor<com.saferoom.grpc.SafeRoomProto.EncryptedPacket, com.saferoom.grpc.SafeRoomProto.Status> getSendEncryptedMessageMethod;
    if ((getSendEncryptedMessageMethod = UDPHoleGrpc.getSendEncryptedMessageMethod) == null) {
      synchronized (UDPHoleGrpc.class) {
        if ((getSendEncryptedMessageMethod = UDPHoleGrpc.getSendEncryptedMessageMethod) == null) {
          UDPHoleGrpc.getSendEncryptedMessageMethod = getSendEncryptedMessageMethod =
              io.grpc.MethodDescriptor.<com.saferoom.grpc.SafeRoomProto.EncryptedPacket, com.saferoom.grpc.SafeRoomProto.Status>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "SendEncryptedMessage"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.saferoom.grpc.SafeRoomProto.EncryptedPacket.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.saferoom.grpc.SafeRoomProto.Status.getDefaultInstance()))
              .setSchemaDescriptor(new UDPHoleMethodDescriptorSupplier("SendEncryptedMessage"))
              .build();
        }
      }
    }
    return getSendEncryptedMessageMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.saferoom.grpc.SafeRoomProto.EncryptedPacket,
      com.saferoom.grpc.SafeRoomProto.DecryptedPacket> getDecryptedMessageMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "DecryptedMessage",
      requestType = com.saferoom.grpc.SafeRoomProto.EncryptedPacket.class,
      responseType = com.saferoom.grpc.SafeRoomProto.DecryptedPacket.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.saferoom.grpc.SafeRoomProto.EncryptedPacket,
      com.saferoom.grpc.SafeRoomProto.DecryptedPacket> getDecryptedMessageMethod() {
    io.grpc.MethodDescriptor<com.saferoom.grpc.SafeRoomProto.EncryptedPacket, com.saferoom.grpc.SafeRoomProto.DecryptedPacket> getDecryptedMessageMethod;
    if ((getDecryptedMessageMethod = UDPHoleGrpc.getDecryptedMessageMethod) == null) {
      synchronized (UDPHoleGrpc.class) {
        if ((getDecryptedMessageMethod = UDPHoleGrpc.getDecryptedMessageMethod) == null) {
          UDPHoleGrpc.getDecryptedMessageMethod = getDecryptedMessageMethod =
              io.grpc.MethodDescriptor.<com.saferoom.grpc.SafeRoomProto.EncryptedPacket, com.saferoom.grpc.SafeRoomProto.DecryptedPacket>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "DecryptedMessage"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.saferoom.grpc.SafeRoomProto.EncryptedPacket.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.saferoom.grpc.SafeRoomProto.DecryptedPacket.getDefaultInstance()))
              .setSchemaDescriptor(new UDPHoleMethodDescriptorSupplier("DecryptedMessage"))
              .build();
        }
      }
    }
    return getDecryptedMessageMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static UDPHoleStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<UDPHoleStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<UDPHoleStub>() {
        @java.lang.Override
        public UDPHoleStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new UDPHoleStub(channel, callOptions);
        }
      };
    return UDPHoleStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static UDPHoleBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<UDPHoleBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<UDPHoleBlockingStub>() {
        @java.lang.Override
        public UDPHoleBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new UDPHoleBlockingStub(channel, callOptions);
        }
      };
    return UDPHoleBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static UDPHoleFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<UDPHoleFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<UDPHoleFutureStub>() {
        @java.lang.Override
        public UDPHoleFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new UDPHoleFutureStub(channel, callOptions);
        }
      };
    return UDPHoleFutureStub.newStub(factory, channel);
  }

  /**
   */
  public interface AsyncService {

    /**
     */
    default void registerClient(com.saferoom.grpc.SafeRoomProto.Stun_Info request,
        io.grpc.stub.StreamObserver<com.saferoom.grpc.SafeRoomProto.Status> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getRegisterClientMethod(), responseObserver);
    }

    /**
     */
    default void getStunInfo(com.saferoom.grpc.SafeRoomProto.Request_Client request,
        io.grpc.stub.StreamObserver<com.saferoom.grpc.SafeRoomProto.Stun_Info> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetStunInfoMethod(), responseObserver);
    }

    /**
     */
    default void punchTest(com.saferoom.grpc.SafeRoomProto.FromTo request,
        io.grpc.stub.StreamObserver<com.saferoom.grpc.SafeRoomProto.Status> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getPunchTestMethod(), responseObserver);
    }

    /**
     */
    default void handShake(com.saferoom.grpc.SafeRoomProto.HandshakeConfirm request,
        io.grpc.stub.StreamObserver<com.saferoom.grpc.SafeRoomProto.Status> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getHandShakeMethod(), responseObserver);
    }

    /**
     */
    default void heartBeat(com.saferoom.grpc.SafeRoomProto.Stun_Info request,
        io.grpc.stub.StreamObserver<com.saferoom.grpc.SafeRoomProto.Status> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getHeartBeatMethod(), responseObserver);
    }

    /**
     */
    default void finish(com.saferoom.grpc.SafeRoomProto.Request_Client request,
        io.grpc.stub.StreamObserver<com.saferoom.grpc.SafeRoomProto.Status> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getFinishMethod(), responseObserver);
    }

    /**
     */
    default void getServerPublicKey(com.saferoom.grpc.SafeRoomProto.Empty request,
        io.grpc.stub.StreamObserver<com.saferoom.grpc.SafeRoomProto.PublicKeyMessage> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetServerPublicKeyMethod(), responseObserver);
    }

    /**
     */
    default void sendEncryptedAESKey(com.saferoom.grpc.SafeRoomProto.EncryptedAESKeyMessage request,
        io.grpc.stub.StreamObserver<com.saferoom.grpc.SafeRoomProto.Status> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getSendEncryptedAESKeyMethod(), responseObserver);
    }

    /**
     */
    default void sendEncryptedMessage(com.saferoom.grpc.SafeRoomProto.EncryptedPacket request,
        io.grpc.stub.StreamObserver<com.saferoom.grpc.SafeRoomProto.Status> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getSendEncryptedMessageMethod(), responseObserver);
    }

    /**
     */
    default void decryptedMessage(com.saferoom.grpc.SafeRoomProto.EncryptedPacket request,
        io.grpc.stub.StreamObserver<com.saferoom.grpc.SafeRoomProto.DecryptedPacket> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getDecryptedMessageMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service UDPHole.
   */
  public static abstract class UDPHoleImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return UDPHoleGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service UDPHole.
   */
  public static final class UDPHoleStub
      extends io.grpc.stub.AbstractAsyncStub<UDPHoleStub> {
    private UDPHoleStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected UDPHoleStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new UDPHoleStub(channel, callOptions);
    }

    /**
     */
    public void registerClient(com.saferoom.grpc.SafeRoomProto.Stun_Info request,
        io.grpc.stub.StreamObserver<com.saferoom.grpc.SafeRoomProto.Status> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getRegisterClientMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void getStunInfo(com.saferoom.grpc.SafeRoomProto.Request_Client request,
        io.grpc.stub.StreamObserver<com.saferoom.grpc.SafeRoomProto.Stun_Info> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetStunInfoMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void punchTest(com.saferoom.grpc.SafeRoomProto.FromTo request,
        io.grpc.stub.StreamObserver<com.saferoom.grpc.SafeRoomProto.Status> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getPunchTestMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void handShake(com.saferoom.grpc.SafeRoomProto.HandshakeConfirm request,
        io.grpc.stub.StreamObserver<com.saferoom.grpc.SafeRoomProto.Status> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getHandShakeMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void heartBeat(com.saferoom.grpc.SafeRoomProto.Stun_Info request,
        io.grpc.stub.StreamObserver<com.saferoom.grpc.SafeRoomProto.Status> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getHeartBeatMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void finish(com.saferoom.grpc.SafeRoomProto.Request_Client request,
        io.grpc.stub.StreamObserver<com.saferoom.grpc.SafeRoomProto.Status> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getFinishMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void getServerPublicKey(com.saferoom.grpc.SafeRoomProto.Empty request,
        io.grpc.stub.StreamObserver<com.saferoom.grpc.SafeRoomProto.PublicKeyMessage> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetServerPublicKeyMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void sendEncryptedAESKey(com.saferoom.grpc.SafeRoomProto.EncryptedAESKeyMessage request,
        io.grpc.stub.StreamObserver<com.saferoom.grpc.SafeRoomProto.Status> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getSendEncryptedAESKeyMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void sendEncryptedMessage(com.saferoom.grpc.SafeRoomProto.EncryptedPacket request,
        io.grpc.stub.StreamObserver<com.saferoom.grpc.SafeRoomProto.Status> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getSendEncryptedMessageMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void decryptedMessage(com.saferoom.grpc.SafeRoomProto.EncryptedPacket request,
        io.grpc.stub.StreamObserver<com.saferoom.grpc.SafeRoomProto.DecryptedPacket> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getDecryptedMessageMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service UDPHole.
   */
  public static final class UDPHoleBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<UDPHoleBlockingStub> {
    private UDPHoleBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected UDPHoleBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new UDPHoleBlockingStub(channel, callOptions);
    }

    /**
     */
    public com.saferoom.grpc.SafeRoomProto.Status registerClient(com.saferoom.grpc.SafeRoomProto.Stun_Info request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getRegisterClientMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.saferoom.grpc.SafeRoomProto.Stun_Info getStunInfo(com.saferoom.grpc.SafeRoomProto.Request_Client request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetStunInfoMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.saferoom.grpc.SafeRoomProto.Status punchTest(com.saferoom.grpc.SafeRoomProto.FromTo request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getPunchTestMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.saferoom.grpc.SafeRoomProto.Status handShake(com.saferoom.grpc.SafeRoomProto.HandshakeConfirm request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getHandShakeMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.saferoom.grpc.SafeRoomProto.Status heartBeat(com.saferoom.grpc.SafeRoomProto.Stun_Info request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getHeartBeatMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.saferoom.grpc.SafeRoomProto.Status finish(com.saferoom.grpc.SafeRoomProto.Request_Client request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getFinishMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.saferoom.grpc.SafeRoomProto.PublicKeyMessage getServerPublicKey(com.saferoom.grpc.SafeRoomProto.Empty request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetServerPublicKeyMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.saferoom.grpc.SafeRoomProto.Status sendEncryptedAESKey(com.saferoom.grpc.SafeRoomProto.EncryptedAESKeyMessage request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getSendEncryptedAESKeyMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.saferoom.grpc.SafeRoomProto.Status sendEncryptedMessage(com.saferoom.grpc.SafeRoomProto.EncryptedPacket request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getSendEncryptedMessageMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.saferoom.grpc.SafeRoomProto.DecryptedPacket decryptedMessage(com.saferoom.grpc.SafeRoomProto.EncryptedPacket request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getDecryptedMessageMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service UDPHole.
   */
  public static final class UDPHoleFutureStub
      extends io.grpc.stub.AbstractFutureStub<UDPHoleFutureStub> {
    private UDPHoleFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected UDPHoleFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new UDPHoleFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.saferoom.grpc.SafeRoomProto.Status> registerClient(
        com.saferoom.grpc.SafeRoomProto.Stun_Info request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getRegisterClientMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.saferoom.grpc.SafeRoomProto.Stun_Info> getStunInfo(
        com.saferoom.grpc.SafeRoomProto.Request_Client request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetStunInfoMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.saferoom.grpc.SafeRoomProto.Status> punchTest(
        com.saferoom.grpc.SafeRoomProto.FromTo request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getPunchTestMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.saferoom.grpc.SafeRoomProto.Status> handShake(
        com.saferoom.grpc.SafeRoomProto.HandshakeConfirm request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getHandShakeMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.saferoom.grpc.SafeRoomProto.Status> heartBeat(
        com.saferoom.grpc.SafeRoomProto.Stun_Info request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getHeartBeatMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.saferoom.grpc.SafeRoomProto.Status> finish(
        com.saferoom.grpc.SafeRoomProto.Request_Client request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getFinishMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.saferoom.grpc.SafeRoomProto.PublicKeyMessage> getServerPublicKey(
        com.saferoom.grpc.SafeRoomProto.Empty request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetServerPublicKeyMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.saferoom.grpc.SafeRoomProto.Status> sendEncryptedAESKey(
        com.saferoom.grpc.SafeRoomProto.EncryptedAESKeyMessage request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getSendEncryptedAESKeyMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.saferoom.grpc.SafeRoomProto.Status> sendEncryptedMessage(
        com.saferoom.grpc.SafeRoomProto.EncryptedPacket request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getSendEncryptedMessageMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.saferoom.grpc.SafeRoomProto.DecryptedPacket> decryptedMessage(
        com.saferoom.grpc.SafeRoomProto.EncryptedPacket request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getDecryptedMessageMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_REGISTER_CLIENT = 0;
  private static final int METHODID_GET_STUN_INFO = 1;
  private static final int METHODID_PUNCH_TEST = 2;
  private static final int METHODID_HAND_SHAKE = 3;
  private static final int METHODID_HEART_BEAT = 4;
  private static final int METHODID_FINISH = 5;
  private static final int METHODID_GET_SERVER_PUBLIC_KEY = 6;
  private static final int METHODID_SEND_ENCRYPTED_AESKEY = 7;
  private static final int METHODID_SEND_ENCRYPTED_MESSAGE = 8;
  private static final int METHODID_DECRYPTED_MESSAGE = 9;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final AsyncService serviceImpl;
    private final int methodId;

    MethodHandlers(AsyncService serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_REGISTER_CLIENT:
          serviceImpl.registerClient((com.saferoom.grpc.SafeRoomProto.Stun_Info) request,
              (io.grpc.stub.StreamObserver<com.saferoom.grpc.SafeRoomProto.Status>) responseObserver);
          break;
        case METHODID_GET_STUN_INFO:
          serviceImpl.getStunInfo((com.saferoom.grpc.SafeRoomProto.Request_Client) request,
              (io.grpc.stub.StreamObserver<com.saferoom.grpc.SafeRoomProto.Stun_Info>) responseObserver);
          break;
        case METHODID_PUNCH_TEST:
          serviceImpl.punchTest((com.saferoom.grpc.SafeRoomProto.FromTo) request,
              (io.grpc.stub.StreamObserver<com.saferoom.grpc.SafeRoomProto.Status>) responseObserver);
          break;
        case METHODID_HAND_SHAKE:
          serviceImpl.handShake((com.saferoom.grpc.SafeRoomProto.HandshakeConfirm) request,
              (io.grpc.stub.StreamObserver<com.saferoom.grpc.SafeRoomProto.Status>) responseObserver);
          break;
        case METHODID_HEART_BEAT:
          serviceImpl.heartBeat((com.saferoom.grpc.SafeRoomProto.Stun_Info) request,
              (io.grpc.stub.StreamObserver<com.saferoom.grpc.SafeRoomProto.Status>) responseObserver);
          break;
        case METHODID_FINISH:
          serviceImpl.finish((com.saferoom.grpc.SafeRoomProto.Request_Client) request,
              (io.grpc.stub.StreamObserver<com.saferoom.grpc.SafeRoomProto.Status>) responseObserver);
          break;
        case METHODID_GET_SERVER_PUBLIC_KEY:
          serviceImpl.getServerPublicKey((com.saferoom.grpc.SafeRoomProto.Empty) request,
              (io.grpc.stub.StreamObserver<com.saferoom.grpc.SafeRoomProto.PublicKeyMessage>) responseObserver);
          break;
        case METHODID_SEND_ENCRYPTED_AESKEY:
          serviceImpl.sendEncryptedAESKey((com.saferoom.grpc.SafeRoomProto.EncryptedAESKeyMessage) request,
              (io.grpc.stub.StreamObserver<com.saferoom.grpc.SafeRoomProto.Status>) responseObserver);
          break;
        case METHODID_SEND_ENCRYPTED_MESSAGE:
          serviceImpl.sendEncryptedMessage((com.saferoom.grpc.SafeRoomProto.EncryptedPacket) request,
              (io.grpc.stub.StreamObserver<com.saferoom.grpc.SafeRoomProto.Status>) responseObserver);
          break;
        case METHODID_DECRYPTED_MESSAGE:
          serviceImpl.decryptedMessage((com.saferoom.grpc.SafeRoomProto.EncryptedPacket) request,
              (io.grpc.stub.StreamObserver<com.saferoom.grpc.SafeRoomProto.DecryptedPacket>) responseObserver);
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

  public static final io.grpc.ServerServiceDefinition bindService(AsyncService service) {
    return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
        .addMethod(
          getRegisterClientMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.saferoom.grpc.SafeRoomProto.Stun_Info,
              com.saferoom.grpc.SafeRoomProto.Status>(
                service, METHODID_REGISTER_CLIENT)))
        .addMethod(
          getGetStunInfoMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.saferoom.grpc.SafeRoomProto.Request_Client,
              com.saferoom.grpc.SafeRoomProto.Stun_Info>(
                service, METHODID_GET_STUN_INFO)))
        .addMethod(
          getPunchTestMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.saferoom.grpc.SafeRoomProto.FromTo,
              com.saferoom.grpc.SafeRoomProto.Status>(
                service, METHODID_PUNCH_TEST)))
        .addMethod(
          getHandShakeMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.saferoom.grpc.SafeRoomProto.HandshakeConfirm,
              com.saferoom.grpc.SafeRoomProto.Status>(
                service, METHODID_HAND_SHAKE)))
        .addMethod(
          getHeartBeatMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.saferoom.grpc.SafeRoomProto.Stun_Info,
              com.saferoom.grpc.SafeRoomProto.Status>(
                service, METHODID_HEART_BEAT)))
        .addMethod(
          getFinishMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.saferoom.grpc.SafeRoomProto.Request_Client,
              com.saferoom.grpc.SafeRoomProto.Status>(
                service, METHODID_FINISH)))
        .addMethod(
          getGetServerPublicKeyMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.saferoom.grpc.SafeRoomProto.Empty,
              com.saferoom.grpc.SafeRoomProto.PublicKeyMessage>(
                service, METHODID_GET_SERVER_PUBLIC_KEY)))
        .addMethod(
          getSendEncryptedAESKeyMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.saferoom.grpc.SafeRoomProto.EncryptedAESKeyMessage,
              com.saferoom.grpc.SafeRoomProto.Status>(
                service, METHODID_SEND_ENCRYPTED_AESKEY)))
        .addMethod(
          getSendEncryptedMessageMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.saferoom.grpc.SafeRoomProto.EncryptedPacket,
              com.saferoom.grpc.SafeRoomProto.Status>(
                service, METHODID_SEND_ENCRYPTED_MESSAGE)))
        .addMethod(
          getDecryptedMessageMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.saferoom.grpc.SafeRoomProto.EncryptedPacket,
              com.saferoom.grpc.SafeRoomProto.DecryptedPacket>(
                service, METHODID_DECRYPTED_MESSAGE)))
        .build();
  }

  private static abstract class UDPHoleBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    UDPHoleBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.saferoom.grpc.SafeRoomProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("UDPHole");
    }
  }

  private static final class UDPHoleFileDescriptorSupplier
      extends UDPHoleBaseDescriptorSupplier {
    UDPHoleFileDescriptorSupplier() {}
  }

  private static final class UDPHoleMethodDescriptorSupplier
      extends UDPHoleBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final java.lang.String methodName;

    UDPHoleMethodDescriptorSupplier(java.lang.String methodName) {
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
      synchronized (UDPHoleGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new UDPHoleFileDescriptorSupplier())
              .addMethod(getRegisterClientMethod())
              .addMethod(getGetStunInfoMethod())
              .addMethod(getPunchTestMethod())
              .addMethod(getHandShakeMethod())
              .addMethod(getHeartBeatMethod())
              .addMethod(getFinishMethod())
              .addMethod(getGetServerPublicKeyMethod())
              .addMethod(getSendEncryptedAESKeyMethod())
              .addMethod(getSendEncryptedMessageMethod())
              .addMethod(getDecryptedMessageMethod())
              .build();
        }
      }
    }
    return result;
  }
}
