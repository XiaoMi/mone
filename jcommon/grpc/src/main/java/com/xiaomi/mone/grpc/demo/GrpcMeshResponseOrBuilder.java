// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: service.proto

package com.xiaomi.mone.grpc.demo;

public interface GrpcMeshResponseOrBuilder extends
    // @@protoc_insertion_point(interface_extends:com.xiaomi.mone.grpc.demo.GrpcMeshResponse)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>int32 code = 1;</code>
   * @return The code.
   */
  int getCode();

  /**
   * <code>string message = 2;</code>
   * @return The message.
   */
  java.lang.String getMessage();
  /**
   * <code>string message = 2;</code>
   * @return The bytes for message.
   */
  com.google.protobuf.ByteString
      getMessageBytes();

  /**
   * <code>string data = 3;</code>
   * @return The data.
   */
  java.lang.String getData();
  /**
   * <code>string data = 3;</code>
   * @return The bytes for data.
   */
  com.google.protobuf.ByteString
      getDataBytes();
}