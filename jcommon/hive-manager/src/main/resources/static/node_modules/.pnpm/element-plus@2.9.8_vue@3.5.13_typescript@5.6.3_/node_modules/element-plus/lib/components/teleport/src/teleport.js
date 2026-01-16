'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var runtime = require('../../../utils/vue/props/runtime.js');

const teleportProps = runtime.buildProps({
  to: {
    type: runtime.definePropType([String, Object]),
    required: true
  },
  disabled: Boolean
});

exports.teleportProps = teleportProps;
//# sourceMappingURL=teleport.js.map
