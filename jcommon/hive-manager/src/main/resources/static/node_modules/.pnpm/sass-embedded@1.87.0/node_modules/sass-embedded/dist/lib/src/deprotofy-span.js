"use strict";
// Copyright 2021 Google LLC. Use of this source code is governed by an
// MIT-style license that can be found in the LICENSE file or at
// https://opensource.org/licenses/MIT.
Object.defineProperty(exports, "__esModule", { value: true });
exports.deprotofySourceSpan = deprotofySourceSpan;
const url_1 = require("url");
const utils_1 = require("./utils");
// Creates a SourceSpan from the given protocol `buffer`. Throws if the buffer
// has invalid fields.
function deprotofySourceSpan(buffer) {
    const text = buffer.text;
    if (buffer.start === undefined) {
        throw (0, utils_1.compilerError)('Expected SourceSpan to have start.');
    }
    let end;
    if (buffer.end === undefined) {
        if (text !== '') {
            throw (0, utils_1.compilerError)('Expected SourceSpan text to be empty.');
        }
        else {
            end = buffer.start;
        }
    }
    else {
        end = buffer.end;
        if (end.offset < buffer.start.offset) {
            throw (0, utils_1.compilerError)('Expected SourceSpan end to be after start.');
        }
    }
    const url = buffer.url === '' ? undefined : new url_1.URL(buffer.url);
    const context = buffer.context === '' ? undefined : buffer.context;
    return {
        text,
        start: buffer.start,
        end,
        url,
        context,
    };
}
//# sourceMappingURL=deprotofy-span.js.map