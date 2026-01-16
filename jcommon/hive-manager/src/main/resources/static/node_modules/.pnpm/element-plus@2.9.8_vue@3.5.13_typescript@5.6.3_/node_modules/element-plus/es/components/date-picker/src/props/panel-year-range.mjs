import { panelRangeSharedProps } from './shared.mjs';
import { buildProps } from '../../../../utils/vue/props/runtime.mjs';

const panelYearRangeProps = buildProps({
  ...panelRangeSharedProps
});
const panelYearRangeEmits = [
  "pick",
  "set-picker-option",
  "calendar-change"
];

export { panelYearRangeEmits, panelYearRangeProps };
//# sourceMappingURL=panel-year-range.mjs.map
