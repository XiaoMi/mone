//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.xiaomi.hera.tspandata;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.BitSet;
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
import org.apache.thrift.meta_data.FieldValueMetaData;
import org.apache.thrift.meta_data.ListMetaData;
import org.apache.thrift.protocol.TField;
import org.apache.thrift.protocol.TList;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TProtocolUtil;
import org.apache.thrift.protocol.TStruct;

public class TValue implements TBase<TValue, TValue._Fields>, Serializable, Cloneable {
    private static final TStruct STRUCT_DESC = new TStruct("TValue");
    private static final TField STRING_VALUE_FIELD_DESC = new TField("stringValue", (byte)11, (short)1);
    private static final TField BOOL_VALUE_FIELD_DESC = new TField("boolValue", (byte)2, (short)2);
    private static final TField LONG_VALUE_FIELD_DESC = new TField("longValue", (byte)10, (short)3);
    private static final TField DOUBLE_VALUE_FIELD_DESC = new TField("doubleValue", (byte)4, (short)4);
    private static final TField STRING_ARRAY_VALUE_FIELD_DESC = new TField("stringArrayValue", (byte)15, (short)5);
    private static final TField BOOL_ARRAY_VALUE_FIELD_DESC = new TField("boolArrayValue", (byte)15, (short)6);
    private static final TField LONG_ARRAY_VALUE_FIELD_DESC = new TField("longArrayValue", (byte)15, (short)7);
    private static final TField DOUBLE_ARRAY_VALUE_FIELD_DESC = new TField("doubleArrayValue", (byte)15, (short)8);
    public String stringValue;
    public boolean boolValue;
    public long longValue;
    public double doubleValue;
    public List<String> stringArrayValue;
    public List<Boolean> boolArrayValue;
    public List<Long> longArrayValue;
    public List<Double> doubleArrayValue;
    private static final int __BOOLVALUE_ISSET_ID = 0;
    private static final int __LONGVALUE_ISSET_ID = 1;
    private static final int __DOUBLEVALUE_ISSET_ID = 2;
    private BitSet __isset_bit_vector = new BitSet(3);
    public static final Map<TValue._Fields, FieldMetaData> metaDataMap;

    public TValue() {
    }

    public TValue(TValue var1) {
        this.__isset_bit_vector.clear();
        this.__isset_bit_vector.or(var1.__isset_bit_vector);
        if (var1.isSetStringValue()) {
            this.stringValue = var1.stringValue;
        }

        this.boolValue = var1.boolValue;
        this.longValue = var1.longValue;
        this.doubleValue = var1.doubleValue;
        ArrayList var2;
        Iterator var3;
        if (var1.isSetStringArrayValue()) {
            var2 = new ArrayList();
            var3 = var1.stringArrayValue.iterator();

            while(var3.hasNext()) {
                String var4 = (String)var3.next();
                var2.add(var4);
            }

            this.stringArrayValue = var2;
        }

        if (var1.isSetBoolArrayValue()) {
            var2 = new ArrayList();
            var3 = var1.boolArrayValue.iterator();

            while(var3.hasNext()) {
                Boolean var5 = (Boolean)var3.next();
                var2.add(var5);
            }

            this.boolArrayValue = var2;
        }

        if (var1.isSetLongArrayValue()) {
            var2 = new ArrayList();
            var3 = var1.longArrayValue.iterator();

            while(var3.hasNext()) {
                Long var6 = (Long)var3.next();
                var2.add(var6);
            }

            this.longArrayValue = var2;
        }

        if (var1.isSetDoubleArrayValue()) {
            var2 = new ArrayList();
            var3 = var1.doubleArrayValue.iterator();

            while(var3.hasNext()) {
                Double var7 = (Double)var3.next();
                var2.add(var7);
            }

            this.doubleArrayValue = var2;
        }

    }

    public TValue deepCopy() {
        return new TValue(this);
    }

