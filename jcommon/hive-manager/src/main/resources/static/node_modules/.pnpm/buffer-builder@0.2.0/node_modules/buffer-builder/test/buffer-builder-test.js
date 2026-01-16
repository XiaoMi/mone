var assert = require('assert');
var BufferBuilder = require('../buffer-builder');

/* Build 1   2 3   3 4 5  4 5 6 7 ... and make sure it comes out right */
(function() {
  var builder = new BufferBuilder(7);
  var top = 100;
  for (var base = 1; base < top; base++) {
    var xs = [];
    for (var offset = 0; offset < base; offset++) {
      xs.push(base + offset);
    }
    builder.appendBuffer(new Buffer(xs));
  }
  
  var result = builder.get();
  var idx = 0;
  for (var base = 1; base < top; base++) {
    for (var offset = 0; offset < base; offset++) {
      assert.equal(result[idx++], base+offset);
    }
  }
})();

/* Build 1  2 2  3 3 3  4 4 4 4  5 5 5 5 5 ... and make sure it comes out right. */
(function() {
  var builder = new BufferBuilder(3);
  var top = 20;
  for (var i = 1; i < top; i++) {
    builder.appendFill(i, i);
  }
  
  var result = builder.get();
  var idx = 0;
  for (var i = 1; i < top; i++) {
    for (var j = 0; j < i; j++) {
      assert.equal(result[idx], i);
      idx++;
    }
  }
})();

/* Numeric types come out like they went in */
(function() {
  var builder = new BufferBuilder();
  var n = 500;
  
  var buildFns = BufferBuilder.prototype;
  var readFns = Buffer.prototype;
  var entries = [];
  
  function Entry(buildFn, readFn, length, value) {
    this.buildFn = buildFn;
    this.readFn = readFn;
    this.length = length;
    this.value = value;
  }
  
  for (var i = -n; i <= n; i++) {
    entries.push(new Entry(buildFns.appendInt32LE, readFns.readInt32LE, 4, i));
    entries.push(new Entry(buildFns.appendInt32BE, readFns.readInt32BE, 4, i));
    entries.push(new Entry(buildFns.appendUInt32LE, readFns.readUInt32LE, 4, Math.abs(i)));
    entries.push(new Entry(buildFns.appendUInt32BE, readFns.readUInt32BE, 4, Math.abs(i)));
    entries.push(new Entry(buildFns.appendInt16LE, readFns.readInt16LE, 2, i));
    entries.push(new Entry(buildFns.appendInt16BE, readFns.readInt16BE, 2, i));
    entries.push(new Entry(buildFns.appendUInt16LE, readFns.readUInt16LE, 2, Math.abs(i)));
    entries.push(new Entry(buildFns.appendUInt16BE, readFns.readUInt16BE, 2, Math.abs(i)));
    entries.push(new Entry(buildFns.appendInt8, readFns.readInt8, 1, Math.round(i/50)));
    entries.push(new Entry(buildFns.appendUInt8, readFns.readUInt8, 1, Math.abs(Math.round(i/50))));
    entries.push(new Entry(buildFns.appendFloatLE, readFns.readFloatLE, 4, i));
    entries.push(new Entry(buildFns.appendFloatBE, readFns.readFloatBE, 4, i));
    entries.push(new Entry(buildFns.appendDoubleLE, readFns.readDoubleLE, 8, i));
    entries.push(new Entry(buildFns.appendDoubleBE, readFns.readDoubleBE, 8, i));
  }
  
  for (var i = 0; i < entries.length; i++) {
    var entry = entries[i];
    entry.buildFn.call(builder, entry.value);
  }
  
  var result = builder.get();
  var idx = 0;
  for (var i = 0; i < entries.length; i++) {
    var entry = entries[i];
    var read = entry.readFn.call(result, idx);
    assert.equal(read, entry.value);
    idx += entry.length;
  }
})();

/* Newly-created buffer should be empty */
(function() {
  assert.equal(new BufferBuilder().get().length, 0)
})();

/* It should concatenating strings. */
(function() {
  var words = ['This', 'is', 'a', 'test'];
  var builder = new BufferBuilder();
  for (var i = 0; i < words.length; i++) {
    builder.appendString(words[i]);
  }
  
  assert.equal(builder.get().toString(), words.join(''));
})();

/* Test appendStringZero */
(function() {
  var words = ['alpha', 'bravo', 'charlie', 'delta'];
  var builder = new BufferBuilder();
  for (var i = 0; i < words.length; i++) {
    builder.appendStringZero(words[i]);
  }
  
  function appendZero(str) { return str + '\0'; }
  assert.equal(builder.get().toString() , words.map(appendZero).join(''));
})();

/* copy() sanity checks */
(function() {
  var check = function(low, high,  targetStart, sourceStart, sourceEnd) {
    var source = new BufferBuilder();
    source.appendFill(1, 10);
    var dest = new Buffer(20);
    dest.fill(0);
    
    
    source.copy(dest, targetStart, sourceStart, sourceEnd);
    for (var i = 0; i < 20; i++) {
      assert.equal(dest[i], low<=i && i<high ? 1 : 0);
    }
  };
  
  check(0, 10);
  check(3, 13,  3);
  check(3, 10,  3, 3);
  check(10, 14,  10, 0, 4);
})();

