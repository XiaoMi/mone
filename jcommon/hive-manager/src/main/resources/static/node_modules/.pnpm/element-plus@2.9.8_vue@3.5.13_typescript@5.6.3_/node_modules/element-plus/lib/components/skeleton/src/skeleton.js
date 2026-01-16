'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var runtime = require('../../../utils/vue/props/runtime.js');

const skeletonProps = runtime.buildProps({
  animated: {
    type: Boolean,
    default: false
  },
  count: {
    type: Number,
    default: 1
  },
  rows: {
    type: Number,
    default: 3
  },
  loading: {
    type: Boolean,
    default: true
  },
  throttle: {
    type: runtime.definePropType([Number, Object])
  }
});

exports.skeletonProps = skeletonProps;
//# sourceMappingURL=skeleton.js.map
