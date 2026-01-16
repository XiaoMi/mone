"use strict";
// Copyright 2020 Google Inc. Use of this source code is governed by an
// MIT-style license that can be found in the LICENSE file or at
// https://opensource.org/licenses/MIT.
Object.defineProperty(exports, "__esModule", { value: true });
exports.BUILD_PATH = void 0;
exports.fetchRepo = fetchRepo;
exports.link = link;
exports.cleanDir = cleanDir;
exports.sameTarget = sameTarget;
const fs_1 = require("fs");
const p = require("path");
const shell = require("shelljs");
shell.config.fatal = true;
// Directory that holds source files.
exports.BUILD_PATH = 'build';
// Clones `repo` into `outPath`, then checks out the given Git `ref`.
function fetchRepo(options) {
    const path = p.join(options.outPath, options.repo);
    if ((0, fs_1.existsSync)(p.join(path, '.git')) && (0, fs_1.lstatSync)(path).isSymbolicLink()) {
        throw (`${path} is a symlink to a git repo, not overwriting.\n` +
            `Run "rm ${path}" and try again.`);
    }
    if (!(0, fs_1.existsSync)(path)) {
        console.log(`Cloning ${options.repo} into ${options.outPath}.`);
        shell.exec(`git clone \
      --depth=1 \
      https://github.com/sass/${options.repo} \
      ${path}`);
    }
    const version = options.ref === 'main' ? 'latest update' : `commit ${options.ref}`;
    console.log(`Fetching ${version} for ${options.repo}.`);
    shell.exec(`git fetch --depth=1 origin ${options.ref} && git reset --hard FETCH_HEAD`, { cwd: path });
}
// Links or copies the contents of `source` into `destination`.
async function link(source, destination) {
    await cleanDir(destination);
    if (process.platform === 'win32') {
        console.log(`Copying ${source} into ${destination}.`);
        shell.cp('-R', source, destination);
    }
    else {
        source = p.resolve(source);
        console.log(`Linking ${source} into ${destination}.`);
        // Symlinking doesn't play nice with Jasmine's test globbing on Windows.
        await fs_1.promises.symlink(source, destination);
    }
}
// Ensures that `dir` does not exist, but its parent directory does.
async function cleanDir(dir) {
    await fs_1.promises.mkdir(p.dirname(dir), { recursive: true });
    try {
        await fs_1.promises.rm(dir, { force: true, recursive: true });
    }
    catch (_) {
        // If dir doesn't exist yet, that's fine.
    }
}
// Returns whether [path1] and [path2] are symlinks that refer to the same file.
async function sameTarget(path1, path2) {
    const realpath1 = await tryRealpath(path1);
    if (realpath1 === null)
        return false;
    return realpath1 === (await tryRealpath(path2));
}
// Like `fs.realpath()`, but returns `null` if the path doesn't exist on disk.
async function tryRealpath(path) {
    try {
        return await fs_1.promises.realpath(p.resolve(path));
    }
    catch (_) {
        return null;
    }
}
//# sourceMappingURL=utils.js.map