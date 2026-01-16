import { defineComponent, ref, reactive, computed, watch, openBlock, createElementBlock, unref, normalizeClass, createElementVNode, normalizeStyle, Fragment, renderList, renderSlot, createTextVNode, toDisplayString, createCommentVNode } from 'vue';
import { useActiveElement, useResizeObserver } from '@vueuse/core';
import { segmentedProps, segmentedEmits, defaultProps } from './segmented.mjs';
import _export_sfc from '../../../_virtual/plugin-vue_export-helper.mjs';
import { useNamespace } from '../../../hooks/use-namespace/index.mjs';
import { useId } from '../../../hooks/use-id/index.mjs';
import { useFormSize, useFormDisabled } from '../../form/src/hooks/use-form-common-props.mjs';
import { useFormItem, useFormItemInputId } from '../../form/src/hooks/use-form-item.mjs';
import { isObject } from '@vue/shared';
import { debugWarn } from '../../../utils/error.mjs';
import { UPDATE_MODEL_EVENT, CHANGE_EVENT } from '../../../constants/event.mjs';

const __default__ = defineComponent({
  name: "ElSegmented"
});
const _sfc_main = /* @__PURE__ */ defineComponent({
  ...__default__,
  props: segmentedProps,
  emits: segmentedEmits,
  setup(__props, { emit }) {
    const props = __props;
    const ns = useNamespace("segmented");
    const segmentedId = useId();
    const segmentedSize = useFormSize();
    const _disabled = useFormDisabled();
    const { formItem } = useFormItem();
    const { inputId, isLabeledByFormItem } = useFormItemInputId(props, {
      formItemContext: formItem
    });
    const segmentedRef = ref(null);
    const activeElement = useActiveElement();
    const state = reactive({
      isInit: false,
      width: 0,
      height: 0,
      translateX: 0,
      translateY: 0,
      focusVisible: false
    });
    const handleChange = (item) => {
      const value = getValue(item);
      emit(UPDATE_MODEL_EVENT, value);
      emit(CHANGE_EVENT, value);
    };
    const aliasProps = computed(() => ({ ...defaultProps, ...props.props }));
    const getValue = (item) => {
      return isObject(item) ? item[aliasProps.value.value] : item;
    };
    const getLabel = (item) => {
      return isObject(item) ? item[aliasProps.value.label] : item;
    };
    const getDisabled = (item) => {
      return !!(_disabled.value || (isObject(item) ? item[aliasProps.value.disabled] : false));
    };
    const getSelected = (item) => {
      return props.modelValue === getValue(item);
    };
    const getOption = (value) => {
      return props.options.find((item) => getValue(item) === value);
    };
    const getItemCls = (item) => {
      return [
        ns.e("item"),
        ns.is("selected", getSelected(item)),
        ns.is("disabled", getDisabled(item))
      ];
    };
    const updateSelect = () => {
      if (!segmentedRef.value)
        return;
      const selectedItem = segmentedRef.value.querySelector(".is-selected");
      const selectedItemInput = segmentedRef.value.querySelector(".is-selected input");
      if (!selectedItem || !selectedItemInput) {
        state.width = 0;
        state.height = 0;
        state.translateX = 0;
        state.translateY = 0;
        state.focusVisible = false;
        return;
      }
      const rect = selectedItem.getBoundingClientRect();
      state.isInit = true;
      if (props.direction === "vertical") {
        state.height = rect.height;
        state.translateY = selectedItem.offsetTop;
      } else {
        state.width = rect.width;
        state.translateX = selectedItem.offsetLeft;
      }
      try {
        state.focusVisible = selectedItemInput.matches(":focus-visible");
      } catch (e) {
      }
    };
    const segmentedCls = computed(() => [
      ns.b(),
      ns.m(segmentedSize.value),
      ns.is("block", props.block)
    ]);
    const selectedStyle = computed(() => ({
      width: props.direction === "vertical" ? "100%" : `${state.width}px`,
      height: props.direction === "vertical" ? `${state.height}px` : "100%",
      transform: props.direction === "vertical" ? `translateY(${state.translateY}px)` : `translateX(${state.translateX}px)`,
      display: state.isInit ? "block" : "none"
    }));
    const selectedCls = computed(() => [
      ns.e("item-selected"),
      ns.is("disabled", getDisabled(getOption(props.modelValue))),
      ns.is("focus-visible", state.focusVisible)
    ]);
    const name = computed(() => {
      return props.name || segmentedId.value;
    });
    useResizeObserver(segmentedRef, updateSelect);
    watch(activeElement, updateSelect);
    watch(() => props.modelValue, () => {
      var _a;
      updateSelect();
      if (props.validateEvent) {
        (_a = formItem == null ? void 0 : formItem.validate) == null ? void 0 : _a.call(formItem, "change").catch((err) => debugWarn(err));
      }
    }, {
      flush: "post"
    });
    return (_ctx, _cache) => {
      return _ctx.options.length ? (openBlock(), createElementBlock("div", {
        key: 0,
        id: unref(inputId),
        ref_key: "segmentedRef",
        ref: segmentedRef,
        class: normalizeClass(unref(segmentedCls)),
        role: "radiogroup",
        "aria-label": !unref(isLabeledByFormItem) ? _ctx.ariaLabel || "segmented" : void 0,
        "aria-labelledby": unref(isLabeledByFormItem) ? unref(formItem).labelId : void 0
      }, [
        createElementVNode("div", {
          class: normalizeClass([unref(ns).e("group"), unref(ns).m(props.direction)])
        }, [
          createElementVNode("div", {
            style: normalizeStyle(unref(selectedStyle)),
            class: normalizeClass(unref(selectedCls))
          }, null, 6),
          (openBlock(true), createElementBlock(Fragment, null, renderList(_ctx.options, (item, index) => {
            return openBlock(), createElementBlock("label", {
              key: index,
              class: normalizeClass(getItemCls(item))
            }, [
              createElementVNode("input", {
                class: normalizeClass(unref(ns).e("item-input")),
                type: "radio",
                name: unref(name),
                disabled: getDisabled(item),
                checked: getSelected(item),
                onChange: ($event) => handleChange(item)
              }, null, 42, ["name", "disabled", "checked", "onChange"]),
              createElementVNode("div", {
                class: normalizeClass(unref(ns).e("item-label"))
              }, [
                renderSlot(_ctx.$slots, "default", { item }, () => [
                  createTextVNode(toDisplayString(getLabel(item)), 1)
                ])
              ], 2)
            ], 2);
          }), 128))
        ], 2)
      ], 10, ["id", "aria-label", "aria-labelledby"])) : createCommentVNode("v-if", true);
    };
  }
});
var Segmented = /* @__PURE__ */ _export_sfc(_sfc_main, [["__file", "segmented.vue"]]);

export { Segmented as default };
//# sourceMappingURL=segmented2.mjs.map
