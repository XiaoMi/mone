// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: sidecar.proto

package run.mone.mesh.bo;

public interface SideCarResponseOrBuilder extends
    // @@protoc_insertion_point(interface_extends:run.mone.mesh.bo.SideCarResponse)
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
   * <code>bytes data = 3;</code>
   * @return The data.
   */
  com.google.protobuf.ByteString getData();

  /**
   * <code>map&lt;string, string&gt; attachments = 4;</code>
   */
  int getAttachmentsCount();
  /**
   * <code>map&lt;string, string&gt; attachments = 4;</code>
   */
  boolean containsAttachments(
      java.lang.String key);
  /**
   * Use {@link #getAttachmentsMap()} instead.
   */
  @java.lang.Deprecated
  java.util.Map<java.lang.String, java.lang.String>
  getAttachments();
  /**
   * <code>map&lt;string, string&gt; attachments = 4;</code>
   */
  java.util.Map<java.lang.String, java.lang.String>
  getAttachmentsMap();
  /**
   * <code>map&lt;string, string&gt; attachments = 4;</code>
   */

  java.lang.String getAttachmentsOrDefault(
      java.lang.String key,
      java.lang.String defaultValue);
  /**
   * <code>map&lt;string, string&gt; attachments = 4;</code>
   */

  java.lang.String getAttachmentsOrThrow(
      java.lang.String key);
}
