import { buildProps, definePropType } from '../../../utils/vue/props/runtime.mjs';
import { useSizeProp } from '../../../hooks/use-size/index.mjs';
import { useAriaProps } from '../../../hooks/use-aria/index.mjs';
import { UPDATE_MODEL_EVENT, CHANGE_EVENT } from '../../../constants/event.mjs';
import { isString } from '@vue/shared';
import { isNumber, isBoolean } from '../../../utils/types.mjs';

const defaultProps = {
  label: "label",
  value: "value",
  disabled: "disabled"
};
const segmentedProps = buildProps({
  direction: {
    type: definePropType(String),
    default: "horizontal"
  },
  options: {
    type: definePropType(Array),
    default: () => []
  },
  modelValue: {
    type: [String, Number, Boolean],
    default: void 0
  },
  props: {
    type: definePropType(Object),
    default: () => defaultProps
  },
  block: Boolean,
  size: useSizeProp,
  disabled: Boolean,
  validateEvent: {
    type: Boolean,
    default: true
  },
  id: String,
  name: String,
  ...useAriaProps(["ariaLabel"])
});
const segmentedEmits = {
  [UPDATE_MODEL_EVENT]: (val) => isString(val) || isNumber(val) || isBoolean(val),
  [CHANGE_EVENT]: (val) => isString(val) || isNumber(val) || isBoolean(val)
};

export { defaultProps, segmentedEmits, segmentedProps };
//# sourceMappingURL=segmented.mjs.map