/* copy() applied over ranges */
(function() {
  var source = new BufferBuilder(3);
  for (var i = 1; i < 100; i++) {
    source.appendUInt8(i);
  }
  
  for (var i = 0; i < 20; i++) {
    var sourceStart = i*3;
    var sourceEnd = source.length - i*5;
    var targetStart = i*20;
    
    var target = new Buffer(1000);
    target.fill(0);
    source.copy(target, targetStart, sourceStart, sourceEnd);
    
    for (var i = 0; i < target.length; i++) {
      var expected;
      if (i >= targetStart && i < targetStart + sourceEnd - sourceStart) {
        expected = i - targetStart + sourceStart + 1;
      } else {
        expected = 0;
      }
      assert.equal(expected, target[i]);
    }
  }
})();

/* Overlap small chunks over one large copy, making sure that does not change anything. */
(function() {
  var n = 1000;
  var builder = new BufferBuilder(10);
  var x = .4;
  for (var i = 0; i < n; i++) {
    builder.appendDoubleLE(x);
    x = x * (1-x) * 4;
  }
  
  var buffersMatchOneOff = function(b1, b2) {
    // Why one off? Because then sourceStart != targetStart, so I can't have mixed those up in copy().
    assert.equal(b1.length+1, b2.length);
    for (var i = 0; i < b1.length; i++) {
      assert.equal(b1[i], b2[i+1], 'byte ' + i + ' mismatch');
    }
  }
  
  var original = builder.get();
  var copyTarget = new Buffer(original.length+1);
  original.copy(copyTarget, 1);
  
  buffersMatchOneOff(original, copyTarget);
  var copyLength = 105;
  var copyStart = 4;
  var copyOffset = 143;
  for (var i = copyStart; i+copyLength < builder.length; i += copyOffset) {
    builder.copy(copyTarget, i+1, i, i + copyLength);
    //console.log(copyTarget);
    buffersMatchOneOff(original, copyTarget);
  }
  
})();


/* Try a copy that starts right on a buffer boundary */
(function() {
  var builder = new BufferBuilder(3);
  for (var i = 0; i < 10; i++) {
    builder.appendUInt8(i);
  }
  
  var dest = new Buffer(7);
  builder.copy(dest, 0, 3);
  for (var i = 0; i < 7; i++) {
    assert.equal(dest[i], i+3);
  }
})();

/* BufferBuilder.copy() should behave just like Buffer.copy(), including the exceptions thrown */
(function() {
  var builder = new BufferBuilder(5);
  for (var i = 0; i < 100; i++) {
    builder.appendUInt8(i);
  }
  var buffer = builder.get();
  
  var dest = new Buffer(50);
  
  var behavesSame = function(f) {
    var bufferException, bufferResult, builderException, builderResult;
    try {
      bufferResult = f(buffer);
    } catch (e) {
      bufferException = e;
    }
    try {
      builderResult = f(buffer);
    } catch (e) {
      builderException = e;
    }
    
    assert.equal(bufferResult, builderResult);
    assert.equal(''+bufferException, ''+builderException);
  };
  
  // copies that get cut off because target is small
  behavesSame(function(b) { return b.copy(new Buffer(50), 20) });
  behavesSame(function(b) { return b.copy(new Buffer(50)) });
  behavesSame(function(b) { return b.copy(new Buffer(50)) });
  
  // targetStart too low
  behavesSame(function(b) { return b.copy(new Buffer(50, -1)) });
  // targetStart too high
  behavesSame(function(b) { return b.copy(new Buffer(50, 50)) });
  behavesSame(function(b) { return b.copy(new Buffer(50, 51)) });
  
  // sourceStart too low
  behavesSame(function(b) { return b.copy(new Buffer(50, 0, -4)) });
  // sourceStart too high
  behavesSame(function(b) { return b.copy(new Buffer(50, 0, 130)) });
  
  // copy cut off by end of source
  behavesSame(function(b) { return b.copy(new Buffer(50, 0, 99)) });
  
  // degenerate copy
  behavesSame(function(b) { return b.copy(new Buffer(50, 0, 99, 99)) });
  
  // sourceEnd after sourceStart
  behavesSame(function(b) { return b.copy(new Buffer(50, 0, 99, 90)) });
  
  // sourceEnd too high
  behavesSame(function(b) { return b.copy(new Buffer(50, 0, 99, 150)) });
  
  // sourceStart and sourceEnd too high
  behavesSame(function(b) { return b.copy(new Buffer(50, 0, 120, 150)) });
  
})();

/* Make sure functions are chainable */
(function() {
  var x = new BufferBuilder();
  assert.equal(x, x.appendString('hello'));
  assert.equal(x, x.appendUInt16LE(0x7720));
  assert.equal(x, x.appendBuffer(new Buffer([111, 114, 108, 100])));
  assert.equal(x, x.appendFill(33, 3));
})();

/* Test the degenerate copy */
(function() {
  var x = new BufferBuilder();
  x.appendBuffer(new Buffer([2,4,6,8]));
  var b = new Buffer(7);
  b.fill(0);
  x.copy(b, 0,0,0);
  assert.equal(b[0], 0);
})();
