import { pick } from 'lodash-unified';
import { buildProps } from '../../utils/vue/props/runtime.mjs';

const ariaProps = buildProps({
  ariaLabel: String,
  ariaOrientation: {
    type: String,
    values: ["horizontal", "vertical", "undefined"]
  },
  ariaControls: String
});
const useAriaProps = (arias) => {
  return pick(ariaProps, arias);
};

export { ariaProps, useAriaProps };
//# sourceMappingURL=index.mjs.map
