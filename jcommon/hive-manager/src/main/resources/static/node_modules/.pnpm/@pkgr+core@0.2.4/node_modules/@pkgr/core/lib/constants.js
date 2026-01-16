import { createRequire } from 'node:module';
export const CWD = process.cwd();
export const cjsRequire = typeof require === 'undefined' ? createRequire(import.meta.url) : require;
export const EVAL_FILENAMES = new Set(['[eval]', '[worker eval]']);
export const EXTENSIONS = ['.ts', '.tsx', ...Object.keys(cjsRequire.extensions)];
//# sourceMappingURL=constants.js.map