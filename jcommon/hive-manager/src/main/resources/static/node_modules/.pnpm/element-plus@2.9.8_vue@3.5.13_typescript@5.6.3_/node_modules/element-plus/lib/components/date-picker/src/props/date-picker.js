'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var props = require('../../../time-picker/src/common/props.js');
var runtime = require('../../../../utils/vue/props/runtime.js');

const datePickerProps = runtime.buildProps({
  ...props.timePickerDefaultProps,
  type: {
    type: runtime.definePropType(String),
    default: "date"
  }
});

exports.datePickerProps = datePickerProps;
//# sourceMappingURL=date-picker.js.map
