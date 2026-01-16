'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var helper = require('./helper.js');
var input = require('../../input/src/input.js');
var runtime = require('../../../utils/vue/props/runtime.js');
var shared = require('@vue/shared');
var event = require('../../../constants/event.js');

const mentionProps = runtime.buildProps({
  ...input.inputProps,
  options: {
    type: runtime.definePropType(Array),
    default: () => []
  },
  prefix: {
    type: runtime.definePropType([String, Array]),
    default: "@",
    validator: (val) => {
      if (shared.isString(val))
        return val.length === 1;
      return val.every((v) => shared.isString(v) && v.length === 1);
    }
  },
  split: {
    type: String,
    default: " ",
    validator: (val) => val.length === 1
  },
  filterOption: {
    type: runtime.definePropType([Boolean, Function]),
    default: () => helper.filterOption,
    validator: (val) => {
      if (val === false)
        return true;
      return shared.isFunction(val);
    }
  },
  placement: {
    type: runtime.definePropType(String),
    default: "bottom"
  },
  showArrow: Boolean,
  offset: {
    type: Number,
    default: 0
  },
  whole: Boolean,
  checkIsWhole: {
    type: runtime.definePropType(Function)
  },
  modelValue: String,
  loading: Boolean,
  popperClass: {
    type: String,
    default: ""
  },
  popperOptions: {
    type: runtime.definePropType(Object),
    default: () => ({})
  }
});
const mentionEmits = {
  [event.UPDATE_MODEL_EVENT]: (value) => shared.isString(value),
  search: (pattern, prefix) => shared.isString(pattern) && shared.isString(prefix),
  select: (option, prefix) => shared.isString(option.value) && shared.isString(prefix),
  focus: (evt) => evt instanceof FocusEvent,
  blur: (evt) => evt instanceof FocusEvent
};

exports.mentionEmits = mentionEmits;
exports.mentionProps = mentionProps;
//# sourceMappingURL=mention.js.map
