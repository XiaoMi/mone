import { buildProps } from '../../../utils/vue/props/runtime.mjs';

const barProps = buildProps({
  always: {
    type: Boolean,
    default: true
  },
  minSize: {
    type: Number,
    required: true
  }
});

export { barProps };
//# sourceMappingURL=bar.mjs.map
