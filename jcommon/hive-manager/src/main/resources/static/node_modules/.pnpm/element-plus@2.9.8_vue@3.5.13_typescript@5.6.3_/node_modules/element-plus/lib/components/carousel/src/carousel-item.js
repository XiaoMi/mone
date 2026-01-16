'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var runtime = require('../../../utils/vue/props/runtime.js');

const carouselItemProps = runtime.buildProps({
  name: { type: String, default: "" },
  label: {
    type: [String, Number],
    default: ""
  }
});

exports.carouselItemProps = carouselItemProps;
//# sourceMappingURL=carousel-item.js.map
