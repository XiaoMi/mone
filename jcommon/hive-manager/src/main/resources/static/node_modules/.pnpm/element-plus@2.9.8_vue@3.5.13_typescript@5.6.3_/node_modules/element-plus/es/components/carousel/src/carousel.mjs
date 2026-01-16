import { buildProps } from '../../../utils/vue/props/runtime.mjs';
import { isNumber } from '../../../utils/types.mjs';

const carouselProps = buildProps({
  initialIndex: {
    type: Number,
    default: 0
  },
  height: {
    type: String,
    default: ""
  },
  trigger: {
    type: String,
    values: ["hover", "click"],
    default: "hover"
  },
  autoplay: {
    type: Boolean,
    default: true
  },
  interval: {
    type: Number,
    default: 3e3
  },
  indicatorPosition: {
    type: String,
    values: ["", "none", "outside"],
    default: ""
  },
  arrow: {
    type: String,
    values: ["always", "hover", "never"],
    default: "hover"
  },
  type: {
    type: String,
    values: ["", "card"],
    default: ""
  },
  cardScale: {
    type: Number,
    default: 0.83
  },
  loop: {
    type: Boolean,
    default: true
  },
  direction: {
    type: String,
    values: ["horizontal", "vertical"],
    default: "horizontal"
  },
  pauseOnHover: {
    type: Boolean,
    default: true
  },
  motionBlur: Boolean
});
const carouselEmits = {
  change: (current, prev) => [current, prev].every(isNumber)
};

export { carouselEmits, carouselProps };
//# sourceMappingURL=carousel.mjs.map
