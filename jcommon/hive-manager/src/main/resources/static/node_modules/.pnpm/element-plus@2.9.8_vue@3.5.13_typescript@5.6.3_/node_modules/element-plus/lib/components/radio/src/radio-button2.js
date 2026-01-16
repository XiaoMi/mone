'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var vue = require('vue');
var useRadio = require('./use-radio.js');
var radioButton = require('./radio-button.js');
var pluginVue_exportHelper = require('../../../_virtual/plugin-vue_export-helper.js');
var index = require('../../../hooks/use-namespace/index.js');

const __default__ = vue.defineComponent({
  name: "ElRadioButton"
});
const _sfc_main = /* @__PURE__ */ vue.defineComponent({
  ...__default__,
  props: radioButton.radioButtonProps,
  setup(__props) {
    const props = __props;
    const ns = index.useNamespace("radio");
    const { radioRef, focus, size, disabled, modelValue, radioGroup, actualValue } = useRadio.useRadio(props);
    const activeStyle = vue.computed(() => {
      return {
        backgroundColor: (radioGroup == null ? void 0 : radioGroup.fill) || "",
        borderColor: (radioGroup == null ? void 0 : radioGroup.fill) || "",
        boxShadow: (radioGroup == null ? void 0 : radioGroup.fill) ? `-1px 0 0 0 ${radioGroup.fill}` : "",
        color: (radioGroup == null ? void 0 : radioGroup.textColor) || ""
      };
    });
    return (_ctx, _cache) => {
      var _a;
      return vue.openBlock(), vue.createElementBlock("label", {
        class: vue.normalizeClass([
          vue.unref(ns).b("button"),
          vue.unref(ns).is("active", vue.unref(modelValue) === vue.unref(actualValue)),
          vue.unref(ns).is("disabled", vue.unref(disabled)),
          vue.unref(ns).is("focus", vue.unref(focus)),
          vue.unref(ns).bm("button", vue.unref(size))
        ])
      }, [
        vue.withDirectives(vue.createElementVNode("input", {
          ref_key: "radioRef",
          ref: radioRef,
          "onUpdate:modelValue": ($event) => vue.isRef(modelValue) ? modelValue.value = $event : null,
          class: vue.normalizeClass(vue.unref(ns).be("button", "original-radio")),
          value: vue.unref(actualValue),
          type: "radio",
          name: _ctx.name || ((_a = vue.unref(radioGroup)) == null ? void 0 : _a.name),
          disabled: vue.unref(disabled),
          onFocus: ($event) => focus.value = true,
          onBlur: ($event) => focus.value = false,
          onClick: vue.withModifiers(() => {
          }, ["stop"])
        }, null, 42, ["onUpdate:modelValue", "value", "name", "disabled", "onFocus", "onBlur", "onClick"]), [
          [vue.vModelRadio, vue.unref(modelValue)]
        ]),
        vue.createElementVNode("span", {
          class: vue.normalizeClass(vue.unref(ns).be("button", "inner")),
          style: vue.normalizeStyle(vue.unref(modelValue) === vue.unref(actualValue) ? vue.unref(activeStyle) : {}),
          onKeydown: vue.withModifiers(() => {
          }, ["stop"])
        }, [
          vue.renderSlot(_ctx.$slots, "default", {}, () => [
            vue.createTextVNode(vue.toDisplayString(_ctx.label), 1)
          ])
        ], 46, ["onKeydown"])
      ], 2);
    };
  }
});
var RadioButton = /* @__PURE__ */ pluginVue_exportHelper["default"](_sfc_main, [["__file", "radio-button.vue"]]);

exports["default"] = RadioButton;
//# sourceMappingURL=radio-button2.js.map
