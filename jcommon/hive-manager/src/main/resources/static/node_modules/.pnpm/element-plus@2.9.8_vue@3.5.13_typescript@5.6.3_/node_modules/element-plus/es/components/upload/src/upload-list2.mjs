import { defineComponent, ref, computed, openBlock, createBlock, TransitionGroup, normalizeClass, unref, withCtx, createElementBlock, Fragment, renderList, withKeys, renderSlot, createCommentVNode, createElementVNode, withModifiers, createVNode, toDisplayString, normalizeStyle } from 'vue';
import { ElIcon } from '../../icon/index.mjs';
import { Document, CircleCheck, Check, Close, ZoomIn, Delete } from '@element-plus/icons-vue';
import { ElProgress } from '../../progress/index.mjs';
import { uploadListProps, uploadListEmits } from './upload-list.mjs';
import _export_sfc from '../../../_virtual/plugin-vue_export-helper.mjs';
import { useLocale } from '../../../hooks/use-locale/index.mjs';
import { useNamespace } from '../../../hooks/use-namespace/index.mjs';
import { useFormDisabled } from '../../form/src/hooks/use-form-common-props.mjs';

const __default__ = defineComponent({
  name: "ElUploadList"
});
const _sfc_main = /* @__PURE__ */ defineComponent({
  ...__default__,
  props: uploadListProps,
  emits: uploadListEmits,
  setup(__props, { emit }) {
    const props = __props;
    const { t } = useLocale();
    const nsUpload = useNamespace("upload");
    const nsIcon = useNamespace("icon");
    const nsList = useNamespace("list");
    const disabled = useFormDisabled();
    const focusing = ref(false);
    const containerKls = computed(() => [
      nsUpload.b("list"),
      nsUpload.bm("list", props.listType),
      nsUpload.is("disabled", props.disabled)
    ]);
    const handleRemove = (file) => {
      emit("remove", file);
    };
    return (_ctx, _cache) => {
      return openBlock(), createBlock(TransitionGroup, {
        tag: "ul",
        class: normalizeClass(unref(containerKls)),
        name: unref(nsList).b()
      }, {
        default: withCtx(() => [
          (openBlock(true), createElementBlock(Fragment, null, renderList(_ctx.files, (file, index) => {
            return openBlock(), createElementBlock("li", {
              key: file.uid || file.name,
              class: normalizeClass([
                unref(nsUpload).be("list", "item"),
                unref(nsUpload).is(file.status),
                { focusing: focusing.value }
              ]),
              tabindex: "0",
              onKeydown: withKeys(($event) => !unref(disabled) && handleRemove(file), ["delete"]),
              onFocus: ($event) => focusing.value = true,
              onBlur: ($event) => focusing.value = false,
              onClick: ($event) => focusing.value = false
            }, [
              renderSlot(_ctx.$slots, "default", {
                file,
                index
              }, () => [
                _ctx.listType === "picture" || file.status !== "uploading" && _ctx.listType === "picture-card" ? (openBlock(), createElementBlock("img", {
                  key: 0,
                  class: normalizeClass(unref(nsUpload).be("list", "item-thumbnail")),
                  src: file.url,
                  crossorigin: _ctx.crossorigin,
                  alt: ""
                }, null, 10, ["src", "crossorigin"])) : createCommentVNode("v-if", true),
                file.status === "uploading" || _ctx.listType !== "picture-card" ? (openBlock(), createElementBlock("div", {
                  key: 1,
                  class: normalizeClass(unref(nsUpload).be("list", "item-info"))
                }, [
                  createElementVNode("a", {
                    class: normalizeClass(unref(nsUpload).be("list", "item-name")),
                    onClick: withModifiers(($event) => _ctx.handlePreview(file), ["prevent"])
                  }, [
                    createVNode(unref(ElIcon), {
                      class: normalizeClass(unref(nsIcon).m("document"))
                    }, {
                      default: withCtx(() => [
                        createVNode(unref(Document))
                      ]),
                      _: 1
                    }, 8, ["class"]),
                    createElementVNode("span", {
                      class: normalizeClass(unref(nsUpload).be("list", "item-file-name")),
                      title: file.name
                    }, toDisplayString(file.name), 11, ["title"])
                  ], 10, ["onClick"]),
                  file.status === "uploading" ? (openBlock(), createBlock(unref(ElProgress), {
                    key: 0,
                    type: _ctx.listType === "picture-card" ? "circle" : "line",
                    "stroke-width": _ctx.listType === "picture-card" ? 6 : 2,
                    percentage: Number(file.percentage),
                    style: normalizeStyle(_ctx.listType === "picture-card" ? "" : "margin-top: 0.5rem")
                  }, null, 8, ["type", "stroke-width", "percentage", "style"])) : createCommentVNode("v-if", true)
                ], 2)) : createCommentVNode("v-if", true),
                createElementVNode("label", {
                  class: normalizeClass(unref(nsUpload).be("list", "item-status-label"))
                }, [
                  _ctx.listType === "text" ? (openBlock(), createBlock(unref(ElIcon), {
                    key: 0,
                    class: normalizeClass([unref(nsIcon).m("upload-success"), unref(nsIcon).m("circle-check")])
                  }, {
                    default: withCtx(() => [
                      createVNode(unref(CircleCheck))
                    ]),
                    _: 1
                  }, 8, ["class"])) : ["picture-card", "picture"].includes(_ctx.listType) ? (openBlock(), createBlock(unref(ElIcon), {
                    key: 1,
                    class: normalizeClass([unref(nsIcon).m("upload-success"), unref(nsIcon).m("check")])
                  }, {
                    default: withCtx(() => [
                      createVNode(unref(Check))
                    ]),
                    _: 1
                  }, 8, ["class"])) : createCommentVNode("v-if", true)
                ], 2),
                !unref(disabled) ? (openBlock(), createBlock(unref(ElIcon), {
                  key: 2,
                  class: normalizeClass(unref(nsIcon).m("close")),
                  onClick: ($event) => handleRemove(file)
                }, {
                  default: withCtx(() => [
                    createVNode(unref(Close))
                  ]),
                  _: 2
                }, 1032, ["class", "onClick"])) : createCommentVNode("v-if", true),
                createCommentVNode(" Due to close btn only appears when li gets focused disappears after li gets blurred, thus keyboard navigation can never reach close btn"),
                createCommentVNode(" This is a bug which needs to be fixed "),
                createCommentVNode(" TODO: Fix the incorrect navigation interaction "),
                !unref(disabled) ? (openBlock(), createElementBlock("i", {
                  key: 3,
                  class: normalizeClass(unref(nsIcon).m("close-tip"))
                }, toDisplayString(unref(t)("el.upload.deleteTip")), 3)) : createCommentVNode("v-if", true),
                _ctx.listType === "picture-card" ? (openBlock(), createElementBlock("span", {
                  key: 4,
                  class: normalizeClass(unref(nsUpload).be("list", "item-actions"))
                }, [
                  createElementVNode("span", {
                    class: normalizeClass(unref(nsUpload).be("list", "item-preview")),
                    onClick: ($event) => _ctx.handlePreview(file)
                  }, [
                    createVNode(unref(ElIcon), {
                      class: normalizeClass(unref(nsIcon).m("zoom-in"))
                    }, {
                      default: withCtx(() => [
                        createVNode(unref(ZoomIn))
                      ]),
                      _: 1
                    }, 8, ["class"])
                  ], 10, ["onClick"]),
                  !unref(disabled) ? (openBlock(), createElementBlock("span", {
                    key: 0,
                    class: normalizeClass(unref(nsUpload).be("list", "item-delete")),
                    onClick: ($event) => handleRemove(file)
                  }, [
                    createVNode(unref(ElIcon), {
                      class: normalizeClass(unref(nsIcon).m("delete"))
                    }, {
                      default: withCtx(() => [
                        createVNode(unref(Delete))
                      ]),
                      _: 1
                    }, 8, ["class"])
                  ], 10, ["onClick"])) : createCommentVNode("v-if", true)
                ], 2)) : createCommentVNode("v-if", true)
              ])
            ], 42, ["onKeydown", "onFocus", "onBlur", "onClick"]);
          }), 128)),
          renderSlot(_ctx.$slots, "append")
        ]),
        _: 3
      }, 8, ["class", "name"]);
    };
  }
});
var UploadList = /* @__PURE__ */ _export_sfc(_sfc_main, [["__file", "upload-list.vue"]]);

export { UploadList as default };
//# sourceMappingURL=upload-list2.mjs.map
