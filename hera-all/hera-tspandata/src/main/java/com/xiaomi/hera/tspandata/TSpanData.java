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
import org.apache.thrift.meta_data.EnumMetaData;
import org.apache.thrift.meta_data.FieldMetaData;
import org.apache.thrift.meta_data.FieldValueMetaData;
import org.apache.thrift.meta_data.ListMetaData;
import org.apache.thrift.meta_data.StructMetaData;
import org.apache.thrift.protocol.TField;
import org.apache.thrift.protocol.TList;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TProtocolUtil;
import org.apache.thrift.protocol.TStruct;

public class TSpanData implements TBase<TSpanData, TSpanData._Fields>, Serializable, Cloneable {
    private static final TStruct STRUCT_DESC = new TStruct("TSpanData");
    private static final TField TRACE_ID_FIELD_DESC = new TField("traceId", (byte)11, (short)1);
    private static final TField SPAN_ID_FIELD_DESC = new TField("spanId", (byte)11, (short)2);
    private static final TField SAMPLED_FIELD_DESC = new TField("sampled", (byte)2, (short)3);
    private static final TField TRACE_STATE_FIELD_DESC = new TField("traceState", (byte)12, (short)4);
    private static final TField PARENT_SPAN_CONTEXT_FIELD_DESC = new TField("parentSpanContext", (byte)12, (short)5);
    private static final TField RESOUCE_FIELD_DESC = new TField("resouce", (byte)12, (short)6);
    private static final TField INSTRUMENTATION_LIBRARY_INFO_FIELD_DESC = new TField("instrumentationLibraryInfo", (byte)12, (short)7);
    private static final TField NAME_FIELD_DESC = new TField("name", (byte)11, (short)8);
    private static final TField KIND_FIELD_DESC = new TField("kind", (byte)8, (short)9);
    private static final TField START_EPOCH_NANOS_FIELD_DESC = new TField("startEpochNanos", (byte)10, (short)10);
    private static final TField ATTRIBUTES_FIELD_DESC = new TField("attributes", (byte)12, (short)11);
    private static final TField EVENTS_FIELD_DESC = new TField("events", (byte)15, (short)12);
    private static final TField LINKS_FIELD_DESC = new TField("links", (byte)15, (short)13);
    private static final TField STATUS_FIELD_DESC = new TField("status", (byte)8, (short)14);
    private static final TField END_EPOCH_NANOS_FIELD_DESC = new TField("endEpochNanos", (byte)10, (short)15);
    private static final TField ENDED_FIELD_DESC = new TField("ended", (byte)2, (short)16);
    private static final TField TOTAL_RECORDED_EVENTS_FIELD_DESC = new TField("totalRecordedEvents", (byte)8, (short)17);
    private static final TField TOTAL_RECORDED_LINKS_FIELD_DESC = new TField("totalRecordedLinks", (byte)8, (short)18);
    private static final TField TOTAL_ATTRIBUTE_COUNT_FIELD_DESC = new TField("totalAttributeCount", (byte)8, (short)19);
    private static final TField EXTRA_FIELD_DESC = new TField("extra", (byte)12, (short)20);
    public String traceId;
    public String spanId;
    public boolean sampled;
    public TTraceState traceState;
    public TSpanContext parentSpanContext;
    public TResource resouce;
    public TInstrumentationLibraryInfo instrumentationLibraryInfo;
    public String name;
    public TKind kind;
    public long startEpochNanos;
    public TAttributes attributes;
    public List<TEvent> events;
    public List<TLink> links;
    public TStatus status;
    public long endEpochNanos;
    public boolean ended;
    public int totalRecordedEvents;
    public int totalRecordedLinks;
    public int totalAttributeCount;
    public TExtra extra;
    private static final int __SAMPLED_ISSET_ID = 0;
    private static final int __STARTEPOCHNANOS_ISSET_ID = 1;
    private static final int __ENDEPOCHNANOS_ISSET_ID = 2;
    private static final int __ENDED_ISSET_ID = 3;
    private static final int __TOTALRECORDEDEVENTS_ISSET_ID = 4;
    private static final int __TOTALRECORDEDLINKS_ISSET_ID = 5;
    private static final int __TOTALATTRIBUTECOUNT_ISSET_ID = 6;
    private BitSet __isset_bit_vector = new BitSet(7);
    public static final Map<TSpanData._Fields, FieldMetaData> metaDataMap;

    public TSpanData() {
    }

    public TSpanData(TSpanData var1) {
        this.__isset_bit_vector.clear();
        this.__isset_bit_vector.or(var1.__isset_bit_vector);
        if (var1.isSetTraceId()) {
            this.traceId = var1.traceId;
        }

        if (var1.isSetSpanId()) {
            this.spanId = var1.spanId;
        }

        this.sampled = var1.sampled;
        if (var1.isSetTraceState()) {
            this.traceState = new TTraceState(var1.traceState);
        }

        if (var1.isSetParentSpanContext()) {
            this.parentSpanContext = new TSpanContext(var1.parentSpanContext);
        }

        if (var1.isSetResouce()) {
            this.resouce = new TResource(var1.resouce);
        }

        if (var1.isSetInstrumentationLibraryInfo()) {
            this.instrumentationLibraryInfo = new TInstrumentationLibraryInfo(var1.instrumentationLibraryInfo);
        }

        if (var1.isSetName()) {
            this.name = var1.name;
        }

        if (var1.isSetKind()) {
            this.kind = var1.kind;
        }

        this.startEpochNanos = var1.startEpochNanos;
        if (var1.isSetAttributes()) {
            this.attributes = new TAttributes(var1.attributes);
        }

        ArrayList var2;
        Iterator var3;
        if (var1.isSetEvents()) {
            var2 = new ArrayList();
            var3 = var1.events.iterator();

            while(var3.hasNext()) {
                TEvent var4 = (TEvent)var3.next();
                var2.add(new TEvent(var4));
            }

            this.events = var2;
        }

        if (var1.isSetLinks()) {
            var2 = new ArrayList();
            var3 = var1.links.iterator();

            while(var3.hasNext()) {
                TLink var5 = (TLink)var3.next();
                var2.add(new TLink(var5));
            }

            this.links = var2;
        }

        if (var1.isSetStatus()) {
            this.status = var1.status;
        }

        this.endEpochNanos = var1.endEpochNanos;
        this.ended = var1.ended;
        this.totalRecordedEvents = var1.totalRecordedEvents;
        this.totalRecordedLinks = var1.totalRecordedLinks;
        this.totalAttributeCount = var1.totalAttributeCount;
        if (var1.isSetExtra()) {
            this.extra = new TExtra(var1.extra);
        }

    }

