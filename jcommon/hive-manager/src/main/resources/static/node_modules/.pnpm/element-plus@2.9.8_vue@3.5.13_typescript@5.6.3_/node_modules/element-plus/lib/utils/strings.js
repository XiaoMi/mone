'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var shared = require('@vue/shared');

const kebabCase = shared.hyphenate;
const escapeStringRegexp = (string = "") => string.replace(/[|\\{}()[\]^$+*?.]/g, "\\$&").replace(/-/g, "\\x2d");
const capitalize = (str) => shared.capitalize(str);

Object.defineProperty(exports, 'camelize', {
  enumerable: true,
  get: function () { return shared.camelize; }
});
Object.defineProperty(exports, 'hyphenate', {
  enumerable: true,
  get: function () { return shared.hyphenate; }
});
exports.capitalize = capitalize;
exports.escapeStringRegexp = escapeStringRegexp;
exports.kebabCase = kebabCase;
//# sourceMappingURL=strings.js.map
