'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var lodashUnified = require('lodash-unified');
var shared = require('@vue/shared');

const unique = (arr) => [...new Set(arr)];
const castArray = (arr) => {
  if (!arr && arr !== 0)
    return [];
  return shared.isArray(arr) ? arr : [arr];
};

Object.defineProperty(exports, 'ensureArray', {
  enumerable: true,
  get: function () { return lodashUnified.castArray; }
});
exports.castArray = castArray;
exports.unique = unique;
//# sourceMappingURL=arrays.js.map
