"use strict";
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
const clipboard_1 = __importDefault(require("clipboard"));
exports.default = (opts) => {
    // default appendToBody true
    const appendToBody = (opts === null || opts === void 0 ? void 0 : opts.appendToBody) === undefined ? true : opts.appendToBody;
    return {
        toClipboard(text, container) {
            return new Promise((resolve, reject) => {
                // make fake element
                const fakeEl = document.createElement('button');
                // setup a new Clipboard.js
                const clipboard = new clipboard_1.default(fakeEl, {
                    text: () => text,
                    action: () => 'copy',
                    container: container !== undefined ? container : document.body
                });
                clipboard.on('success', (e) => {
                    clipboard.destroy();
                    resolve(e);
                });
                clipboard.on('error', (e) => {
                    clipboard.destroy();
                    reject(e);
                });
                // appendToBody fixes IE
                if (appendToBody)
                    document.body.appendChild(fakeEl);
                // simulate click
                fakeEl.click();
                // remove from body if appended
                if (appendToBody)
                    document.body.removeChild(fakeEl);
            });
        }
    };
};
//# sourceMappingURL=index.js.map