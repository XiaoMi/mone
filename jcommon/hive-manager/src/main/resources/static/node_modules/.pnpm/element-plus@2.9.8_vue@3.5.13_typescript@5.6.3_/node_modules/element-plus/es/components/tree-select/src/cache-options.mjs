import { defineComponent, inject, watch } from 'vue';
import { selectKey } from '../../select/src/token.mjs';
import { isClient } from '@vueuse/core';

var CacheOptions = defineComponent({
  props: {
    data: {
      type: Array,
      default: () => []
    }
  },
  setup(props) {
    const select = inject(selectKey);
    watch(() => props.data, () => {
      var _a;
      props.data.forEach((item) => {
        if (!select.states.cachedOptions.has(item.value)) {
          select.states.cachedOptions.set(item.value, item);
        }
      });
      const inputs = ((_a = select.selectRef) == null ? void 0 : _a.querySelectorAll("input")) || [];
      if (isClient && !Array.from(inputs).includes(document.activeElement)) {
        select.setSelected();
      }
    }, { flush: "post", immediate: true });
    return () => void 0;
  }
});

export { CacheOptions as default };
//# sourceMappingURL=cache-options.mjs.map
