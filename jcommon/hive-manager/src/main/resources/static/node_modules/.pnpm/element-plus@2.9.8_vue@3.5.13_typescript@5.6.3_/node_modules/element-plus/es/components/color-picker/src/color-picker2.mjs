import { isNil } from 'lodash-unified';
import { buildProps, definePropType } from '../../../utils/vue/props/runtime.mjs';
import { useSizeProp } from '../../../hooks/use-size/index.mjs';
import { useTooltipContentProps } from '../../tooltip/src/content.mjs';
import { useAriaProps } from '../../../hooks/use-aria/index.mjs';
import { UPDATE_MODEL_EVENT, CHANGE_EVENT } from '../../../constants/event.mjs';
import { isString } from '@vue/shared';

const colorPickerProps = buildProps({
  modelValue: String,
  id: String,
  showAlpha: Boolean,
  colorFormat: String,
  disabled: Boolean,
  size: useSizeProp,
  popperClass: {
    type: String,
    default: ""
  },
  tabindex: {
    type: [String, Number],
    default: 0
  },
  teleported: useTooltipContentProps.teleported,
  predefine: {
    type: definePropType(Array)
  },
  validateEvent: {
    type: Boolean,
    default: true
  },
  ...useAriaProps(["ariaLabel"])
});
const colorPickerEmits = {
  [UPDATE_MODEL_EVENT]: (val) => isString(val) || isNil(val),
  [CHANGE_EVENT]: (val) => isString(val) || isNil(val),
  activeChange: (val) => isString(val) || isNil(val),
  focus: (evt) => evt instanceof FocusEvent,
  blur: (evt) => evt instanceof FocusEvent
};
const colorPickerContextKey = Symbol("colorPickerContextKey");

export { colorPickerContextKey, colorPickerEmits, colorPickerProps };
//# sourceMappingURL=color-picker2.mjs.map
