'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var content = require('./content.js');
var runtime = require('../../../utils/vue/props/runtime.js');
var icon = require('../../../utils/vue/icon.js');
var event = require('../../../constants/event.js');
var types = require('../../../utils/types.js');

const tourProps = runtime.buildProps({
  modelValue: Boolean,
  current: {
    type: Number,
    default: 0
  },
  showArrow: {
    type: Boolean,
    default: true
  },
  showClose: {
    type: Boolean,
    default: true
  },
  closeIcon: {
    type: icon.iconPropType
  },
  placement: content.tourContentProps.placement,
  contentStyle: {
    type: runtime.definePropType([Object])
  },
  mask: {
    type: runtime.definePropType([Boolean, Object]),
    default: true
  },
  gap: {
    type: runtime.definePropType(Object),
    default: () => ({
      offset: 6,
      radius: 2
    })
  },
  zIndex: {
    type: Number
  },
  scrollIntoViewOptions: {
    type: runtime.definePropType([Boolean, Object]),
    default: () => ({
      block: "center"
    })
  },
  type: {
    type: runtime.definePropType(String)
  },
  appendTo: {
    type: runtime.definePropType([String, Object]),
    default: "body"
  },
  closeOnPressEscape: {
    type: Boolean,
    default: true
  },
  targetAreaClickable: {
    type: Boolean,
    default: true
  }
});
const tourEmits = {
  [event.UPDATE_MODEL_EVENT]: (value) => types.isBoolean(value),
  ["update:current"]: (current) => types.isNumber(current),
  close: (current) => types.isNumber(current),
  finish: () => true,
  change: (current) => types.isNumber(current)
};

exports.tourEmits = tourEmits;
exports.tourProps = tourProps;
//# sourceMappingURL=tour.js.map
