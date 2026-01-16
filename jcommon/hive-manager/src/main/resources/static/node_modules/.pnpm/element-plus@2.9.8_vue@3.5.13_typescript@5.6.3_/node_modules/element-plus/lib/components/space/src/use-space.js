'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var vue = require('vue');
var index = require('../../../hooks/use-namespace/index.js');
var shared = require('@vue/shared');
var types = require('../../../utils/types.js');

const SIZE_MAP = {
  small: 8,
  default: 12,
  large: 16
};
function useSpace(props) {
  const ns = index.useNamespace("space");
  const classes = vue.computed(() => [ns.b(), ns.m(props.direction), props.class]);
  const horizontalSize = vue.ref(0);
  const verticalSize = vue.ref(0);
  const containerStyle = vue.computed(() => {
    const wrapKls = props.wrap || props.fill ? { flexWrap: "wrap" } : {};
    const alignment = {
      alignItems: props.alignment
    };
    const gap = {
      rowGap: `${verticalSize.value}px`,
      columnGap: `${horizontalSize.value}px`
    };
    return [wrapKls, alignment, gap, props.style];
  });
  const itemStyle = vue.computed(() => {
    return props.fill ? { flexGrow: 1, minWidth: `${props.fillRatio}%` } : {};
  });
  vue.watchEffect(() => {
    const { size = "small", wrap, direction: dir, fill } = props;
    if (shared.isArray(size)) {
      const [h = 0, v = 0] = size;
      horizontalSize.value = h;
      verticalSize.value = v;
    } else {
      let val;
      if (types.isNumber(size)) {
        val = size;
      } else {
        val = SIZE_MAP[size || "small"] || SIZE_MAP.small;
      }
      if ((wrap || fill) && dir === "horizontal") {
        horizontalSize.value = verticalSize.value = val;
      } else {
        if (dir === "horizontal") {
          horizontalSize.value = val;
          verticalSize.value = 0;
        } else {
          verticalSize.value = val;
          horizontalSize.value = 0;
        }
      }
    }
  });
  return {
    classes,
    containerStyle,
    itemStyle
  };
}

exports.useSpace = useSpace;
//# sourceMappingURL=use-space.js.map
