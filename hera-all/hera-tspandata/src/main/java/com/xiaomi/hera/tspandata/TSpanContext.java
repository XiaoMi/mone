//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.xiaomi.hera.tspandata;

import java.io.Serializable;
import java.util.BitSet;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.thrift.TBase;
import org.apache.thrift.TBaseHelper;
import org.apache.thrift.TException;
import org.apache.thrift.TFieldIdEnum;
import org.apache.thrift.meta_data.FieldMetaData;
import org.apache.thrift.meta_data.FieldValueMetaData;
import org.apache.thrift.meta_data.StructMetaData;
import org.apache.thrift.protocol.TField;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TProtocolUtil;
import org.apache.thrift.protocol.TStruct;

public class TSpanContext implements TBase<TSpanContext, TSpanContext._Fields>, Serializable, Cloneable {
    private static final TStruct STRUCT_DESC = new TStruct("TSpanContext");
    private static final TField TRACE_ID_FIELD_DESC = new TField("traceId", (byte)11, (short)1);
    private static final TField SPAN_ID_FIELD_DESC = new TField("spanId", (byte)11, (short)2);
    private static final TField TRACE_FLAGS_FIELD_DESC = new TField("traceFlags", (byte)3, (short)3);
    private static final TField TRACE_STATE_FIELD_DESC = new TField("traceState", (byte)12, (short)4);
    private static final TField REMOTE_FIELD_DESC = new TField("remote", (byte)2, (short)5);
    public String traceId;
    public String spanId;
    public byte traceFlags;
    public TTraceState traceState;
    public boolean remote;
    private static final int __TRACEFLAGS_ISSET_ID = 0;
    private static final int __REMOTE_ISSET_ID = 1;
    private BitSet __isset_bit_vector = new BitSet(2);
    public static final Map<TSpanContext._Fields, FieldMetaData> metaDataMap;

    public TSpanContext() {
    }

    public TSpanContext(TSpanContext var1) {
        this.__isset_bit_vector.clear();
        this.__isset_bit_vector.or(var1.__isset_bit_vector);
        if (var1.isSetTraceId()) {
            this.traceId = var1.traceId;
        }

        if (var1.isSetSpanId()) {
            this.spanId = var1.spanId;
        }

        this.traceFlags = var1.traceFlags;
        if (var1.isSetTraceState()) {
            this.traceState = new TTraceState(var1.traceState);
        }

        this.remote = var1.remote;
    }

    public TSpanContext deepCopy() {
        return new TSpanContext(this);
    }

    public void clear() {
        this.traceId = null;
        this.spanId = null;
        this.setTraceFlagsIsSet(false);
        this.traceFlags = 0;
        this.traceState = null;
        this.setRemoteIsSet(false);
        this.remote = false;
    }

    public String getTraceId() {
        return this.traceId;
    }

    public TSpanContext setTraceId(String var1) {
        this.traceId = var1;
        return this;
    }

    public void unsetTraceId() {
        this.traceId = null;
    }

    public boolean isSetTraceId() {
        return this.traceId != null;
    }

    public void setTraceIdIsSet(boolean var1) {
        if (!var1) {
            this.traceId = null;
        }

    }

    public String getSpanId() {
        return this.spanId;
    }

    public TSpanContext setSpanId(String var1) {
        this.spanId = var1;
        return this;
    }

    public void unsetSpanId() {
        this.spanId = null;
    }

    public boolean isSetSpanId() {
        return this.spanId != null;
    }

    public void setSpanIdIsSet(boolean var1) {
        if (!var1) {
            this.spanId = null;
        }

    }

    public byte getTraceFlags() {
        return this.traceFlags;
    }

    public TSpanContext setTraceFlags(byte var1) {
        this.traceFlags = var1;
        this.setTraceFlagsIsSet(true);
        return this;
    }

    public void unsetTraceFlags() {
        this.__isset_bit_vector.clear(0);
    }

    public boolean isSetTraceFlags() {
        return this.__isset_bit_vector.get(0);
    }

    public void setTraceFlagsIsSet(boolean var1) {
        this.__isset_bit_vector.set(0, var1);
    }

    public TTraceState getTraceState() {
        return this.traceState;
    }

    public TSpanContext setTraceState(TTraceState var1) {
        this.traceState = var1;
        return this;
    }

    public void unsetTraceState() {
        this.traceState = null;
    }

    public boolean isSetTraceState() {
        return this.traceState != null;
    }

