'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var vue = require('vue');
var token = require('../../select/src/token.js');
var core = require('@vueuse/core');

var CacheOptions = vue.defineComponent({
  props: {
    data: {
      type: Array,
      default: () => []
    }
  },
  setup(props) {
    const select = vue.inject(token.selectKey);
    vue.watch(() => props.data, () => {
      var _a;
      props.data.forEach((item) => {
        if (!select.states.cachedOptions.has(item.value)) {
          select.states.cachedOptions.set(item.value, item);
        }
      });
      const inputs = ((_a = select.selectRef) == null ? void 0 : _a.querySelectorAll("input")) || [];
      if (core.isClient && !Array.from(inputs).includes(document.activeElement)) {
        select.setSelected();
      }
    }, { flush: "post", immediate: true });
    return () => void 0;
  }
});

exports["default"] = CacheOptions;
//# sourceMappingURL=cache-options.js.map
