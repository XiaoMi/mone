import { buildProps, definePropType } from '../../../utils/vue/props/runtime.mjs';

const cardProps = buildProps({
  header: {
    type: String,
    default: ""
  },
  footer: {
    type: String,
    default: ""
  },
  bodyStyle: {
    type: definePropType([String, Object, Array]),
    default: ""
  },
  headerClass: String,
  bodyClass: String,
  footerClass: String,
  shadow: {
    type: String,
    values: ["always", "hover", "never"],
    default: "always"
  }
});

export { cardProps };
//# sourceMappingURL=card.mjs.map
