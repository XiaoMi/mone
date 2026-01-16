# buffer-builder.js

BufferBuilder accumulates pieces of data into a buffer, appending each onto the end. The data can be Buffers, strings, a repetition of a byte, or any of the types such as UInt32LE or FloatBE that can be written into Buffers.

If you are thinking about using this, you should probably have considered streaming your data instead of putting it into a buffer.

## Usage

``` js
var BufferBuilder = require('buffer-builder');
var helloWorld = new BufferBuilder();

// Append a string, utf8 encoded by default.
helloWorld.appendString('hello');

// Append any type that Buffer has read and write functions for.
helloWorld.appendUInt16LE(0x7720);

// Append a buffer
helloWorld.appendBuffer(new Buffer([111, 114, 108, 100]));

// Appended a repetition of a byte
helloWorld.appendFill(33, 3);

// Convert to an ordinary buffer
var buffer = helloWorld.get();

buffer.toString(); // hello world!!!
```

## API

### new BufferBuilder([initialCapacity])
Allocate an empty BufferBuilder. If you know approximately what size the Buffer will end up being and are trying to squeeze out more performance, you can set the initial size of the backing buffer.

### appendBuffer(source)
Append a buffer. Use [slice](http://nodejs.org/docs/latest/api/buffers.html#buffer.slice) if you want to append just part of one.

### appendString(string, [encoding])
Append a string, encoded by utf8 by default. No trailing 0 is appended.

### appendStringZero(string, [encoding])
Append a null-terminated string, encoded by utf8 by default.

### appendUInt8(value)
Append 8-bit unsigned integer.

### appendUInt16LE(value)
Append 16-bit unsigned integer, little endian. 1 is encoded as 01 00.

### appendUInt16BE(value)
Append 16-bit unsigned integer, big endian. 1 is encoded as 00 01.

### appendUInt32LE(value)
Append 32-bit unsigned integer, little endian. 1 is encoded as 01 00 00 00.

### appendUInt32BE(value)
Append 32-bit unsigned integer, big endian. 1 is encoded as 00 00 00 01.

### appendInt8(value)
Append 8-bit signed integer.

### appendInt16LE(value)
Append 16-bit signed integer, little endian. 1 is encoded as 01 00.

### appendInt16BE(value)
Append 16-bit signed integer, big endian. 1 is encoded as 00 01.

### appendInt32LE(value)
Append 32-bit signed integer, little endian. 1 is encoded as 01 00 00 00.

### appendInt32BE(value)
Append 32-bit signed integer, big endian. 1 is encoded as 00 00 00 01.

### appendFloatLE(value)
Little-endian float. Occupies 4 bytes.

### appendFloatBE(value)
Big-endian float. Occupies 4 bytes.

### appendDoubleLE(value)
Little-endian double. Occupies 8 bytes.

### appendDoubleBE(value)
Big-endian double. Occupies 8 bytes.

### appendFill(value, count)
Append _count_ repetitions of _value_ (a byte).

### get()
Convert to a buffer. This is a deep copy; modifications to the returned buffer will not affect the BufferBuilder.

### copy(targetBuffer, [targetStart], [sourceStart], [sourceEnd])
Copy bytes from the BufferBuilder into _targetBuffer_. _targetStart_ and _sourceStart_ default to 0. _sourceEnd_ defaults to the BufferBuilder's length.

### length
Number of bytes appended so far.
