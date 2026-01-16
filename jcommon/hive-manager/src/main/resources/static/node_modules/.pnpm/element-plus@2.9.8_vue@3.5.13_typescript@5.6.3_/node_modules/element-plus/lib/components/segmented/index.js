'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var segmented$1 = require('./src/segmented2.js');
var segmented = require('./src/segmented.js');
var install = require('../../utils/vue/install.js');

const ElSegmented = install.withInstall(segmented$1["default"]);

exports.defaultProps = segmented.defaultProps;
exports.segmentedEmits = segmented.segmentedEmits;
exports.segmentedProps = segmented.segmentedProps;
exports.ElSegmented = ElSegmented;
exports["default"] = ElSegmented;
//# sourceMappingURL=index.js.map
