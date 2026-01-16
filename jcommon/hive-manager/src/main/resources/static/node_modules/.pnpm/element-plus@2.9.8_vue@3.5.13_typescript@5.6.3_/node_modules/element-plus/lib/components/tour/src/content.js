'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var runtime = require('../../../utils/vue/props/runtime.js');

const tourStrategies = ["absolute", "fixed"];
const tourPlacements = [
  "top-start",
  "top-end",
  "top",
  "bottom-start",
  "bottom-end",
  "bottom",
  "left-start",
  "left-end",
  "left",
  "right-start",
  "right-end",
  "right"
];
const tourContentProps = runtime.buildProps({
  placement: {
    type: runtime.definePropType(String),
    values: tourPlacements,
    default: "bottom"
  },
  reference: {
    type: runtime.definePropType(Object),
    default: null
  },
  strategy: {
    type: runtime.definePropType(String),
    values: tourStrategies,
    default: "absolute"
  },
  offset: {
    type: Number,
    default: 10
  },
  showArrow: Boolean,
  zIndex: {
    type: Number,
    default: 2001
  }
});
const tourContentEmits = {
  close: () => true
};

exports.tourContentEmits = tourContentEmits;
exports.tourContentProps = tourContentProps;
exports.tourPlacements = tourPlacements;
exports.tourStrategies = tourStrategies;
//# sourceMappingURL=content.js.map
