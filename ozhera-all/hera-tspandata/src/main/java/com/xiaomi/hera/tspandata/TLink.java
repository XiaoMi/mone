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

public class TLink implements TBase<TLink, TLink._Fields>, Serializable, Cloneable {
    private static final TStruct STRUCT_DESC = new TStruct("TLink");
    private static final TField SPAN_CONTEXT_FIELD_DESC = new TField("spanContext", (byte)12, (short)1);
    private static final TField ATTRIBUTES_FIELD_DESC = new TField("attributes", (byte)12, (short)2);
    private static final TField TOTAL_ATTRIBUTE_COUNT_FIELD_DESC = new TField("totalAttributeCount", (byte)8, (short)3);
    public TSpanContext spanContext;
    public TAttributes attributes;
    public int totalAttributeCount;
    private static final int __TOTALATTRIBUTECOUNT_ISSET_ID = 0;
    private BitSet __isset_bit_vector = new BitSet(1);
    public static final Map<TLink._Fields, FieldMetaData> metaDataMap;

    public TLink() {
    }

    public TLink(TLink var1) {
        this.__isset_bit_vector.clear();
        this.__isset_bit_vector.or(var1.__isset_bit_vector);
        if (var1.isSetSpanContext()) {
            this.spanContext = new TSpanContext(var1.spanContext);
        }

        if (var1.isSetAttributes()) {
            this.attributes = new TAttributes(var1.attributes);
        }

        this.totalAttributeCount = var1.totalAttributeCount;
    }

    public TLink deepCopy() {
        return new TLink(this);
    }

    public void clear() {
        this.spanContext = null;
        this.attributes = null;
        this.setTotalAttributeCountIsSet(false);
        this.totalAttributeCount = 0;
    }

    public TSpanContext getSpanContext() {
        return this.spanContext;
    }

    public TLink setSpanContext(TSpanContext var1) {
        this.spanContext = var1;
        return this;
    }

    public void unsetSpanContext() {
        this.spanContext = null;
    }

    public boolean isSetSpanContext() {
        return this.spanContext != null;
    }

    public void setSpanContextIsSet(boolean var1) {
        if (!var1) {
            this.spanContext = null;
        }

    }

    public TAttributes getAttributes() {
        return this.attributes;
    }

    public TLink setAttributes(TAttributes var1) {
        this.attributes = var1;
        return this;
    }

    public void unsetAttributes() {
        this.attributes = null;
    }

    public boolean isSetAttributes() {
        return this.attributes != null;
    }

    public void setAttributesIsSet(boolean var1) {
        if (!var1) {
            this.attributes = null;
        }

    }

    public int getTotalAttributeCount() {
        return this.totalAttributeCount;
    }

    public TLink setTotalAttributeCount(int var1) {
        this.totalAttributeCount = var1;
        this.setTotalAttributeCountIsSet(true);
        return this;
    }

    public void unsetTotalAttributeCount() {
        this.__isset_bit_vector.clear(0);
    }

    public boolean isSetTotalAttributeCount() {
        return this.__isset_bit_vector.get(0);
    }

    public void setTotalAttributeCountIsSet(boolean var1) {
        this.__isset_bit_vector.set(0, var1);
    }

    public void setFieldValue(TLink._Fields var1, Object var2) {
        switch(var1) {
            case SPAN_CONTEXT:
                if (var2 == null) {
                    this.unsetSpanContext();
                } else {
                    this.setSpanContext((TSpanContext)var2);
                }
                break;
            case ATTRIBUTES:
                if (var2 == null) {
                    this.unsetAttributes();
                } else {
                    this.setAttributes((TAttributes)var2);
                }
                break;
            case TOTAL_ATTRIBUTE_COUNT:
                if (var2 == null) {
                    this.unsetTotalAttributeCount();
                } else {
                    this.setTotalAttributeCount((Integer)var2);
                }
        }

    }

