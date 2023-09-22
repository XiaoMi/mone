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

public class TExtra implements TBase<TExtra, TExtra._Fields>, Serializable, Cloneable {
    private static final TStruct STRUCT_DESC = new TStruct("TExtra");
    private static final TField IP_FIELD_DESC = new TField("ip", (byte)11, (short)1);
    private static final TField HOSTNAME_FIELD_DESC = new TField("hostname", (byte)11, (short)2);
    private static final TField SERVICE_NAME_FIELD_DESC = new TField("serviceName", (byte)11, (short)3);
    public String ip;
    public String hostname;
    public String serviceName;
    public static final Map<TExtra._Fields, FieldMetaData> metaDataMap;

    public TExtra() {
    }

    public TExtra(TExtra var1) {
        if (var1.isSetIp()) {
            this.ip = var1.ip;
        }

        if (var1.isSetHostname()) {
            this.hostname = var1.hostname;
        }

        if (var1.isSetServiceName()) {
            this.serviceName = var1.serviceName;
        }

    }

    public TExtra deepCopy() {
        return new TExtra(this);
    }

    public void clear() {
        this.ip = null;
        this.hostname = null;
        this.serviceName = null;
    }

    public String getIp() {
        return this.ip;
    }

    public TExtra setIp(String var1) {
        this.ip = var1;
        return this;
    }

    public void unsetIp() {
        this.ip = null;
    }

    public boolean isSetIp() {
        return this.ip != null;
    }

    public void setIpIsSet(boolean var1) {
        if (!var1) {
            this.ip = null;
        }

    }

    public String getHostname() {
        return this.hostname;
    }

    public TExtra setHostname(String var1) {
        this.hostname = var1;
        return this;
    }

    public void unsetHostname() {
        this.hostname = null;
    }

    public boolean isSetHostname() {
        return this.hostname != null;
    }

    public void setHostnameIsSet(boolean var1) {
        if (!var1) {
            this.hostname = null;
        }

    }

    public String getServiceName() {
        return this.serviceName;
    }

    public TExtra setServiceName(String var1) {
        this.serviceName = var1;
        return this;
    }

    public void unsetServiceName() {
        this.serviceName = null;
    }

    public boolean isSetServiceName() {
        return this.serviceName != null;
    }

    public void setServiceNameIsSet(boolean var1) {
        if (!var1) {
            this.serviceName = null;
        }

    }

    public void setFieldValue(TExtra._Fields var1, Object var2) {
        switch(var1) {
            case IP:
                if (var2 == null) {
                    this.unsetIp();
                } else {
                    this.setIp((String)var2);
                }
                break;
            case HOSTNAME:
                if (var2 == null) {
                    this.unsetHostname();
                } else {
                    this.setHostname((String)var2);
                }
                break;
            case SERVICE_NAME:
                if (var2 == null) {
                    this.unsetServiceName();
                } else {
                    this.setServiceName((String)var2);
                }
        }

    }

    public Object getFieldValue(TExtra._Fields var1) {
        switch(var1) {
            case IP:
                return this.getIp();
            case HOSTNAME:
                return this.getHostname();
            case SERVICE_NAME:
                return this.getServiceName();
            default:
                throw new IllegalStateException();
        }
    }

    public boolean isSet(TExtra._Fields var1) {
        if (var1 == null) {
            throw new IllegalArgumentException();
        } else {
            switch(var1) {
                case IP:
                    return this.isSetIp();
                case HOSTNAME:
                    return this.isSetHostname();
                case SERVICE_NAME:
                    return this.isSetServiceName();
                default:
                    throw new IllegalStateException();
            }
        }
    }

    public boolean equals(Object var1) {
        if (var1 == null) {
            return false;
        } else {
            return var1 instanceof TExtra ? this.equals((TExtra)var1) : false;
        }
    }

    public boolean equals(TExtra var1) {
        if (var1 == null) {
            return false;
        } else {
            boolean var2 = this.isSetIp();
            boolean var3 = var1.isSetIp();
            if (var2 || var3) {
                if (!var2 || !var3) {
                    return false;
                }

                if (!this.ip.equals(var1.ip)) {
                    return false;
                }
            }

            boolean var4 = this.isSetHostname();
            boolean var5 = var1.isSetHostname();
            if (var4 || var5) {
                if (!var4 || !var5) {
                    return false;
                }

                if (!this.hostname.equals(var1.hostname)) {
                    return false;
                }
            }

            boolean var6 = this.isSetServiceName();
            boolean var7 = var1.isSetServiceName();
            if (var6 || var7) {
                if (!var6 || !var7) {
                    return false;
                }

                if (!this.serviceName.equals(var1.serviceName)) {
                    return false;
                }
            }

            return true;
        }
    }

    public int hashCode() {
        return 0;
    }

