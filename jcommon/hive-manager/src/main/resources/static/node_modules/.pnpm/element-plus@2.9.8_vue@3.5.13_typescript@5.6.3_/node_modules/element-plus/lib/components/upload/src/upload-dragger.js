'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var vue = require('vue');
var error = require('../../../utils/error.js');
var constants = require('./constants.js');
var uploadDragger = require('./upload-dragger2.js');
var pluginVue_exportHelper = require('../../../_virtual/plugin-vue_export-helper.js');
var index = require('../../../hooks/use-namespace/index.js');
var useFormCommonProps = require('../../form/src/hooks/use-form-common-props.js');

const COMPONENT_NAME = "ElUploadDrag";
const __default__ = vue.defineComponent({
  name: COMPONENT_NAME
});
const _sfc_main = /* @__PURE__ */ vue.defineComponent({
  ...__default__,
  props: uploadDragger.uploadDraggerProps,
  emits: uploadDragger.uploadDraggerEmits,
  setup(__props, { emit }) {
    const uploaderContext = vue.inject(constants.uploadContextKey);
    if (!uploaderContext) {
      error.throwError(COMPONENT_NAME, "usage: <el-upload><el-upload-dragger /></el-upload>");
    }
    const ns = index.useNamespace("upload");
    const dragover = vue.ref(false);
    const disabled = useFormCommonProps.useFormDisabled();
    const onDrop = (e) => {
      if (disabled.value)
        return;
      dragover.value = false;
      e.stopPropagation();
      const files = Array.from(e.dataTransfer.files);
      const items = e.dataTransfer.items || [];
      files.forEach((file, index) => {
        var _a;
        const item = items[index];
        const entry = (_a = item == null ? void 0 : item.webkitGetAsEntry) == null ? void 0 : _a.call(item);
        if (entry) {
          file.isDirectory = entry.isDirectory;
        }
      });
      emit("file", files);
    };
    const onDragover = () => {
      if (!disabled.value)
        dragover.value = true;
    };
    return (_ctx, _cache) => {
      return vue.openBlock(), vue.createElementBlock("div", {
        class: vue.normalizeClass([vue.unref(ns).b("dragger"), vue.unref(ns).is("dragover", dragover.value)]),
        onDrop: vue.withModifiers(onDrop, ["prevent"]),
        onDragover: vue.withModifiers(onDragover, ["prevent"]),
        onDragleave: vue.withModifiers(($event) => dragover.value = false, ["prevent"])
      }, [
        vue.renderSlot(_ctx.$slots, "default")
      ], 42, ["onDrop", "onDragover", "onDragleave"]);
    };
  }
});
var UploadDragger = /* @__PURE__ */ pluginVue_exportHelper["default"](_sfc_main, [["__file", "upload-dragger.vue"]]);

exports["default"] = UploadDragger;
//# sourceMappingURL=upload-dragger.js.map
