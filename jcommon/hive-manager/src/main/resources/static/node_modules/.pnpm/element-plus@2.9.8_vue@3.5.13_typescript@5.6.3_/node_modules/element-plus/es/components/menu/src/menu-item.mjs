import { buildProps, definePropType } from '../../../utils/vue/props/runtime.mjs';
import { isString, isArray } from '@vue/shared';

const menuItemProps = buildProps({
  index: {
    type: definePropType([String, null]),
    default: null
  },
  route: {
    type: definePropType([String, Object])
  },
  disabled: Boolean
});
const menuItemEmits = {
  click: (item) => isString(item.index) && isArray(item.indexPath)
};

export { menuItemEmits, menuItemProps };
//# sourceMappingURL=menu-item.mjs.map
