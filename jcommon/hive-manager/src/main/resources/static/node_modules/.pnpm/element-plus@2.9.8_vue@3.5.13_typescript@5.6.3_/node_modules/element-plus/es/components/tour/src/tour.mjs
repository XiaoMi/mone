import { tourContentProps } from './content.mjs';
import { buildProps, definePropType } from '../../../utils/vue/props/runtime.mjs';
import { iconPropType } from '../../../utils/vue/icon.mjs';
import { UPDATE_MODEL_EVENT } from '../../../constants/event.mjs';
import { isBoolean, isNumber } from '../../../utils/types.mjs';

const tourProps = buildProps({
  modelValue: Boolean,
  current: {
    type: Number,
    default: 0
  },
  showArrow: {
    type: Boolean,
    default: true
  },
  showClose: {
    type: Boolean,
    default: true
  },
  closeIcon: {
    type: iconPropType
  },
  placement: tourContentProps.placement,
  contentStyle: {
    type: definePropType([Object])
  },
  mask: {
    type: definePropType([Boolean, Object]),
    default: true
  },
  gap: {
    type: definePropType(Object),
    default: () => ({
      offset: 6,
      radius: 2
    })
  },
  zIndex: {
    type: Number
  },
  scrollIntoViewOptions: {
    type: definePropType([Boolean, Object]),
    default: () => ({
      block: "center"
    })
  },
  type: {
    type: definePropType(String)
  },
  appendTo: {
    type: definePropType([String, Object]),
    default: "body"
  },
  closeOnPressEscape: {
    type: Boolean,
    default: true
  },
  targetAreaClickable: {
    type: Boolean,
    default: true
  }
});
const tourEmits = {
  [UPDATE_MODEL_EVENT]: (value) => isBoolean(value),
  ["update:current"]: (current) => isNumber(current),
  close: (current) => isNumber(current),
  finish: () => true,
  change: (current) => isNumber(current)
};

export { tourEmits, tourProps };
//# sourceMappingURL=tour.mjs.map
