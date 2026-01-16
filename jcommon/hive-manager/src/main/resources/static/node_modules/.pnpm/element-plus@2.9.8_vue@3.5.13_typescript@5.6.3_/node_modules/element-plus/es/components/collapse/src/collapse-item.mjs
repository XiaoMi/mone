import { ArrowRight } from '@element-plus/icons-vue';
import { buildProps, definePropType } from '../../../utils/vue/props/runtime.mjs';
import { iconPropType } from '../../../utils/vue/icon.mjs';

const collapseItemProps = buildProps({
  title: {
    type: String,
    default: ""
  },
  name: {
    type: definePropType([String, Number]),
    default: void 0
  },
  icon: {
    type: iconPropType,
    default: ArrowRight
  },
  disabled: Boolean
});

export { collapseItemProps };
//# sourceMappingURL=collapse-item.mjs.map
