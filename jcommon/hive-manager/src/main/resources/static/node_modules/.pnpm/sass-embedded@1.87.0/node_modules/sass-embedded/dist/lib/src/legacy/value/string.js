"use strict";
// Copyright 2022 Google LLC. Use of this source code is governed by an
// MIT-style license that can be found in the LICENSE file or at
// https://opensource.org/licenses/MIT.
Object.defineProperty(exports, "__esModule", { value: true });
exports.LegacyString = void 0;
const string_1 = require("../../value/string");
const base_1 = require("./base");
class LegacyString extends base_1.LegacyValueBase {
    constructor(valueOrInner) {
        if (valueOrInner instanceof string_1.SassString) {
            super(valueOrInner);
        }
        else {
            super(new string_1.SassString(valueOrInner, { quotes: false }));
        }
    }
    getValue() {
        return this.inner.text;
    }
    setValue(value) {
        this.inner = new string_1.SassString(value, { quotes: false });
    }
}
exports.LegacyString = LegacyString;
Object.defineProperty(LegacyString, 'name', { value: 'sass.types.String' });
//# sourceMappingURL=string.js.map