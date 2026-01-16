"use strict";
// Copyright 2021 Google LLC. Use of this source code is governed by an
// MIT-style license that can be found in the LICENSE file or at
// https://opensource.org/licenses/MIT.
Object.defineProperty(exports, "__esModule", { value: true });
exports.compilerCommand = void 0;
const fs = require("fs");
const p = require("path");
const elf_1 = require("./elf");
const utils_1 = require("./utils");
/**
 * Detect if the given binary is linked with musl libc by checking if
 * the interpreter basename starts with "ld-musl-"
 */
function isLinuxMusl(path) {
    try {
        const interpreter = (0, elf_1.getElfInterpreter)(path);
        return p.basename(interpreter).startsWith('ld-musl-');
    }
    catch (error) {
        console.warn(`Warning: Failed to detect linux-musl, fallback to linux-gnu: ${error.message}`);
        return false;
    }
}
/** The full command for the embedded compiler executable. */
exports.compilerCommand = (() => {
    const platform = process.platform === 'linux' && isLinuxMusl(process.execPath)
        ? 'linux-musl'
        : process.platform;
    const arch = process.arch;
    // find for development
    for (const path of ['vendor', '../../../lib/src/vendor']) {
        const executable = p.resolve(__dirname, path, `dart-sass/sass${platform === 'win32' ? '.bat' : ''}`);
        if (fs.existsSync(executable))
            return [executable];
    }
    try {
        return [
            require.resolve(`sass-embedded-${platform}-${arch}/dart-sass/src/dart` +
                (platform === 'win32' ? '.exe' : '')),
            require.resolve(`sass-embedded-${platform}-${arch}/dart-sass/src/sass.snapshot`),
        ];
    }
    catch (ignored) {
        // ignored
    }
    try {
        return [
            require.resolve(`sass-embedded-${platform}-${arch}/dart-sass/sass` +
                (platform === 'win32' ? '.bat' : '')),
        ];
    }
    catch (e) {
        if (!((0, utils_1.isErrnoException)(e) && e.code === 'MODULE_NOT_FOUND')) {
            throw e;
        }
    }
    throw new Error("Embedded Dart Sass couldn't find the embedded compiler executable. " +
        'Please make sure the optional dependency ' +
        `sass-embedded-${platform}-${arch} is installed in ` +
        'node_modules.');
})();
//# sourceMappingURL=compiler-path.js.map