    public void clear() {
        this.stringValue = null;
        this.setBoolValueIsSet(false);
        this.boolValue = false;
        this.setLongValueIsSet(false);
        this.longValue = 0L;
        this.setDoubleValueIsSet(false);
        this.doubleValue = 0.0D;
        this.stringArrayValue = null;
        this.boolArrayValue = null;
        this.longArrayValue = null;
        this.doubleArrayValue = null;
    }

    public String getStringValue() {
        return this.stringValue;
    }

    public TValue setStringValue(String var1) {
        this.stringValue = var1;
        return this;
    }

    public void unsetStringValue() {
        this.stringValue = null;
    }

    public boolean isSetStringValue() {
        return this.stringValue != null;
    }

    public void setStringValueIsSet(boolean var1) {
        if (!var1) {
            this.stringValue = null;
        }

    }

    public boolean isBoolValue() {
        return this.boolValue;
    }

    public TValue setBoolValue(boolean var1) {
        this.boolValue = var1;
        this.setBoolValueIsSet(true);
        return this;
    }

    public void unsetBoolValue() {
        this.__isset_bit_vector.clear(0);
    }

    public boolean isSetBoolValue() {
        return this.__isset_bit_vector.get(0);
    }

    public void setBoolValueIsSet(boolean var1) {
        this.__isset_bit_vector.set(0, var1);
    }

    public long getLongValue() {
        return this.longValue;
    }

    public TValue setLongValue(long var1) {
        this.longValue = var1;
        this.setLongValueIsSet(true);
        return this;
    }

    public void unsetLongValue() {
        this.__isset_bit_vector.clear(1);
    }

    public boolean isSetLongValue() {
        return this.__isset_bit_vector.get(1);
    }

    public void setLongValueIsSet(boolean var1) {
        this.__isset_bit_vector.set(1, var1);
    }

    public double getDoubleValue() {
        return this.doubleValue;
    }

    public TValue setDoubleValue(double var1) {
        this.doubleValue = var1;
        this.setDoubleValueIsSet(true);
        return this;
    }

    public void unsetDoubleValue() {
        this.__isset_bit_vector.clear(2);
    }

    public boolean isSetDoubleValue() {
        return this.__isset_bit_vector.get(2);
    }

    public void setDoubleValueIsSet(boolean var1) {
        this.__isset_bit_vector.set(2, var1);
    }

    public int getStringArrayValueSize() {
        return this.stringArrayValue == null ? 0 : this.stringArrayValue.size();
    }

    public Iterator<String> getStringArrayValueIterator() {
        return this.stringArrayValue == null ? null : this.stringArrayValue.iterator();
    }

    public void addToStringArrayValue(String var1) {
        if (this.stringArrayValue == null) {
            this.stringArrayValue = new ArrayList();
        }

        this.stringArrayValue.add(var1);
    }

    public List<String> getStringArrayValue() {
        return this.stringArrayValue;
    }

    public TValue setStringArrayValue(List<String> var1) {
        this.stringArrayValue = var1;
        return this;
    }

    public void unsetStringArrayValue() {
        this.stringArrayValue = null;
    }

    public boolean isSetStringArrayValue() {
        return this.stringArrayValue != null;
    }

    public void setStringArrayValueIsSet(boolean var1) {
        if (!var1) {
            this.stringArrayValue = null;
        }

    }

    public int getBoolArrayValueSize() {
        return this.boolArrayValue == null ? 0 : this.boolArrayValue.size();
    }

    public Iterator<Boolean> getBoolArrayValueIterator() {
        return this.boolArrayValue == null ? null : this.boolArrayValue.iterator();
    }

    public void addToBoolArrayValue(boolean var1) {
        if (this.boolArrayValue == null) {
            this.boolArrayValue = new ArrayList();
        }

        this.boolArrayValue.add(var1);
    }

    public List<Boolean> getBoolArrayValue() {
        return this.boolArrayValue;
    }

