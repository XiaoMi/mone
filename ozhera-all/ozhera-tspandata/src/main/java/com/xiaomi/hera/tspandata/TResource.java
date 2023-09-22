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
import org.apache.thrift.meta_data.FieldMetaData;
import org.apache.thrift.meta_data.StructMetaData;
import org.apache.thrift.protocol.TField;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TProtocolUtil;
import org.apache.thrift.protocol.TStruct;

public class TResource implements TBase<TResource, TResource._Fields>, Serializable, Cloneable {
    private static final TStruct STRUCT_DESC = new TStruct("TResource");
    private static final TField ATTRIBUTES_FIELD_DESC = new TField("attributes", (byte)12, (short)1);
    public TAttributes attributes;
    public static final Map<TResource._Fields, FieldMetaData> metaDataMap;

    public TResource() {
    }

    public TResource(TResource var1) {
        if (var1.isSetAttributes()) {
            this.attributes = new TAttributes(var1.attributes);
        }

    }

    public TResource deepCopy() {
        return new TResource(this);
    }

    public void clear() {
        this.attributes = null;
    }

    public TAttributes getAttributes() {
        return this.attributes;
    }

    public TResource setAttributes(TAttributes var1) {
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

    public void setFieldValue(TResource._Fields var1, Object var2) {
        switch(var1) {
            case ATTRIBUTES:
                if (var2 == null) {
                    this.unsetAttributes();
                } else {
                    this.setAttributes((TAttributes)var2);
                }
            default:
        }
    }

    public Object getFieldValue(TResource._Fields var1) {
        switch(var1) {
            case ATTRIBUTES:
                return this.getAttributes();
            default:
                throw new IllegalStateException();
        }
    }

    public boolean isSet(TResource._Fields var1) {
        if (var1 == null) {
            throw new IllegalArgumentException();
        } else {
            switch(var1) {
                case ATTRIBUTES:
                    return this.isSetAttributes();
                default:
                    throw new IllegalStateException();
            }
        }
    }

    public boolean equals(Object var1) {
        if (var1 == null) {
            return false;
        } else {
            return var1 instanceof TResource ? this.equals((TResource)var1) : false;
        }
    }

    public boolean equals(TResource var1) {
        if (var1 == null) {
            return false;
        } else {
            boolean var2 = this.isSetAttributes();
            boolean var3 = var1.isSetAttributes();
            if (var2 || var3) {
                if (!var2 || !var3) {
                    return false;
                }

                if (!this.attributes.equals(var1.attributes)) {
                    return false;
                }
            }

            return true;
        }
    }

    public int hashCode() {
        return 0;
    }

    public int compareTo(TResource var1) {
        if (!this.getClass().equals(var1.getClass())) {
            return this.getClass().getName().compareTo(var1.getClass().getName());
        } else {
            boolean var2 = false;
            int var4 = Boolean.valueOf(this.isSetAttributes()).compareTo(var1.isSetAttributes());
            if (var4 != 0) {
                return var4;
            } else {
                if (this.isSetAttributes()) {
                    var4 = TBaseHelper.compareTo(this.attributes, var1.attributes);
                    if (var4 != 0) {
                        return var4;
                    }
                }

                return 0;
            }
        }
    }

    public TResource._Fields fieldForId(int var1) {
        return TResource._Fields.findByThriftId(var1);
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
                        this.attributes = new TAttributes();
                        this.attributes.read(var1);
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
        if (this.attributes != null && this.isSetAttributes()) {
            var1.writeFieldBegin(ATTRIBUTES_FIELD_DESC);
            this.attributes.write(var1);
            var1.writeFieldEnd();
        }

        var1.writeFieldStop();
        var1.writeStructEnd();
    }

    public String toString() {
        StringBuilder var1 = new StringBuilder("TResource(");
        boolean var2 = true;
        if (this.isSetAttributes()) {
            var1.append("attributes:");
            if (this.attributes == null) {
                var1.append("null");
            } else {
                var1.append(this.attributes);
            }

            var2 = false;
        }

        var1.append(")");
        return var1.toString();
    }

    public void validate() throws TException {
    }

    static {
        EnumMap var0 = new EnumMap(TResource._Fields.class);
        var0.put(TResource._Fields.ATTRIBUTES, new FieldMetaData("attributes", (byte)2, new StructMetaData((byte)12, TAttributes.class)));
        metaDataMap = Collections.unmodifiableMap(var0);
        FieldMetaData.addStructMetaDataMap(TResource.class, metaDataMap);
    }

    public static enum _Fields implements TFieldIdEnum {
        ATTRIBUTES((short)1, "attributes");

        private static final Map<String, TResource._Fields> byName = new HashMap();
        private final short _thriftId;
        private final String _fieldName;

        public static TResource._Fields findByThriftId(int var0) {
            switch(var0) {
                case 1:
                    return ATTRIBUTES;
                default:
                    return null;
            }
        }

        public static TResource._Fields findByThriftIdOrThrow(int var0) {
            TResource._Fields var1 = findByThriftId(var0);
            if (var1 == null) {
                throw new IllegalArgumentException("Field " + var0 + " doesn't exist!");
            } else {
                return var1;
            }
        }

        public static TResource._Fields findByName(String var0) {
            return (TResource._Fields)byName.get(var0);
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
            Iterator var0 = EnumSet.allOf(TResource._Fields.class).iterator();

            while(var0.hasNext()) {
                TResource._Fields var1 = (TResource._Fields)var0.next();
                byName.put(var1.getFieldName(), var1);
            }

        }
    }
}
