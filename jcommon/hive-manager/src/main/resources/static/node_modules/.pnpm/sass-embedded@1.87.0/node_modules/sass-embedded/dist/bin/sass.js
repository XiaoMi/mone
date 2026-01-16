#!/usr/bin/env node
"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const child_process = require("child_process");
const compiler_path_1 = require("../lib/src/compiler-path");
// TODO npm/cmd-shim#152 and yarnpkg/berry#6422 - If and when the package
// managers support it, we should make this a proper shell script rather than a
// JS wrapper.
try {
    child_process.execFileSync(compiler_path_1.compilerCommand[0], [...compiler_path_1.compilerCommand.slice(1), ...process.argv.slice(2)], {
        stdio: 'inherit',
        windowsHide: true,
    });
}
catch (error) {
    if (error.code) {
        throw error;
    }
    else {
        process.exitCode = error.status;
    }
}
//# sourceMappingURL=sass.js.map