"use strict";
// Copyright 2024 Google LLC. Use of this source code is governed by an
// MIT-style license that can be found in the LICENSE file or at
// https://opensource.org/licenses/MIT.
Object.defineProperty(exports, "__esModule", { value: true });
exports.createDispatcher = createDispatcher;
exports.newCompilePathRequest = newCompilePathRequest;
exports.newCompileStringRequest = newCompileStringRequest;
exports.handleLogEvent = handleLogEvent;
exports.handleCompileResponse = handleCompileResponse;
const p = require("path");
const supportsColor = require("supports-color");
const protobuf_1 = require("@bufbuild/protobuf");
const deprecations_1 = require("../deprecations");
const deprotofy_span_1 = require("../deprotofy-span");
const dispatcher_1 = require("../dispatcher");
const exception_1 = require("../exception");
const utils_1 = require("../legacy/utils");
const logger_1 = require("../logger");
const utils = require("../utils");
const proto = require("../vendor/embedded_sass_pb");
/**
 * Creates a dispatcher that dispatches messages from the given `stdout` stream.
 */
function createDispatcher(compilationId, messageTransformer, handlers) {
    return new dispatcher_1.Dispatcher(compilationId, messageTransformer.outboundMessages$, message => messageTransformer.writeInboundMessage(message), handlers);
}
// Creates a compilation request for the given `options` without adding any
// input-specific options.
function newCompileRequest(importers, options) {
    const request = (0, protobuf_1.create)(proto.InboundMessage_CompileRequestSchema, {
        importers: importers.importers,
        globalFunctions: Object.keys(options?.functions ?? {}),
        sourceMap: !!options?.sourceMap,
        sourceMapIncludeSources: !!options?.sourceMapIncludeSources,
        alertColor: options?.alertColor ?? !!supportsColor.stdout,
        alertAscii: !!options?.alertAscii,
        quietDeps: !!options?.quietDeps,
        verbose: !!options?.verbose,
        charset: !!(options?.charset ?? true),
        silent: options?.logger === logger_1.Logger.silent,
        fatalDeprecation: (0, deprecations_1.getDeprecationIds)(options?.fatalDeprecations ?? []),
        silenceDeprecation: (0, deprecations_1.getDeprecationIds)(options?.silenceDeprecations ?? []),
        futureDeprecation: (0, deprecations_1.getDeprecationIds)(options?.futureDeprecations ?? []),
    });
    switch (options?.style ?? 'expanded') {
        case 'expanded':
            request.style = proto.OutputStyle.EXPANDED;
            break;
        case 'compressed':
            request.style = proto.OutputStyle.COMPRESSED;
            break;
        default:
            throw new Error(`Unknown options.style: "${options?.style}"`);
    }
    return request;
}
// Creates a request for compiling a file.
function newCompilePathRequest(path, importers, options) {
    const absPath = p.resolve(path);
    const request = newCompileRequest(importers, options);
    request.input = { case: 'path', value: absPath };
    return request;
}
// Creates a request for compiling a string.
function newCompileStringRequest(source, importers, options) {
    const input = (0, protobuf_1.create)(proto.InboundMessage_CompileRequest_StringInputSchema, {
        source,
        syntax: utils.protofySyntax(options?.syntax ?? 'scss'),
    });
    const url = options?.url?.toString();
    if (url && url !== utils_1.legacyImporterProtocol) {
        input.url = url;
    }
    if (options && 'importer' in options && options.importer) {
        input.importer = importers.register(options.importer);
    }
    else if (url === utils_1.legacyImporterProtocol) {
        input.importer = (0, protobuf_1.create)(proto.InboundMessage_CompileRequest_ImporterSchema, {
            importer: { case: 'path', value: p.resolve('.') },
        });
    }
    else {
        // When importer is not set on the host, the compiler will set a
        // FileSystemImporter if `url` is set to a file: url or a NoOpImporter.
    }
    const request = newCompileRequest(importers, options);
    request.input = { case: 'string', value: input };
    return request;
}
/** Type guard to check that `id` is a valid deprecation ID. */
function validDeprecationId(id) {
    return !!id && id in deprecations_1.deprecations;
}
/** Handles a log event according to `options`. */
function handleLogEvent(options, event) {
    let span = event.span ? (0, deprotofy_span_1.deprotofySourceSpan)(event.span) : null;
    if (span && options?.legacy)
        span = (0, utils_1.removeLegacyImporterFromSpan)(span);
    let message = event.message;
    if (options?.legacy)
        message = (0, utils_1.removeLegacyImporter)(message);
    let formatted = event.formatted;
    if (options?.legacy)
        formatted = (0, utils_1.removeLegacyImporter)(formatted);
    const deprecationType = validDeprecationId(event.deprecationType)
        ? deprecations_1.deprecations[event.deprecationType]
        : null;
    if (event.type === proto.LogEventType.DEBUG) {
        if (options?.logger?.debug) {
            options.logger.debug(message, {
                span: span,
            });
        }
        else {
            console.error(formatted);
        }
    }
    else {
        if (options?.logger?.warn) {
            const params = deprecationType
                ? { deprecation: true, deprecationType: deprecationType }
                : { deprecation: false };
            if (span)
                params.span = span;
            const stack = event.stackTrace;
            if (stack) {
                params.stack = options?.legacy ? (0, utils_1.removeLegacyImporter)(stack) : stack;
            }
            options.logger.warn(message, params);
        }
        else {
            console.error(formatted);
        }
    }
}
/**
 * Converts a `CompileResponse` into a `CompileResult`.
 *
 * Throws a `SassException` if the compilation failed.
 */
function handleCompileResponse(response) {
    if (response.result.case === 'success') {
        const success = response.result.value;
        const result = {
            css: success.css,
            loadedUrls: response.loadedUrls.map(url => new URL(url)),
        };
        const sourceMap = success.sourceMap;
        if (sourceMap)
            result.sourceMap = JSON.parse(sourceMap);
        return result;
    }
    else if (response.result.case === 'failure') {
        throw new exception_1.Exception(response.result.value);
    }
    else {
        throw utils.compilerError('Compiler sent empty CompileResponse.');
    }
}
//# sourceMappingURL=utils.js.map