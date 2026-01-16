'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var runtime = require('../../../utils/vue/props/runtime.js');
var index = require('../../../hooks/use-size/index.js');

const descriptionProps = runtime.buildProps({
  border: Boolean,
  column: {
    type: Number,
    default: 3
  },
  direction: {
    type: String,
    values: ["horizontal", "vertical"],
    default: "horizontal"
  },
  size: index.useSizeProp,
  title: {
    type: String,
    default: ""
  },
  extra: {
    type: String,
    default: ""
  },
  labelWidth: {
    type: [String, Number],
    default: ""
  }
});

exports.descriptionProps = descriptionProps;
//# sourceMappingURL=description.js.map
