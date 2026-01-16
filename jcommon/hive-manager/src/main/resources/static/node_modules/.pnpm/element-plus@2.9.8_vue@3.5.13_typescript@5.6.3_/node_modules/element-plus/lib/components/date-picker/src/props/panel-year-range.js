'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var shared = require('./shared.js');
var runtime = require('../../../../utils/vue/props/runtime.js');

const panelYearRangeProps = runtime.buildProps({
  ...shared.panelRangeSharedProps
});
const panelYearRangeEmits = [
  "pick",
  "set-picker-option",
  "calendar-change"
];

exports.panelYearRangeEmits = panelYearRangeEmits;
exports.panelYearRangeProps = panelYearRangeProps;
//# sourceMappingURL=panel-year-range.js.map
