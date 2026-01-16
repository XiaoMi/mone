"use strict";
// Copyright 2024 Google LLC. Use of this source code is governed by an
// MIT-style license that can be found in the LICENSE file or at
// https://opensource.org/licenses/MIT.
Object.defineProperty(exports, "__esModule", { value: true });
exports.Compiler = void 0;
exports.initCompiler = initCompiler;
const rxjs_1 = require("rxjs");
const sync_child_process_1 = require("sync-child-process");
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
 * Flag allowing the constructor passed by `initCompiler` so we can
 * differentiate and throw an error if the `Compiler` is constructed via `new
 * Compiler`.
 */
const initFlag = Symbol();
/** A synchronous wrapper for the embedded Sass compiler */
class Compiler {
    /** The underlying process that's being wrapped. */
    process = new sync_child_process_1.SyncChildProcess(compiler_path_1.compilerCommand[0], [...compiler_path_1.compilerCommand.slice(1), '--embedded'], {
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
    /** A list of active dispatchers. */
    dispatchers = new Set();
    /** The buffers emitted by the child process's stdout. */
    stdout$ = new rxjs_1.Subject();
    /** The buffers emitted by the child process's stderr. */
    stderr$ = new rxjs_1.Subject();
    /** Whether the underlying compiler has already exited. */
    disposed = false;
    /** Reusable message transformer for all compilations.  */
    messageTransformer;
    /** Writes `buffer` to the child process's stdin. */
    writeStdin(buffer) {
        this.process.stdin.write(buffer);
    }
    /** Yields the next event from the underlying process. */
    yield() {
        const result = this.process.next();
        if (result.done) {
            this.disposed = true;
            return false;
        }
        const event = result.value;
        switch (event.type) {
            case 'stdout':
                this.stdout$.next(event.data);
                return true;
            case 'stderr':
                this.stderr$.next(event.data);
                return true;
        }
    }
    /** Blocks until the underlying process exits. */
    yieldUntilExit() {
        while (!this.disposed) {
            this.yield();
        }
    }
    /**
     * Sends a compile request to the child process and returns the CompileResult.
     * Throws if there were any protocol or compilation errors.
     */
    compileRequestSync(request, importers, options) {
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
            this.dispatchers.add(dispatcher);
            dispatcher.logEvents$.subscribe(event => (0, utils_1.handleLogEvent)(options, event));
            let error;
            let response;
            dispatcher.sendCompileRequest(request, (error_, response_) => {
                this.dispatchers.delete(dispatcher);
                // Reset the compilation ID when the compiler goes idle (no active
                // dispatchers) to avoid overflowing it.
                // https://github.com/sass/embedded-host-node/pull/261#discussion_r1429266794
                if (this.dispatchers.size === 0)
                    this.compilationId = 1;
                if (error_) {
                    error = error_;
                }
                else {
                    response = response_;
                }
            });
            for (;;) {
                if (!this.yield()) {
                    throw utils.compilerError('Embedded compiler exited unexpectedly.');
                }
                if (error)
                    throw error;
                if (response)
                    return (0, utils_1.handleCompileResponse)(response);
            }
        }
        finally {
            deprecations_1.activeDeprecationOptions.delete(optionsKey);
        }
    }
    /** Guards against using a disposed compiler. */
    throwIfDisposed() {
        if (this.disposed) {
            throw utils.compilerError('Sync compiler has already been disposed.');
        }
    }
    /** Initialize resources shared across compilations. */
    constructor(flag) {
        if (flag !== initFlag) {
            throw utils.compilerError('Compiler can not be directly constructed. ' +
                'Please use `sass.initAsyncCompiler()` instead.');
        }
        this.stderr$.subscribe(data => process.stderr.write(data));
        const packetTransformer = new packet_transformer_1.PacketTransformer(this.stdout$, buffer => {
            this.writeStdin(buffer);
        });
        this.messageTransformer = new message_transformer_1.MessageTransformer(packetTransformer.outboundProtobufs$, packet => packetTransformer.writeInboundProtobuf(packet));
    }
    compile(path, options) {
        this.throwIfDisposed();
        const importers = new importer_registry_1.ImporterRegistry(options);
        return this.compileRequestSync((0, utils_1.newCompilePathRequest)(path, importers, options), importers, options);
    }
    compileString(source, options) {
        this.throwIfDisposed();
        const importers = new importer_registry_1.ImporterRegistry(options);
        return this.compileRequestSync((0, utils_1.newCompileStringRequest)(source, importers, options), importers, options);
    }
    dispose() {
        this.process.stdin.end();
        this.yieldUntilExit();
    }
}
exports.Compiler = Compiler;
function initCompiler() {
    return new Compiler(initFlag);
}
//# sourceMappingURL=sync.js.map