    public TValue setBoolArrayValue(List<Boolean> var1) {
        this.boolArrayValue = var1;
        return this;
    }

    public void unsetBoolArrayValue() {
        this.boolArrayValue = null;
    }

    public boolean isSetBoolArrayValue() {
        return this.boolArrayValue != null;
    }

    public void setBoolArrayValueIsSet(boolean var1) {
        if (!var1) {
            this.boolArrayValue = null;
        }

    }

    public int getLongArrayValueSize() {
        return this.longArrayValue == null ? 0 : this.longArrayValue.size();
    }

    public Iterator<Long> getLongArrayValueIterator() {
        return this.longArrayValue == null ? null : this.longArrayValue.iterator();
    }

    public void addToLongArrayValue(long var1) {
        if (this.longArrayValue == null) {
            this.longArrayValue = new ArrayList();
        }

        this.longArrayValue.add(var1);
    }

    public List<Long> getLongArrayValue() {
        return this.longArrayValue;
    }

    public TValue setLongArrayValue(List<Long> var1) {
        this.longArrayValue = var1;
        return this;
    }

    public void unsetLongArrayValue() {
        this.longArrayValue = null;
    }

    public boolean isSetLongArrayValue() {
        return this.longArrayValue != null;
    }

    public void setLongArrayValueIsSet(boolean var1) {
        if (!var1) {
            this.longArrayValue = null;
        }

    }

    public int getDoubleArrayValueSize() {
        return this.doubleArrayValue == null ? 0 : this.doubleArrayValue.size();
    }

    public Iterator<Double> getDoubleArrayValueIterator() {
        return this.doubleArrayValue == null ? null : this.doubleArrayValue.iterator();
    }

    public void addToDoubleArrayValue(double var1) {
        if (this.doubleArrayValue == null) {
            this.doubleArrayValue = new ArrayList();
        }

        this.doubleArrayValue.add(var1);
    }

    public List<Double> getDoubleArrayValue() {
        return this.doubleArrayValue;
    }

    public TValue setDoubleArrayValue(List<Double> var1) {
        this.doubleArrayValue = var1;
        return this;
    }

    public void unsetDoubleArrayValue() {
        this.doubleArrayValue = null;
    }

    public boolean isSetDoubleArrayValue() {
        return this.doubleArrayValue != null;
    }

    public void setDoubleArrayValueIsSet(boolean var1) {
        if (!var1) {
            this.doubleArrayValue = null;
        }

    }

    public void setFieldValue(TValue._Fields var1, Object var2) {
        switch(var1) {
            case STRING_VALUE:
                if (var2 == null) {
                    this.unsetStringValue();
                } else {
                    this.setStringValue((String)var2);
                }
                break;
            case BOOL_VALUE:
                if (var2 == null) {
                    this.unsetBoolValue();
                } else {
                    this.setBoolValue((Boolean)var2);
                }
                break;
            case LONG_VALUE:
                if (var2 == null) {
                    this.unsetLongValue();
                } else {
                    this.setLongValue((Long)var2);
                }
                break;
            case DOUBLE_VALUE:
                if (var2 == null) {
                    this.unsetDoubleValue();
                } else {
                    this.setDoubleValue((Double)var2);
                }
                break;
            case STRING_ARRAY_VALUE:
                if (var2 == null) {
                    this.unsetStringArrayValue();
                } else {
                    this.setStringArrayValue((List)var2);
                }
                break;
            case BOOL_ARRAY_VALUE:
                if (var2 == null) {
                    this.unsetBoolArrayValue();
                } else {
                    this.setBoolArrayValue((List)var2);
                }
                break;
            case LONG_ARRAY_VALUE:
                if (var2 == null) {
                    this.unsetLongArrayValue();
                } else {
                    this.setLongArrayValue((List)var2);
                }
                break;
            case DOUBLE_ARRAY_VALUE:
                if (var2 == null) {
                    this.unsetDoubleArrayValue();
                } else {
                    this.setDoubleArrayValue((List)var2);
                }
        }

    }

