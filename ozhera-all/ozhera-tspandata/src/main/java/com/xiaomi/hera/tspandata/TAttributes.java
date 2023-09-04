//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.xiaomi.hera.tspandata;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.thrift.TBase;
import org.apache.thrift.TBaseHelper;
import org.apache.thrift.TException;
import org.apache.thrift.TFieldIdEnum;
import org.apache.thrift.meta_data.FieldMetaData;
import org.apache.thrift.meta_data.ListMetaData;
import org.apache.thrift.meta_data.StructMetaData;
import org.apache.thrift.protocol.TField;
import org.apache.thrift.protocol.TList;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TProtocolUtil;
import org.apache.thrift.protocol.TStruct;

public class TAttributes implements TBase<TAttributes, TAttributes._Fields>, Serializable, Cloneable {
    private static final TStruct STRUCT_DESC = new TStruct("TAttributes");
    private static final TField KEYS_FIELD_DESC = new TField("keys", (byte)15, (short)1);
    private static final TField VALUES_FIELD_DESC = new TField("values", (byte)15, (short)2);
    public List<TAttributeKey> keys;
    public List<TValue> values;
    public static final Map<TAttributes._Fields, FieldMetaData> metaDataMap;

    public TAttributes() {
    }

    public TAttributes(TAttributes var1) {
        ArrayList var2;
        Iterator var3;
        if (var1.isSetKeys()) {
            var2 = new ArrayList();
            var3 = var1.keys.iterator();

            while(var3.hasNext()) {
                TAttributeKey var4 = (TAttributeKey)var3.next();
                var2.add(new TAttributeKey(var4));
            }

            this.keys = var2;
        }

        if (var1.isSetValues()) {
            var2 = new ArrayList();
            var3 = var1.values.iterator();

            while(var3.hasNext()) {
                TValue var5 = (TValue)var3.next();
                var2.add(new TValue(var5));
            }

            this.values = var2;
        }

    }

    public TAttributes deepCopy() {
        return new TAttributes(this);
    }

    public void clear() {
        this.keys = null;
        this.values = null;
    }

    public int getKeysSize() {
        return this.keys == null ? 0 : this.keys.size();
    }

    public Iterator<TAttributeKey> getKeysIterator() {
        return this.keys == null ? null : this.keys.iterator();
    }

    public void addToKeys(TAttributeKey var1) {
        if (this.keys == null) {
            this.keys = new ArrayList();
        }

        this.keys.add(var1);
    }

    public List<TAttributeKey> getKeys() {
        return this.keys;
    }

    public TAttributes setKeys(List<TAttributeKey> var1) {
        this.keys = var1;
        return this;
    }

    public void unsetKeys() {
        this.keys = null;
    }

    public boolean isSetKeys() {
        return this.keys != null;
    }

    public void setKeysIsSet(boolean var1) {
        if (!var1) {
            this.keys = null;
        }

    }

    public int getValuesSize() {
        return this.values == null ? 0 : this.values.size();
    }

    public Iterator<TValue> getValuesIterator() {
        return this.values == null ? null : this.values.iterator();
    }

    public void addToValues(TValue var1) {
        if (this.values == null) {
            this.values = new ArrayList();
        }

        this.values.add(var1);
    }

    public List<TValue> getValues() {
        return this.values;
    }

    public TAttributes setValues(List<TValue> var1) {
        this.values = var1;
        return this;
    }

    public void unsetValues() {
        this.values = null;
    }

    public boolean isSetValues() {
        return this.values != null;
    }

    public void setValuesIsSet(boolean var1) {
        if (!var1) {
            this.values = null;
        }

    }

    public void setFieldValue(TAttributes._Fields var1, Object var2) {
        switch(var1) {
            case KEYS:
                if (var2 == null) {
                    this.unsetKeys();
                } else {
                    this.setKeys((List)var2);
                }
                break;
            case VALUES:
                if (var2 == null) {
                    this.unsetValues();
                } else {
                    this.setValues((List)var2);
                }
        }

    }

    public Object getFieldValue(TAttributes._Fields var1) {
        switch(var1) {
            case KEYS:
                return this.getKeys();
            case VALUES:
                return this.getValues();
            default:
                throw new IllegalStateException();
        }
    }

    public boolean isSet(TAttributes._Fields var1) {
        if (var1 == null) {
            throw new IllegalArgumentException();
        } else {
            switch(var1) {
                case KEYS:
                    return this.isSetKeys();
                case VALUES:
                    return this.isSetValues();
                default:
                    throw new IllegalStateException();
            }
        }
    }

    public boolean equals(Object var1) {
        if (var1 == null) {
            return false;
        } else {
            return var1 instanceof TAttributes ? this.equals((TAttributes)var1) : false;
        }
    }

    public boolean equals(TAttributes var1) {
        if (var1 == null) {
            return false;
        } else {
            boolean var2 = this.isSetKeys();
            boolean var3 = var1.isSetKeys();
            if (var2 || var3) {
                if (!var2 || !var3) {
                    return false;
                }

                if (!this.keys.equals(var1.keys)) {
                    return false;
                }
            }

            boolean var4 = this.isSetValues();
            boolean var5 = var1.isSetValues();
            if (var4 || var5) {
                if (!var4 || !var5) {
                    return false;
                }

                if (!this.values.equals(var1.values)) {
                    return false;
                }
            }

            return true;
        }
    }

