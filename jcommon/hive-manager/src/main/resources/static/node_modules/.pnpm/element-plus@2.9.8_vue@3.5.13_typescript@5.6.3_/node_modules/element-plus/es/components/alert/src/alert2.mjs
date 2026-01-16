import { defineComponent, useSlots, ref, computed, openBlock, createBlock, Transition, unref, withCtx, withDirectives, createElementVNode, normalizeClass, renderSlot, resolveDynamicComponent, createCommentVNode, createElementBlock, createTextVNode, toDisplayString, Fragment, createVNode, vShow } from 'vue';
import { ElIcon } from '../../icon/index.mjs';
import { alertProps, alertEmits } from './alert.mjs';
import _export_sfc from '../../../_virtual/plugin-vue_export-helper.mjs';
import { TypeComponentsMap, TypeComponents } from '../../../utils/vue/icon.mjs';
import { useNamespace } from '../../../hooks/use-namespace/index.mjs';

const __default__ = defineComponent({
  name: "ElAlert"
});
const _sfc_main = /* @__PURE__ */ defineComponent({
  ...__default__,
  props: alertProps,
  emits: alertEmits,
  setup(__props, { emit }) {
    const props = __props;
    const { Close } = TypeComponents;
    const slots = useSlots();
    const ns = useNamespace("alert");
    const visible = ref(true);
    const iconComponent = computed(() => TypeComponentsMap[props.type]);
    const hasDesc = computed(() => !!(props.description || slots.default));
    const close = (evt) => {
      visible.value = false;
      emit("close", evt);
    };
    return (_ctx, _cache) => {
      return openBlock(), createBlock(Transition, {
        name: unref(ns).b("fade"),
        persisted: ""
      }, {
        default: withCtx(() => [
          withDirectives(createElementVNode("div", {
            class: normalizeClass([unref(ns).b(), unref(ns).m(_ctx.type), unref(ns).is("center", _ctx.center), unref(ns).is(_ctx.effect)]),
            role: "alert"
          }, [
            _ctx.showIcon && (_ctx.$slots.icon || unref(iconComponent)) ? (openBlock(), createBlock(unref(ElIcon), {
              key: 0,
              class: normalizeClass([unref(ns).e("icon"), { [unref(ns).is("big")]: unref(hasDesc) }])
            }, {
              default: withCtx(() => [
                renderSlot(_ctx.$slots, "icon", {}, () => [
                  (openBlock(), createBlock(resolveDynamicComponent(unref(iconComponent))))
                ])
              ]),
              _: 3
            }, 8, ["class"])) : createCommentVNode("v-if", true),
            createElementVNode("div", {
              class: normalizeClass(unref(ns).e("content"))
            }, [
              _ctx.title || _ctx.$slots.title ? (openBlock(), createElementBlock("span", {
                key: 0,
                class: normalizeClass([unref(ns).e("title"), { "with-description": unref(hasDesc) }])
              }, [
                renderSlot(_ctx.$slots, "title", {}, () => [
                  createTextVNode(toDisplayString(_ctx.title), 1)
                ])
              ], 2)) : createCommentVNode("v-if", true),
              unref(hasDesc) ? (openBlock(), createElementBlock("p", {
                key: 1,
                class: normalizeClass(unref(ns).e("description"))
              }, [
                renderSlot(_ctx.$slots, "default", {}, () => [
                  createTextVNode(toDisplayString(_ctx.description), 1)
                ])
              ], 2)) : createCommentVNode("v-if", true),
              _ctx.closable ? (openBlock(), createElementBlock(Fragment, { key: 2 }, [
                _ctx.closeText ? (openBlock(), createElementBlock("div", {
                  key: 0,
                  class: normalizeClass([unref(ns).e("close-btn"), unref(ns).is("customed")]),
                  onClick: close
                }, toDisplayString(_ctx.closeText), 3)) : (openBlock(), createBlock(unref(ElIcon), {
                  key: 1,
                  class: normalizeClass(unref(ns).e("close-btn")),
                  onClick: close
                }, {
                  default: withCtx(() => [
                    createVNode(unref(Close))
                  ]),
                  _: 1
                }, 8, ["class"]))
              ], 64)) : createCommentVNode("v-if", true)
            ], 2)
          ], 2), [
            [vShow, visible.value]
          ])
        ]),
        _: 3
      }, 8, ["name"]);
    };
  }
});
var Alert = /* @__PURE__ */ _export_sfc(_sfc_main, [["__file", "alert.vue"]]);

export { Alert as default };
//# sourceMappingURL=alert2.mjs.map
