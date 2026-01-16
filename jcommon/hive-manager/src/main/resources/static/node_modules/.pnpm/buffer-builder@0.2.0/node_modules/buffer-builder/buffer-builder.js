module.exports = BufferBuilder;

function BufferBuilder(initialCapacity) {
  var buffer = Buffer.isBuffer(initialCapacity) ? initialCapacity : new Buffer(initialCapacity || 512);
  this.buffers = [buffer];

  this.writeIndex = 0;
  this.length = 0;
}

/* Append a (subsequence of a) Buffer */
BufferBuilder.prototype.appendBuffer = function(source) {
  if (source.length === 0) return this;
  
  var tail = this.buffers[this.buffers.length-1];
  
  var spaceInCurrent = tail.length - this.writeIndex;
  if (source.length <= spaceInCurrent) {
    // We can fit the whole thing in the current buffer
    source.copy(tail, this.writeIndex);
    this.writeIndex += source.length;
  } else {
    // Copy as much as we can into the current buffer
    if (spaceInCurrent) { // Buffer.copy does not handle the degenerate case well
      source.copy(tail, this.writeIndex);//, start, start + spaceInCurrent);
    }
    // Fit the rest into a new buffer. Make sure it is at least as big as
    // what we're being asked to add, and also follow our double-previous-buffer pattern.
    var newBuf = new Buffer(Math.max(tail.length*2, source.length));
    
    this.buffers.push(newBuf);
    this.writeIndex = source.copy(newBuf, 0, spaceInCurrent);
  }
  
  this.length += source.length;
  
  return this;
};

function makeAppender(encoder, size) {
  return function(x) {
    var buf = this.buffers[this.buffers.length-1];
    if (this.writeIndex + size <= buf.length) {
      encoder.call(buf, x, this.writeIndex, true);
      this.writeIndex += size;
      this.length += size;
    } else {
      var scratchBuffer = new Buffer(size);
      encoder.call(scratchBuffer, x, 0, true);
      this.appendBuffer(scratchBuffer);
    }
    
    return this;
  };
}

BufferBuilder.prototype.appendUInt8 = makeAppender(Buffer.prototype.writeUInt8, 1);
BufferBuilder.prototype.appendUInt16LE = makeAppender(Buffer.prototype.writeUInt16LE, 2);
BufferBuilder.prototype.appendUInt16BE = makeAppender(Buffer.prototype.writeUInt16BE, 2);
BufferBuilder.prototype.appendUInt32LE = makeAppender(Buffer.prototype.writeUInt32LE, 4);
BufferBuilder.prototype.appendUInt32BE = makeAppender(Buffer.prototype.writeUInt32BE, 4);
BufferBuilder.prototype.appendInt8 = makeAppender(Buffer.prototype.writeInt8, 1);
BufferBuilder.prototype.appendInt16LE = makeAppender(Buffer.prototype.writeInt16LE, 2);
BufferBuilder.prototype.appendInt16BE = makeAppender(Buffer.prototype.writeInt16BE, 2);
BufferBuilder.prototype.appendInt32LE = makeAppender(Buffer.prototype.writeInt32LE, 4);
BufferBuilder.prototype.appendInt32BE = makeAppender(Buffer.prototype.writeInt32BE, 4);
BufferBuilder.prototype.appendFloatLE = makeAppender(Buffer.prototype.writeFloatLE, 4);
BufferBuilder.prototype.appendFloatBE = makeAppender(Buffer.prototype.writeFloatBE, 4);
BufferBuilder.prototype.appendDoubleLE = makeAppender(Buffer.prototype.writeDoubleLE, 8);
BufferBuilder.prototype.appendDoubleBE = makeAppender(Buffer.prototype.writeDoubleBE, 8);

BufferBuilder.prototype.appendString = function(str, encoding) {
  return this.appendBuffer(new Buffer(str, encoding));
};

BufferBuilder.prototype.appendStringZero = function(str, encoding) {
  return this.appendString(str + '\0', encoding);
}

BufferBuilder.prototype.appendFill = function(value, count) {
  if (!count) return;
  
  var tail = this.buffers[this.buffers.length-1];
  
  var spaceInCurrent = tail.length - this.writeIndex;
  if (count <= spaceInCurrent) {
    // We can fit the whole thing in the current buffer
    tail.fill(value, this.writeIndex, this.writeIndex + count);
    this.writeIndex += count;
  } else {
    // Copy as much as we can into the current buffer
    if (spaceInCurrent) { // does not handle the degenerate case well
      tail.fill(value, this.writeIndex);
    }
    // Fit the rest into a new buffer. Make sure it is at least as big as
    // what we're being asked to add, and also follow our double-previous-buffer pattern.
    var newBuf = new Buffer(Math.max(tail.length*2, count));
    var couldNotFit = count - spaceInCurrent;
    newBuf.fill(value, 0, couldNotFit);
    this.buffers.push(newBuf);
    this.writeIndex = couldNotFit;
  }
  
  this.length += count;
  
  return this;
};

/* Convert to a plain Buffer */
BufferBuilder.prototype.get = function() {
  var concatted = new Buffer(this.length);
  this.copy(concatted);
  return concatted;
};

/* Copy into targetBuffer */
BufferBuilder.prototype.copy = function(targetBuffer, targetStart, sourceStart, sourceEnd) {
  targetStart || (targetStart = 0);
  sourceStart || (sourceStart = 0);
  sourceEnd !== undefined || (sourceEnd = this.length);
  
  // Validation. Besides making us fail nicely, this makes it so we can skip checks below.
  if (targetStart < 0 || (targetStart>0 && targetStart >= targetBuffer.length)) {
    throw new Error('targetStart is out of bounds');
  }
  if (sourceEnd < sourceStart) {
    throw new Error('sourceEnd < sourceStart');
  }
  if (sourceStart < 0 || (sourceStart>0 && sourceStart >= this.length)) {
    throw new Error('sourceStart is out of bounds');
  }
  if (sourceEnd > this.length) {
    throw new Error('sourceEnd out of bounds');
  }
  
  sourceEnd = Math.min(sourceEnd, sourceStart + (targetBuffer.length-targetStart));
  var targetWriteIdx = targetStart;
  var readBuffer = 0;
  
  // Skip through our buffers until we get to where the copying should start.
  var copyLength = sourceEnd - sourceStart;
  var skipped = 0;
  while (skipped < sourceStart) {
    var buffer = this.buffers[readBuffer];
    if (buffer.length + skipped < targetStart) {
      skipped += buffer.length;
    } else {
      // Do the first copy. This one is different from the others in that it
      // does not start from the beginning of one of our internal buffers.
      var copyStart = sourceStart - skipped;
      var inThisBuffer = Math.min(copyLength, buffer.length - copyStart);
      
      buffer.copy(targetBuffer, targetWriteIdx, copyStart, copyStart + inThisBuffer);
      targetWriteIdx += inThisBuffer;
      copyLength -= inThisBuffer;
      readBuffer++;
      break;
    }
    readBuffer++;
  }
  
  // Copy the rest. Note that we can't run off of our end because we validated the range up above
  while (copyLength > 0) {
    var buffer = this.buffers[readBuffer];
    var toCopy = Math.min(buffer.length, copyLength);
    
    buffer.copy(targetBuffer, targetWriteIdx, 0, toCopy);
    copyLength -= toCopy;
    targetWriteIdx += toCopy;
    readBuffer++;
  }
  
  // Return how many bytes were copied
  return sourceEnd - sourceStart;
};
