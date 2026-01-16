'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var runtime = require('../../../utils/vue/props/runtime.js');
var icon = require('../../../utils/vue/icon.js');

const linkProps = runtime.buildProps({
  type: {
    type: String,
    values: ["primary", "success", "warning", "info", "danger", "default"],
    default: "default"
  },
  underline: {
    type: Boolean,
    default: true
  },
  disabled: Boolean,
  href: { type: String, default: "" },
  target: {
    type: String,
    default: "_self"
  },
  icon: {
    type: icon.iconPropType
  }
});
const linkEmits = {
  click: (evt) => evt instanceof MouseEvent
};

exports.linkEmits = linkEmits;
exports.linkProps = linkProps;
//# sourceMappingURL=link.js.map
