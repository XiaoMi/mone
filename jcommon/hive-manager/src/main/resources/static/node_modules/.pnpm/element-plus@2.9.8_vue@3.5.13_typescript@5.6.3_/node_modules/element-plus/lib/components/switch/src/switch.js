'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var validator = require('../../../utils/vue/validator.js');
var runtime = require('../../../utils/vue/props/runtime.js');
var icon = require('../../../utils/vue/icon.js');
var index = require('../../../hooks/use-aria/index.js');
var event = require('../../../constants/event.js');
var types = require('../../../utils/types.js');
var shared = require('@vue/shared');

const switchProps = runtime.buildProps({
  modelValue: {
    type: [Boolean, String, Number],
    default: false
  },
  disabled: Boolean,
  loading: Boolean,
  size: {
    type: String,
    validator: validator.isValidComponentSize
  },
  width: {
    type: [String, Number],
    default: ""
  },
  inlinePrompt: Boolean,
  inactiveActionIcon: {
    type: icon.iconPropType
  },
  activeActionIcon: {
    type: icon.iconPropType
  },
  activeIcon: {
    type: icon.iconPropType
  },
  inactiveIcon: {
    type: icon.iconPropType
  },
  activeText: {
    type: String,
    default: ""
  },
  inactiveText: {
    type: String,
    default: ""
  },
  activeValue: {
    type: [Boolean, String, Number],
    default: true
  },
  inactiveValue: {
    type: [Boolean, String, Number],
    default: false
  },
  name: {
    type: String,
    default: ""
  },
  validateEvent: {
    type: Boolean,
    default: true
  },
  beforeChange: {
    type: runtime.definePropType(Function)
  },
  id: String,
  tabindex: {
    type: [String, Number]
  },
  ...index.useAriaProps(["ariaLabel"])
});
const switchEmits = {
  [event.UPDATE_MODEL_EVENT]: (val) => types.isBoolean(val) || shared.isString(val) || types.isNumber(val),
  [event.CHANGE_EVENT]: (val) => types.isBoolean(val) || shared.isString(val) || types.isNumber(val),
  [event.INPUT_EVENT]: (val) => types.isBoolean(val) || shared.isString(val) || types.isNumber(val)
};

exports.switchEmits = switchEmits;
exports.switchProps = switchProps;
//# sourceMappingURL=switch.js.map
