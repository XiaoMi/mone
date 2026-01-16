"use strict";
// Copyright 2021 Google LLC. Use of this source code is governed by an
// MIT-style license that can be found in the LICENSE file or at
// https://opensource.org/licenses/MIT.
Object.defineProperty(exports, "__esModule", { value: true });
const worker_threads_1 = require("worker_threads");
const child_process_1 = require("child_process");
const assert_1 = require("assert");
const sync_message_port_1 = require("sync-message-port");
const port = new sync_message_port_1.SyncMessagePort(worker_threads_1.workerData.port);
/** A more type-safe way to call `port.postMesage()` */
function emit(event, transferList) {
    port.postMessage(event, transferList);
}
const process = (0, child_process_1.spawn)(worker_threads_1.workerData.command, worker_threads_1.workerData.args, worker_threads_1.workerData.options);
port.on('message', message => {
    if (message.type === 'stdin') {
        process.stdin.write(message.data);
    }
    else if (message.type === 'stdinClosed') {
        process.stdin.end();
    }
    else {
        assert_1.strict.equal(message.type, 'kill');
        process.kill(message.signal);
    }
});
process.stdout.on('data', data => {
    emit({ type: 'stdout', data }, [data.buffer]);
});
process.stderr.on('data', data => {
    emit({ type: 'stderr', data }, [data.buffer]);
});
process.on('error', error => {
    emit({ type: 'error', error });
    process.kill();
    worker_threads_1.parentPort.close();
    port.close();
});
process.on('exit', (code, signal) => {
    if (code !== null) {
        emit({ type: 'exit', code });
    }
    else {
        (0, assert_1.strict)(signal);
        emit({ type: 'exit', signal });
    }
    worker_threads_1.parentPort.close();
    port.close();
});
//# sourceMappingURL=worker.js.map