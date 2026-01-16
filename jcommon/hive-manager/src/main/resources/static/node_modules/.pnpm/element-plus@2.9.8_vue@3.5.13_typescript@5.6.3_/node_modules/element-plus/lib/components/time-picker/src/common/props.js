'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var core = require('@popperjs/core');
var iconsVue = require('@element-plus/icons-vue');
var shared = require('../props/shared.js');
var runtime = require('../../../../utils/vue/props/runtime.js');
var index = require('../../../../hooks/use-size/index.js');
var index$1 = require('../../../../hooks/use-empty-values/index.js');
var index$2 = require('../../../../hooks/use-aria/index.js');

const timePickerDefaultProps = runtime.buildProps({
  id: {
    type: runtime.definePropType([Array, String])
  },
  name: {
    type: runtime.definePropType([Array, String])
  },
  popperClass: {
    type: String,
    default: ""
  },
  format: String,
  valueFormat: String,
  dateFormat: String,
  timeFormat: String,
  type: {
    type: String,
    default: ""
  },
  clearable: {
    type: Boolean,
    default: true
  },
  clearIcon: {
    type: runtime.definePropType([String, Object]),
    default: iconsVue.CircleClose
  },
  editable: {
    type: Boolean,
    default: true
  },
  prefixIcon: {
    type: runtime.definePropType([String, Object]),
    default: ""
  },
  size: index.useSizeProp,
  readonly: Boolean,
  disabled: Boolean,
  placeholder: {
    type: String,
    default: ""
  },
  popperOptions: {
    type: runtime.definePropType(Object),
    default: () => ({})
  },
  modelValue: {
    type: runtime.definePropType([Date, Array, String, Number]),
    default: ""
  },
  rangeSeparator: {
    type: String,
    default: "-"
  },
  startPlaceholder: String,
  endPlaceholder: String,
  defaultValue: {
    type: runtime.definePropType([Date, Array])
  },
  defaultTime: {
    type: runtime.definePropType([Date, Array])
  },
  isRange: Boolean,
  ...shared.disabledTimeListsProps,
  disabledDate: {
    type: Function
  },
  cellClassName: {
    type: Function
  },
  shortcuts: {
    type: Array,
    default: () => []
  },
  arrowControl: Boolean,
  tabindex: {
    type: runtime.definePropType([String, Number]),
    default: 0
  },
  validateEvent: {
    type: Boolean,
    default: true
  },
  unlinkPanels: Boolean,
  placement: {
    type: runtime.definePropType(String),
    values: core.placements,
    default: "bottom"
  },
  fallbackPlacements: {
    type: runtime.definePropType(Array),
    default: ["bottom", "top", "right", "left"]
  },
  ...index$1.useEmptyValuesProps,
  ...index$2.useAriaProps(["ariaLabel"]),
  showNow: {
    type: Boolean,
    default: true
  }
});
const timePickerRangeTriggerProps = runtime.buildProps({
  id: {
    type: runtime.definePropType(Array)
  },
  name: {
    type: runtime.definePropType(Array)
  },
  modelValue: {
    type: runtime.definePropType([Array, String])
  },
  startPlaceholder: String,
  endPlaceholder: String,
  disabled: Boolean
});
const timePickerRngeTriggerProps = timePickerRangeTriggerProps;

exports.timePickerDefaultProps = timePickerDefaultProps;
exports.timePickerRangeTriggerProps = timePickerRangeTriggerProps;
exports.timePickerRngeTriggerProps = timePickerRngeTriggerProps;
//# sourceMappingURL=props.js.map