    public void setTraceStateIsSet(boolean var1) {
        if (!var1) {
            this.traceState = null;
        }

    }

    public boolean isRemote() {
        return this.remote;
    }

    public TSpanContext setRemote(boolean var1) {
        this.remote = var1;
        this.setRemoteIsSet(true);
        return this;
    }

    public void unsetRemote() {
        this.__isset_bit_vector.clear(1);
    }

    public boolean isSetRemote() {
        return this.__isset_bit_vector.get(1);
    }

    public void setRemoteIsSet(boolean var1) {
        this.__isset_bit_vector.set(1, var1);
    }

    public void setFieldValue(TSpanContext._Fields var1, Object var2) {
        switch(var1) {
            case TRACE_ID:
                if (var2 == null) {
                    this.unsetTraceId();
                } else {
                    this.setTraceId((String)var2);
                }
                break;
            case SPAN_ID:
                if (var2 == null) {
                    this.unsetSpanId();
                } else {
                    this.setSpanId((String)var2);
                }
                break;
            case TRACE_FLAGS:
                if (var2 == null) {
                    this.unsetTraceFlags();
                } else {
                    this.setTraceFlags((Byte)var2);
                }
                break;
            case TRACE_STATE:
                if (var2 == null) {
                    this.unsetTraceState();
                } else {
                    this.setTraceState((TTraceState)var2);
                }
                break;
            case REMOTE:
                if (var2 == null) {
                    this.unsetRemote();
                } else {
                    this.setRemote((Boolean)var2);
                }
        }

    }

    public Object getFieldValue(TSpanContext._Fields var1) {
        switch(var1) {
            case TRACE_ID:
                return this.getTraceId();
            case SPAN_ID:
                return this.getSpanId();
            case TRACE_FLAGS:
                return new Byte(this.getTraceFlags());
            case TRACE_STATE:
                return this.getTraceState();
            case REMOTE:
                return new Boolean(this.isRemote());
            default:
                throw new IllegalStateException();
        }
    }

    public boolean isSet(TSpanContext._Fields var1) {
        if (var1 == null) {
            throw new IllegalArgumentException();
        } else {
            switch(var1) {
                case TRACE_ID:
                    return this.isSetTraceId();
                case SPAN_ID:
                    return this.isSetSpanId();
                case TRACE_FLAGS:
                    return this.isSetTraceFlags();
                case TRACE_STATE:
                    return this.isSetTraceState();
                case REMOTE:
                    return this.isSetRemote();
                default:
                    throw new IllegalStateException();
            }
        }
    }

    public boolean equals(Object var1) {
        if (var1 == null) {
            return false;
        } else {
            return var1 instanceof TSpanContext ? this.equals((TSpanContext)var1) : false;
        }
    }

    public boolean equals(TSpanContext var1) {
        if (var1 == null) {
            return false;
        } else {
            boolean var2 = this.isSetTraceId();
            boolean var3 = var1.isSetTraceId();
            if (var2 || var3) {
                if (!var2 || !var3) {
                    return false;
                }

                if (!this.traceId.equals(var1.traceId)) {
                    return false;
                }
            }

            boolean var4 = this.isSetSpanId();
            boolean var5 = var1.isSetSpanId();
            if (var4 || var5) {
                if (!var4 || !var5) {
                    return false;
                }

                if (!this.spanId.equals(var1.spanId)) {
                    return false;
                }
            }

            boolean var6 = this.isSetTraceFlags();
            boolean var7 = var1.isSetTraceFlags();
            if (var6 || var7) {
                if (!var6 || !var7) {
                    return false;
                }

                if (this.traceFlags != var1.traceFlags) {
                    return false;
                }
            }

            boolean var8 = this.isSetTraceState();
            boolean var9 = var1.isSetTraceState();
            if (var8 || var9) {
                if (!var8 || !var9) {
                    return false;
                }

                if (!this.traceState.equals(var1.traceState)) {
                    return false;
                }
            }

            boolean var10 = this.isSetRemote();
            boolean var11 = var1.isSetRemote();
            if (var10 || var11) {
                if (!var10 || !var11) {
                    return false;
                }

                if (this.remote != var1.remote) {
                    return false;
                }
            }

            return true;
        }
    }

    public int hashCode() {
        return 0;
    }

