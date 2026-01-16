import { datePickerSharedProps, selectionModeWithDefault } from './shared.mjs';
import { buildProps } from '../../../../utils/vue/props/runtime.mjs';

const basicYearTableProps = buildProps({
  ...datePickerSharedProps,
  selectionMode: selectionModeWithDefault("year")
});

export { basicYearTableProps };
//# sourceMappingURL=basic-year-table.mjs.map
