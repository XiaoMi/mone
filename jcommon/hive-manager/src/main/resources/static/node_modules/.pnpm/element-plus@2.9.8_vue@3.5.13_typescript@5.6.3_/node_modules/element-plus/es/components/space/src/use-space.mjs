import { computed, ref, watchEffect } from 'vue';
import { useNamespace } from '../../../hooks/use-namespace/index.mjs';
import { isArray } from '@vue/shared';
import { isNumber } from '../../../utils/types.mjs';

const SIZE_MAP = {
  small: 8,
  default: 12,
  large: 16
};
function useSpace(props) {
  const ns = useNamespace("space");
  const classes = computed(() => [ns.b(), ns.m(props.direction), props.class]);
  const horizontalSize = ref(0);
  const verticalSize = ref(0);
  const containerStyle = computed(() => {
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
  const itemStyle = computed(() => {
    return props.fill ? { flexGrow: 1, minWidth: `${props.fillRatio}%` } : {};
  });
  watchEffect(() => {
    const { size = "small", wrap, direction: dir, fill } = props;
    if (isArray(size)) {
      const [h = 0, v = 0] = size;
      horizontalSize.value = h;
      verticalSize.value = v;
    } else {
      let val;
      if (isNumber(size)) {
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

export { useSpace };
//# sourceMappingURL=use-space.mjs.map
