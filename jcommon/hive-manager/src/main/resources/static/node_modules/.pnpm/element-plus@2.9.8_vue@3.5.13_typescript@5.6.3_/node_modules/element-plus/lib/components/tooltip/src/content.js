'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var index = require('../../../hooks/use-delayed-toggle/index.js');
var content = require('../../popper/src/content.js');
var runtime = require('../../../utils/vue/props/runtime.js');
var index$1 = require('../../../hooks/use-aria/index.js');

const useTooltipContentProps = runtime.buildProps({
  ...index.useDelayedToggleProps,
  ...content.popperContentProps,
  appendTo: {
    type: runtime.definePropType([String, Object])
  },
  content: {
    type: String,
    default: ""
  },
  rawContent: Boolean,
  persistent: Boolean,
  visible: {
    type: runtime.definePropType(Boolean),
    default: null
  },
  transition: String,
  teleported: {
    type: Boolean,
    default: true
  },
  disabled: Boolean,
  ...index$1.useAriaProps(["ariaLabel"])
});

exports.useTooltipContentProps = useTooltipContentProps;
//# sourceMappingURL=content.js.map
