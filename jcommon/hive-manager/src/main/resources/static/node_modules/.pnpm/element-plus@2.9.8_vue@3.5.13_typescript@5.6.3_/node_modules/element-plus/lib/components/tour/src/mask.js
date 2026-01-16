'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var runtime = require('../../../utils/vue/props/runtime.js');

const maskProps = runtime.buildProps({
  zIndex: {
    type: Number,
    default: 1001
  },
  visible: Boolean,
  fill: {
    type: String,
    default: "rgba(0,0,0,0.5)"
  },
  pos: {
    type: runtime.definePropType(Object)
  },
  targetAreaClickable: {
    type: Boolean,
    default: true
  }
});

exports.maskProps = maskProps;
//# sourceMappingURL=mask.js.map
