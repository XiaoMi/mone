'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var runtime = require('../../../utils/vue/props/runtime.js');
var icon = require('../../../utils/vue/icon.js');

const timelineItemProps = runtime.buildProps({
  timestamp: {
    type: String,
    default: ""
  },
  hideTimestamp: Boolean,
  center: Boolean,
  placement: {
    type: String,
    values: ["top", "bottom"],
    default: "bottom"
  },
  type: {
    type: String,
    values: ["primary", "success", "warning", "danger", "info"],
    default: ""
  },
  color: {
    type: String,
    default: ""
  },
  size: {
    type: String,
    values: ["normal", "large"],
    default: "normal"
  },
  icon: {
    type: icon.iconPropType
  },
  hollow: Boolean
});

exports.timelineItemProps = timelineItemProps;
//# sourceMappingURL=timeline-item.js.map
