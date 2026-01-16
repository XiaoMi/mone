'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var vue = require('vue');
var radioGroup = require('./radio-group.js');
var constants = require('./constants.js');
var pluginVue_exportHelper = require('../../../_virtual/plugin-vue_export-helper.js');
var index = require('../../../hooks/use-namespace/index.js');
var index$1 = require('../../../hooks/use-id/index.js');
var useFormItem = require('../../form/src/hooks/use-form-item.js');
var error = require('../../../utils/error.js');
var event = require('../../../constants/event.js');

const __default__ = vue.defineComponent({
  name: "ElRadioGroup"
});
const _sfc_main = /* @__PURE__ */ vue.defineComponent({
  ...__default__,
  props: radioGroup.radioGroupProps,
  emits: radioGroup.radioGroupEmits,
  setup(__props, { emit }) {
    const props = __props;
    const ns = index.useNamespace("radio");
    const radioId = index$1.useId();
    const radioGroupRef = vue.ref();
    const { formItem } = useFormItem.useFormItem();
    const { inputId: groupId, isLabeledByFormItem } = useFormItem.useFormItemInputId(props, {
      formItemContext: formItem
    });
    const changeEvent = (value) => {
      emit(event.UPDATE_MODEL_EVENT, value);
      vue.nextTick(() => emit(event.CHANGE_EVENT, value));
    };
    vue.onMounted(() => {
      const radios = radioGroupRef.value.querySelectorAll("[type=radio]");
      const firstLabel = radios[0];
      if (!Array.from(radios).some((radio) => radio.checked) && firstLabel) {
        firstLabel.tabIndex = 0;
      }
    });
    const name = vue.computed(() => {
      return props.name || radioId.value;
    });
    vue.provide(constants.radioGroupKey, vue.reactive({
      ...vue.toRefs(props),
      changeEvent,
      name
    }));
    vue.watch(() => props.modelValue, () => {
      if (props.validateEvent) {
        formItem == null ? void 0 : formItem.validate("change").catch((err) => error.debugWarn(err));
      }
    });
    return (_ctx, _cache) => {
      return vue.openBlock(), vue.createElementBlock("div", {
        id: vue.unref(groupId),
        ref_key: "radioGroupRef",
        ref: radioGroupRef,
        class: vue.normalizeClass(vue.unref(ns).b("group")),
        role: "radiogroup",
        "aria-label": !vue.unref(isLabeledByFormItem) ? _ctx.ariaLabel || "radio-group" : void 0,
        "aria-labelledby": vue.unref(isLabeledByFormItem) ? vue.unref(formItem).labelId : void 0
      }, [
        vue.renderSlot(_ctx.$slots, "default")
      ], 10, ["id", "aria-label", "aria-labelledby"]);
    };
  }
});
var RadioGroup = /* @__PURE__ */ pluginVue_exportHelper["default"](_sfc_main, [["__file", "radio-group.vue"]]);

exports["default"] = RadioGroup;
//# sourceMappingURL=radio-group2.js.map
