import { buildProps } from '../../../utils/vue/props/runtime.mjs';
import { useSizeProp } from '../../../hooks/use-size/index.mjs';

const descriptionProps = buildProps({
  border: Boolean,
  column: {
    type: Number,
    default: 3
  },
  direction: {
    type: String,
    values: ["horizontal", "vertical"],
    default: "horizontal"
  },
  size: useSizeProp,
  title: {
    type: String,
    default: ""
  },
  extra: {
    type: String,
    default: ""
  },
  labelWidth: {
    type: [String, Number],
    default: ""
  }
});

export { descriptionProps };
//# sourceMappingURL=description.mjs.map
