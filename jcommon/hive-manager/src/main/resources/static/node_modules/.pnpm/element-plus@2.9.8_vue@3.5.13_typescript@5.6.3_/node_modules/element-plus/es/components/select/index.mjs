import Select from './src/select2.mjs';
import Option from './src/option2.mjs';
import OptionGroup from './src/option-group.mjs';
export { selectGroupKey, selectKey } from './src/token.mjs';
export { SelectProps, selectEmits } from './src/select.mjs';
import { withInstall, withNoopInstall } from '../../utils/vue/install.mjs';

const ElSelect = withInstall(Select, {
  Option,
  OptionGroup
});
const ElOption = withNoopInstall(Option);
const ElOptionGroup = withNoopInstall(OptionGroup);

export { ElOption, ElOptionGroup, ElSelect, ElSelect as default };
//# sourceMappingURL=index.mjs.map
