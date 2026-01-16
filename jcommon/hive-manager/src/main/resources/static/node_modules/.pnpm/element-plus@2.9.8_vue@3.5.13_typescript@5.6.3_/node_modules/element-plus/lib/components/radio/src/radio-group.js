'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var radio = require('./radio.js');
var runtime = require('../../../utils/vue/props/runtime.js');
var index = require('../../../hooks/use-size/index.js');
var index$1 = require('../../../hooks/use-aria/index.js');

const radioGroupProps = runtime.buildProps({
  id: {
    type: String,
    default: void 0
  },
  size: index.useSizeProp,
  disabled: Boolean,
  modelValue: {
    type: [String, Number, Boolean],
    default: void 0
  },
  fill: {
    type: String,
    default: ""
  },
  textColor: {
    type: String,
    default: ""
  },
  name: {
    type: String,
    default: void 0
  },
  validateEvent: {
    type: Boolean,
    default: true
  },
  ...index$1.useAriaProps(["ariaLabel"])
});
const radioGroupEmits = radio.radioEmits;

exports.radioGroupEmits = radioGroupEmits;
exports.radioGroupProps = radioGroupProps;
//# sourceMappingURL=radio-group.js.map
