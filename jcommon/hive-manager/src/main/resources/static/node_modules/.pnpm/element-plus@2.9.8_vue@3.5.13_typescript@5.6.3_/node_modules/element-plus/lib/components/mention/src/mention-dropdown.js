'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var runtime = require('../../../utils/vue/props/runtime.js');
var shared = require('@vue/shared');

const mentionDropdownProps = runtime.buildProps({
  options: {
    type: runtime.definePropType(Array),
    default: () => []
  },
  loading: Boolean,
  disabled: Boolean,
  contentId: String,
  ariaLabel: String
});
const mentionDropdownEmits = {
  select: (option) => shared.isString(option.value)
};

exports.mentionDropdownEmits = mentionDropdownEmits;
exports.mentionDropdownProps = mentionDropdownProps;
//# sourceMappingURL=mention-dropdown.js.map
