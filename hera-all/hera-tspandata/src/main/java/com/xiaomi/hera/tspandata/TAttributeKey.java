//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.xiaomi.hera.tspandata;

import java.io.Serializable;
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
import org.apache.thrift.meta_data.EnumMetaData;
import org.apache.thrift.meta_data.FieldMetaData;
import org.apache.thrift.meta_data.FieldValueMetaData;
import org.apache.thrift.protocol.TField;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TProtocolUtil;
import org.apache.thrift.protocol.TStruct;

public class TAttributeKey implements TBase<TAttributeKey, TAttributeKey._Fields>, Serializable, Cloneable {
    private static final TStruct STRUCT_DESC = new TStruct("TAttributeKey");
    private static final TField TYPE_FIELD_DESC = new TField("type", (byte)8, (short)1);
    private static final TField VALUE_FIELD_DESC = new TField("value", (byte)11, (short)2);
    public TAttributeType type;
    public String value;
    public static final Map<TAttributeKey._Fields, FieldMetaData> metaDataMap;

    public TAttributeKey() {
    }

    public TAttributeKey(TAttributeKey var1) {
        if (var1.isSetType()) {
            this.type = var1.type;
        }

        if (var1.isSetValue()) {
            this.value = var1.value;
        }

    }

    public TAttributeKey deepCopy() {
        return new TAttributeKey(this);
    }

    public void clear() {
        this.type = null;
        this.value = null;
    }

    public TAttributeType getType() {
        return this.type;
    }

    public TAttributeKey setType(TAttributeType var1) {
        this.type = var1;
        return this;
    }

    public void unsetType() {
        this.type = null;
    }

    public boolean isSetType() {
        return this.type != null;
    }

    public void setTypeIsSet(boolean var1) {
        if (!var1) {
            this.type = null;
        }

    }

    public String getValue() {
        return this.value;
    }

    public TAttributeKey setValue(String var1) {
        this.value = var1;
        return this;
    }

    public void unsetValue() {
        this.value = null;
    }

    public boolean isSetValue() {
        return this.value != null;
    }

    public void setValueIsSet(boolean var1) {
        if (!var1) {
            this.value = null;
        }

    }

    public void setFieldValue(TAttributeKey._Fields var1, Object var2) {
        switch(var1) {
            case TYPE:
                if (var2 == null) {
                    this.unsetType();
                } else {
                    this.setType((TAttributeType)var2);
                }
                break;
            case VALUE:
                if (var2 == null) {
                    this.unsetValue();
                } else {
                    this.setValue((String)var2);
                }
        }

    }

    public Object getFieldValue(TAttributeKey._Fields var1) {
        switch(var1) {
            case TYPE:
                return this.getType();
            case VALUE:
                return this.getValue();
            default:
                throw new IllegalStateException();
        }
    }

    public boolean isSet(TAttributeKey._Fields var1) {
        if (var1 == null) {
            throw new IllegalArgumentException();
        } else {
            switch(var1) {
                case TYPE:
                    return this.isSetType();
                case VALUE:
                    return this.isSetValue();
                default:
                    throw new IllegalStateException();
            }
        }
    }

    public boolean equals(Object var1) {
        if (var1 == null) {
            return false;
        } else {
            return var1 instanceof TAttributeKey ? this.equals((TAttributeKey)var1) : false;
        }
    }

    public boolean equals(TAttributeKey var1) {
        if (var1 == null) {
            return false;
        } else {
            boolean var2 = this.isSetType();
            boolean var3 = var1.isSetType();
            if (var2 || var3) {
                if (!var2 || !var3) {
                    return false;
                }

                if (!this.type.equals(var1.type)) {
                    return false;
                }
            }

            boolean var4 = this.isSetValue();
            boolean var5 = var1.isSetValue();
            if (var4 || var5) {
                if (!var4 || !var5) {
                    return false;
                }

                if (!this.value.equals(var1.value)) {
                    return false;
                }
            }

            return true;
        }
    }

    public int hashCode() {
        return 0;
    }