    public Object getFieldValue(TLink._Fields var1) {
        switch(var1) {
            case SPAN_CONTEXT:
                return this.getSpanContext();
            case ATTRIBUTES:
                return this.getAttributes();
            case TOTAL_ATTRIBUTE_COUNT:
                return new Integer(this.getTotalAttributeCount());
            default:
                throw new IllegalStateException();
        }
    }

    public boolean isSet(TLink._Fields var1) {
        if (var1 == null) {
            throw new IllegalArgumentException();
        } else {
            switch(var1) {
                case SPAN_CONTEXT:
                    return this.isSetSpanContext();
                case ATTRIBUTES:
                    return this.isSetAttributes();
                case TOTAL_ATTRIBUTE_COUNT:
                    return this.isSetTotalAttributeCount();
                default:
                    throw new IllegalStateException();
            }
        }
    }

    public boolean equals(Object var1) {
        if (var1 == null) {
            return false;
        } else {
            return var1 instanceof TLink ? this.equals((TLink)var1) : false;
        }
    }

    public boolean equals(TLink var1) {
        if (var1 == null) {
            return false;
        } else {
            boolean var2 = this.isSetSpanContext();
            boolean var3 = var1.isSetSpanContext();
            if (var2 || var3) {
                if (!var2 || !var3) {
                    return false;
                }

                if (!this.spanContext.equals(var1.spanContext)) {
                    return false;
                }
            }

            boolean var4 = this.isSetAttributes();
            boolean var5 = var1.isSetAttributes();
            if (var4 || var5) {
                if (!var4 || !var5) {
                    return false;
                }

                if (!this.attributes.equals(var1.attributes)) {
                    return false;
                }
            }

            boolean var6 = this.isSetTotalAttributeCount();
            boolean var7 = var1.isSetTotalAttributeCount();
            if (var6 || var7) {
                if (!var6 || !var7) {
                    return false;
                }

                if (this.totalAttributeCount != var1.totalAttributeCount) {
                    return false;
                }
            }

            return true;
        }
    }

    public int hashCode() {
        return 0;
    }

