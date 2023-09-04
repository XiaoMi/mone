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

public class TEvent implements TBase<TEvent, TEvent._Fields>, Serializable, Cloneable {
    private static final TStruct STRUCT_DESC = new TStruct("TEvent");
    private static final TField NAME_FIELD_DESC = new TField("name", (byte)11, (short)1);
    private static final TField ATTRIBUTES_FIELD_DESC = new TField("attributes", (byte)12, (short)2);
    private static final TField EPOCH_NANOS_FIELD_DESC = new TField("epochNanos", (byte)10, (short)3);
    private static final TField TOTAL_ATTRIBUTE_COUNT_FIELD_DESC = new TField("totalAttributeCount", (byte)8, (short)4);
    public String name;
    public TAttributes attributes;
    public long epochNanos;
    public int totalAttributeCount;
    private static final int __EPOCHNANOS_ISSET_ID = 0;
    private static final int __TOTALATTRIBUTECOUNT_ISSET_ID = 1;
    private BitSet __isset_bit_vector = new BitSet(2);
    public static final Map<TEvent._Fields, FieldMetaData> metaDataMap;

    public TEvent() {
    }

    public TEvent(TEvent var1) {
        this.__isset_bit_vector.clear();
        this.__isset_bit_vector.or(var1.__isset_bit_vector);
        if (var1.isSetName()) {
            this.name = var1.name;
        }

        if (var1.isSetAttributes()) {
            this.attributes = new TAttributes(var1.attributes);
        }

        this.epochNanos = var1.epochNanos;
        this.totalAttributeCount = var1.totalAttributeCount;
    }

    public TEvent deepCopy() {
        return new TEvent(this);
    }

    public void clear() {
        this.name = null;
        this.attributes = null;
        this.setEpochNanosIsSet(false);
        this.epochNanos = 0L;
        this.setTotalAttributeCountIsSet(false);
        this.totalAttributeCount = 0;
    }

    public String getName() {
        return this.name;
    }

    public TEvent setName(String var1) {
        this.name = var1;
        return this;
    }

    public void unsetName() {
        this.name = null;
    }

    public boolean isSetName() {
        return this.name != null;
    }

    public void setNameIsSet(boolean var1) {
        if (!var1) {
            this.name = null;
        }

    }

    public TAttributes getAttributes() {
        return this.attributes;
    }

