'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var runtime = require('../../../utils/vue/props/runtime.js');

const skeletonItemProps = runtime.buildProps({
  variant: {
    type: String,
    values: [
      "circle",
      "rect",
      "h1",
      "h3",
      "text",
      "caption",
      "p",
      "image",
      "button"
    ],
    default: "text"
  }
});

exports.skeletonItemProps = skeletonItemProps;
//# sourceMappingURL=skeleton-item2.js.map
