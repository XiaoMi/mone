package com.xiaomi.data.push.uds.codes.protostuff;

import io.protostuff.Input;
import io.protostuff.Output;
import io.protostuff.Pipe;
import io.protostuff.WireFormat;
import io.protostuff.runtime.Delegate;

import java.io.IOException;
import java.sql.Timestamp;

/**
 * @author goodjava@qq.com
 * @date 2023/2/2 15:49
 */
public class TimestampDelegate implements Delegate<Timestamp> {

    @Override
    public WireFormat.FieldType getFieldType() {
        return WireFormat.FieldType.FIXED64;
    }

    @Override
    public Timestamp readFrom(Input input) throws IOException {
        return new Timestamp(input.readFixed64());
    }

    @Override
    public void writeTo(Output output, int number, Timestamp value, boolean repeated) throws IOException {
        output.writeFixed64(number, value.getTime(), repeated);
    }

    @Override
    public void transfer(Pipe pipe, Input input, Output output, int number, boolean repeated) throws IOException {
        output.writeFixed64(number, input.readFixed64(), repeated);
    }

    @Override
    public Class<?> typeClass() {
        return Timestamp.class;
    }
}
