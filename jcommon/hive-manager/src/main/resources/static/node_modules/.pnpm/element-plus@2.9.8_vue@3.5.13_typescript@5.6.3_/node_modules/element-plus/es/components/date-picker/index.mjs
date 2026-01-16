import DatePicker from './src/date-picker.mjs';
export { ROOT_PICKER_INJECTION_KEY } from './src/constants.mjs';
export { datePickerProps } from './src/props/date-picker.mjs';
import { withInstall } from '../../utils/vue/install.mjs';

const ElDatePicker = withInstall(DatePicker);

export { ElDatePicker, ElDatePicker as default };
//# sourceMappingURL=index.mjs.map
