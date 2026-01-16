"use strict";
// Copyright 2024 Google LLC. Use of this source code is governed by an
// MIT-style license that can be found in the LICENSE file or at
// https://opensource.org/licenses/MIT.
Object.defineProperty(exports, "__esModule", { value: true });
exports.AsyncCompiler = void 0;
exports.initAsyncCompiler = initAsyncCompiler;
const child_process_1 = require("child_process");
const rxjs_1 = require("rxjs");
const operators_1 = require("rxjs/operators");
const path = require("path");
const utils_1 = require("./utils");
const compiler_path_1 = require("../compiler-path");
const deprecations_1 = require("../deprecations");
const function_registry_1 = require("../function-registry");
const importer_registry_1 = require("../importer-registry");
const message_transformer_1 = require("../message-transformer");
const packet_transformer_1 = require("../packet-transformer");
const utils = require("../utils");
/**
 * Flag allowing the constructor passed by `initAsyncCompiler` so we can
 * differentiate and throw an error if the `AsyncCompiler` is constructed via
 * `new AsyncCompiler`.
 */
const initFlag = Symbol();
/** An asynchronous wrapper for the embedded Sass compiler */
class AsyncCompiler {
    /** The underlying process that's being wrapped. */
    process = (0, child_process_1.spawn)(compiler_path_1.compilerCommand[0], [...compiler_path_1.compilerCommand.slice(1), '--embedded'], {
        // Use the command's cwd so the compiler survives the removal of the
        // current working directory.
        // https://github.com/sass/embedded-host-node/pull/261#discussion_r1438712923
        cwd: path.dirname(compiler_path_1.compilerCommand[0]),
        // Node blocks launching .bat and .cmd without a shell due to CVE-2024-27980
        shell: ['.bat', '.cmd'].includes(path.extname(compiler_path_1.compilerCommand[0]).toLowerCase()),
        windowsHide: true,
    });
    /** The next compilation ID. */
    compilationId = 1;
    /** A list of active compilations. */
    compilations = new Set();
    /** Whether the underlying compiler has already exited. */
    disposed = false;
    /** Reusable message transformer for all compilations.  */
    messageTransformer;
    /** The child process's exit event. */
    exit$ = new Promise(resolve => {
        this.process.on('exit', code => resolve(code));
    });
    /** The buffers emitted by the child process's stdout. */
    stdout$ = new rxjs_1.Observable(observer => {
        this.process.stdout.on('data', buffer => observer.next(buffer));
    }).pipe((0, operators_1.takeUntil)(this.exit$));
    /** The buffers emitted by the child process's stderr. */
    stderr$ = new rxjs_1.Observable(observer => {
        this.process.stderr.on('data', buffer => observer.next(buffer));
    }).pipe((0, operators_1.takeUntil)(this.exit$));
    /** Writes `buffer` to the child process's stdin. */
    writeStdin(buffer) {
        this.process.stdin.write(buffer);
    }
    /** Guards against using a disposed compiler. */
    throwIfDisposed() {
        if (this.disposed) {
            throw utils.compilerError('Async compiler has already been disposed.');
        }
    }
    /**
     * Sends a compile request to the child process and returns a Promise that
     * resolves with the CompileResult. Rejects the promise if there were any
     * protocol or compilation errors.
     */
    async compileRequestAsync(request, importers, options) {
        const optionsKey = Symbol();
        deprecations_1.activeDeprecationOptions.set(optionsKey, options ?? {});
        try {
            const functions = new function_registry_1.FunctionRegistry(options?.functions);
            const dispatcher = (0, utils_1.createDispatcher)(this.compilationId++, this.messageTransformer, {
                handleImportRequest: request => importers.import(request),
                handleFileImportRequest: request => importers.fileImport(request),
                handleCanonicalizeRequest: request => importers.canonicalize(request),
                handleFunctionCallRequest: request => functions.call(request),
            });
            dispatcher.logEvents$.subscribe(event => (0, utils_1.handleLogEvent)(options, event));
            const compilation = new Promise((resolve, reject) => dispatcher.sendCompileRequest(request, (err, response) => {
                this.compilations.delete(compilation);
                // Reset the compilation ID when the compiler goes idle (no active
                // compilations) to avoid overflowing it.
                // https://github.com/sass/embedded-host-node/pull/261#discussion_r1429266794
                if (this.compilations.size === 0)
                    this.compilationId = 1;
                if (err) {
                    reject(err);
                }
                else {
                    resolve(response);
                }
            }));
            this.compilations.add(compilation);
            return (0, utils_1.handleCompileResponse)(await compilation);
        }
        finally {
            deprecations_1.activeDeprecationOptions.delete(optionsKey);
        }
    }
    /** Initialize resources shared across compilations. */
    constructor(flag) {
        if (flag !== initFlag) {
            throw utils.compilerError('AsyncCompiler can not be directly constructed. ' +
                'Please use `sass.initAsyncCompiler()` instead.');
        }
        this.stderr$.subscribe(data => process.stderr.write(data));
        const packetTransformer = new packet_transformer_1.PacketTransformer(this.stdout$, buffer => {
            this.writeStdin(buffer);
        });
        this.messageTransformer = new message_transformer_1.MessageTransformer(packetTransformer.outboundProtobufs$, packet => packetTransformer.writeInboundProtobuf(packet));
    }
    compileAsync(path, options) {
        this.throwIfDisposed();
        const importers = new importer_registry_1.ImporterRegistry(options);
        return this.compileRequestAsync((0, utils_1.newCompilePathRequest)(path, importers, options), importers, options);
    }
    compileStringAsync(source, options) {
        this.throwIfDisposed();
        const importers = new importer_registry_1.ImporterRegistry(options);
        return this.compileRequestAsync((0, utils_1.newCompileStringRequest)(source, importers, options), importers, options);
    }
    async dispose() {
        this.disposed = true;
        await Promise.all(this.compilations);
        this.process.stdin.end();
        await this.exit$;
    }
}
exports.AsyncCompiler = AsyncCompiler;
async function initAsyncCompiler() {
    return new AsyncCompiler(initFlag);
}
//# sourceMappingURL=async.js.map