    public int compareTo(TSpanContext var1) {
        if (!this.getClass().equals(var1.getClass())) {
            return this.getClass().getName().compareTo(var1.getClass().getName());
        } else {
            boolean var2 = false;
            int var4 = Boolean.valueOf(this.isSetTraceId()).compareTo(var1.isSetTraceId());
            if (var4 != 0) {
                return var4;
            } else {
                if (this.isSetTraceId()) {
                    var4 = TBaseHelper.compareTo(this.traceId, var1.traceId);
                    if (var4 != 0) {
                        return var4;
                    }
                }

                var4 = Boolean.valueOf(this.isSetSpanId()).compareTo(var1.isSetSpanId());
                if (var4 != 0) {
                    return var4;
                } else {
                    if (this.isSetSpanId()) {
                        var4 = TBaseHelper.compareTo(this.spanId, var1.spanId);
                        if (var4 != 0) {
                            return var4;
                        }
                    }

                    var4 = Boolean.valueOf(this.isSetTraceFlags()).compareTo(var1.isSetTraceFlags());
                    if (var4 != 0) {
                        return var4;
                    } else {
                        if (this.isSetTraceFlags()) {
                            var4 = TBaseHelper.compareTo(this.traceFlags, var1.traceFlags);
                            if (var4 != 0) {
                                return var4;
                            }
                        }

                        var4 = Boolean.valueOf(this.isSetTraceState()).compareTo(var1.isSetTraceState());
                        if (var4 != 0) {
                            return var4;
                        } else {
                            if (this.isSetTraceState()) {
                                var4 = TBaseHelper.compareTo(this.traceState, var1.traceState);
                                if (var4 != 0) {
                                    return var4;
                                }
                            }

                            var4 = Boolean.valueOf(this.isSetRemote()).compareTo(var1.isSetRemote());
                            if (var4 != 0) {
                                return var4;
                            } else {
                                if (this.isSetRemote()) {
                                    var4 = TBaseHelper.compareTo(this.remote, var1.remote);
                                    if (var4 != 0) {
                                        return var4;
                                    }
                                }

                                return 0;
                            }
                        }
                    }
                }
            }
        }
    }

    public TSpanContext._Fields fieldForId(int var1) {
        return TSpanContext._Fields.findByThriftId(var1);
    }

    public void read(TProtocol var1) throws TException {
        var1.readStructBegin();

        while(true) {
            TField var2 = var1.readFieldBegin();
            if (var2.type == 0) {
                var1.readStructEnd();
                this.validate();
                return;
            }

            switch(var2.id) {
                case 1:
                    if (var2.type == 11) {
                        this.traceId = var1.readString();
                    } else {
                        TProtocolUtil.skip(var1, var2.type);
                    }
                    break;
                case 2:
                    if (var2.type == 11) {
                        this.spanId = var1.readString();
                    } else {
                        TProtocolUtil.skip(var1, var2.type);
                    }
                    break;
                case 3:
                    if (var2.type == 3) {
                        this.traceFlags = var1.readByte();
                        this.setTraceFlagsIsSet(true);
                    } else {
                        TProtocolUtil.skip(var1, var2.type);
                    }
                    break;
                case 4:
                    if (var2.type == 12) {
                        this.traceState = new TTraceState();
                        this.traceState.read(var1);
                    } else {
                        TProtocolUtil.skip(var1, var2.type);
                    }
                    break;
                case 5:
                    if (var2.type == 2) {
                        this.remote = var1.readBool();
                        this.setRemoteIsSet(true);
                    } else {
                        TProtocolUtil.skip(var1, var2.type);
                    }
                    break;
                default:
                    TProtocolUtil.skip(var1, var2.type);
            }

            var1.readFieldEnd();
        }
    }

    public void write(TProtocol var1) throws TException {
        this.validate();
        var1.writeStructBegin(STRUCT_DESC);
        if (this.traceId != null && this.isSetTraceId()) {
            var1.writeFieldBegin(TRACE_ID_FIELD_DESC);
            var1.writeString(this.traceId);
            var1.writeFieldEnd();
        }

        if (this.spanId != null && this.isSetSpanId()) {
            var1.writeFieldBegin(SPAN_ID_FIELD_DESC);
            var1.writeString(this.spanId);
            var1.writeFieldEnd();
        }

        if (this.isSetTraceFlags()) {
            var1.writeFieldBegin(TRACE_FLAGS_FIELD_DESC);
            var1.writeByte(this.traceFlags);
            var1.writeFieldEnd();
        }

        if (this.traceState != null && this.isSetTraceState()) {
            var1.writeFieldBegin(TRACE_STATE_FIELD_DESC);
            this.traceState.write(var1);
            var1.writeFieldEnd();
        }

        if (this.isSetRemote()) {
            var1.writeFieldBegin(REMOTE_FIELD_DESC);
            var1.writeBool(this.remote);
            var1.writeFieldEnd();
        }

        var1.writeFieldStop();
        var1.writeStructEnd();
    }

