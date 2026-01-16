"use strict";
// Copyright 2024 Google LLC. Use of this source code is governed by an
// MIT-style license that can be found in the LICENSE file or at
// https://opensource.org/licenses/MIT.
Object.defineProperty(exports, "__esModule", { value: true });
exports.getElfInterpreter = getElfInterpreter;
const fs = require("fs");
/** Read a chunk of data from a file descriptor into a new Buffer. */
function readFileDescriptor(fd, position, length) {
    const buffer = Buffer.alloc(length);
    let offset = 0;
    while (offset < length) {
        const bytesRead = fs.readSync(fd, buffer, {
            offset: offset,
            position: position + offset,
        });
        if (bytesRead === 0) {
            throw new Error(`failed to read fd ${fd}`);
        }
        offset += bytesRead;
    }
    return buffer;
}
/** Parse an ELF file and return its interpreter. */
function getElfInterpreter(path) {
    const fd = fs.openSync(path, 'r');
    try {
        const elfIdentification = new DataView(readFileDescriptor(fd, 0, 64).buffer);
        if (elfIdentification.getUint8(0) !== 0x7f ||
            elfIdentification.getUint8(1) !== 0x45 ||
            elfIdentification.getUint8(2) !== 0x4c ||
            elfIdentification.getUint8(3) !== 0x46) {
            throw new Error(`${path} is not an ELF file.`);
        }
        const elfIdentificationClass = elfIdentification.getUint8(4);
        if (elfIdentificationClass !== 1 && elfIdentificationClass !== 2) {
            throw new Error(`${path} has an invalid ELF class.`);
        }
        const elfClass32 = elfIdentificationClass === 1;
        const elfIdentificationData = elfIdentification.getUint8(5);
        if (elfIdentificationData !== 1 && elfIdentificationData !== 2) {
            throw new Error(`${path} has an invalid endianness.`);
        }
        const littleEndian = elfIdentificationData === 1;
        // Converting BigUint64 to Number because node Buffer length has to be
        // number type, and we don't expect any elf we check with this method to
        // be larger than 9007199254740991 bytes.
        const programHeadersOffset = elfClass32
            ? elfIdentification.getUint32(28, littleEndian)
            : Number(elfIdentification.getBigUint64(32, littleEndian));
        const programHeadersEntrySize = elfClass32
            ? elfIdentification.getUint16(42, littleEndian)
            : elfIdentification.getUint16(54, littleEndian);
        const programHeadersEntryCount = elfClass32
            ? elfIdentification.getUint16(44, littleEndian)
            : elfIdentification.getUint16(56, littleEndian);
        const programHeaders = new DataView(readFileDescriptor(fd, programHeadersOffset, programHeadersEntrySize * programHeadersEntryCount).buffer);
        for (let i = 0; i < programHeadersEntryCount; i++) {
            const byteOffset = i * programHeadersEntrySize;
            const segmentType = programHeaders.getUint32(byteOffset, littleEndian);
            if (segmentType !== 3)
                continue; // 3 is PT_INTERP, the interpreter
            const segmentOffset = elfClass32
                ? programHeaders.getUint32(byteOffset + 4, littleEndian)
                : Number(programHeaders.getBigUint64(byteOffset + 8, littleEndian));
            const segmentFileSize = elfClass32
                ? programHeaders.getUint32(byteOffset + 16, littleEndian)
                : Number(programHeaders.getBigUint64(byteOffset + 32, littleEndian));
            const buffer = readFileDescriptor(fd, segmentOffset, segmentFileSize);
            if (buffer[segmentFileSize - 1] !== 0) {
                throw new Error(`${path} is corrupted.`);
            }
            return buffer.toString('utf8', 0, segmentFileSize - 1);
        }
        throw new Error(`${path} does not contain an interpreter entry.`);
    }
    finally {
        fs.closeSync(fd);
    }
}
//# sourceMappingURL=elf.js.map