import { buildProps, definePropType } from '../../../utils/vue/props/runtime.mjs';

const breadcrumbItemProps = buildProps({
  to: {
    type: definePropType([String, Object]),
    default: ""
  },
  replace: Boolean
});

export { breadcrumbItemProps };
//# sourceMappingURL=breadcrumb-item.mjs.map