    public int compareTo(TExtra var1) {
        if (!this.getClass().equals(var1.getClass())) {
            return this.getClass().getName().compareTo(var1.getClass().getName());
        } else {
            boolean var2 = false;
            int var4 = Boolean.valueOf(this.isSetIp()).compareTo(var1.isSetIp());
            if (var4 != 0) {
                return var4;
            } else {
                if (this.isSetIp()) {
                    var4 = TBaseHelper.compareTo(this.ip, var1.ip);
                    if (var4 != 0) {
                        return var4;
                    }
                }

                var4 = Boolean.valueOf(this.isSetHostname()).compareTo(var1.isSetHostname());
                if (var4 != 0) {
                    return var4;
                } else {
                    if (this.isSetHostname()) {
                        var4 = TBaseHelper.compareTo(this.hostname, var1.hostname);
                        if (var4 != 0) {
                            return var4;
                        }
                    }

                    var4 = Boolean.valueOf(this.isSetServiceName()).compareTo(var1.isSetServiceName());
                    if (var4 != 0) {
                        return var4;
                    } else {
                        if (this.isSetServiceName()) {
                            var4 = TBaseHelper.compareTo(this.serviceName, var1.serviceName);
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

    public TExtra._Fields fieldForId(int var1) {
        return TExtra._Fields.findByThriftId(var1);
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
                        this.ip = var1.readString();
                    } else {
                        TProtocolUtil.skip(var1, var2.type);
                    }
                    break;
                case 2:
                    if (var2.type == 11) {
                        this.hostname = var1.readString();
                    } else {
                        TProtocolUtil.skip(var1, var2.type);
                    }
                    break;
                case 3:
                    if (var2.type == 11) {
                        this.serviceName = var1.readString();
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
        if (this.ip != null && this.isSetIp()) {
            var1.writeFieldBegin(IP_FIELD_DESC);
            var1.writeString(this.ip);
            var1.writeFieldEnd();
        }

        if (this.hostname != null && this.isSetHostname()) {
            var1.writeFieldBegin(HOSTNAME_FIELD_DESC);
            var1.writeString(this.hostname);
            var1.writeFieldEnd();
        }

        if (this.serviceName != null && this.isSetServiceName()) {
            var1.writeFieldBegin(SERVICE_NAME_FIELD_DESC);
            var1.writeString(this.serviceName);
            var1.writeFieldEnd();
        }

        var1.writeFieldStop();
        var1.writeStructEnd();
    }

    public String toString() {
        StringBuilder var1 = new StringBuilder("TExtra(");
        boolean var2 = true;
        if (this.isSetIp()) {
            var1.append("ip:");
            if (this.ip == null) {
                var1.append("null");
            } else {
                var1.append(this.ip);
            }

            var2 = false;
        }

        if (this.isSetHostname()) {
            if (!var2) {
                var1.append(", ");
            }

            var1.append("hostname:");
            if (this.hostname == null) {
                var1.append("null");
            } else {
                var1.append(this.hostname);
            }

            var2 = false;
        }

        if (this.isSetServiceName()) {
            if (!var2) {
                var1.append(", ");
            }

            var1.append("serviceName:");
            if (this.serviceName == null) {
                var1.append("null");
            } else {
                var1.append(this.serviceName);
            }

            var2 = false;
        }

        var1.append(")");
        return var1.toString();
    }

    public void validate() throws TException {
    }

    static {
        EnumMap var0 = new EnumMap(TExtra._Fields.class);
        var0.put(TExtra._Fields.IP, new FieldMetaData("ip", (byte)2, new FieldValueMetaData((byte)11)));
        var0.put(TExtra._Fields.HOSTNAME, new FieldMetaData("hostname", (byte)2, new FieldValueMetaData((byte)11)));
        var0.put(TExtra._Fields.SERVICE_NAME, new FieldMetaData("serviceName", (byte)2, new FieldValueMetaData((byte)11)));
        metaDataMap = Collections.unmodifiableMap(var0);
        FieldMetaData.addStructMetaDataMap(TExtra.class, metaDataMap);
    }

    public static enum _Fields implements TFieldIdEnum {
        IP((short)1, "ip"),
        HOSTNAME((short)2, "hostname"),
        SERVICE_NAME((short)3, "serviceName");

        private static final Map<String, TExtra._Fields> byName = new HashMap();
        private final short _thriftId;
        private final String _fieldName;

        public static TExtra._Fields findByThriftId(int var0) {
            switch(var0) {
                case 1:
                    return IP;
                case 2:
                    return HOSTNAME;
                case 3:
                    return SERVICE_NAME;
                default:
                    return null;
            }
        }

        public static TExtra._Fields findByThriftIdOrThrow(int var0) {
            TExtra._Fields var1 = findByThriftId(var0);
            if (var1 == null) {
                throw new IllegalArgumentException("Field " + var0 + " doesn't exist!");
            } else {
                return var1;
            }
        }

        public static TExtra._Fields findByName(String var0) {
            return (TExtra._Fields)byName.get(var0);
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
            Iterator var0 = EnumSet.allOf(TExtra._Fields.class).iterator();

            while(var0.hasNext()) {
                TExtra._Fields var1 = (TExtra._Fields)var0.next();
                byName.put(var1.getFieldName(), var1);
            }

        }
    }
}
