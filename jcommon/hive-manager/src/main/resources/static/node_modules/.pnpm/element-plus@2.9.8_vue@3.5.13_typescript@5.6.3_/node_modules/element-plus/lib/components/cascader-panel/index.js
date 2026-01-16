'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var index = require('./src/index.js');
var types = require('./src/types.js');
var config = require('./src/config.js');
var install = require('../../utils/vue/install.js');

const ElCascaderPanel = install.withInstall(index["default"]);

exports.CASCADER_PANEL_INJECTION_KEY = types.CASCADER_PANEL_INJECTION_KEY;
exports.CommonProps = config.CommonProps;
exports.DefaultProps = config.DefaultProps;
exports.useCascaderConfig = config.useCascaderConfig;
exports.ElCascaderPanel = ElCascaderPanel;
exports["default"] = ElCascaderPanel;
//# sourceMappingURL=index.js.map
