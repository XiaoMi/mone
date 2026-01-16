'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var content = require('../../tooltip/src/content.js');
var runtime = require('../../../utils/vue/props/runtime.js');
var shared = require('@vue/shared');
var index = require('../../../hooks/use-aria/index.js');
var event = require('../../../constants/event.js');

const autocompleteProps = runtime.buildProps({
  valueKey: {
    type: String,
    default: "value"
  },
  modelValue: {
    type: [String, Number],
    default: ""
  },
  debounce: {
    type: Number,
    default: 300
  },
  placement: {
    type: runtime.definePropType(String),
    values: [
      "top",
      "top-start",
      "top-end",
      "bottom",
      "bottom-start",
      "bottom-end"
    ],
    default: "bottom-start"
  },
  fetchSuggestions: {
    type: runtime.definePropType([Function, Array]),
    default: shared.NOOP
  },
  popperClass: {
    type: String,
    default: ""
  },
  triggerOnFocus: {
    type: Boolean,
    default: true
  },
  selectWhenUnmatched: {
    type: Boolean,
    default: false
  },
  hideLoading: {
    type: Boolean,
    default: false
  },
  teleported: content.useTooltipContentProps.teleported,
  highlightFirstItem: {
    type: Boolean,
    default: false
  },
  fitInputWidth: {
    type: Boolean,
    default: false
  },
  clearable: {
    type: Boolean,
    default: false
  },
  disabled: {
    type: Boolean,
    default: false
  },
  name: String,
  ...index.useAriaProps(["ariaLabel"])
});
const autocompleteEmits = {
  [event.UPDATE_MODEL_EVENT]: (value) => shared.isString(value),
  [event.INPUT_EVENT]: (value) => shared.isString(value),
  [event.CHANGE_EVENT]: (value) => shared.isString(value),
  focus: (evt) => evt instanceof FocusEvent,
  blur: (evt) => evt instanceof FocusEvent,
  clear: () => true,
  select: (item) => shared.isObject(item)
};

exports.autocompleteEmits = autocompleteEmits;
exports.autocompleteProps = autocompleteProps;
//# sourceMappingURL=autocomplete.js.map
