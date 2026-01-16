'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var runtime = require('../../../utils/vue/props/runtime.js');
var index = require('../../../hooks/use-size/index.js');
var index$1 = require('../../../hooks/use-aria/index.js');
var event = require('../../../constants/event.js');
var shared = require('@vue/shared');
var types = require('../../../utils/types.js');

const defaultProps = {
  label: "label",
  value: "value",
  disabled: "disabled"
};
const segmentedProps = runtime.buildProps({
  direction: {
    type: runtime.definePropType(String),
    default: "horizontal"
  },
  options: {
    type: runtime.definePropType(Array),
    default: () => []
  },
  modelValue: {
    type: [String, Number, Boolean],
    default: void 0
  },
  props: {
    type: runtime.definePropType(Object),
    default: () => defaultProps
  },
  block: Boolean,
  size: index.useSizeProp,
  disabled: Boolean,
  validateEvent: {
    type: Boolean,
    default: true
  },
  id: String,
  name: String,
  ...index$1.useAriaProps(["ariaLabel"])
});
const segmentedEmits = {
  [event.UPDATE_MODEL_EVENT]: (val) => shared.isString(val) || types.isNumber(val) || types.isBoolean(val),
  [event.CHANGE_EVENT]: (val) => shared.isString(val) || types.isNumber(val) || types.isBoolean(val)
};

exports.defaultProps = defaultProps;
exports.segmentedEmits = segmentedEmits;
exports.segmentedProps = segmentedProps;
//# sourceMappingURL=segmented.js.map
