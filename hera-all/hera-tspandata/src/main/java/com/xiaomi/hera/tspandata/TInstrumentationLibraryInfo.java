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
import org.apache.thrift.meta_data.FieldValueMetaData;
import org.apache.thrift.protocol.TField;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TProtocolUtil;
import org.apache.thrift.protocol.TStruct;

public class TInstrumentationLibraryInfo implements TBase<TInstrumentationLibraryInfo, TInstrumentationLibraryInfo._Fields>, Serializable, Cloneable {
    private static final TStruct STRUCT_DESC = new TStruct("TInstrumentationLibraryInfo");
    private static final TField NAME_FIELD_DESC = new TField("name", (byte)11, (short)1);
    private static final TField VERSION_FIELD_DESC = new TField("version", (byte)11, (short)2);
    public String name;
    public String version;
    public static final Map<TInstrumentationLibraryInfo._Fields, FieldMetaData> metaDataMap;

    public TInstrumentationLibraryInfo() {
    }

    public TInstrumentationLibraryInfo(TInstrumentationLibraryInfo var1) {
        if (var1.isSetName()) {
            this.name = var1.name;
        }

        if (var1.isSetVersion()) {
            this.version = var1.version;
        }

    }

    public TInstrumentationLibraryInfo deepCopy() {
        return new TInstrumentationLibraryInfo(this);
    }

    public void clear() {
        this.name = null;
        this.version = null;
    }

    public String getName() {
        return this.name;
    }

    public TInstrumentationLibraryInfo setName(String var1) {
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

    public String getVersion() {
        return this.version;
    }

    public TInstrumentationLibraryInfo setVersion(String var1) {
        this.version = var1;
        return this;
    }

    public void unsetVersion() {
        this.version = null;
    }

    public boolean isSetVersion() {
        return this.version != null;
    }

    public void setVersionIsSet(boolean var1) {
        if (!var1) {
            this.version = null;
        }

    }

    public void setFieldValue(TInstrumentationLibraryInfo._Fields var1, Object var2) {
        switch(var1) {
            case NAME:
                if (var2 == null) {
                    this.unsetName();
                } else {
                    this.setName((String)var2);
                }
                break;
            case VERSION:
                if (var2 == null) {
                    this.unsetVersion();
                } else {
                    this.setVersion((String)var2);
                }
        }

    }

    public Object getFieldValue(TInstrumentationLibraryInfo._Fields var1) {
        switch(var1) {
            case NAME:
                return this.getName();
            case VERSION:
                return this.getVersion();
            default:
                throw new IllegalStateException();
        }
    }

    public boolean isSet(TInstrumentationLibraryInfo._Fields var1) {
        if (var1 == null) {
            throw new IllegalArgumentException();
        } else {
            switch(var1) {
                case NAME:
                    return this.isSetName();
                case VERSION:
                    return this.isSetVersion();
                default:
                    throw new IllegalStateException();
            }
        }
    }

    public boolean equals(Object var1) {
        if (var1 == null) {
            return false;
        } else {
            return var1 instanceof TInstrumentationLibraryInfo ? this.equals((TInstrumentationLibraryInfo)var1) : false;
        }
    }

    public boolean equals(TInstrumentationLibraryInfo var1) {
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

            boolean var4 = this.isSetVersion();
            boolean var5 = var1.isSetVersion();
            if (var4 || var5) {
                if (!var4 || !var5) {
                    return false;
                }

                if (!this.version.equals(var1.version)) {
                    return false;
                }
            }

            return true;
        }
    }

    public int hashCode() {
        return 0;
    }

    public int compareTo(TInstrumentationLibraryInfo var1) {
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

                var4 = Boolean.valueOf(this.isSetVersion()).compareTo(var1.isSetVersion());
                if (var4 != 0) {
                    return var4;
                } else {
                    if (this.isSetVersion()) {
                        var4 = TBaseHelper.compareTo(this.version, var1.version);
                        if (var4 != 0) {
                            return var4;
                        }
                    }

                    return 0;
                }
            }
        }
    }

    public TInstrumentationLibraryInfo._Fields fieldForId(int var1) {
        return TInstrumentationLibraryInfo._Fields.findByThriftId(var1);
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
                    if (var2.type == 11) {
                        this.version = var1.readString();
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

        if (this.version != null && this.isSetVersion()) {
            var1.writeFieldBegin(VERSION_FIELD_DESC);
            var1.writeString(this.version);
            var1.writeFieldEnd();
        }

        var1.writeFieldStop();
        var1.writeStructEnd();
    }

    public String toString() {
        StringBuilder var1 = new StringBuilder("TInstrumentationLibraryInfo(");
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

        if (this.isSetVersion()) {
            if (!var2) {
                var1.append(", ");
            }

            var1.append("version:");
            if (this.version == null) {
                var1.append("null");
            } else {
                var1.append(this.version);
            }

            var2 = false;
        }

        var1.append(")");
        return var1.toString();
    }

    public void validate() throws TException {
    }

    static {
        EnumMap var0 = new EnumMap(TInstrumentationLibraryInfo._Fields.class);
        var0.put(TInstrumentationLibraryInfo._Fields.NAME, new FieldMetaData("name", (byte)2, new FieldValueMetaData((byte)11)));
        var0.put(TInstrumentationLibraryInfo._Fields.VERSION, new FieldMetaData("version", (byte)2, new FieldValueMetaData((byte)11)));
        metaDataMap = Collections.unmodifiableMap(var0);
        FieldMetaData.addStructMetaDataMap(TInstrumentationLibraryInfo.class, metaDataMap);
    }

    public static enum _Fields implements TFieldIdEnum {
        NAME((short)1, "name"),
        VERSION((short)2, "version");

        private static final Map<String, TInstrumentationLibraryInfo._Fields> byName = new HashMap();
        private final short _thriftId;
        private final String _fieldName;

        public static TInstrumentationLibraryInfo._Fields findByThriftId(int var0) {
            switch(var0) {
                case 1:
                    return NAME;
                case 2:
                    return VERSION;
                default:
                    return null;
            }
        }

        public static TInstrumentationLibraryInfo._Fields findByThriftIdOrThrow(int var0) {
            TInstrumentationLibraryInfo._Fields var1 = findByThriftId(var0);
            if (var1 == null) {
                throw new IllegalArgumentException("Field " + var0 + " doesn't exist!");
            } else {
                return var1;
            }
        }

        public static TInstrumentationLibraryInfo._Fields findByName(String var0) {
            return (TInstrumentationLibraryInfo._Fields)byName.get(var0);
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
            Iterator var0 = EnumSet.allOf(TInstrumentationLibraryInfo._Fields.class).iterator();

            while(var0.hasNext()) {
                TInstrumentationLibraryInfo._Fields var1 = (TInstrumentationLibraryInfo._Fields)var0.next();
                byName.put(var1.getFieldName(), var1);
            }

        }
    }
}
