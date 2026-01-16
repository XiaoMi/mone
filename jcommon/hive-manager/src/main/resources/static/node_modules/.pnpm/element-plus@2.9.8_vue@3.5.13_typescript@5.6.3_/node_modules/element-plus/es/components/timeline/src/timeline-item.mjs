import { buildProps } from '../../../utils/vue/props/runtime.mjs';
import { iconPropType } from '../../../utils/vue/icon.mjs';

const timelineItemProps = buildProps({
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
    type: iconPropType
  },
  hollow: Boolean
});

export { timelineItemProps };
//# sourceMappingURL=timeline-item.mjs.map
