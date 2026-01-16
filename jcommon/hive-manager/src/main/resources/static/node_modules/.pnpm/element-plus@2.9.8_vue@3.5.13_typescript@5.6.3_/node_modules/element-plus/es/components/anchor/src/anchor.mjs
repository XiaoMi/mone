import { buildProps, definePropType } from '../../../utils/vue/props/runtime.mjs';
import { isString } from '@vue/shared';
import { isUndefined } from '../../../utils/types.mjs';

const anchorProps = buildProps({
  container: {
    type: definePropType([
      String,
      Object
    ])
  },
  offset: {
    type: Number,
    default: 0
  },
  bound: {
    type: Number,
    default: 15
  },
  duration: {
    type: Number,
    default: 300
  },
  marker: {
    type: Boolean,
    default: true
  },
  type: {
    type: definePropType(String),
    default: "default"
  },
  direction: {
    type: definePropType(String),
    default: "vertical"
  },
  selectScrollTop: {
    type: Boolean,
    default: false
  }
});
const anchorEmits = {
  change: (href) => isString(href),
  click: (e, href) => e instanceof MouseEvent && (isString(href) || isUndefined(href))
};

export { anchorEmits, anchorProps };
//# sourceMappingURL=anchor.mjs.map