    public TEvent setAttributes(TAttributes var1) {
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

    public long getEpochNanos() {
        return this.epochNanos;
    }

    public TEvent setEpochNanos(long var1) {
        this.epochNanos = var1;
        this.setEpochNanosIsSet(true);
        return this;
    }

    public void unsetEpochNanos() {
        this.__isset_bit_vector.clear(0);
    }

    public boolean isSetEpochNanos() {
        return this.__isset_bit_vector.get(0);
    }

    public void setEpochNanosIsSet(boolean var1) {
        this.__isset_bit_vector.set(0, var1);
    }

    public int getTotalAttributeCount() {
        return this.totalAttributeCount;
    }

    public TEvent setTotalAttributeCount(int var1) {
        this.totalAttributeCount = var1;
        this.setTotalAttributeCountIsSet(true);
        return this;
    }

    public void unsetTotalAttributeCount() {
        this.__isset_bit_vector.clear(1);
    }

    public boolean isSetTotalAttributeCount() {
        return this.__isset_bit_vector.get(1);
    }

    public void setTotalAttributeCountIsSet(boolean var1) {
        this.__isset_bit_vector.set(1, var1);
    }

    public void setFieldValue(TEvent._Fields var1, Object var2) {
        switch(var1) {
            case NAME:
                if (var2 == null) {
                    this.unsetName();
                } else {
                    this.setName((String)var2);
                }
                break;
            case ATTRIBUTES:
                if (var2 == null) {
                    this.unsetAttributes();
                } else {
                    this.setAttributes((TAttributes)var2);
                }
                break;
            case EPOCH_NANOS:
                if (var2 == null) {
                    this.unsetEpochNanos();
                } else {
                    this.setEpochNanos((Long)var2);
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

    public Object getFieldValue(TEvent._Fields var1) {
        switch(var1) {
            case NAME:
                return this.getName();
            case ATTRIBUTES:
                return this.getAttributes();
            case EPOCH_NANOS:
                return new Long(this.getEpochNanos());
            case TOTAL_ATTRIBUTE_COUNT:
                return new Integer(this.getTotalAttributeCount());
            default:
                throw new IllegalStateException();
        }
    }

    public boolean isSet(TEvent._Fields var1) {
        if (var1 == null) {
            throw new IllegalArgumentException();
        } else {
            switch(var1) {
                case NAME:
                    return this.isSetName();
                case ATTRIBUTES:
                    return this.isSetAttributes();
                case EPOCH_NANOS:
                    return this.isSetEpochNanos();
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
            return var1 instanceof TEvent ? this.equals((TEvent)var1) : false;
        }
    }

    public boolean equals(TEvent var1) {
        if (var1 == null) {
            return false;
        } else {
            boolean var2 = this.isSetName();
            boolean var3 = var1.isSetName();
            if (var2 || var3) {
                if (!var2 || !var3) {
                    return false;
                }

                if (!this.name.equals(var1.name)) {
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

            boolean var6 = this.isSetEpochNanos();
            boolean var7 = var1.isSetEpochNanos();
            if (var6 || var7) {
                if (!var6 || !var7) {
                    return false;
                }

                if (this.epochNanos != var1.epochNanos) {
                    return false;
                }
            }

            boolean var8 = this.isSetTotalAttributeCount();
            boolean var9 = var1.isSetTotalAttributeCount();
            if (var8 || var9) {
                if (!var8 || !var9) {
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

    public int compareTo(TEvent var1) {
        if (!this.getClass().equals(var1.getClass())) {
            return this.getClass().getName().compareTo(var1.getClass().getName());
        } else {
            boolean var2 = false;
            int var4 = Boolean.valueOf(this.isSetName()).compareTo(var1.isSetName());
            if (var4 != 0) {
                return var4;
            } else {
                if (this.isSetName()) {
                    var4 = TBaseHelper.compareTo(this.name, var1.name);
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

                    var4 = Boolean.valueOf(this.isSetEpochNanos()).compareTo(var1.isSetEpochNanos());
                    if (var4 != 0) {
                        return var4;
                    } else {
                        if (this.isSetEpochNanos()) {
                            var4 = TBaseHelper.compareTo(this.epochNanos, var1.epochNanos);
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
    }

    public TEvent._Fields fieldForId(int var1) {
        return TEvent._Fields.findByThriftId(var1);
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
                        this.name = var1.readString();
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
                    if (var2.type == 10) {
                        this.epochNanos = var1.readI64();
                        this.setEpochNanosIsSet(true);
                    } else {
                        TProtocolUtil.skip(var1, var2.type);
                    }
                    break;
                case 4:
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
        if (this.name != null && this.isSetName()) {
            var1.writeFieldBegin(NAME_FIELD_DESC);
            var1.writeString(this.name);
            var1.writeFieldEnd();
        }

        if (this.attributes != null && this.isSetAttributes()) {
            var1.writeFieldBegin(ATTRIBUTES_FIELD_DESC);
            this.attributes.write(var1);
            var1.writeFieldEnd();
        }

        if (this.isSetEpochNanos()) {
            var1.writeFieldBegin(EPOCH_NANOS_FIELD_DESC);
            var1.writeI64(this.epochNanos);
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
        StringBuilder var1 = new StringBuilder("TEvent(");
        boolean var2 = true;
        if (this.isSetName()) {
            var1.append("name:");
            if (this.name == null) {
                var1.append("null");
            } else {
                var1.append(this.name);
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

        if (this.isSetEpochNanos()) {
            if (!var2) {
                var1.append(", ");
            }

            var1.append("epochNanos:");
            var1.append(this.epochNanos);
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
        EnumMap var0 = new EnumMap(TEvent._Fields.class);
        var0.put(TEvent._Fields.NAME, new FieldMetaData("name", (byte)2, new FieldValueMetaData((byte)11)));
        var0.put(TEvent._Fields.ATTRIBUTES, new FieldMetaData("attributes", (byte)2, new StructMetaData((byte)12, TAttributes.class)));
        var0.put(TEvent._Fields.EPOCH_NANOS, new FieldMetaData("epochNanos", (byte)2, new FieldValueMetaData((byte)10)));
        var0.put(TEvent._Fields.TOTAL_ATTRIBUTE_COUNT, new FieldMetaData("totalAttributeCount", (byte)2, new FieldValueMetaData((byte)8)));
        metaDataMap = Collections.unmodifiableMap(var0);
        FieldMetaData.addStructMetaDataMap(TEvent.class, metaDataMap);
    }

    public static enum _Fields implements TFieldIdEnum {
        NAME((short)1, "name"),
        ATTRIBUTES((short)2, "attributes"),
        EPOCH_NANOS((short)3, "epochNanos"),
        TOTAL_ATTRIBUTE_COUNT((short)4, "totalAttributeCount");

        private static final Map<String, TEvent._Fields> byName = new HashMap();
        private final short _thriftId;
        private final String _fieldName;

        public static TEvent._Fields findByThriftId(int var0) {
            switch(var0) {
                case 1:
                    return NAME;
                case 2:
                    return ATTRIBUTES;
                case 3:
                    return EPOCH_NANOS;
                case 4:
                    return TOTAL_ATTRIBUTE_COUNT;
                default:
                    return null;
            }
        }

        public static TEvent._Fields findByThriftIdOrThrow(int var0) {
            TEvent._Fields var1 = findByThriftId(var0);
            if (var1 == null) {
                throw new IllegalArgumentException("Field " + var0 + " doesn't exist!");
            } else {
                return var1;
            }
        }

        public static TEvent._Fields findByName(String var0) {
            return (TEvent._Fields)byName.get(var0);
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
            Iterator var0 = EnumSet.allOf(TEvent._Fields.class).iterator();

            while(var0.hasNext()) {
                TEvent._Fields var1 = (TEvent._Fields)var0.next();
                byName.put(var1.getFieldName(), var1);
            }

        }
    }
}
