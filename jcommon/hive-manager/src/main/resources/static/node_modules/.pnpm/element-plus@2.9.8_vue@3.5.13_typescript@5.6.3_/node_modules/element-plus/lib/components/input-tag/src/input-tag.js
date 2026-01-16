'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var tag = require('../../tag/src/tag.js');
var runtime = require('../../../utils/vue/props/runtime.js');
var aria = require('../../../constants/aria.js');
var index = require('../../../hooks/use-size/index.js');
var event = require('../../../constants/event.js');
var shared = require('@vue/shared');
var types = require('../../../utils/types.js');

const inputTagProps = runtime.buildProps({
  modelValue: {
    type: runtime.definePropType(Array)
  },
  max: Number,
  tagType: { ...tag.tagProps.type, default: "info" },
  tagEffect: tag.tagProps.effect,
  trigger: {
    type: runtime.definePropType(String),
    default: aria.EVENT_CODE.enter
  },
  draggable: {
    type: Boolean,
    default: false
  },
  size: index.useSizeProp,
  clearable: Boolean,
  disabled: {
    type: Boolean,
    default: void 0
  },
  validateEvent: {
    type: Boolean,
    default: true
  },
  readonly: Boolean,
  autofocus: Boolean,
  id: {
    type: String,
    default: void 0
  },
  tabindex: {
    type: [String, Number],
    default: 0
  },
  maxlength: {
    type: [String, Number]
  },
  minlength: {
    type: [String, Number]
  },
  placeholder: String,
  autocomplete: {
    type: String,
    default: "off"
  },
  saveOnBlur: {
    type: Boolean,
    default: true
  },
  ariaLabel: String
});
const inputTagEmits = {
  [event.UPDATE_MODEL_EVENT]: (value) => shared.isArray(value) || types.isUndefined(value),
  [event.CHANGE_EVENT]: (value) => shared.isArray(value) || types.isUndefined(value),
  [event.INPUT_EVENT]: (value) => shared.isString(value),
  "add-tag": (value) => shared.isString(value),
  "remove-tag": (value) => shared.isString(value),
  focus: (evt) => evt instanceof FocusEvent,
  blur: (evt) => evt instanceof FocusEvent,
  clear: () => true
};

exports.inputTagEmits = inputTagEmits;
exports.inputTagProps = inputTagProps;
//# sourceMappingURL=input-tag.js.map
