'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var cascader$1 = require('./src/cascader2.js');
var cascader = require('./src/cascader.js');
var install = require('../../utils/vue/install.js');

const ElCascader = install.withInstall(cascader$1["default"]);

exports.cascaderEmits = cascader.cascaderEmits;
exports.cascaderProps = cascader.cascaderProps;
exports.ElCascader = ElCascader;
exports["default"] = ElCascader;
//# sourceMappingURL=index.js.map
