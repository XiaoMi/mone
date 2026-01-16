'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var select = require('./src/select.js');
var token = require('./src/token.js');
var install = require('../../utils/vue/install.js');

const ElSelectV2 = install.withInstall(select["default"]);

exports.selectV2InjectionKey = token.selectV2InjectionKey;
exports.ElSelectV2 = ElSelectV2;
exports["default"] = ElSelectV2;
//# sourceMappingURL=index.js.map
