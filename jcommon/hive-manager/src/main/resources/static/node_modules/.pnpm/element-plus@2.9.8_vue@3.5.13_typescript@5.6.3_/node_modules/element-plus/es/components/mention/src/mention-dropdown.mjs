import { buildProps, definePropType } from '../../../utils/vue/props/runtime.mjs';
import { isString } from '@vue/shared';

const mentionDropdownProps = buildProps({
  options: {
    type: definePropType(Array),
    default: () => []
  },
  loading: Boolean,
  disabled: Boolean,
  contentId: String,
  ariaLabel: String
});
const mentionDropdownEmits = {
  select: (option) => isString(option.value)
};

export { mentionDropdownEmits, mentionDropdownProps };
//# sourceMappingURL=mention-dropdown.mjs.map
