'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var runtime = require('../../../utils/vue/props/runtime.js');
var size = require('../../../constants/size.js');

const textProps = runtime.buildProps({
  type: {
    type: String,
    values: ["primary", "success", "info", "warning", "danger", ""],
    default: ""
  },
  size: {
    type: String,
    values: size.componentSizes,
    default: ""
  },
  truncated: Boolean,
  lineClamp: {
    type: [String, Number]
  },
  tag: {
    type: String,
    default: "span"
  }
});

exports.textProps = textProps;
//# sourceMappingURL=text.js.map