    public Object getFieldValue(TValue._Fields var1) {
        switch(var1) {
            case STRING_VALUE:
                return this.getStringValue();
            case BOOL_VALUE:
                return new Boolean(this.isBoolValue());
            case LONG_VALUE:
                return new Long(this.getLongValue());
            case DOUBLE_VALUE:
                return new Double(this.getDoubleValue());
            case STRING_ARRAY_VALUE:
                return this.getStringArrayValue();
            case BOOL_ARRAY_VALUE:
                return this.getBoolArrayValue();
            case LONG_ARRAY_VALUE:
                return this.getLongArrayValue();
            case DOUBLE_ARRAY_VALUE:
                return this.getDoubleArrayValue();
            default:
                throw new IllegalStateException();
        }
    }

    public boolean isSet(TValue._Fields var1) {
        if (var1 == null) {
            throw new IllegalArgumentException();
        } else {
            switch(var1) {
                case STRING_VALUE:
                    return this.isSetStringValue();
                case BOOL_VALUE:
                    return this.isSetBoolValue();
                case LONG_VALUE:
                    return this.isSetLongValue();
                case DOUBLE_VALUE:
                    return this.isSetDoubleValue();
                case STRING_ARRAY_VALUE:
                    return this.isSetStringArrayValue();
                case BOOL_ARRAY_VALUE:
                    return this.isSetBoolArrayValue();
                case LONG_ARRAY_VALUE:
                    return this.isSetLongArrayValue();
                case DOUBLE_ARRAY_VALUE:
                    return this.isSetDoubleArrayValue();
                default:
                    throw new IllegalStateException();
            }
        }
    }

    public boolean equals(Object var1) {
        if (var1 == null) {
            return false;
        } else {
            return var1 instanceof TValue ? this.equals((TValue)var1) : false;
        }
    }

    public boolean equals(TValue var1) {
        if (var1 == null) {
            return false;
        } else {
            boolean var2 = this.isSetStringValue();
            boolean var3 = var1.isSetStringValue();
            if (var2 || var3) {
                if (!var2 || !var3) {
                    return false;
                }

                if (!this.stringValue.equals(var1.stringValue)) {
                    return false;
                }
            }

            boolean var4 = this.isSetBoolValue();
            boolean var5 = var1.isSetBoolValue();
            if (var4 || var5) {
                if (!var4 || !var5) {
                    return false;
                }

                if (this.boolValue != var1.boolValue) {
                    return false;
                }
            }

            boolean var6 = this.isSetLongValue();
            boolean var7 = var1.isSetLongValue();
            if (var6 || var7) {
                if (!var6 || !var7) {
                    return false;
                }

                if (this.longValue != var1.longValue) {
                    return false;
                }
            }

            boolean var8 = this.isSetDoubleValue();
            boolean var9 = var1.isSetDoubleValue();
            if (var8 || var9) {
                if (!var8 || !var9) {
                    return false;
                }

                if (this.doubleValue != var1.doubleValue) {
                    return false;
                }
            }

            boolean var10 = this.isSetStringArrayValue();
            boolean var11 = var1.isSetStringArrayValue();
            if (var10 || var11) {
                if (!var10 || !var11) {
                    return false;
                }

                if (!this.stringArrayValue.equals(var1.stringArrayValue)) {
                    return false;
                }
            }

            boolean var12 = this.isSetBoolArrayValue();
            boolean var13 = var1.isSetBoolArrayValue();
            if (var12 || var13) {
                if (!var12 || !var13) {
                    return false;
                }

                if (!this.boolArrayValue.equals(var1.boolArrayValue)) {
                    return false;
                }
            }

            boolean var14 = this.isSetLongArrayValue();
            boolean var15 = var1.isSetLongArrayValue();
            if (var14 || var15) {
                if (!var14 || !var15) {
                    return false;
                }

                if (!this.longArrayValue.equals(var1.longArrayValue)) {
                    return false;
                }
            }

            boolean var16 = this.isSetDoubleArrayValue();
            boolean var17 = var1.isSetDoubleArrayValue();
            if (var16 || var17) {
                if (!var16 || !var17) {
                    return false;
                }

                if (!this.doubleArrayValue.equals(var1.doubleArrayValue)) {
                    return false;
                }
            }

            return true;
        }
    }

