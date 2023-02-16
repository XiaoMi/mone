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
import java.util.Map.Entry;
import org.apache.thrift.TBase;
import org.apache.thrift.TBaseHelper;
import org.apache.thrift.TException;
import org.apache.thrift.TFieldIdEnum;
import org.apache.thrift.meta_data.FieldMetaData;
import org.apache.thrift.meta_data.FieldValueMetaData;
import org.apache.thrift.meta_data.MapMetaData;
import org.apache.thrift.protocol.TField;
import org.apache.thrift.protocol.TMap;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TProtocolUtil;
import org.apache.thrift.protocol.TStruct;

public class TTraceState implements TBase<TTraceState, TTraceState._Fields>, Serializable, Cloneable {
    private static final TStruct STRUCT_DESC = new TStruct("TTraceState");
    private static final TField ENTRIES_FIELD_DESC = new TField("entries", (byte)13, (short)1);
    public Map<String, String> entries;
    public static final Map<TTraceState._Fields, FieldMetaData> metaDataMap;

    public TTraceState() {
    }

    public TTraceState(TTraceState var1) {
        if (var1.isSetEntries()) {
            HashMap var2 = new HashMap();
            Iterator var3 = var1.entries.entrySet().iterator();

            while(var3.hasNext()) {
                Entry var4 = (Entry)var3.next();
                String var5 = (String)var4.getKey();
                String var6 = (String)var4.getValue();
                var2.put(var5, var6);
            }

            this.entries = var2;
        }

    }

    public TTraceState deepCopy() {
        return new TTraceState(this);
    }

    public void clear() {
        this.entries = null;
    }

    public int getEntriesSize() {
        return this.entries == null ? 0 : this.entries.size();
    }

    public void putToEntries(String var1, String var2) {
        if (this.entries == null) {
            this.entries = new HashMap();
        }

        this.entries.put(var1, var2);
    }

    public Map<String, String> getEntries() {
        return this.entries;
    }

    public TTraceState setEntries(Map<String, String> var1) {
        this.entries = var1;
        return this;
    }

    public void unsetEntries() {
        this.entries = null;
    }

    public boolean isSetEntries() {
        return this.entries != null;
    }

    public void setEntriesIsSet(boolean var1) {
        if (!var1) {
            this.entries = null;
        }

    }

    public void setFieldValue(TTraceState._Fields var1, Object var2) {
        switch(var1) {
            case ENTRIES:
                if (var2 == null) {
                    this.unsetEntries();
                } else {
                    this.setEntries((Map)var2);
                }
            default:
        }
    }

    public Object getFieldValue(TTraceState._Fields var1) {
        switch(var1) {
            case ENTRIES:
                return this.getEntries();
            default:
                throw new IllegalStateException();
        }
    }

    public boolean isSet(TTraceState._Fields var1) {
        if (var1 == null) {
            throw new IllegalArgumentException();
        } else {
            switch(var1) {
                case ENTRIES:
                    return this.isSetEntries();
                default:
                    throw new IllegalStateException();
            }
        }
    }

    public boolean equals(Object var1) {
        if (var1 == null) {
            return false;
        } else {
            return var1 instanceof TTraceState ? this.equals((TTraceState)var1) : false;
        }
    }

    public boolean equals(TTraceState var1) {
        if (var1 == null) {
            return false;
        } else {
            boolean var2 = this.isSetEntries();
            boolean var3 = var1.isSetEntries();
            if (var2 || var3) {
                if (!var2 || !var3) {
                    return false;
                }

                if (!this.entries.equals(var1.entries)) {
                    return false;
                }
            }

            return true;
        }
    }

    public int hashCode() {
        return 0;
    }

    public int compareTo(TTraceState var1) {
        if (!this.getClass().equals(var1.getClass())) {
            return this.getClass().getName().compareTo(var1.getClass().getName());
        } else {
            boolean var2 = false;
            int var4 = Boolean.valueOf(this.isSetEntries()).compareTo(var1.isSetEntries());
            if (var4 != 0) {
                return var4;
            } else {
                if (this.isSetEntries()) {
                    var4 = TBaseHelper.compareTo(this.entries, var1.entries);
                    if (var4 != 0) {
                        return var4;
                    }
                }

                return 0;
            }
        }
    }

    public TTraceState._Fields fieldForId(int var1) {
        return TTraceState._Fields.findByThriftId(var1);
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
                    if (var2.type != 13) {
                        TProtocolUtil.skip(var1, var2.type);
                        break;
                    }

                    TMap var3 = var1.readMapBegin();
                    this.entries = new HashMap(2 * var3.size);

                    for(int var4 = 0; var4 < var3.size; ++var4) {
                        String var5 = var1.readString();
                        String var6 = var1.readString();
                        this.entries.put(var5, var6);
                    }

                    var1.readMapEnd();
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
        if (this.entries != null && this.isSetEntries()) {
            var1.writeFieldBegin(ENTRIES_FIELD_DESC);
            var1.writeMapBegin(new TMap((byte)11, (byte)11, this.entries.size()));
            Iterator var2 = this.entries.entrySet().iterator();

            while(var2.hasNext()) {
                Entry var3 = (Entry)var2.next();
                var1.writeString((String)var3.getKey());
                var1.writeString((String)var3.getValue());
            }

            var1.writeMapEnd();
            var1.writeFieldEnd();
        }

        var1.writeFieldStop();
        var1.writeStructEnd();
    }

    public String toString() {
        StringBuilder var1 = new StringBuilder("TTraceState(");
        boolean var2 = true;
        if (this.isSetEntries()) {
            var1.append("entries:");
            if (this.entries == null) {
                var1.append("null");
            } else {
                var1.append(this.entries);
            }

            var2 = false;
        }

        var1.append(")");
        return var1.toString();
    }

    public void validate() throws TException {
    }

    static {
        EnumMap var0 = new EnumMap(TTraceState._Fields.class);
        var0.put(TTraceState._Fields.ENTRIES, new FieldMetaData("entries", (byte)2, new MapMetaData((byte)13, new FieldValueMetaData((byte)11), new FieldValueMetaData((byte)11))));
        metaDataMap = Collections.unmodifiableMap(var0);
        FieldMetaData.addStructMetaDataMap(TTraceState.class, metaDataMap);
    }

    public static enum _Fields implements TFieldIdEnum {
        ENTRIES((short)1, "entries");

        private static final Map<String, TTraceState._Fields> byName = new HashMap();
        private final short _thriftId;
        private final String _fieldName;

        public static TTraceState._Fields findByThriftId(int var0) {
            switch(var0) {
                case 1:
                    return ENTRIES;
                default:
                    return null;
            }
        }

        public static TTraceState._Fields findByThriftIdOrThrow(int var0) {
            TTraceState._Fields var1 = findByThriftId(var0);
            if (var1 == null) {
                throw new IllegalArgumentException("Field " + var0 + " doesn't exist!");
            } else {
                return var1;
            }
        }

        public static TTraceState._Fields findByName(String var0) {
            return (TTraceState._Fields)byName.get(var0);
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
            Iterator var0 = EnumSet.allOf(TTraceState._Fields.class).iterator();

            while(var0.hasNext()) {
                TTraceState._Fields var1 = (TTraceState._Fields)var0.next();
                byName.put(var1.getFieldName(), var1);
            }

        }
    }
}
