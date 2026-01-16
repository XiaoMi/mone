'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var runtime = require('../../../utils/vue/props/runtime.js');

const badgeProps = runtime.buildProps({
  value: {
    type: [String, Number],
    default: ""
  },
  max: {
    type: Number,
    default: 99
  },
  isDot: Boolean,
  hidden: Boolean,
  type: {
    type: String,
    values: ["primary", "success", "warning", "info", "danger"],
    default: "danger"
  },
  showZero: {
    type: Boolean,
    default: true
  },
  color: String,
  badgeStyle: {
    type: runtime.definePropType([String, Object, Array])
  },
  offset: {
    type: runtime.definePropType(Array),
    default: [0, 0]
  },
  badgeClass: {
    type: String
  }
});

exports.badgeProps = badgeProps;
//# sourceMappingURL=badge.js.map
