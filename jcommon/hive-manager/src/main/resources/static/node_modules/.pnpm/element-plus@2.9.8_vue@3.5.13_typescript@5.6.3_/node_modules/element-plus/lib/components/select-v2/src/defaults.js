'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var core = require('@popperjs/core');
var iconsVue = require('@element-plus/icons-vue');
var useProps = require('./useProps.js');
var runtime = require('../../../utils/vue/props/runtime.js');
var icon = require('../../../utils/vue/icon.js');
var content = require('../../tooltip/src/content.js');
var index = require('../../../hooks/use-size/index.js');
var tag = require('../../tag/src/tag.js');
var types = require('../../../utils/types.js');
var index$1 = require('../../../hooks/use-empty-values/index.js');
var index$2 = require('../../../hooks/use-aria/index.js');
var event = require('../../../constants/event.js');

const SelectProps = runtime.buildProps({
  allowCreate: Boolean,
  autocomplete: {
    type: runtime.definePropType(String),
    default: "none"
  },
  automaticDropdown: Boolean,
  clearable: Boolean,
  clearIcon: {
    type: icon.iconPropType,
    default: iconsVue.CircleClose
  },
  effect: {
    type: runtime.definePropType(String),
    default: "light"
  },
  collapseTags: Boolean,
  collapseTagsTooltip: Boolean,
  maxCollapseTags: {
    type: Number,
    default: 1
  },
  defaultFirstOption: Boolean,
  disabled: Boolean,
  estimatedOptionHeight: {
    type: Number,
    default: void 0
  },
  filterable: Boolean,
  filterMethod: Function,
  height: {
    type: Number,
    default: 274
  },
  itemHeight: {
    type: Number,
    default: 34
  },
  id: String,
  loading: Boolean,
  loadingText: String,
  modelValue: {
    type: runtime.definePropType([Array, String, Number, Boolean, Object])
  },
  multiple: Boolean,
  multipleLimit: {
    type: Number,
    default: 0
  },
  name: String,
  noDataText: String,
  noMatchText: String,
  remoteMethod: Function,
  reserveKeyword: {
    type: Boolean,
    default: true
  },
  options: {
    type: runtime.definePropType(Array),
    required: true
  },
  placeholder: {
    type: String
  },
  teleported: content.useTooltipContentProps.teleported,
  persistent: {
    type: Boolean,
    default: true
  },
  popperClass: {
    type: String,
    default: ""
  },
  popperOptions: {
    type: runtime.definePropType(Object),
    default: () => ({})
  },
  remote: Boolean,
  size: index.useSizeProp,
  props: {
    type: runtime.definePropType(Object),
    default: () => useProps.defaultProps
  },
  valueKey: {
    type: String,
    default: "value"
  },
  scrollbarAlwaysOn: Boolean,
  validateEvent: {
    type: Boolean,
    default: true
  },
  offset: {
    type: Number,
    default: 12
  },
  showArrow: {
    type: Boolean,
    default: true
  },
  placement: {
    type: runtime.definePropType(String),
    values: core.placements,
    default: "bottom-start"
  },
  fallbackPlacements: {
    type: runtime.definePropType(Array),
    default: ["bottom-start", "top-start", "right", "left"]
  },
  tagType: { ...tag.tagProps.type, default: "info" },
  tagEffect: { ...tag.tagProps.effect, default: "light" },
  tabindex: {
    type: [String, Number],
    default: 0
  },
  appendTo: String,
  fitInputWidth: {
    type: [Boolean, Number],
    default: true,
    validator(val) {
      return types.isBoolean(val) || types.isNumber(val);
    }
  },
  suffixIcon: {
    type: icon.iconPropType,
    default: iconsVue.ArrowDown
  },
  ...index$1.useEmptyValuesProps,
  ...index$2.useAriaProps(["ariaLabel"])
});
const OptionProps = runtime.buildProps({
  data: Array,
  disabled: Boolean,
  hovering: Boolean,
  item: {
    type: runtime.definePropType(Object),
    required: true
  },
  index: Number,
  style: Object,
  selected: Boolean,
  created: Boolean
});
const selectEmits = {
  [event.UPDATE_MODEL_EVENT]: (val) => true,
  [event.CHANGE_EVENT]: (val) => true,
  "remove-tag": (val) => true,
  "visible-change": (visible) => true,
  focus: (evt) => evt instanceof FocusEvent,
  blur: (evt) => evt instanceof FocusEvent,
  clear: () => true
};
const optionEmits = {
  hover: (index) => types.isNumber(index),
  select: (val, index) => true
};

exports.OptionProps = OptionProps;
exports.SelectProps = SelectProps;
exports.optionEmits = optionEmits;
exports.selectEmits = selectEmits;
//# sourceMappingURL=defaults.js.map