    public int hashCode() {
        return 0;
    }

    public int compareTo(TAttributes var1) {
        if (!this.getClass().equals(var1.getClass())) {
            return this.getClass().getName().compareTo(var1.getClass().getName());
        } else {
            boolean var2 = false;
            int var4 = Boolean.valueOf(this.isSetKeys()).compareTo(var1.isSetKeys());
            if (var4 != 0) {
                return var4;
            } else {
                if (this.isSetKeys()) {
                    var4 = TBaseHelper.compareTo(this.keys, var1.keys);
                    if (var4 != 0) {
                        return var4;
                    }
                }

                var4 = Boolean.valueOf(this.isSetValues()).compareTo(var1.isSetValues());
                if (var4 != 0) {
                    return var4;
                } else {
                    if (this.isSetValues()) {
                        var4 = TBaseHelper.compareTo(this.values, var1.values);
                        if (var4 != 0) {
                            return var4;
                        }
                    }

                    return 0;
                }
            }
        }
    }

    public TAttributes._Fields fieldForId(int var1) {
        return TAttributes._Fields.findByThriftId(var1);
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

            TList var3;
            int var4;
            switch(var2.id) {
                case 1:
                    if (var2.type != 15) {
                        TProtocolUtil.skip(var1, var2.type);
                        break;
                    }

                    var3 = var1.readListBegin();
                    this.keys = new ArrayList(var3.size);

                    for(var4 = 0; var4 < var3.size; ++var4) {
                        TAttributeKey var6 = new TAttributeKey();
                        var6.read(var1);
                        this.keys.add(var6);
                    }

                    var1.readListEnd();
                    break;
                case 2:
                    if (var2.type != 15) {
                        TProtocolUtil.skip(var1, var2.type);
                        break;
                    }

                    var3 = var1.readListBegin();
                    this.values = new ArrayList(var3.size);

                    for(var4 = 0; var4 < var3.size; ++var4) {
                        TValue var5 = new TValue();
                        var5.read(var1);
                        this.values.add(var5);
                    }

                    var1.readListEnd();
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
        Iterator var2;
        if (this.keys != null && this.isSetKeys()) {
            var1.writeFieldBegin(KEYS_FIELD_DESC);
            var1.writeListBegin(new TList((byte)12, this.keys.size()));
            var2 = this.keys.iterator();

            while(var2.hasNext()) {
                TAttributeKey var3 = (TAttributeKey)var2.next();
                var3.write(var1);
            }

            var1.writeListEnd();
            var1.writeFieldEnd();
        }

        if (this.values != null && this.isSetValues()) {
            var1.writeFieldBegin(VALUES_FIELD_DESC);
            var1.writeListBegin(new TList((byte)12, this.values.size()));
            var2 = this.values.iterator();

            while(var2.hasNext()) {
                TValue var4 = (TValue)var2.next();
                var4.write(var1);
            }

            var1.writeListEnd();
            var1.writeFieldEnd();
        }

        var1.writeFieldStop();
        var1.writeStructEnd();
    }

    public String toString() {
        StringBuilder var1 = new StringBuilder("TAttributes(");
        boolean var2 = true;
        if (this.isSetKeys()) {
            var1.append("keys:");
            if (this.keys == null) {
                var1.append("null");
            } else {
                var1.append(this.keys);
            }

            var2 = false;
        }

        if (this.isSetValues()) {
            if (!var2) {
                var1.append(", ");
            }

            var1.append("values:");
            if (this.values == null) {
                var1.append("null");
            } else {
                var1.append(this.values);
            }

            var2 = false;
        }

        var1.append(")");
        return var1.toString();
    }

    public void validate() throws TException {
    }

    static {
        EnumMap var0 = new EnumMap(TAttributes._Fields.class);
        var0.put(TAttributes._Fields.KEYS, new FieldMetaData("keys", (byte)2, new ListMetaData((byte)15, new StructMetaData((byte)12, TAttributeKey.class))));
        var0.put(TAttributes._Fields.VALUES, new FieldMetaData("values", (byte)2, new ListMetaData((byte)15, new StructMetaData((byte)12, TValue.class))));
        metaDataMap = Collections.unmodifiableMap(var0);
        FieldMetaData.addStructMetaDataMap(TAttributes.class, metaDataMap);
    }

    public static enum _Fields implements TFieldIdEnum {
        KEYS((short)1, "keys"),
        VALUES((short)2, "values");

        private static final Map<String, TAttributes._Fields> byName = new HashMap();
        private final short _thriftId;
        private final String _fieldName;

        public static TAttributes._Fields findByThriftId(int var0) {
            switch(var0) {
                case 1:
                    return KEYS;
                case 2:
                    return VALUES;
                default:
                    return null;
            }
        }

        public static TAttributes._Fields findByThriftIdOrThrow(int var0) {
            TAttributes._Fields var1 = findByThriftId(var0);
            if (var1 == null) {
                throw new IllegalArgumentException("Field " + var0 + " doesn't exist!");
            } else {
                return var1;
            }
        }

        public static TAttributes._Fields findByName(String var0) {
            return (TAttributes._Fields)byName.get(var0);
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
            Iterator var0 = EnumSet.allOf(TAttributes._Fields.class).iterator();

            while(var0.hasNext()) {
                TAttributes._Fields var1 = (TAttributes._Fields)var0.next();
                byName.put(var1.getFieldName(), var1);
            }

        }
    }
}
