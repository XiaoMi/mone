"use strict";
// Copyright 2024 Google LLC. Use of this source code is governed by an
// MIT-style license that can be found in the LICENSE file or at
// https://opensource.org/licenses/MIT.
Object.defineProperty(exports, "__esModule", { value: true });
exports.CanonicalizeContext = void 0;
class CanonicalizeContext {
    fromImport;
    _containingUrl;
    get containingUrl() {
        this._containingUrlAccessed = true;
        return this._containingUrl;
    }
    _containingUrlAccessed = false;
    /**
     * Whether the `containingUrl` getter has been accessed.
     *
     * This is marked as public so that the importer registry can access it, but
     * it's not part of the package's public API and should not be accessed by
     * user code. It may be renamed or removed without warning in the future.
     */
    get containingUrlAccessed() {
        return this._containingUrlAccessed;
    }
    constructor(containingUrl, fromImport) {
        this._containingUrl = containingUrl;
        this.fromImport = fromImport;
    }
}
exports.CanonicalizeContext = CanonicalizeContext;
//# sourceMappingURL=canonicalize-context.js.map