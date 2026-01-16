'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var runtime = require('../../../utils/vue/props/runtime.js');

const EventHandler = {
  type: runtime.definePropType(Function)
};
const tooltipV2TriggerProps = runtime.buildProps({
  onBlur: EventHandler,
  onClick: EventHandler,
  onFocus: EventHandler,
  onMouseDown: EventHandler,
  onMouseEnter: EventHandler,
  onMouseLeave: EventHandler
});

exports.tooltipV2TriggerProps = tooltipV2TriggerProps;
//# sourceMappingURL=trigger.js.map
