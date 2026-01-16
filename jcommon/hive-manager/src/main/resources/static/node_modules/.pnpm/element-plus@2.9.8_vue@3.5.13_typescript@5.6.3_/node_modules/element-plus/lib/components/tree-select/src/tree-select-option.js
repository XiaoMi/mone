'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var vue = require('vue');
var index = require('../../select/index.js');

const component = vue.defineComponent({
  extends: index.ElOption,
  setup(props, ctx) {
    const result = index.ElOption.setup(props, ctx);
    delete result.selectOptionClick;
    const vm = vue.getCurrentInstance().proxy;
    vue.nextTick(() => {
      if (!result.select.states.cachedOptions.get(vm.value)) {
        result.select.onOptionCreate(vm);
      }
    });
    vue.watch(() => ctx.attrs.visible, (val) => {
      vue.nextTick(() => {
        result.states.visible = val;
      });
    }, {
      immediate: true
    });
    return result;
  },
  methods: {
    selectOptionClick() {
      this.$el.parentElement.click();
    }
  }
});

exports["default"] = component;
//# sourceMappingURL=tree-select-option.js.map
