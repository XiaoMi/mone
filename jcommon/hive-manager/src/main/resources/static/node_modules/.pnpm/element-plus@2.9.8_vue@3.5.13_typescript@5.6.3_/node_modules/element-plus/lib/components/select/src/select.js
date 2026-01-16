'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var core = require('@popperjs/core');
var iconsVue = require('@element-plus/icons-vue');
var scrollbar = require('../../scrollbar/src/scrollbar.js');
var runtime = require('../../../utils/vue/props/runtime.js');
var index = require('../../../hooks/use-size/index.js');
var content = require('../../tooltip/src/content.js');
var icon = require('../../../utils/vue/icon.js');
var tag = require('../../tag/src/tag.js');
var index$1 = require('../../../hooks/use-empty-values/index.js');
var index$2 = require('../../../hooks/use-aria/index.js');
var event = require('../../../constants/event.js');

const SelectProps = runtime.buildProps({
  name: String,
  id: String,
  modelValue: {
    type: runtime.definePropType([
      Array,
      String,
      Number,
      Boolean,
      Object
    ]),
    default: void 0
  },
  autocomplete: {
    type: String,
    default: "off"
  },
  automaticDropdown: Boolean,
  size: index.useSizeProp,
  effect: {
    type: runtime.definePropType(String),
    default: "light"
  },
  disabled: Boolean,
  clearable: Boolean,
  filterable: Boolean,
  allowCreate: Boolean,
  loading: Boolean,
  popperClass: {
    type: String,
    default: ""
  },
  popperOptions: {
    type: runtime.definePropType(Object),
    default: () => ({})
  },
  remote: Boolean,
  loadingText: String,
  noMatchText: String,
  noDataText: String,
  remoteMethod: Function,
  filterMethod: Function,
  multiple: Boolean,
  multipleLimit: {
    type: Number,
    default: 0
  },
  placeholder: {
    type: String
  },
  defaultFirstOption: Boolean,
  reserveKeyword: {
    type: Boolean,
    default: true
  },
  valueKey: {
    type: String,
    default: "value"
  },
  collapseTags: Boolean,
  collapseTagsTooltip: Boolean,
  maxCollapseTags: {
    type: Number,
    default: 1
  },
  teleported: content.useTooltipContentProps.teleported,
  persistent: {
    type: Boolean,
    default: true
  },
  clearIcon: {
    type: icon.iconPropType,
    default: iconsVue.CircleClose
  },
  fitInputWidth: Boolean,
  suffixIcon: {
    type: icon.iconPropType,
    default: iconsVue.ArrowDown
  },
  tagType: { ...tag.tagProps.type, default: "info" },
  tagEffect: { ...tag.tagProps.effect, default: "light" },
  validateEvent: {
    type: Boolean,
    default: true
  },
  remoteShowSuffix: Boolean,
  showArrow: {
    type: Boolean,
    default: true
  },
  offset: {
    type: Number,
    default: 12
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
  tabindex: {
    type: [String, Number],
    default: 0
  },
  appendTo: String,
  ...index$1.useEmptyValuesProps,
  ...index$2.useAriaProps(["ariaLabel"])
});
const selectEmits = {
  [event.UPDATE_MODEL_EVENT]: (val) => true,
  [event.CHANGE_EVENT]: (val) => true,
  "popup-scroll": scrollbar.scrollbarEmits.scroll,
  "remove-tag": (val) => true,
  "visible-change": (visible) => true,
  focus: (evt) => evt instanceof FocusEvent,
  blur: (evt) => evt instanceof FocusEvent,
  clear: () => true
};

exports.SelectProps = SelectProps;
exports.selectEmits = selectEmits;
//# sourceMappingURL=select.js.map
