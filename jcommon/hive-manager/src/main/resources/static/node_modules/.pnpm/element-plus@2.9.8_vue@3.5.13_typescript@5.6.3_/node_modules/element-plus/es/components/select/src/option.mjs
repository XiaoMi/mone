import { buildProps } from '../../../utils/vue/props/runtime.mjs';

const COMPONENT_NAME = "ElOption";
const optionProps = buildProps({
  value: {
    type: [String, Number, Boolean, Object],
    required: true
  },
  label: {
    type: [String, Number]
  },
  created: Boolean,
  disabled: Boolean
});

export { COMPONENT_NAME, optionProps };
//# sourceMappingURL=option.mjs.map
