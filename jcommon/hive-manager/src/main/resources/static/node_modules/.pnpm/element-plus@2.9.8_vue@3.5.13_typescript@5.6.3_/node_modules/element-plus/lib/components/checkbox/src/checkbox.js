'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var index = require('../../../hooks/use-size/index.js');
var index$1 = require('../../../hooks/use-aria/index.js');
var event = require('../../../constants/event.js');
var shared = require('@vue/shared');
var types = require('../../../utils/types.js');

const checkboxProps = {
  modelValue: {
    type: [Number, String, Boolean],
    default: void 0
  },
  label: {
    type: [String, Boolean, Number, Object],
    default: void 0
  },
  value: {
    type: [String, Boolean, Number, Object],
    default: void 0
  },
  indeterminate: Boolean,
  disabled: Boolean,
  checked: Boolean,
  name: {
    type: String,
    default: void 0
  },
  trueValue: {
    type: [String, Number],
    default: void 0
  },
  falseValue: {
    type: [String, Number],
    default: void 0
  },
  trueLabel: {
    type: [String, Number],
    default: void 0
  },
  falseLabel: {
    type: [String, Number],
    default: void 0
  },
  id: {
    type: String,
    default: void 0
  },
  border: Boolean,
  size: index.useSizeProp,
  tabindex: [String, Number],
  validateEvent: {
    type: Boolean,
    default: true
  },
  ...index$1.useAriaProps(["ariaControls"])
};
const checkboxEmits = {
  [event.UPDATE_MODEL_EVENT]: (val) => shared.isString(val) || types.isNumber(val) || types.isBoolean(val),
  change: (val) => shared.isString(val) || types.isNumber(val) || types.isBoolean(val)
};

exports.checkboxEmits = checkboxEmits;
exports.checkboxProps = checkboxProps;
//# sourceMappingURL=checkbox.js.map
