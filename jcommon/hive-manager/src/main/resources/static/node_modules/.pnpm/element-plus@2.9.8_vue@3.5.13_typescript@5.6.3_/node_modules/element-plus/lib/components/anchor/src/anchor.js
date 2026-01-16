'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var runtime = require('../../../utils/vue/props/runtime.js');
var shared = require('@vue/shared');
var types = require('../../../utils/types.js');

const anchorProps = runtime.buildProps({
  container: {
    type: runtime.definePropType([
      String,
      Object
    ])
  },
  offset: {
    type: Number,
    default: 0
  },
  bound: {
    type: Number,
    default: 15
  },
  duration: {
    type: Number,
    default: 300
  },
  marker: {
    type: Boolean,
    default: true
  },
  type: {
    type: runtime.definePropType(String),
    default: "default"
  },
  direction: {
    type: runtime.definePropType(String),
    default: "vertical"
  },
  selectScrollTop: {
    type: Boolean,
    default: false
  }
});
const anchorEmits = {
  change: (href) => shared.isString(href),
  click: (e, href) => e instanceof MouseEvent && (shared.isString(href) || types.isUndefined(href))
};

exports.anchorEmits = anchorEmits;
exports.anchorProps = anchorProps;
//# sourceMappingURL=anchor.js.map
