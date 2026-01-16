'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var datePicker$1 = require('./src/date-picker.js');
var constants = require('./src/constants.js');
var datePicker = require('./src/props/date-picker.js');
var install = require('../../utils/vue/install.js');

const ElDatePicker = install.withInstall(datePicker$1["default"]);

exports.ROOT_PICKER_INJECTION_KEY = constants.ROOT_PICKER_INJECTION_KEY;
exports.datePickerProps = datePicker.datePickerProps;
exports.ElDatePicker = ElDatePicker;
exports["default"] = ElDatePicker;
//# sourceMappingURL=index.js.map
