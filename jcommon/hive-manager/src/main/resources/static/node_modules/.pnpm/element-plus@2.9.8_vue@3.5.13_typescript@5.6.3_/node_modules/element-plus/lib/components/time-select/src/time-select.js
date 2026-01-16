'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var iconsVue = require('@element-plus/icons-vue');
var runtime = require('../../../utils/vue/props/runtime.js');
var index = require('../../../hooks/use-size/index.js');
var index$1 = require('../../../hooks/use-empty-values/index.js');

const timeSelectProps = runtime.buildProps({
  format: {
    type: String,
    default: "HH:mm"
  },
  modelValue: String,
  disabled: Boolean,
  editable: {
    type: Boolean,
    default: true
  },
  effect: {
    type: runtime.definePropType(String),
    default: "light"
  },
  clearable: {
    type: Boolean,
    default: true
  },
  size: index.useSizeProp,
  placeholder: String,
  start: {
    type: String,
    default: "09:00"
  },
  end: {
    type: String,
    default: "18:00"
  },
  step: {
    type: String,
    default: "00:30"
  },
  minTime: String,
  maxTime: String,
  includeEndTime: {
    type: Boolean,
    default: false
  },
  name: String,
  prefixIcon: {
    type: runtime.definePropType([String, Object]),
    default: () => iconsVue.Clock
  },
  clearIcon: {
    type: runtime.definePropType([String, Object]),
    default: () => iconsVue.CircleClose
  },
  ...index$1.useEmptyValuesProps
});

exports.timeSelectProps = timeSelectProps;
//# sourceMappingURL=time-select.js.map
