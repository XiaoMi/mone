'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var runtime = require('../../../utils/vue/props/runtime.js');
var index = require('../../../hooks/use-size/index.js');
var event = require('../../../constants/event.js');
var shared = require('@vue/shared');
var types = require('../../../utils/types.js');

const radioPropsBase = runtime.buildProps({
  modelValue: {
    type: [String, Number, Boolean],
    default: void 0
  },
  size: index.useSizeProp,
  disabled: Boolean,
  label: {
    type: [String, Number, Boolean],
    default: void 0
  },
  value: {
    type: [String, Number, Boolean],
    default: void 0
  },
  name: {
    type: String,
    default: void 0
  }
});
const radioProps = runtime.buildProps({
  ...radioPropsBase,
  border: Boolean
});
const radioEmits = {
  [event.UPDATE_MODEL_EVENT]: (val) => shared.isString(val) || types.isNumber(val) || types.isBoolean(val),
  [event.CHANGE_EVENT]: (val) => shared.isString(val) || types.isNumber(val) || types.isBoolean(val)
};

exports.radioEmits = radioEmits;
exports.radioProps = radioProps;
exports.radioPropsBase = radioPropsBase;
//# sourceMappingURL=radio.js.map
