import { buildProps, definePropType } from '../../../utils/vue/props/runtime.mjs';

const teleportProps = buildProps({
  to: {
    type: definePropType([String, Object]),
    required: true
  },
  disabled: Boolean
});

export { teleportProps };
//# sourceMappingURL=teleport.mjs.map
