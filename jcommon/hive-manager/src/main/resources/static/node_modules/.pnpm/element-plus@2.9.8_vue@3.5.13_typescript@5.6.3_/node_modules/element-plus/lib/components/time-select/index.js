'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var timeSelect$1 = require('./src/time-select2.js');
var timeSelect = require('./src/time-select.js');
var install = require('../../utils/vue/install.js');

const ElTimeSelect = install.withInstall(timeSelect$1["default"]);

exports.timeSelectProps = timeSelect.timeSelectProps;
exports.ElTimeSelect = ElTimeSelect;
exports["default"] = ElTimeSelect;
//# sourceMappingURL=index.js.map