    public int hashCode() {
        return 0;
    }

    public int compareTo(TValue var1) {
        if (!this.getClass().equals(var1.getClass())) {
            return this.getClass().getName().compareTo(var1.getClass().getName());
        } else {
            boolean var2 = false;
            int var4 = Boolean.valueOf(this.isSetStringValue()).compareTo(var1.isSetStringValue());
            if (var4 != 0) {
                return var4;
            } else {
                if (this.isSetStringValue()) {
                    var4 = TBaseHelper.compareTo(this.stringValue, var1.stringValue);
                    if (var4 != 0) {
                        return var4;
                    }
                }

                var4 = Boolean.valueOf(this.isSetBoolValue()).compareTo(var1.isSetBoolValue());
                if (var4 != 0) {
                    return var4;
                } else {
                    if (this.isSetBoolValue()) {
                        var4 = TBaseHelper.compareTo(this.boolValue, var1.boolValue);
                        if (var4 != 0) {
                            return var4;
                        }
                    }

                    var4 = Boolean.valueOf(this.isSetLongValue()).compareTo(var1.isSetLongValue());
                    if (var4 != 0) {
                        return var4;
                    } else {
                        if (this.isSetLongValue()) {
                            var4 = TBaseHelper.compareTo(this.longValue, var1.longValue);
                            if (var4 != 0) {
                                return var4;
                            }
                        }

                        var4 = Boolean.valueOf(this.isSetDoubleValue()).compareTo(var1.isSetDoubleValue());
                        if (var4 != 0) {
                            return var4;
                        } else {
                            if (this.isSetDoubleValue()) {
                                var4 = TBaseHelper.compareTo(this.doubleValue, var1.doubleValue);
                                if (var4 != 0) {
                                    return var4;
                                }
                            }

                            var4 = Boolean.valueOf(this.isSetStringArrayValue()).compareTo(var1.isSetStringArrayValue());
                            if (var4 != 0) {
                                return var4;
                            } else {
                                if (this.isSetStringArrayValue()) {
                                    var4 = TBaseHelper.compareTo(this.stringArrayValue, var1.stringArrayValue);
                                    if (var4 != 0) {
                                        return var4;
                                    }
                                }

                                var4 = Boolean.valueOf(this.isSetBoolArrayValue()).compareTo(var1.isSetBoolArrayValue());
                                if (var4 != 0) {
                                    return var4;
                                } else {
                                    if (this.isSetBoolArrayValue()) {
                                        var4 = TBaseHelper.compareTo(this.boolArrayValue, var1.boolArrayValue);
                                        if (var4 != 0) {
                                            return var4;
                                        }
                                    }

                                    var4 = Boolean.valueOf(this.isSetLongArrayValue()).compareTo(var1.isSetLongArrayValue());
                                    if (var4 != 0) {
                                        return var4;
                                    } else {
                                        if (this.isSetLongArrayValue()) {
                                            var4 = TBaseHelper.compareTo(this.longArrayValue, var1.longArrayValue);
                                            if (var4 != 0) {
                                                return var4;
                                            }
                                        }

                                        var4 = Boolean.valueOf(this.isSetDoubleArrayValue()).compareTo(var1.isSetDoubleArrayValue());
                                        if (var4 != 0) {
                                            return var4;
                                        } else {
                                            if (this.isSetDoubleArrayValue()) {
                                                var4 = TBaseHelper.compareTo(this.doubleArrayValue, var1.doubleArrayValue);
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
            }
        }
    }

    public TValue._Fields fieldForId(int var1) {
        return TValue._Fields.findByThriftId(var1);
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
                    if (var2.type == 11) {
                        this.stringValue = var1.readString();
                    } else {
                        TProtocolUtil.skip(var1, var2.type);
                    }
                    break;
                case 2:
                    if (var2.type == 2) {
                        this.boolValue = var1.readBool();
                        this.setBoolValueIsSet(true);
                    } else {
                        TProtocolUtil.skip(var1, var2.type);
                    }
                    break;
                case 3:
                    if (var2.type == 10) {
                        this.longValue = var1.readI64();
                        this.setLongValueIsSet(true);
                    } else {
                        TProtocolUtil.skip(var1, var2.type);
                    }
                    break;
                case 4:
                    if (var2.type == 4) {
                        this.doubleValue = var1.readDouble();
                        this.setDoubleValueIsSet(true);
                    } else {
                        TProtocolUtil.skip(var1, var2.type);
                    }
                    break;
                case 5:
                    if (var2.type != 15) {
                        TProtocolUtil.skip(var1, var2.type);
                        break;
                    }

                    var3 = var1.readListBegin();
                    this.stringArrayValue = new ArrayList(var3.size);

                    for(var4 = 0; var4 < var3.size; ++var4) {
                        String var9 = var1.readString();
                        this.stringArrayValue.add(var9);
                    }

                    var1.readListEnd();
                    break;
                case 6:
                    if (var2.type != 15) {
                        TProtocolUtil.skip(var1, var2.type);
                        break;
                    }

                    var3 = var1.readListBegin();
                    this.boolArrayValue = new ArrayList(var3.size);

                    for(var4 = 0; var4 < var3.size; ++var4) {
                        boolean var8 = var1.readBool();
                        this.boolArrayValue.add(var8);
                    }

                    var1.readListEnd();
                    break;
                case 7:
                    if (var2.type != 15) {
                        TProtocolUtil.skip(var1, var2.type);
                        break;
                    }

                    var3 = var1.readListBegin();
                    this.longArrayValue = new ArrayList(var3.size);

                    for(var4 = 0; var4 < var3.size; ++var4) {
                        long var7 = var1.readI64();
                        this.longArrayValue.add(var7);
                    }

                    var1.readListEnd();
                    break;
                case 8:
                    if (var2.type != 15) {
                        TProtocolUtil.skip(var1, var2.type);
                        break;
                    }

                    var3 = var1.readListBegin();
                    this.doubleArrayValue = new ArrayList(var3.size);

                    for(var4 = 0; var4 < var3.size; ++var4) {
                        double var5 = var1.readDouble();
                        this.doubleArrayValue.add(var5);
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
        if (this.stringValue != null && this.isSetStringValue()) {
            var1.writeFieldBegin(STRING_VALUE_FIELD_DESC);
            var1.writeString(this.stringValue);
            var1.writeFieldEnd();
        }

        if (this.isSetBoolValue()) {
            var1.writeFieldBegin(BOOL_VALUE_FIELD_DESC);
            var1.writeBool(this.boolValue);
            var1.writeFieldEnd();
        }

        if (this.isSetLongValue()) {
            var1.writeFieldBegin(LONG_VALUE_FIELD_DESC);
            var1.writeI64(this.longValue);
            var1.writeFieldEnd();
        }

        if (this.isSetDoubleValue()) {
            var1.writeFieldBegin(DOUBLE_VALUE_FIELD_DESC);
            var1.writeDouble(this.doubleValue);
            var1.writeFieldEnd();
        }

        Iterator var2;
        if (this.stringArrayValue != null && this.isSetStringArrayValue()) {
            var1.writeFieldBegin(STRING_ARRAY_VALUE_FIELD_DESC);
            var1.writeListBegin(new TList((byte)11, this.stringArrayValue.size()));
            var2 = this.stringArrayValue.iterator();

            while(var2.hasNext()) {
                String var3 = (String)var2.next();
                var1.writeString(var3);
            }

            var1.writeListEnd();
            var1.writeFieldEnd();
        }

        if (this.boolArrayValue != null && this.isSetBoolArrayValue()) {
            var1.writeFieldBegin(BOOL_ARRAY_VALUE_FIELD_DESC);
            var1.writeListBegin(new TList((byte)2, this.boolArrayValue.size()));
            var2 = this.boolArrayValue.iterator();

            while(var2.hasNext()) {
                boolean var5 = (Boolean)var2.next();
                var1.writeBool(var5);
            }

            var1.writeListEnd();
            var1.writeFieldEnd();
        }

        if (this.longArrayValue != null && this.isSetLongArrayValue()) {
            var1.writeFieldBegin(LONG_ARRAY_VALUE_FIELD_DESC);
            var1.writeListBegin(new TList((byte)10, this.longArrayValue.size()));
            var2 = this.longArrayValue.iterator();

            while(var2.hasNext()) {
                long var6 = (Long)var2.next();
                var1.writeI64(var6);
            }

            var1.writeListEnd();
            var1.writeFieldEnd();
        }

        if (this.doubleArrayValue != null && this.isSetDoubleArrayValue()) {
            var1.writeFieldBegin(DOUBLE_ARRAY_VALUE_FIELD_DESC);
            var1.writeListBegin(new TList((byte)4, this.doubleArrayValue.size()));
            var2 = this.doubleArrayValue.iterator();

            while(var2.hasNext()) {
                double var7 = (Double)var2.next();
                var1.writeDouble(var7);
            }

            var1.writeListEnd();
            var1.writeFieldEnd();
        }

        var1.writeFieldStop();
        var1.writeStructEnd();
    }

    public String toString() {
        StringBuilder var1 = new StringBuilder("TValue(");
        boolean var2 = true;
        if (this.isSetStringValue()) {
            var1.append("stringValue:");
            if (this.stringValue == null) {
                var1.append("null");
            } else {
                var1.append(this.stringValue);
            }

            var2 = false;
        }

        if (this.isSetBoolValue()) {
            if (!var2) {
                var1.append(", ");
            }

            var1.append("boolValue:");
            var1.append(this.boolValue);
            var2 = false;
        }

        if (this.isSetLongValue()) {
            if (!var2) {
                var1.append(", ");
            }

            var1.append("longValue:");
            var1.append(this.longValue);
            var2 = false;
        }

        if (this.isSetDoubleValue()) {
            if (!var2) {
                var1.append(", ");
            }

            var1.append("doubleValue:");
            var1.append(this.doubleValue);
            var2 = false;
        }

        if (this.isSetStringArrayValue()) {
            if (!var2) {
                var1.append(", ");
            }

            var1.append("stringArrayValue:");
            if (this.stringArrayValue == null) {
                var1.append("null");
            } else {
                var1.append(this.stringArrayValue);
            }

            var2 = false;
        }

        if (this.isSetBoolArrayValue()) {
            if (!var2) {
                var1.append(", ");
            }

            var1.append("boolArrayValue:");
            if (this.boolArrayValue == null) {
                var1.append("null");
            } else {
                var1.append(this.boolArrayValue);
            }

            var2 = false;
        }

        if (this.isSetLongArrayValue()) {
            if (!var2) {
                var1.append(", ");
            }

            var1.append("longArrayValue:");
            if (this.longArrayValue == null) {
                var1.append("null");
            } else {
                var1.append(this.longArrayValue);
            }

            var2 = false;
        }

        if (this.isSetDoubleArrayValue()) {
            if (!var2) {
                var1.append(", ");
            }

            var1.append("doubleArrayValue:");
            if (this.doubleArrayValue == null) {
                var1.append("null");
            } else {
                var1.append(this.doubleArrayValue);
            }

            var2 = false;
        }

        var1.append(")");
        return var1.toString();
    }

    public void validate() throws TException {
    }

    static {
        EnumMap var0 = new EnumMap(TValue._Fields.class);
        var0.put(TValue._Fields.STRING_VALUE, new FieldMetaData("stringValue", (byte)2, new FieldValueMetaData((byte)11)));
        var0.put(TValue._Fields.BOOL_VALUE, new FieldMetaData("boolValue", (byte)2, new FieldValueMetaData((byte)2)));
        var0.put(TValue._Fields.LONG_VALUE, new FieldMetaData("longValue", (byte)2, new FieldValueMetaData((byte)10)));
        var0.put(TValue._Fields.DOUBLE_VALUE, new FieldMetaData("doubleValue", (byte)2, new FieldValueMetaData((byte)4)));
        var0.put(TValue._Fields.STRING_ARRAY_VALUE, new FieldMetaData("stringArrayValue", (byte)2, new ListMetaData((byte)15, new FieldValueMetaData((byte)11))));
        var0.put(TValue._Fields.BOOL_ARRAY_VALUE, new FieldMetaData("boolArrayValue", (byte)2, new ListMetaData((byte)15, new FieldValueMetaData((byte)2))));
        var0.put(TValue._Fields.LONG_ARRAY_VALUE, new FieldMetaData("longArrayValue", (byte)2, new ListMetaData((byte)15, new FieldValueMetaData((byte)10))));
        var0.put(TValue._Fields.DOUBLE_ARRAY_VALUE, new FieldMetaData("doubleArrayValue", (byte)2, new ListMetaData((byte)15, new FieldValueMetaData((byte)4))));
        metaDataMap = Collections.unmodifiableMap(var0);
        FieldMetaData.addStructMetaDataMap(TValue.class, metaDataMap);
    }

    public static enum _Fields implements TFieldIdEnum {
        STRING_VALUE((short)1, "stringValue"),
        BOOL_VALUE((short)2, "boolValue"),
        LONG_VALUE((short)3, "longValue"),
        DOUBLE_VALUE((short)4, "doubleValue"),
        STRING_ARRAY_VALUE((short)5, "stringArrayValue"),
        BOOL_ARRAY_VALUE((short)6, "boolArrayValue"),
        LONG_ARRAY_VALUE((short)7, "longArrayValue"),
        DOUBLE_ARRAY_VALUE((short)8, "doubleArrayValue");

        private static final Map<String, TValue._Fields> byName = new HashMap();
        private final short _thriftId;
        private final String _fieldName;

        public static TValue._Fields findByThriftId(int var0) {
            switch(var0) {
                case 1:
                    return STRING_VALUE;
                case 2:
                    return BOOL_VALUE;
                case 3:
                    return LONG_VALUE;
                case 4:
                    return DOUBLE_VALUE;
                case 5:
                    return STRING_ARRAY_VALUE;
                case 6:
                    return BOOL_ARRAY_VALUE;
                case 7:
                    return LONG_ARRAY_VALUE;
                case 8:
                    return DOUBLE_ARRAY_VALUE;
                default:
                    return null;
            }
        }

        public static TValue._Fields findByThriftIdOrThrow(int var0) {
            TValue._Fields var1 = findByThriftId(var0);
            if (var1 == null) {
                throw new IllegalArgumentException("Field " + var0 + " doesn't exist!");
            } else {
                return var1;
            }
        }

        public static TValue._Fields findByName(String var0) {
            return (TValue._Fields)byName.get(var0);
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
            Iterator var0 = EnumSet.allOf(TValue._Fields.class).iterator();

            while(var0.hasNext()) {
                TValue._Fields var1 = (TValue._Fields)var0.next();
                byName.put(var1.getFieldName(), var1);
            }

        }
    }
}
