'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var vue = require('vue');
var checkTag = require('./check-tag.js');
var pluginVue_exportHelper = require('../../../_virtual/plugin-vue_export-helper.js');
var index = require('../../../hooks/use-namespace/index.js');
var event = require('../../../constants/event.js');

const __default__ = vue.defineComponent({
  name: "ElCheckTag"
});
const _sfc_main = /* @__PURE__ */ vue.defineComponent({
  ...__default__,
  props: checkTag.checkTagProps,
  emits: checkTag.checkTagEmits,
  setup(__props, { emit }) {
    const props = __props;
    const ns = index.useNamespace("check-tag");
    const isDisabled = vue.computed(() => props.disabled);
    const containerKls = vue.computed(() => [
      ns.b(),
      ns.is("checked", props.checked),
      ns.is("disabled", isDisabled.value),
      ns.m(props.type || "primary")
    ]);
    const handleChange = () => {
      if (isDisabled.value)
        return;
      const checked = !props.checked;
      emit(event.CHANGE_EVENT, checked);
      emit("update:checked", checked);
    };
    return (_ctx, _cache) => {
      return vue.openBlock(), vue.createElementBlock("span", {
        class: vue.normalizeClass(vue.unref(containerKls)),
        onClick: handleChange
      }, [
        vue.renderSlot(_ctx.$slots, "default")
      ], 2);
    };
  }
});
var CheckTag = /* @__PURE__ */ pluginVue_exportHelper["default"](_sfc_main, [["__file", "check-tag.vue"]]);

exports["default"] = CheckTag;
//# sourceMappingURL=check-tag2.js.map
