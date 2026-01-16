import { buildProps, definePropType } from '../../../utils/vue/props/runtime.mjs';
import { useSizeProp } from '../../../hooks/use-size/index.mjs';
import { useAriaProps } from '../../../hooks/use-aria/index.mjs';
import { UPDATE_MODEL_EVENT } from '../../../constants/event.mjs';
import { isArray } from '@vue/shared';

const checkboxGroupProps = buildProps({
  modelValue: {
    type: definePropType(Array),
    default: () => []
  },
  disabled: Boolean,
  min: Number,
  max: Number,
  size: useSizeProp,
  fill: String,
  textColor: String,
  tag: {
    type: String,
    default: "div"
  },
  validateEvent: {
    type: Boolean,
    default: true
  },
  ...useAriaProps(["ariaLabel"])
});
const checkboxGroupEmits = {
  [UPDATE_MODEL_EVENT]: (val) => isArray(val),
  change: (val) => isArray(val)
};

export { checkboxGroupEmits, checkboxGroupProps };
//# sourceMappingURL=checkbox-group.mjs.map
