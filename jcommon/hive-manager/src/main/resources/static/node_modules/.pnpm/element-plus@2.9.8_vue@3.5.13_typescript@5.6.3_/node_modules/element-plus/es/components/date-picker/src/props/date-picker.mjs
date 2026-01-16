import { timePickerDefaultProps } from '../../../time-picker/src/common/props.mjs';
import { buildProps, definePropType } from '../../../../utils/vue/props/runtime.mjs';

const datePickerProps = buildProps({
  ...timePickerDefaultProps,
  type: {
    type: definePropType(String),
    default: "date"
  }
});

export { datePickerProps };
//# sourceMappingURL=date-picker.mjs.map