    public int compareTo(TLink var1) {
        if (!this.getClass().equals(var1.getClass())) {
            return this.getClass().getName().compareTo(var1.getClass().getName());
        } else {
            boolean var2 = false;
            int var4 = Boolean.valueOf(this.isSetSpanContext()).compareTo(var1.isSetSpanContext());
            if (var4 != 0) {
                return var4;
            } else {
                if (this.isSetSpanContext()) {
                    var4 = TBaseHelper.compareTo(this.spanContext, var1.spanContext);
                    if (var4 != 0) {
                        return var4;
                    }
                }

                var4 = Boolean.valueOf(this.isSetAttributes()).compareTo(var1.isSetAttributes());
                if (var4 != 0) {
                    return var4;
                } else {
                    if (this.isSetAttributes()) {
                        var4 = TBaseHelper.compareTo(this.attributes, var1.attributes);
                        if (var4 != 0) {
                            return var4;
                        }
                    }

                    var4 = Boolean.valueOf(this.isSetTotalAttributeCount()).compareTo(var1.isSetTotalAttributeCount());
                    if (var4 != 0) {
                        return var4;
                    } else {
                        if (this.isSetTotalAttributeCount()) {
                            var4 = TBaseHelper.compareTo(this.totalAttributeCount, var1.totalAttributeCount);
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

    public TLink._Fields fieldForId(int var1) {
        return TLink._Fields.findByThriftId(var1);
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
                    if (var2.type == 12) {
                        this.spanContext = new TSpanContext();
                        this.spanContext.read(var1);
                    } else {
                        TProtocolUtil.skip(var1, var2.type);
                    }
                    break;
                case 2:
                    if (var2.type == 12) {
                        this.attributes = new TAttributes();
                        this.attributes.read(var1);
                    } else {
                        TProtocolUtil.skip(var1, var2.type);
                    }
                    break;
                case 3:
                    if (var2.type == 8) {
                        this.totalAttributeCount = var1.readI32();
                        this.setTotalAttributeCountIsSet(true);
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
        if (this.spanContext != null && this.isSetSpanContext()) {
            var1.writeFieldBegin(SPAN_CONTEXT_FIELD_DESC);
            this.spanContext.write(var1);
            var1.writeFieldEnd();
        }

        if (this.attributes != null && this.isSetAttributes()) {
            var1.writeFieldBegin(ATTRIBUTES_FIELD_DESC);
            this.attributes.write(var1);
            var1.writeFieldEnd();
        }

        if (this.isSetTotalAttributeCount()) {
            var1.writeFieldBegin(TOTAL_ATTRIBUTE_COUNT_FIELD_DESC);
            var1.writeI32(this.totalAttributeCount);
            var1.writeFieldEnd();
        }

        var1.writeFieldStop();
        var1.writeStructEnd();
    }

    public String toString() {
        StringBuilder var1 = new StringBuilder("TLink(");
        boolean var2 = true;
        if (this.isSetSpanContext()) {
            var1.append("spanContext:");
            if (this.spanContext == null) {
                var1.append("null");
            } else {
                var1.append(this.spanContext);
            }

            var2 = false;
        }

        if (this.isSetAttributes()) {
            if (!var2) {
                var1.append(", ");
            }

            var1.append("attributes:");
            if (this.attributes == null) {
                var1.append("null");
            } else {
                var1.append(this.attributes);
            }

            var2 = false;
        }

        if (this.isSetTotalAttributeCount()) {
            if (!var2) {
                var1.append(", ");
            }

            var1.append("totalAttributeCount:");
            var1.append(this.totalAttributeCount);
            var2 = false;
        }

        var1.append(")");
        return var1.toString();
    }

    public void validate() throws TException {
    }

    static {
        EnumMap var0 = new EnumMap(TLink._Fields.class);
        var0.put(TLink._Fields.SPAN_CONTEXT, new FieldMetaData("spanContext", (byte)2, new StructMetaData((byte)12, TSpanContext.class)));
        var0.put(TLink._Fields.ATTRIBUTES, new FieldMetaData("attributes", (byte)2, new StructMetaData((byte)12, TAttributes.class)));
        var0.put(TLink._Fields.TOTAL_ATTRIBUTE_COUNT, new FieldMetaData("totalAttributeCount", (byte)2, new FieldValueMetaData((byte)8)));
        metaDataMap = Collections.unmodifiableMap(var0);
        FieldMetaData.addStructMetaDataMap(TLink.class, metaDataMap);
    }

    public static enum _Fields implements TFieldIdEnum {
        SPAN_CONTEXT((short)1, "spanContext"),
        ATTRIBUTES((short)2, "attributes"),
        TOTAL_ATTRIBUTE_COUNT((short)3, "totalAttributeCount");

        private static final Map<String, TLink._Fields> byName = new HashMap();
        private final short _thriftId;
        private final String _fieldName;

        public static TLink._Fields findByThriftId(int var0) {
            switch(var0) {
                case 1:
                    return SPAN_CONTEXT;
                case 2:
                    return ATTRIBUTES;
                case 3:
                    return TOTAL_ATTRIBUTE_COUNT;
                default:
                    return null;
            }
        }

        public static TLink._Fields findByThriftIdOrThrow(int var0) {
            TLink._Fields var1 = findByThriftId(var0);
            if (var1 == null) {
                throw new IllegalArgumentException("Field " + var0 + " doesn't exist!");
            } else {
                return var1;
            }
        }

        public static TLink._Fields findByName(String var0) {
            return (TLink._Fields)byName.get(var0);
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
            Iterator var0 = EnumSet.allOf(TLink._Fields.class).iterator();

            while(var0.hasNext()) {
                TLink._Fields var1 = (TLink._Fields)var0.next();
                byName.put(var1.getFieldName(), var1);
            }

        }
    }
}