    public TSpanData deepCopy() {
        return new TSpanData(this);
    }

    public void clear() {
        this.traceId = null;
        this.spanId = null;
        this.setSampledIsSet(false);
        this.sampled = false;
        this.traceState = null;
        this.parentSpanContext = null;
        this.resouce = null;
        this.instrumentationLibraryInfo = null;
        this.name = null;
        this.kind = null;
        this.setStartEpochNanosIsSet(false);
        this.startEpochNanos = 0L;
        this.attributes = null;
        this.events = null;
        this.links = null;
        this.status = null;
        this.setEndEpochNanosIsSet(false);
        this.endEpochNanos = 0L;
        this.setEndedIsSet(false);
        this.ended = false;
        this.setTotalRecordedEventsIsSet(false);
        this.totalRecordedEvents = 0;
        this.setTotalRecordedLinksIsSet(false);
        this.totalRecordedLinks = 0;
        this.setTotalAttributeCountIsSet(false);
        this.totalAttributeCount = 0;
        this.extra = null;
    }

    public String getTraceId() {
        return this.traceId;
    }

    public TSpanData setTraceId(String var1) {
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

    public TSpanData setSpanId(String var1) {
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

    public boolean isSampled() {
        return this.sampled;
    }

    public TSpanData setSampled(boolean var1) {
        this.sampled = var1;
        this.setSampledIsSet(true);
        return this;
    }

    public void unsetSampled() {
        this.__isset_bit_vector.clear(0);
    }

    public boolean isSetSampled() {
        return this.__isset_bit_vector.get(0);
    }

    public void setSampledIsSet(boolean var1) {
        this.__isset_bit_vector.set(0, var1);
    }

    public TTraceState getTraceState() {
        return this.traceState;
    }

    public TSpanData setTraceState(TTraceState var1) {
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

    public TSpanContext getParentSpanContext() {
        return this.parentSpanContext;
    }

    public TSpanData setParentSpanContext(TSpanContext var1) {
        this.parentSpanContext = var1;
        return this;
    }

    public void unsetParentSpanContext() {
        this.parentSpanContext = null;
    }

    public boolean isSetParentSpanContext() {
        return this.parentSpanContext != null;
    }

    public void setParentSpanContextIsSet(boolean var1) {
        if (!var1) {
            this.parentSpanContext = null;
        }

    }

    public TResource getResouce() {
        return this.resouce;
    }

    public TSpanData setResouce(TResource var1) {
        this.resouce = var1;
        return this;
    }

    public void unsetResouce() {
        this.resouce = null;
    }

    public boolean isSetResouce() {
        return this.resouce != null;
    }

    public void setResouceIsSet(boolean var1) {
        if (!var1) {
            this.resouce = null;
        }

    }

    public TInstrumentationLibraryInfo getInstrumentationLibraryInfo() {
        return this.instrumentationLibraryInfo;
    }

    public TSpanData setInstrumentationLibraryInfo(TInstrumentationLibraryInfo var1) {
        this.instrumentationLibraryInfo = var1;
        return this;
    }

    public void unsetInstrumentationLibraryInfo() {
        this.instrumentationLibraryInfo = null;
    }

    public boolean isSetInstrumentationLibraryInfo() {
        return this.instrumentationLibraryInfo != null;
    }

    public void setInstrumentationLibraryInfoIsSet(boolean var1) {
        if (!var1) {
            this.instrumentationLibraryInfo = null;
        }

    }

    public String getName() {
        return this.name;
    }

    public TSpanData setName(String var1) {
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

    public TKind getKind() {
        return this.kind;
    }

    public TSpanData setKind(TKind var1) {
        this.kind = var1;
        return this;
    }

    public void unsetKind() {
        this.kind = null;
    }

    public boolean isSetKind() {
        return this.kind != null;
    }

    public void setKindIsSet(boolean var1) {
        if (!var1) {
            this.kind = null;
        }

    }

    public long getStartEpochNanos() {
        return this.startEpochNanos;
    }

    public TSpanData setStartEpochNanos(long var1) {
        this.startEpochNanos = var1;
        this.setStartEpochNanosIsSet(true);
        return this;
    }

    public void unsetStartEpochNanos() {
        this.__isset_bit_vector.clear(1);
    }

    public boolean isSetStartEpochNanos() {
        return this.__isset_bit_vector.get(1);
    }

    public void setStartEpochNanosIsSet(boolean var1) {
        this.__isset_bit_vector.set(1, var1);
    }

    public TAttributes getAttributes() {
        return this.attributes;
    }

    public TSpanData setAttributes(TAttributes var1) {
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

    public int getEventsSize() {
        return this.events == null ? 0 : this.events.size();
    }

    public Iterator<TEvent> getEventsIterator() {
        return this.events == null ? null : this.events.iterator();
    }

    public void addToEvents(TEvent var1) {
        if (this.events == null) {
            this.events = new ArrayList();
        }

        this.events.add(var1);
    }

    public List<TEvent> getEvents() {
        return this.events;
    }

    public TSpanData setEvents(List<TEvent> var1) {
        this.events = var1;
        return this;
    }

    public void unsetEvents() {
        this.events = null;
    }

    public boolean isSetEvents() {
        return this.events != null;
    }

    public void setEventsIsSet(boolean var1) {
        if (!var1) {
            this.events = null;
        }

    }

    public int getLinksSize() {
        return this.links == null ? 0 : this.links.size();
    }

    public Iterator<TLink> getLinksIterator() {
        return this.links == null ? null : this.links.iterator();
    }

    public void addToLinks(TLink var1) {
        if (this.links == null) {
            this.links = new ArrayList();
        }

        this.links.add(var1);
    }

    public List<TLink> getLinks() {
        return this.links;
    }

    public TSpanData setLinks(List<TLink> var1) {
        this.links = var1;
        return this;
    }

    public void unsetLinks() {
        this.links = null;
    }

    public boolean isSetLinks() {
        return this.links != null;
    }

    public void setLinksIsSet(boolean var1) {
        if (!var1) {
            this.links = null;
        }

    }

    public TStatus getStatus() {
        return this.status;
    }

    public TSpanData setStatus(TStatus var1) {
        this.status = var1;
        return this;
    }

    public void unsetStatus() {
        this.status = null;
    }

    public boolean isSetStatus() {
        return this.status != null;
    }

    public void setStatusIsSet(boolean var1) {
        if (!var1) {
            this.status = null;
        }

    }

    public long getEndEpochNanos() {
        return this.endEpochNanos;
    }

    public TSpanData setEndEpochNanos(long var1) {
        this.endEpochNanos = var1;
        this.setEndEpochNanosIsSet(true);
        return this;
    }

    public void unsetEndEpochNanos() {
        this.__isset_bit_vector.clear(2);
    }

    public boolean isSetEndEpochNanos() {
        return this.__isset_bit_vector.get(2);
    }

    public void setEndEpochNanosIsSet(boolean var1) {
        this.__isset_bit_vector.set(2, var1);
    }

    public boolean isEnded() {
        return this.ended;
    }

    public TSpanData setEnded(boolean var1) {
        this.ended = var1;
        this.setEndedIsSet(true);
        return this;
    }

    public void unsetEnded() {
        this.__isset_bit_vector.clear(3);
    }

    public boolean isSetEnded() {
        return this.__isset_bit_vector.get(3);
    }

    public void setEndedIsSet(boolean var1) {
        this.__isset_bit_vector.set(3, var1);
    }

    public int getTotalRecordedEvents() {
        return this.totalRecordedEvents;
    }

    public TSpanData setTotalRecordedEvents(int var1) {
        this.totalRecordedEvents = var1;
        this.setTotalRecordedEventsIsSet(true);
        return this;
    }

    public void unsetTotalRecordedEvents() {
        this.__isset_bit_vector.clear(4);
    }

    public boolean isSetTotalRecordedEvents() {
        return this.__isset_bit_vector.get(4);
    }

    public void setTotalRecordedEventsIsSet(boolean var1) {
        this.__isset_bit_vector.set(4, var1);
    }

    public int getTotalRecordedLinks() {
        return this.totalRecordedLinks;
    }

    public TSpanData setTotalRecordedLinks(int var1) {
        this.totalRecordedLinks = var1;
        this.setTotalRecordedLinksIsSet(true);
        return this;
    }

    public void unsetTotalRecordedLinks() {
        this.__isset_bit_vector.clear(5);
    }

    public boolean isSetTotalRecordedLinks() {
        return this.__isset_bit_vector.get(5);
    }

    public void setTotalRecordedLinksIsSet(boolean var1) {
        this.__isset_bit_vector.set(5, var1);
    }

    public int getTotalAttributeCount() {
        return this.totalAttributeCount;
    }

    public TSpanData setTotalAttributeCount(int var1) {
        this.totalAttributeCount = var1;
        this.setTotalAttributeCountIsSet(true);
        return this;
    }

    public void unsetTotalAttributeCount() {
        this.__isset_bit_vector.clear(6);
    }

    public boolean isSetTotalAttributeCount() {
        return this.__isset_bit_vector.get(6);
    }

    public void setTotalAttributeCountIsSet(boolean var1) {
        this.__isset_bit_vector.set(6, var1);
    }

    public TExtra getExtra() {
        return this.extra;
    }

    public TSpanData setExtra(TExtra var1) {
        this.extra = var1;
        return this;
    }

    public void unsetExtra() {
        this.extra = null;
    }

    public boolean isSetExtra() {
        return this.extra != null;
    }

    public void setExtraIsSet(boolean var1) {
        if (!var1) {
            this.extra = null;
        }

    }

    public void setFieldValue(TSpanData._Fields var1, Object var2) {
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
            case SAMPLED:
                if (var2 == null) {
                    this.unsetSampled();
                } else {
                    this.setSampled((Boolean)var2);
                }
                break;
            case TRACE_STATE:
                if (var2 == null) {
                    this.unsetTraceState();
                } else {
                    this.setTraceState((TTraceState)var2);
                }
                break;
            case PARENT_SPAN_CONTEXT:
                if (var2 == null) {
                    this.unsetParentSpanContext();
                } else {
                    this.setParentSpanContext((TSpanContext)var2);
                }
                break;
            case RESOUCE:
                if (var2 == null) {
                    this.unsetResouce();
                } else {
                    this.setResouce((TResource)var2);
                }
                break;
            case INSTRUMENTATION_LIBRARY_INFO:
                if (var2 == null) {
                    this.unsetInstrumentationLibraryInfo();
                } else {
                    this.setInstrumentationLibraryInfo((TInstrumentationLibraryInfo)var2);
                }
                break;
            case NAME:
                if (var2 == null) {
                    this.unsetName();
                } else {
                    this.setName((String)var2);
                }
                break;
            case KIND:
                if (var2 == null) {
                    this.unsetKind();
                } else {
                    this.setKind((TKind)var2);
                }
                break;
            case START_EPOCH_NANOS:
                if (var2 == null) {
                    this.unsetStartEpochNanos();
                } else {
                    this.setStartEpochNanos((Long)var2);
                }
                break;
            case ATTRIBUTES:
                if (var2 == null) {
                    this.unsetAttributes();
                } else {
                    this.setAttributes((TAttributes)var2);
                }
                break;
            case EVENTS:
                if (var2 == null) {
                    this.unsetEvents();
                } else {
                    this.setEvents((List)var2);
                }
                break;
            case LINKS:
                if (var2 == null) {
                    this.unsetLinks();
                } else {
                    this.setLinks((List)var2);
                }
                break;
            case STATUS:
                if (var2 == null) {
                    this.unsetStatus();
                } else {
                    this.setStatus((TStatus)var2);
                }
                break;
            case END_EPOCH_NANOS:
                if (var2 == null) {
                    this.unsetEndEpochNanos();
                } else {
                    this.setEndEpochNanos((Long)var2);
                }
                break;
            case ENDED:
                if (var2 == null) {
                    this.unsetEnded();
                } else {
                    this.setEnded((Boolean)var2);
                }
                break;
            case TOTAL_RECORDED_EVENTS:
                if (var2 == null) {
                    this.unsetTotalRecordedEvents();
                } else {
                    this.setTotalRecordedEvents((Integer)var2);
                }
                break;
            case TOTAL_RECORDED_LINKS:
                if (var2 == null) {
                    this.unsetTotalRecordedLinks();
                } else {
                    this.setTotalRecordedLinks((Integer)var2);
                }
                break;
            case TOTAL_ATTRIBUTE_COUNT:
                if (var2 == null) {
                    this.unsetTotalAttributeCount();
                } else {
                    this.setTotalAttributeCount((Integer)var2);
                }
                break;
            case EXTRA:
                if (var2 == null) {
                    this.unsetExtra();
                } else {
                    this.setExtra((TExtra)var2);
                }
        }

    }

    public Object getFieldValue(TSpanData._Fields var1) {
        switch(var1) {
            case TRACE_ID:
                return this.getTraceId();
            case SPAN_ID:
                return this.getSpanId();
            case SAMPLED:
                return new Boolean(this.isSampled());
            case TRACE_STATE:
                return this.getTraceState();
            case PARENT_SPAN_CONTEXT:
                return this.getParentSpanContext();
            case RESOUCE:
                return this.getResouce();
            case INSTRUMENTATION_LIBRARY_INFO:
                return this.getInstrumentationLibraryInfo();
            case NAME:
                return this.getName();
            case KIND:
                return this.getKind();
            case START_EPOCH_NANOS:
                return new Long(this.getStartEpochNanos());
            case ATTRIBUTES:
                return this.getAttributes();
            case EVENTS:
                return this.getEvents();
            case LINKS:
                return this.getLinks();
            case STATUS:
                return this.getStatus();
            case END_EPOCH_NANOS:
                return new Long(this.getEndEpochNanos());
            case ENDED:
                return new Boolean(this.isEnded());
            case TOTAL_RECORDED_EVENTS:
                return new Integer(this.getTotalRecordedEvents());
            case TOTAL_RECORDED_LINKS:
                return new Integer(this.getTotalRecordedLinks());
            case TOTAL_ATTRIBUTE_COUNT:
                return new Integer(this.getTotalAttributeCount());
            case EXTRA:
                return this.getExtra();
            default:
                throw new IllegalStateException();
        }
    }

    public boolean isSet(TSpanData._Fields var1) {
        if (var1 == null) {
            throw new IllegalArgumentException();
        } else {
            switch(var1) {
                case TRACE_ID:
                    return this.isSetTraceId();
                case SPAN_ID:
                    return this.isSetSpanId();
                case SAMPLED:
                    return this.isSetSampled();
                case TRACE_STATE:
                    return this.isSetTraceState();
                case PARENT_SPAN_CONTEXT:
                    return this.isSetParentSpanContext();
                case RESOUCE:
                    return this.isSetResouce();
                case INSTRUMENTATION_LIBRARY_INFO:
                    return this.isSetInstrumentationLibraryInfo();
                case NAME:
                    return this.isSetName();
                case KIND:
                    return this.isSetKind();
                case START_EPOCH_NANOS:
                    return this.isSetStartEpochNanos();
                case ATTRIBUTES:
                    return this.isSetAttributes();
                case EVENTS:
                    return this.isSetEvents();
                case LINKS:
                    return this.isSetLinks();
                case STATUS:
                    return this.isSetStatus();
                case END_EPOCH_NANOS:
                    return this.isSetEndEpochNanos();
                case ENDED:
                    return this.isSetEnded();
                case TOTAL_RECORDED_EVENTS:
                    return this.isSetTotalRecordedEvents();
                case TOTAL_RECORDED_LINKS:
                    return this.isSetTotalRecordedLinks();
                case TOTAL_ATTRIBUTE_COUNT:
                    return this.isSetTotalAttributeCount();
                case EXTRA:
                    return this.isSetExtra();
                default:
                    throw new IllegalStateException();
            }
        }
    }

    public boolean equals(Object var1) {
        if (var1 == null) {
            return false;
        } else {
            return var1 instanceof TSpanData ? this.equals((TSpanData)var1) : false;
        }
    }

    public boolean equals(TSpanData var1) {
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

            boolean var6 = this.isSetSampled();
            boolean var7 = var1.isSetSampled();
            if (var6 || var7) {
                if (!var6 || !var7) {
                    return false;
                }

                if (this.sampled != var1.sampled) {
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

            boolean var10 = this.isSetParentSpanContext();
            boolean var11 = var1.isSetParentSpanContext();
            if (var10 || var11) {
                if (!var10 || !var11) {
                    return false;
                }

                if (!this.parentSpanContext.equals(var1.parentSpanContext)) {
                    return false;
                }
            }

            boolean var12 = this.isSetResouce();
            boolean var13 = var1.isSetResouce();
            if (var12 || var13) {
                if (!var12 || !var13) {
                    return false;
                }

                if (!this.resouce.equals(var1.resouce)) {
                    return false;
                }
            }

            boolean var14 = this.isSetInstrumentationLibraryInfo();
            boolean var15 = var1.isSetInstrumentationLibraryInfo();
            if (var14 || var15) {
                if (!var14 || !var15) {
                    return false;
                }

                if (!this.instrumentationLibraryInfo.equals(var1.instrumentationLibraryInfo)) {
                    return false;
                }
            }

            boolean var16 = this.isSetName();
            boolean var17 = var1.isSetName();
            if (var16 || var17) {
                if (!var16 || !var17) {
                    return false;
                }

                if (!this.name.equals(var1.name)) {
                    return false;
                }
            }

            boolean var18 = this.isSetKind();
            boolean var19 = var1.isSetKind();
            if (var18 || var19) {
                if (!var18 || !var19) {
                    return false;
                }

                if (!this.kind.equals(var1.kind)) {
                    return false;
                }
            }

            boolean var20 = this.isSetStartEpochNanos();
            boolean var21 = var1.isSetStartEpochNanos();
            if (var20 || var21) {
                if (!var20 || !var21) {
                    return false;
                }

                if (this.startEpochNanos != var1.startEpochNanos) {
                    return false;
                }
            }

            boolean var22 = this.isSetAttributes();
            boolean var23 = var1.isSetAttributes();
            if (var22 || var23) {
                if (!var22 || !var23) {
                    return false;
                }

                if (!this.attributes.equals(var1.attributes)) {
                    return false;
                }
            }

            boolean var24 = this.isSetEvents();
            boolean var25 = var1.isSetEvents();
            if (var24 || var25) {
                if (!var24 || !var25) {
                    return false;
                }

                if (!this.events.equals(var1.events)) {
                    return false;
                }
            }

            boolean var26 = this.isSetLinks();
            boolean var27 = var1.isSetLinks();
            if (var26 || var27) {
                if (!var26 || !var27) {
                    return false;
                }

                if (!this.links.equals(var1.links)) {
                    return false;
                }
            }

            boolean var28 = this.isSetStatus();
            boolean var29 = var1.isSetStatus();
            if (var28 || var29) {
                if (!var28 || !var29) {
                    return false;
                }

                if (!this.status.equals(var1.status)) {
                    return false;
                }
            }

            boolean var30 = this.isSetEndEpochNanos();
            boolean var31 = var1.isSetEndEpochNanos();
            if (var30 || var31) {
                if (!var30 || !var31) {
                    return false;
                }

                if (this.endEpochNanos != var1.endEpochNanos) {
                    return false;
                }
            }

            boolean var32 = this.isSetEnded();
            boolean var33 = var1.isSetEnded();
            if (var32 || var33) {
                if (!var32 || !var33) {
                    return false;
                }

                if (this.ended != var1.ended) {
                    return false;
                }
            }

            boolean var34 = this.isSetTotalRecordedEvents();
            boolean var35 = var1.isSetTotalRecordedEvents();
            if (var34 || var35) {
                if (!var34 || !var35) {
                    return false;
                }

                if (this.totalRecordedEvents != var1.totalRecordedEvents) {
                    return false;
                }
            }

            boolean var36 = this.isSetTotalRecordedLinks();
            boolean var37 = var1.isSetTotalRecordedLinks();
            if (var36 || var37) {
                if (!var36 || !var37) {
                    return false;
                }

                if (this.totalRecordedLinks != var1.totalRecordedLinks) {
                    return false;
                }
            }

            boolean var38 = this.isSetTotalAttributeCount();
            boolean var39 = var1.isSetTotalAttributeCount();
            if (var38 || var39) {
                if (!var38 || !var39) {
                    return false;
                }

                if (this.totalAttributeCount != var1.totalAttributeCount) {
                    return false;
                }
            }

            boolean var40 = this.isSetExtra();
            boolean var41 = var1.isSetExtra();
            if (var40 || var41) {
                if (!var40 || !var41) {
                    return false;
                }

                if (!this.extra.equals(var1.extra)) {
                    return false;
                }
            }

            return true;
        }
    }

    public int hashCode() {
        return 0;
    }

    public int compareTo(TSpanData var1) {
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

                    var4 = Boolean.valueOf(this.isSetSampled()).compareTo(var1.isSetSampled());
                    if (var4 != 0) {
                        return var4;
                    } else {
                        if (this.isSetSampled()) {
                            var4 = TBaseHelper.compareTo(this.sampled, var1.sampled);
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

                            var4 = Boolean.valueOf(this.isSetParentSpanContext()).compareTo(var1.isSetParentSpanContext());
                            if (var4 != 0) {
                                return var4;
                            } else {
                                if (this.isSetParentSpanContext()) {
                                    var4 = TBaseHelper.compareTo(this.parentSpanContext, var1.parentSpanContext);
                                    if (var4 != 0) {
                                        return var4;
                                    }
                                }

                                var4 = Boolean.valueOf(this.isSetResouce()).compareTo(var1.isSetResouce());
                                if (var4 != 0) {
                                    return var4;
                                } else {
                                    if (this.isSetResouce()) {
                                        var4 = TBaseHelper.compareTo(this.resouce, var1.resouce);
                                        if (var4 != 0) {
                                            return var4;
                                        }
                                    }

                                    var4 = Boolean.valueOf(this.isSetInstrumentationLibraryInfo()).compareTo(var1.isSetInstrumentationLibraryInfo());
                                    if (var4 != 0) {
                                        return var4;
                                    } else {
                                        if (this.isSetInstrumentationLibraryInfo()) {
                                            var4 = TBaseHelper.compareTo(this.instrumentationLibraryInfo, var1.instrumentationLibraryInfo);
                                            if (var4 != 0) {
                                                return var4;
                                            }
                                        }

                                        var4 = Boolean.valueOf(this.isSetName()).compareTo(var1.isSetName());
                                        if (var4 != 0) {
                                            return var4;
                                        } else {
                                            if (this.isSetName()) {
                                                var4 = TBaseHelper.compareTo(this.name, var1.name);
                                                if (var4 != 0) {
                                                    return var4;
                                                }
                                            }

                                            var4 = Boolean.valueOf(this.isSetKind()).compareTo(var1.isSetKind());
                                            if (var4 != 0) {
                                                return var4;
                                            } else {
                                                if (this.isSetKind()) {
                                                    var4 = TBaseHelper.compareTo(this.kind, var1.kind);
                                                    if (var4 != 0) {
                                                        return var4;
                                                    }
                                                }

                                                var4 = Boolean.valueOf(this.isSetStartEpochNanos()).compareTo(var1.isSetStartEpochNanos());
                                                if (var4 != 0) {
                                                    return var4;
                                                } else {
                                                    if (this.isSetStartEpochNanos()) {
                                                        var4 = TBaseHelper.compareTo(this.startEpochNanos, var1.startEpochNanos);
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

                                                        var4 = Boolean.valueOf(this.isSetEvents()).compareTo(var1.isSetEvents());
                                                        if (var4 != 0) {
                                                            return var4;
                                                        } else {
                                                            if (this.isSetEvents()) {
                                                                var4 = TBaseHelper.compareTo(this.events, var1.events);
                                                                if (var4 != 0) {
                                                                    return var4;
                                                                }
                                                            }

                                                            var4 = Boolean.valueOf(this.isSetLinks()).compareTo(var1.isSetLinks());
                                                            if (var4 != 0) {
                                                                return var4;
                                                            } else {
                                                                if (this.isSetLinks()) {
                                                                    var4 = TBaseHelper.compareTo(this.links, var1.links);
                                                                    if (var4 != 0) {
                                                                        return var4;
                                                                    }
                                                                }

                                                                var4 = Boolean.valueOf(this.isSetStatus()).compareTo(var1.isSetStatus());
                                                                if (var4 != 0) {
                                                                    return var4;
                                                                } else {
                                                                    if (this.isSetStatus()) {
                                                                        var4 = TBaseHelper.compareTo(this.status, var1.status);
                                                                        if (var4 != 0) {
                                                                            return var4;
                                                                        }
                                                                    }

                                                                    var4 = Boolean.valueOf(this.isSetEndEpochNanos()).compareTo(var1.isSetEndEpochNanos());
                                                                    if (var4 != 0) {
                                                                        return var4;
                                                                    } else {
                                                                        if (this.isSetEndEpochNanos()) {
                                                                            var4 = TBaseHelper.compareTo(this.endEpochNanos, var1.endEpochNanos);
                                                                            if (var4 != 0) {
                                                                                return var4;
                                                                            }
                                                                        }

                                                                        var4 = Boolean.valueOf(this.isSetEnded()).compareTo(var1.isSetEnded());
                                                                        if (var4 != 0) {
                                                                            return var4;
                                                                        } else {
                                                                            if (this.isSetEnded()) {
                                                                                var4 = TBaseHelper.compareTo(this.ended, var1.ended);
                                                                                if (var4 != 0) {
                                                                                    return var4;
                                                                                }
                                                                            }

                                                                            var4 = Boolean.valueOf(this.isSetTotalRecordedEvents()).compareTo(var1.isSetTotalRecordedEvents());
                                                                            if (var4 != 0) {
                                                                                return var4;
                                                                            } else {
                                                                                if (this.isSetTotalRecordedEvents()) {
                                                                                    var4 = TBaseHelper.compareTo(this.totalRecordedEvents, var1.totalRecordedEvents);
                                                                                    if (var4 != 0) {
                                                                                        return var4;
                                                                                    }
                                                                                }

                                                                                var4 = Boolean.valueOf(this.isSetTotalRecordedLinks()).compareTo(var1.isSetTotalRecordedLinks());
                                                                                if (var4 != 0) {
                                                                                    return var4;
                                                                                } else {
                                                                                    if (this.isSetTotalRecordedLinks()) {
                                                                                        var4 = TBaseHelper.compareTo(this.totalRecordedLinks, var1.totalRecordedLinks);
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

                                                                                        var4 = Boolean.valueOf(this.isSetExtra()).compareTo(var1.isSetExtra());
                                                                                        if (var4 != 0) {
                                                                                            return var4;
                                                                                        } else {
                                                                                            if (this.isSetExtra()) {
                                                                                                var4 = TBaseHelper.compareTo(this.extra, var1.extra);
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
        }
    }

    public TSpanData._Fields fieldForId(int var1) {
        return TSpanData._Fields.findByThriftId(var1);
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
                    if (var2.type == 2) {
                        this.sampled = var1.readBool();
                        this.setSampledIsSet(true);
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
                    if (var2.type == 12) {
                        this.parentSpanContext = new TSpanContext();
                        this.parentSpanContext.read(var1);
                    } else {
                        TProtocolUtil.skip(var1, var2.type);
                    }
                    break;
                case 6:
                    if (var2.type == 12) {
                        this.resouce = new TResource();
                        this.resouce.read(var1);
                    } else {
                        TProtocolUtil.skip(var1, var2.type);
                    }
                    break;
                case 7:
                    if (var2.type == 12) {
                        this.instrumentationLibraryInfo = new TInstrumentationLibraryInfo();
                        this.instrumentationLibraryInfo.read(var1);
                    } else {
                        TProtocolUtil.skip(var1, var2.type);
                    }
                    break;
                case 8:
                    if (var2.type == 11) {
                        this.name = var1.readString();
                    } else {
                        TProtocolUtil.skip(var1, var2.type);
                    }
                    break;
                case 9:
                    if (var2.type == 8) {
                        this.kind = TKind.findByValue(var1.readI32());
                    } else {
                        TProtocolUtil.skip(var1, var2.type);
                    }
                    break;
                case 10:
                    if (var2.type == 10) {
                        this.startEpochNanos = var1.readI64();
                        this.setStartEpochNanosIsSet(true);
                    } else {
                        TProtocolUtil.skip(var1, var2.type);
                    }
                    break;
                case 11:
                    if (var2.type == 12) {
                        this.attributes = new TAttributes();
                        this.attributes.read(var1);
                    } else {
                        TProtocolUtil.skip(var1, var2.type);
                    }
                    break;
                case 12:
                    if (var2.type != 15) {
                        TProtocolUtil.skip(var1, var2.type);
                        break;
                    }

                    var3 = var1.readListBegin();
                    this.events = new ArrayList(var3.size);

                    for(var4 = 0; var4 < var3.size; ++var4) {
                        TEvent var6 = new TEvent();
                        var6.read(var1);
                        this.events.add(var6);
                    }

                    var1.readListEnd();
                    break;
                case 13:
                    if (var2.type != 15) {
                        TProtocolUtil.skip(var1, var2.type);
                        break;
                    }

                    var3 = var1.readListBegin();
                    this.links = new ArrayList(var3.size);

                    for(var4 = 0; var4 < var3.size; ++var4) {
                        TLink var5 = new TLink();
                        var5.read(var1);
                        this.links.add(var5);
                    }

                    var1.readListEnd();
                    break;
                case 14:
                    if (var2.type == 8) {
                        this.status = TStatus.findByValue(var1.readI32());
                    } else {
                        TProtocolUtil.skip(var1, var2.type);
                    }
                    break;
                case 15:
                    if (var2.type == 10) {
                        this.endEpochNanos = var1.readI64();
                        this.setEndEpochNanosIsSet(true);
                    } else {
                        TProtocolUtil.skip(var1, var2.type);
                    }
                    break;
                case 16:
                    if (var2.type == 2) {
                        this.ended = var1.readBool();
                        this.setEndedIsSet(true);
                    } else {
                        TProtocolUtil.skip(var1, var2.type);
                    }
                    break;
                case 17:
                    if (var2.type == 8) {
                        this.totalRecordedEvents = var1.readI32();
                        this.setTotalRecordedEventsIsSet(true);
                    } else {
                        TProtocolUtil.skip(var1, var2.type);
                    }
                    break;
                case 18:
                    if (var2.type == 8) {
                        this.totalRecordedLinks = var1.readI32();
                        this.setTotalRecordedLinksIsSet(true);
                    } else {
                        TProtocolUtil.skip(var1, var2.type);
                    }
                    break;
                case 19:
                    if (var2.type == 8) {
                        this.totalAttributeCount = var1.readI32();
                        this.setTotalAttributeCountIsSet(true);
                    } else {
                        TProtocolUtil.skip(var1, var2.type);
                    }
                    break;
                case 20:
                    if (var2.type == 12) {
                        this.extra = new TExtra();
                        this.extra.read(var1);
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

        if (this.isSetSampled()) {
            var1.writeFieldBegin(SAMPLED_FIELD_DESC);
            var1.writeBool(this.sampled);
            var1.writeFieldEnd();
        }

        if (this.traceState != null && this.isSetTraceState()) {
            var1.writeFieldBegin(TRACE_STATE_FIELD_DESC);
            this.traceState.write(var1);
            var1.writeFieldEnd();
        }

        if (this.parentSpanContext != null && this.isSetParentSpanContext()) {
            var1.writeFieldBegin(PARENT_SPAN_CONTEXT_FIELD_DESC);
            this.parentSpanContext.write(var1);
            var1.writeFieldEnd();
        }

        if (this.resouce != null && this.isSetResouce()) {
            var1.writeFieldBegin(RESOUCE_FIELD_DESC);
            this.resouce.write(var1);
            var1.writeFieldEnd();
        }

        if (this.instrumentationLibraryInfo != null && this.isSetInstrumentationLibraryInfo()) {
            var1.writeFieldBegin(INSTRUMENTATION_LIBRARY_INFO_FIELD_DESC);
            this.instrumentationLibraryInfo.write(var1);
            var1.writeFieldEnd();
        }

        if (this.name != null && this.isSetName()) {
            var1.writeFieldBegin(NAME_FIELD_DESC);
            var1.writeString(this.name);
            var1.writeFieldEnd();
        }

        if (this.kind != null && this.isSetKind()) {
            var1.writeFieldBegin(KIND_FIELD_DESC);
            var1.writeI32(this.kind.getValue());
            var1.writeFieldEnd();
        }

        if (this.isSetStartEpochNanos()) {
            var1.writeFieldBegin(START_EPOCH_NANOS_FIELD_DESC);
            var1.writeI64(this.startEpochNanos);
            var1.writeFieldEnd();
        }

        if (this.attributes != null && this.isSetAttributes()) {
            var1.writeFieldBegin(ATTRIBUTES_FIELD_DESC);
            this.attributes.write(var1);
            var1.writeFieldEnd();
        }

        Iterator var2;
        if (this.events != null && this.isSetEvents()) {
            var1.writeFieldBegin(EVENTS_FIELD_DESC);
            var1.writeListBegin(new TList((byte)12, this.events.size()));
            var2 = this.events.iterator();

            while(var2.hasNext()) {
                TEvent var3 = (TEvent)var2.next();
                var3.write(var1);
            }

            var1.writeListEnd();
            var1.writeFieldEnd();
        }

        if (this.links != null && this.isSetLinks()) {
            var1.writeFieldBegin(LINKS_FIELD_DESC);
            var1.writeListBegin(new TList((byte)12, this.links.size()));
            var2 = this.links.iterator();

            while(var2.hasNext()) {
                TLink var4 = (TLink)var2.next();
                var4.write(var1);
            }

            var1.writeListEnd();
            var1.writeFieldEnd();
        }

        if (this.status != null && this.isSetStatus()) {
            var1.writeFieldBegin(STATUS_FIELD_DESC);
            var1.writeI32(this.status.getValue());
            var1.writeFieldEnd();
        }

        if (this.isSetEndEpochNanos()) {
            var1.writeFieldBegin(END_EPOCH_NANOS_FIELD_DESC);
            var1.writeI64(this.endEpochNanos);
            var1.writeFieldEnd();
        }

        if (this.isSetEnded()) {
            var1.writeFieldBegin(ENDED_FIELD_DESC);
            var1.writeBool(this.ended);
            var1.writeFieldEnd();
        }

        if (this.isSetTotalRecordedEvents()) {
            var1.writeFieldBegin(TOTAL_RECORDED_EVENTS_FIELD_DESC);
            var1.writeI32(this.totalRecordedEvents);
            var1.writeFieldEnd();
        }

        if (this.isSetTotalRecordedLinks()) {
            var1.writeFieldBegin(TOTAL_RECORDED_LINKS_FIELD_DESC);
            var1.writeI32(this.totalRecordedLinks);
            var1.writeFieldEnd();
        }

        if (this.isSetTotalAttributeCount()) {
            var1.writeFieldBegin(TOTAL_ATTRIBUTE_COUNT_FIELD_DESC);
            var1.writeI32(this.totalAttributeCount);
            var1.writeFieldEnd();
        }

        if (this.extra != null && this.isSetExtra()) {
            var1.writeFieldBegin(EXTRA_FIELD_DESC);
            this.extra.write(var1);
            var1.writeFieldEnd();
        }

        var1.writeFieldStop();
        var1.writeStructEnd();
    }

    public String toString() {
        StringBuilder var1 = new StringBuilder("TSpanData(");
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

        if (this.isSetSampled()) {
            if (!var2) {
                var1.append(", ");
            }

            var1.append("sampled:");
            var1.append(this.sampled);
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

        if (this.isSetParentSpanContext()) {
            if (!var2) {
                var1.append(", ");
            }

            var1.append("parentSpanContext:");
            if (this.parentSpanContext == null) {
                var1.append("null");
            } else {
                var1.append(this.parentSpanContext);
            }

            var2 = false;
        }

        if (this.isSetResouce()) {
            if (!var2) {
                var1.append(", ");
            }

            var1.append("resouce:");
            if (this.resouce == null) {
                var1.append("null");
            } else {
                var1.append(this.resouce);
            }

            var2 = false;
        }

        if (this.isSetInstrumentationLibraryInfo()) {
            if (!var2) {
                var1.append(", ");
            }

            var1.append("instrumentationLibraryInfo:");
            if (this.instrumentationLibraryInfo == null) {
                var1.append("null");
            } else {
                var1.append(this.instrumentationLibraryInfo);
            }

            var2 = false;
        }

        if (this.isSetName()) {
            if (!var2) {
                var1.append(", ");
            }

            var1.append("name:");
            if (this.name == null) {
                var1.append("null");
            } else {
                var1.append(this.name);
            }

            var2 = false;
        }

        if (this.isSetKind()) {
            if (!var2) {
                var1.append(", ");
            }

            var1.append("kind:");
            if (this.kind == null) {
                var1.append("null");
            } else {
                var1.append(this.kind);
            }

            var2 = false;
        }

        if (this.isSetStartEpochNanos()) {
            if (!var2) {
                var1.append(", ");
            }

            var1.append("startEpochNanos:");
            var1.append(this.startEpochNanos);
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

        if (this.isSetEvents()) {
            if (!var2) {
                var1.append(", ");
            }

            var1.append("events:");
            if (this.events == null) {
                var1.append("null");
            } else {
                var1.append(this.events);
            }

            var2 = false;
        }

        if (this.isSetLinks()) {
            if (!var2) {
                var1.append(", ");
            }

            var1.append("links:");
            if (this.links == null) {
                var1.append("null");
            } else {
                var1.append(this.links);
            }

            var2 = false;
        }

        if (this.isSetStatus()) {
            if (!var2) {
                var1.append(", ");
            }

            var1.append("status:");
            if (this.status == null) {
                var1.append("null");
            } else {
                var1.append(this.status);
            }

            var2 = false;
        }

        if (this.isSetEndEpochNanos()) {
            if (!var2) {
                var1.append(", ");
            }

            var1.append("endEpochNanos:");
            var1.append(this.endEpochNanos);
            var2 = false;
        }

        if (this.isSetEnded()) {
            if (!var2) {
                var1.append(", ");
            }

            var1.append("ended:");
            var1.append(this.ended);
            var2 = false;
        }

        if (this.isSetTotalRecordedEvents()) {
            if (!var2) {
                var1.append(", ");
            }

            var1.append("totalRecordedEvents:");
            var1.append(this.totalRecordedEvents);
            var2 = false;
        }

        if (this.isSetTotalRecordedLinks()) {
            if (!var2) {
                var1.append(", ");
            }

            var1.append("totalRecordedLinks:");
            var1.append(this.totalRecordedLinks);
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

        if (this.isSetExtra()) {
            if (!var2) {
                var1.append(", ");
            }

            var1.append("extra:");
            if (this.extra == null) {
                var1.append("null");
            } else {
                var1.append(this.extra);
            }

            var2 = false;
        }

        var1.append(")");
        return var1.toString();
    }

    public void validate() throws TException {
    }

    static {
        EnumMap var0 = new EnumMap(TSpanData._Fields.class);
        var0.put(TSpanData._Fields.TRACE_ID, new FieldMetaData("traceId", (byte)2, new FieldValueMetaData((byte)11)));
        var0.put(TSpanData._Fields.SPAN_ID, new FieldMetaData("spanId", (byte)2, new FieldValueMetaData((byte)11)));
        var0.put(TSpanData._Fields.SAMPLED, new FieldMetaData("sampled", (byte)2, new FieldValueMetaData((byte)2)));
        var0.put(TSpanData._Fields.TRACE_STATE, new FieldMetaData("traceState", (byte)2, new StructMetaData((byte)12, TTraceState.class)));
        var0.put(TSpanData._Fields.PARENT_SPAN_CONTEXT, new FieldMetaData("parentSpanContext", (byte)2, new StructMetaData((byte)12, TSpanContext.class)));
        var0.put(TSpanData._Fields.RESOUCE, new FieldMetaData("resouce", (byte)2, new StructMetaData((byte)12, TResource.class)));
        var0.put(TSpanData._Fields.INSTRUMENTATION_LIBRARY_INFO, new FieldMetaData("instrumentationLibraryInfo", (byte)2, new StructMetaData((byte)12, TInstrumentationLibraryInfo.class)));
        var0.put(TSpanData._Fields.NAME, new FieldMetaData("name", (byte)2, new FieldValueMetaData((byte)11)));
        var0.put(TSpanData._Fields.KIND, new FieldMetaData("kind", (byte)2, new EnumMetaData((byte)16, TKind.class)));
        var0.put(TSpanData._Fields.START_EPOCH_NANOS, new FieldMetaData("startEpochNanos", (byte)2, new FieldValueMetaData((byte)10)));
        var0.put(TSpanData._Fields.ATTRIBUTES, new FieldMetaData("attributes", (byte)2, new StructMetaData((byte)12, TAttributes.class)));
        var0.put(TSpanData._Fields.EVENTS, new FieldMetaData("events", (byte)2, new ListMetaData((byte)15, new StructMetaData((byte)12, TEvent.class))));
        var0.put(TSpanData._Fields.LINKS, new FieldMetaData("links", (byte)2, new ListMetaData((byte)15, new StructMetaData((byte)12, TLink.class))));
        var0.put(TSpanData._Fields.STATUS, new FieldMetaData("status", (byte)2, new EnumMetaData((byte)16, TStatus.class)));
        var0.put(TSpanData._Fields.END_EPOCH_NANOS, new FieldMetaData("endEpochNanos", (byte)2, new FieldValueMetaData((byte)10)));
        var0.put(TSpanData._Fields.ENDED, new FieldMetaData("ended", (byte)2, new FieldValueMetaData((byte)2)));
        var0.put(TSpanData._Fields.TOTAL_RECORDED_EVENTS, new FieldMetaData("totalRecordedEvents", (byte)2, new FieldValueMetaData((byte)8)));
        var0.put(TSpanData._Fields.TOTAL_RECORDED_LINKS, new FieldMetaData("totalRecordedLinks", (byte)2, new FieldValueMetaData((byte)8)));
        var0.put(TSpanData._Fields.TOTAL_ATTRIBUTE_COUNT, new FieldMetaData("totalAttributeCount", (byte)2, new FieldValueMetaData((byte)8)));
        var0.put(TSpanData._Fields.EXTRA, new FieldMetaData("extra", (byte)2, new StructMetaData((byte)12, TExtra.class)));
        metaDataMap = Collections.unmodifiableMap(var0);
        FieldMetaData.addStructMetaDataMap(TSpanData.class, metaDataMap);
    }

    public static enum _Fields implements TFieldIdEnum {
        TRACE_ID((short)1, "traceId"),
        SPAN_ID((short)2, "spanId"),
        SAMPLED((short)3, "sampled"),
        TRACE_STATE((short)4, "traceState"),
        PARENT_SPAN_CONTEXT((short)5, "parentSpanContext"),
        RESOUCE((short)6, "resouce"),
        INSTRUMENTATION_LIBRARY_INFO((short)7, "instrumentationLibraryInfo"),
        NAME((short)8, "name"),
        KIND((short)9, "kind"),
        START_EPOCH_NANOS((short)10, "startEpochNanos"),
        ATTRIBUTES((short)11, "attributes"),
        EVENTS((short)12, "events"),
        LINKS((short)13, "links"),
        STATUS((short)14, "status"),
        END_EPOCH_NANOS((short)15, "endEpochNanos"),
        ENDED((short)16, "ended"),
        TOTAL_RECORDED_EVENTS((short)17, "totalRecordedEvents"),
        TOTAL_RECORDED_LINKS((short)18, "totalRecordedLinks"),
        TOTAL_ATTRIBUTE_COUNT((short)19, "totalAttributeCount"),
        EXTRA((short)20, "extra");

        private static final Map<String, TSpanData._Fields> byName = new HashMap();
        private final short _thriftId;
        private final String _fieldName;

        public static TSpanData._Fields findByThriftId(int var0) {
            switch(var0) {
                case 1:
                    return TRACE_ID;
                case 2:
                    return SPAN_ID;
                case 3:
                    return SAMPLED;
                case 4:
                    return TRACE_STATE;
                case 5:
                    return PARENT_SPAN_CONTEXT;
                case 6:
                    return RESOUCE;
                case 7:
                    return INSTRUMENTATION_LIBRARY_INFO;
                case 8:
                    return NAME;
                case 9:
                    return KIND;
                case 10:
                    return START_EPOCH_NANOS;
                case 11:
                    return ATTRIBUTES;
                case 12:
                    return EVENTS;
                case 13:
                    return LINKS;
                case 14:
                    return STATUS;
                case 15:
                    return END_EPOCH_NANOS;
                case 16:
                    return ENDED;
                case 17:
                    return TOTAL_RECORDED_EVENTS;
                case 18:
                    return TOTAL_RECORDED_LINKS;
                case 19:
                    return TOTAL_ATTRIBUTE_COUNT;
                case 20:
                    return EXTRA;
                default:
                    return null;
            }
        }

        public static TSpanData._Fields findByThriftIdOrThrow(int var0) {
            TSpanData._Fields var1 = findByThriftId(var0);
            if (var1 == null) {
                throw new IllegalArgumentException("Field " + var0 + " doesn't exist!");
            } else {
                return var1;
            }
        }

        public static TSpanData._Fields findByName(String var0) {
            return (TSpanData._Fields)byName.get(var0);
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
            Iterator var0 = EnumSet.allOf(TSpanData._Fields.class).iterator();

            while(var0.hasNext()) {
                TSpanData._Fields var1 = (TSpanData._Fields)var0.next();
                byName.put(var1.getFieldName(), var1);
            }

        }
    }
}