    public String toString() {
        StringBuilder var1 = new StringBuilder("TSpanContext(");
        boolean var2 = true;
        if (this.isSetTraceId()) {
            var1.append("traceId:");
            if (this.traceId == null) {
                var1.append("null");
            } else {
                var1.append(this.traceId);
            }

            var2 = false;
        }

        if (this.isSetSpanId()) {
            if (!var2) {
                var1.append(", ");
            }

            var1.append("spanId:");
            if (this.spanId == null) {
                var1.append("null");
            } else {
                var1.append(this.spanId);
            }

            var2 = false;
        }

        if (this.isSetTraceFlags()) {
            if (!var2) {
                var1.append(", ");
            }

            var1.append("traceFlags:");
            var1.append(this.traceFlags);
            var2 = false;
        }

        if (this.isSetTraceState()) {
            if (!var2) {
                var1.append(", ");
            }

            var1.append("traceState:");
            if (this.traceState == null) {
                var1.append("null");
            } else {
                var1.append(this.traceState);
            }

            var2 = false;
        }

        if (this.isSetRemote()) {
            if (!var2) {
                var1.append(", ");
            }

            var1.append("remote:");
            var1.append(this.remote);
            var2 = false;
        }

        var1.append(")");
        return var1.toString();
    }

    public void validate() throws TException {
    }

    static {
        EnumMap var0 = new EnumMap(TSpanContext._Fields.class);
        var0.put(TSpanContext._Fields.TRACE_ID, new FieldMetaData("traceId", (byte)2, new FieldValueMetaData((byte)11)));
        var0.put(TSpanContext._Fields.SPAN_ID, new FieldMetaData("spanId", (byte)2, new FieldValueMetaData((byte)11)));
        var0.put(TSpanContext._Fields.TRACE_FLAGS, new FieldMetaData("traceFlags", (byte)2, new FieldValueMetaData((byte)3)));
        var0.put(TSpanContext._Fields.TRACE_STATE, new FieldMetaData("traceState", (byte)2, new StructMetaData((byte)12, TTraceState.class)));
        var0.put(TSpanContext._Fields.REMOTE, new FieldMetaData("remote", (byte)2, new FieldValueMetaData((byte)2)));
        metaDataMap = Collections.unmodifiableMap(var0);
        FieldMetaData.addStructMetaDataMap(TSpanContext.class, metaDataMap);
    }

    public static enum _Fields implements TFieldIdEnum {
        TRACE_ID((short)1, "traceId"),
        SPAN_ID((short)2, "spanId"),
        TRACE_FLAGS((short)3, "traceFlags"),
        TRACE_STATE((short)4, "traceState"),
        REMOTE((short)5, "remote");

        private static final Map<String, TSpanContext._Fields> byName = new HashMap();
        private final short _thriftId;
        private final String _fieldName;

        public static TSpanContext._Fields findByThriftId(int var0) {
            switch(var0) {
                case 1:
                    return TRACE_ID;
                case 2:
                    return SPAN_ID;
                case 3:
                    return TRACE_FLAGS;
                case 4:
                    return TRACE_STATE;
                case 5:
                    return REMOTE;
                default:
                    return null;
            }
        }

        public static TSpanContext._Fields findByThriftIdOrThrow(int var0) {
            TSpanContext._Fields var1 = findByThriftId(var0);
            if (var1 == null) {
                throw new IllegalArgumentException("Field " + var0 + " doesn't exist!");
            } else {
                return var1;
            }
        }

        public static TSpanContext._Fields findByName(String var0) {
            return (TSpanContext._Fields)byName.get(var0);
        }

        private _Fields(short var3, String var4) {
            this._thriftId = var3;
            this._fieldName = var4;
        }

        public short getThriftFieldId() {
            return this._thriftId;
        }

        public String getFieldName() {
            return this._fieldName;
        }

        static {
            Iterator var0 = EnumSet.allOf(TSpanContext._Fields.class).iterator();

            while(var0.hasNext()) {
                TSpanContext._Fields var1 = (TSpanContext._Fields)var0.next();
                byName.put(var1.getFieldName(), var1);
            }

        }
    }
}
