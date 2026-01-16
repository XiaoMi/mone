import { buildProps, definePropType } from '../../../utils/vue/props/runtime.mjs';

const maskProps = buildProps({
  zIndex: {
    type: Number,
    default: 1001
  },
  visible: Boolean,
  fill: {
    type: String,
    default: "rgba(0,0,0,0.5)"
  },
  pos: {
    type: definePropType(Object)
  },
  targetAreaClickable: {
    type: Boolean,
    default: true
  }
});

export { maskProps };
//# sourceMappingURL=mask.mjs.map
