"use strict";
// Copyright 2021 Google Inc. Use of this source code is governed by an
// MIT-style license that can be found in the LICENSE file or at
// https://opensource.org/licenses/MIT.
Object.defineProperty(exports, "__esModule", { value: true });
exports.FunctionRegistry = void 0;
const util_1 = require("util");
const protobuf_1 = require("@bufbuild/protobuf");
const utils = require("./utils");
const proto = require("./vendor/embedded_sass_pb");
const utils_1 = require("./utils");
const protofier_1 = require("./protofier");
const value_1 = require("./value");
/**
 * Tracks functions that are defined on the host so that the compiler can
 * execute them.
 */
class FunctionRegistry {
    functionsByName = new Map();
    functionsById = new Map();
    idsByFunction = new Map();
    /** The next ID to use for a function. */
    id = 0;
    constructor(functionsBySignature) {
        for (const [signature, fn] of Object.entries(functionsBySignature ?? {})) {
            const openParen = signature.indexOf('(');
            if (openParen === -1) {
                throw new Error(`options.functions: "${signature}" is missing "("`);
            }
            this.functionsByName.set(signature.substring(0, openParen), fn);
        }
    }
    /** Registers `fn` as a function that can be called using the returned ID. */
    register(fn) {
        return utils.putIfAbsent(this.idsByFunction, fn, () => {
            const id = this.id;
            this.id += 1;
            this.functionsById.set(id, fn);
            return id;
        });
    }
    /**
     * Returns the function to which `request` refers and returns its response.
     */
    call(request) {
        const protofier = new protofier_1.Protofier(this);
        const fn = this.get(request);
        return (0, utils_1.catchOr)(() => {
            return (0, utils_1.thenOr)(fn(request.arguments.map(value => protofier.deprotofy(value))), result => {
                if (!(result instanceof value_1.Value)) {
                    const name = request.identifier.case === 'name'
                        ? `"${request.identifier.value}"`
                        : 'anonymous function';
                    throw (`options.functions: ${name} returned non-Value: ` +
                        (0, util_1.inspect)(result));
                }
                return (0, protobuf_1.create)(proto.InboundMessage_FunctionCallResponseSchema, {
                    result: { case: 'success', value: protofier.protofy(result) },
                    accessedArgumentLists: protofier.accessedArgumentLists,
                });
            });
        }, error => (0, protobuf_1.create)(proto.InboundMessage_FunctionCallResponseSchema, {
            result: { case: 'error', value: `${error}` },
        }));
    }
    /** Returns the function to which `request` refers. */
    get(request) {
        if (request.identifier.case === 'name') {
            const fn = this.functionsByName.get(request.identifier.value);
            if (fn)
                return fn;
            throw (0, utils_1.compilerError)('Invalid OutboundMessage_FunctionCallRequest: there is no function ' +
                `named "${request.identifier.value}"`);
        }
        else if (request.identifier.case === 'functionId') {
            const fn = this.functionsById.get(request.identifier.value);
            if (fn)
                return fn;
            throw (0, utils_1.compilerError)('Invalid OutboundMessage_FunctionCallRequest: there is no function ' +
                `with ID "${request.identifier.value}"`);
        }
        else {
            throw (0, utils_1.compilerError)('Invalid OutboundMessage_FunctionCallRequest: function identifier is ' +
                'unset');
        }
    }
}
exports.FunctionRegistry = FunctionRegistry;
//# sourceMappingURL=function-registry.js.map