    public int compareTo(TAttributeKey var1) {
        if (!this.getClass().equals(var1.getClass())) {
            return this.getClass().getName().compareTo(var1.getClass().getName());
        } else {
            boolean var2 = false;
            int var4 = Boolean.valueOf(this.isSetType()).compareTo(var1.isSetType());
            if (var4 != 0) {
                return var4;
            } else {
                if (this.isSetType()) {
                    var4 = TBaseHelper.compareTo(this.type, var1.type);
                    if (var4 != 0) {
                        return var4;
                    }
                }

                var4 = Boolean.valueOf(this.isSetValue()).compareTo(var1.isSetValue());
                if (var4 != 0) {
                    return var4;
                } else {
                    if (this.isSetValue()) {
                        var4 = TBaseHelper.compareTo(this.value, var1.value);
                        if (var4 != 0) {
                            return var4;
                        }
                    }

                    return 0;
                }
            }
        }
    }

    public TAttributeKey._Fields fieldForId(int var1) {
        return TAttributeKey._Fields.findByThriftId(var1);
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
                    if (var2.type == 8) {
                        this.type = TAttributeType.findByValue(var1.readI32());
                    } else {
                        TProtocolUtil.skip(var1, var2.type);
                    }
                    break;
                case 2:
                    if (var2.type == 11) {
                        this.value = var1.readString();
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
        if (this.type != null && this.isSetType()) {
            var1.writeFieldBegin(TYPE_FIELD_DESC);
            var1.writeI32(this.type.getValue());
            var1.writeFieldEnd();
        }

        if (this.value != null && this.isSetValue()) {
            var1.writeFieldBegin(VALUE_FIELD_DESC);
            var1.writeString(this.value);
            var1.writeFieldEnd();
        }

        var1.writeFieldStop();
        var1.writeStructEnd();
    }

    public String toString() {
        StringBuilder var1 = new StringBuilder("TAttributeKey(");
        boolean var2 = true;
        if (this.isSetType()) {
            var1.append("type:");
            if (this.type == null) {
                var1.append("null");
            } else {
                var1.append(this.type);
            }

            var2 = false;
        }

        if (this.isSetValue()) {
            if (!var2) {
                var1.append(", ");
            }

            var1.append("value:");
            if (this.value == null) {
                var1.append("null");
            } else {
                var1.append(this.value);
            }

            var2 = false;
        }

        var1.append(")");
        return var1.toString();
    }

    public void validate() throws TException {
    }

    static {
        EnumMap var0 = new EnumMap(TAttributeKey._Fields.class);
        var0.put(TAttributeKey._Fields.TYPE, new FieldMetaData("type", (byte)2, new EnumMetaData((byte)16, TAttributeType.class)));
        var0.put(TAttributeKey._Fields.VALUE, new FieldMetaData("value", (byte)2, new FieldValueMetaData((byte)11)));
        metaDataMap = Collections.unmodifiableMap(var0);
        FieldMetaData.addStructMetaDataMap(TAttributeKey.class, metaDataMap);
    }

    public static enum _Fields implements TFieldIdEnum {
        TYPE((short)1, "type"),
        VALUE((short)2, "value");

        private static final Map<String, TAttributeKey._Fields> byName = new HashMap();
        private final short _thriftId;
        private final String _fieldName;

        public static TAttributeKey._Fields findByThriftId(int var0) {
            switch(var0) {
                case 1:
                    return TYPE;
                case 2:
                    return VALUE;
                default:
                    return null;
            }
        }

        public static TAttributeKey._Fields findByThriftIdOrThrow(int var0) {
            TAttributeKey._Fields var1 = findByThriftId(var0);
            if (var1 == null) {
                throw new IllegalArgumentException("Field " + var0 + " doesn't exist!");
            } else {
                return var1;
            }
        }

        public static TAttributeKey._Fields findByName(String var0) {
            return (TAttributeKey._Fields)byName.get(var0);
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
            Iterator var0 = EnumSet.allOf(TAttributeKey._Fields.class).iterator();

            while(var0.hasNext()) {
                TAttributeKey._Fields var1 = (TAttributeKey._Fields)var0.next();
                byName.put(var1.getFieldName(), var1);
            }

        }
    }
}
