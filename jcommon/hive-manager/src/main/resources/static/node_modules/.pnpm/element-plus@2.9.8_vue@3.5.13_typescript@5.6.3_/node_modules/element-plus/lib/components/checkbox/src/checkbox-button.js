'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var vue = require('vue');
var constants = require('./constants.js');
var checkbox = require('./checkbox.js');
var pluginVue_exportHelper = require('../../../_virtual/plugin-vue_export-helper.js');
var useCheckbox = require('./composables/use-checkbox.js');
var index = require('../../../hooks/use-namespace/index.js');

const __default__ = vue.defineComponent({
  name: "ElCheckboxButton"
});
const _sfc_main = /* @__PURE__ */ vue.defineComponent({
  ...__default__,
  props: checkbox.checkboxProps,
  emits: checkbox.checkboxEmits,
  setup(__props) {
    const props = __props;
    const slots = vue.useSlots();
    const {
      isFocused,
      isChecked,
      isDisabled,
      checkboxButtonSize,
      model,
      actualValue,
      handleChange
    } = useCheckbox.useCheckbox(props, slots);
    const checkboxGroup = vue.inject(constants.checkboxGroupContextKey, void 0);
    const ns = index.useNamespace("checkbox");
    const activeStyle = vue.computed(() => {
      var _a, _b, _c, _d;
      const fillValue = (_b = (_a = checkboxGroup == null ? void 0 : checkboxGroup.fill) == null ? void 0 : _a.value) != null ? _b : "";
      return {
        backgroundColor: fillValue,
        borderColor: fillValue,
        color: (_d = (_c = checkboxGroup == null ? void 0 : checkboxGroup.textColor) == null ? void 0 : _c.value) != null ? _d : "",
        boxShadow: fillValue ? `-1px 0 0 0 ${fillValue}` : void 0
      };
    });
    const labelKls = vue.computed(() => {
      return [
        ns.b("button"),
        ns.bm("button", checkboxButtonSize.value),
        ns.is("disabled", isDisabled.value),
        ns.is("checked", isChecked.value),
        ns.is("focus", isFocused.value)
      ];
    });
    return (_ctx, _cache) => {
      var _a, _b, _c, _d;
      return vue.openBlock(), vue.createElementBlock("label", {
        class: vue.normalizeClass(vue.unref(labelKls))
      }, [
        _ctx.trueValue || _ctx.falseValue || _ctx.trueLabel || _ctx.falseLabel ? vue.withDirectives((vue.openBlock(), vue.createElementBlock("input", {
          key: 0,
          "onUpdate:modelValue": ($event) => vue.isRef(model) ? model.value = $event : null,
          class: vue.normalizeClass(vue.unref(ns).be("button", "original")),
          type: "checkbox",
          name: _ctx.name,
          tabindex: _ctx.tabindex,
          disabled: vue.unref(isDisabled),
          "true-value": (_b = (_a = _ctx.trueValue) != null ? _a : _ctx.trueLabel) != null ? _b : true,
          "false-value": (_d = (_c = _ctx.falseValue) != null ? _c : _ctx.falseLabel) != null ? _d : false,
          onChange: vue.unref(handleChange),
          onFocus: ($event) => isFocused.value = true,
          onBlur: ($event) => isFocused.value = false,
          onClick: vue.withModifiers(() => {
          }, ["stop"])
        }, null, 42, ["onUpdate:modelValue", "name", "tabindex", "disabled", "true-value", "false-value", "onChange", "onFocus", "onBlur", "onClick"])), [
          [vue.vModelCheckbox, vue.unref(model)]
        ]) : vue.withDirectives((vue.openBlock(), vue.createElementBlock("input", {
          key: 1,
          "onUpdate:modelValue": ($event) => vue.isRef(model) ? model.value = $event : null,
          class: vue.normalizeClass(vue.unref(ns).be("button", "original")),
          type: "checkbox",
          name: _ctx.name,
          tabindex: _ctx.tabindex,
          disabled: vue.unref(isDisabled),
          value: vue.unref(actualValue),
          onChange: vue.unref(handleChange),
          onFocus: ($event) => isFocused.value = true,
          onBlur: ($event) => isFocused.value = false,
          onClick: vue.withModifiers(() => {
          }, ["stop"])
        }, null, 42, ["onUpdate:modelValue", "name", "tabindex", "disabled", "value", "onChange", "onFocus", "onBlur", "onClick"])), [
          [vue.vModelCheckbox, vue.unref(model)]
        ]),
        _ctx.$slots.default || _ctx.label ? (vue.openBlock(), vue.createElementBlock("span", {
          key: 2,
          class: vue.normalizeClass(vue.unref(ns).be("button", "inner")),
          style: vue.normalizeStyle(vue.unref(isChecked) ? vue.unref(activeStyle) : void 0)
        }, [
          vue.renderSlot(_ctx.$slots, "default", {}, () => [
            vue.createTextVNode(vue.toDisplayString(_ctx.label), 1)
          ])
        ], 6)) : vue.createCommentVNode("v-if", true)
      ], 2);
    };
  }
});
var CheckboxButton = /* @__PURE__ */ pluginVue_exportHelper["default"](_sfc_main, [["__file", "checkbox-button.vue"]]);

exports["default"] = CheckboxButton;
//# sourceMappingURL=checkbox-button.js.map
