"use strict";
// Copyright 2020 Google Inc. Use of this source code is governed by an
// MIT-style license that can be found in the LICENSE file or at
// https://opensource.org/licenses/MIT.
Object.defineProperty(exports, "__esModule", { value: true });
exports.RequestTracker = void 0;
/**
 * Manages pending inbound and outbound requests. Ensures that requests and
 * responses interact correctly and obey the Embedded Protocol.
 */
class RequestTracker {
    // The indices of this array correspond to each pending request's ID. Stores
    // the response type expected by each request.
    requests = [];
    /** The next available request ID. */
    get nextId() {
        for (let i = 0; i < this.requests.length; i++) {
            if (this.requests[i] === undefined || this.requests[i] === null) {
                return i;
            }
        }
        return this.requests.length;
    }
    /**
     * Adds an entry for a pending request with ID `id`. The entry stores the
     * expected response type. Throws an error if the Protocol Error is violated.
     */
    add(id, expectedResponseType) {
        if (id < 0) {
            throw Error(`Invalid request ID ${id}.`);
        }
        else if (this.requests[id]) {
            throw Error(`Request ID ${id} is already in use by an in-flight request.`);
        }
        this.requests[id] = expectedResponseType;
    }
    /**
     * Resolves a pending request with matching ID `id` and expected response type
     * `type`. Throws an error if the Protocol Error is violated.
     */
    resolve(id, type) {
        if (this.requests[id] === undefined || this.requests[id] === null) {
            throw Error(`Response ID ${id} does not match any pending requests.`);
        }
        else if (this.requests[id] !== type) {
            throw Error(`Response with ID ${id} does not match pending request's type. Expected ${this.requests[id]} but received ${type}.`);
        }
        this.requests[id] = null;
    }
}
exports.RequestTracker = RequestTracker;
//# sourceMappingURL=request-tracker.js.map