"use strict";
// Copyright 2021 Google LLC. Use of this source code is governed by an
// MIT-style license that can be found in the LICENSE file or at
// https://opensource.org/licenses/MIT.
Object.defineProperty(exports, "__esModule", { value: true });
exports.SassArgumentList = void 0;
const immutable_1 = require("immutable");
const list_1 = require("./list");
class SassArgumentList extends list_1.SassList {
    /**
     * The `FunctionCallRequest`-scoped ID of this argument list, used to tell the
     * compiler which argument lists have had their keywords accessed during a
     * function call.
     *
     * The special ID 0 indicates an argument list constructed in the host.
     *
     * This is marked as public so that the protofier can access it, but it's not
     * part of the package's public API and should not be accessed by user code.
     * It may be renamed or removed without warning in the future.
     */
    id;
    /**
     * The argument list's keywords. This isn't exposed directly so that we can
     * set `keywordsAccessed` when the user reads it.
     *
     * This is marked as public so that the protofier can access it, but it's not
     * part of the package's public API and should not be accessed by user code.
     * It may be renamed or removed without warning in the future.
     */
    keywordsInternal;
    _keywordsAccessed = false;
    /**
     * Whether the `keywords` getter has been accessed.
     *
     * This is marked as public so that the protofier can access it, but it's not
     * part of the package's public API and should not be accessed by user code.
     * It may be renamed or removed without warning in the future.
     */
    get keywordsAccessed() {
        return this._keywordsAccessed;
    }
    get keywords() {
        this._keywordsAccessed = true;
        return this.keywordsInternal;
    }
    constructor(contents, keywords, separator, id) {
        super(contents, { separator });
        this.keywordsInternal = (0, immutable_1.isOrderedMap)(keywords)
            ? keywords
            : (0, immutable_1.OrderedMap)(keywords);
        this.id = id ?? 0;
    }
}
exports.SassArgumentList = SassArgumentList;
//# sourceMappingURL=argument-list.js.map