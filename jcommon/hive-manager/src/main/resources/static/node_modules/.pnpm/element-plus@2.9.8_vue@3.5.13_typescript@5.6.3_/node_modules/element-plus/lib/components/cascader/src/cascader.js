'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var core = require('@popperjs/core');
var config = require('../../cascader-panel/src/config.js');
var tag = require('../../tag/src/tag.js');
var runtime = require('../../../utils/vue/props/runtime.js');
var index = require('../../../hooks/use-size/index.js');
var content = require('../../tooltip/src/content.js');
var index$1 = require('../../../hooks/use-empty-values/index.js');
var event = require('../../../constants/event.js');
var types = require('../../../utils/types.js');

const cascaderProps = runtime.buildProps({
  ...config.CommonProps,
  size: index.useSizeProp,
  placeholder: String,
  disabled: Boolean,
  clearable: Boolean,
  filterable: Boolean,
  filterMethod: {
    type: runtime.definePropType(Function),
    default: (node, keyword) => node.text.includes(keyword)
  },
  separator: {
    type: String,
    default: " / "
  },
  showAllLevels: {
    type: Boolean,
    default: true
  },
  collapseTags: Boolean,
  maxCollapseTags: {
    type: Number,
    default: 1
  },
  collapseTagsTooltip: {
    type: Boolean,
    default: false
  },
  debounce: {
    type: Number,
    default: 300
  },
  beforeFilter: {
    type: runtime.definePropType(Function),
    default: () => true
  },
  placement: {
    type: runtime.definePropType(String),
    values: core.placements,
    default: "bottom-start"
  },
  fallbackPlacements: {
    type: runtime.definePropType(Array),
    default: ["bottom-start", "bottom", "top-start", "top", "right", "left"]
  },
  popperClass: {
    type: String,
    default: ""
  },
  teleported: content.useTooltipContentProps.teleported,
  tagType: { ...tag.tagProps.type, default: "info" },
  tagEffect: { ...tag.tagProps.effect, default: "light" },
  validateEvent: {
    type: Boolean,
    default: true
  },
  persistent: {
    type: Boolean,
    default: true
  },
  ...index$1.useEmptyValuesProps
});
const cascaderEmits = {
  [event.UPDATE_MODEL_EVENT]: (_) => true,
  [event.CHANGE_EVENT]: (_) => true,
  focus: (evt) => evt instanceof FocusEvent,
  blur: (evt) => evt instanceof FocusEvent,
  clear: () => true,
  visibleChange: (val) => types.isBoolean(val),
  expandChange: (val) => !!val,
  removeTag: (val) => !!val
};

exports.cascaderEmits = cascaderEmits;
exports.cascaderProps = cascaderProps;
//# sourceMappingURL=cascader.js.map
