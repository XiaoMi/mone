import { filterOption } from './helper.mjs';
import { inputProps } from '../../input/src/input.mjs';
import { buildProps, definePropType } from '../../../utils/vue/props/runtime.mjs';
import { isString, isFunction } from '@vue/shared';
import { UPDATE_MODEL_EVENT } from '../../../constants/event.mjs';

const mentionProps = buildProps({
  ...inputProps,
  options: {
    type: definePropType(Array),
    default: () => []
  },
  prefix: {
    type: definePropType([String, Array]),
    default: "@",
    validator: (val) => {
      if (isString(val))
        return val.length === 1;
      return val.every((v) => isString(v) && v.length === 1);
    }
  },
  split: {
    type: String,
    default: " ",
    validator: (val) => val.length === 1
  },
  filterOption: {
    type: definePropType([Boolean, Function]),
    default: () => filterOption,
    validator: (val) => {
      if (val === false)
        return true;
      return isFunction(val);
    }
  },
  placement: {
    type: definePropType(String),
    default: "bottom"
  },
  showArrow: Boolean,
  offset: {
    type: Number,
    default: 0
  },
  whole: Boolean,
  checkIsWhole: {
    type: definePropType(Function)
  },
  modelValue: String,
  loading: Boolean,
  popperClass: {
    type: String,
    default: ""
  },
  popperOptions: {
    type: definePropType(Object),
    default: () => ({})
  }
});
const mentionEmits = {
  [UPDATE_MODEL_EVENT]: (value) => isString(value),
  search: (pattern, prefix) => isString(pattern) && isString(prefix),
  select: (option, prefix) => isString(option.value) && isString(prefix),
  focus: (evt) => evt instanceof FocusEvent,
  blur: (evt) => evt instanceof FocusEvent
};

export { mentionEmits, mentionProps };
//# sourceMappingURL=mention.mjs.map
