'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var shared = require('./shared.js');
var runtime = require('../../../../utils/vue/props/runtime.js');

const basicMonthTableProps = runtime.buildProps({
  ...shared.datePickerSharedProps,
  selectionMode: shared.selectionModeWithDefault("month")
});

exports.basicMonthTableProps = basicMonthTableProps;
//# sourceMappingURL=basic-month-table.js.map
