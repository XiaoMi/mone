'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var runtime = require('../../../utils/vue/props/runtime.js');

const COMPONENT_NAME = "ElOption";
const optionProps = runtime.buildProps({
  value: {
    type: [String, Number, Boolean, Object],
    required: true
  },
  label: {
    type: [String, Number]
  },
  created: Boolean,
  disabled: Boolean
});

exports.COMPONENT_NAME = COMPONENT_NAME;
exports.optionProps = optionProps;
//# sourceMappingURL=option.js.map
