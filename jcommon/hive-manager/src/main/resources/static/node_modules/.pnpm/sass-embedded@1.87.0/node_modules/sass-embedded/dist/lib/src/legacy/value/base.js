"use strict";
// Copyright 2022 Google LLC. Use of this source code is governed by an
// MIT-style license that can be found in the LICENSE file or at
// https://opensource.org/licenses/MIT.
Object.defineProperty(exports, "__esModule", { value: true });
exports.LegacyValueBase = void 0;
/**
 * A base class for legacy value types. A shared base class makes it easier to
 * detect legacy values and extract their inner value objects.
 */
class LegacyValueBase {
    inner;
    constructor(inner) {
        this.inner = inner;
    }
}
exports.LegacyValueBase = LegacyValueBase;
//# sourceMappingURL=base.js.map