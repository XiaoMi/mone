// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: bo.proto

package run.mone.local.docean.protobuf;

public interface ListResponseOrBuilder extends
    // @@protoc_insertion_point(interface_extends:run.mone.local.docean.protobuf.ListResponse)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>string data = 1;</code>
   * @return The data.
   */
  java.lang.String getData();
  /**
   * <code>string data = 1;</code>
   * @return The bytes for data.
   */
  com.google.protobuf.ByteString
      getDataBytes();

  /**
   * <pre>
   *detail
   * </pre>
   *
   * <code>repeated .run.mone.local.docean.protobuf.InstanceProto instances = 2;</code>
   */
  java.util.List<run.mone.local.docean.protobuf.InstanceProto> 
      getInstancesList();
  /**
   * <pre>
   *detail
   * </pre>
   *
   * <code>repeated .run.mone.local.docean.protobuf.InstanceProto instances = 2;</code>
   */
  run.mone.local.docean.protobuf.InstanceProto getInstances(int index);
  /**
   * <pre>
   *detail
   * </pre>
   *
   * <code>repeated .run.mone.local.docean.protobuf.InstanceProto instances = 2;</code>
   */
  int getInstancesCount();
  /**
   * <pre>
   *detail
   * </pre>
   *
   * <code>repeated .run.mone.local.docean.protobuf.InstanceProto instances = 2;</code>
   */
  java.util.List<? extends run.mone.local.docean.protobuf.InstanceProtoOrBuilder> 
      getInstancesOrBuilderList();
  /**
   * <pre>
   *detail
   * </pre>
   *
   * <code>repeated .run.mone.local.docean.protobuf.InstanceProto instances = 2;</code>
   */
  run.mone.local.docean.protobuf.InstanceProtoOrBuilder getInstancesOrBuilder(
      int index);

  /**
   * <code>int32 code = 3;</code>
   * @return The code.
   */
  int getCode();

  /**
   * <code>.run.mone.local.docean.protobuf.InstanceProto instance = 4;</code>
   * @return Whether the instance field is set.
   */
  boolean hasInstance();
  /**
   * <code>.run.mone.local.docean.protobuf.InstanceProto instance = 4;</code>
   * @return The instance.
   */
  run.mone.local.docean.protobuf.InstanceProto getInstance();
  /**
   * <code>.run.mone.local.docean.protobuf.InstanceProto instance = 4;</code>
   */
  run.mone.local.docean.protobuf.InstanceProtoOrBuilder getInstanceOrBuilder();
}