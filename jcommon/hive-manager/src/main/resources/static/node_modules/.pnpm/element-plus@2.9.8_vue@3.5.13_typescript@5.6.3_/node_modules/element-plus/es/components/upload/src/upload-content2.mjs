import { defineComponent, shallowRef, openBlock, createElementBlock, normalizeClass, unref, withKeys, withModifiers, createBlock, withCtx, renderSlot, createElementVNode } from 'vue';
import { cloneDeep, isEqual } from 'lodash-unified';
import UploadDragger from './upload-dragger.mjs';
import { uploadContentProps } from './upload-content.mjs';
import { genFileId } from './upload.mjs';
import _export_sfc from '../../../_virtual/plugin-vue_export-helper.mjs';
import { entriesOf } from '../../../utils/objects.mjs';
import { useNamespace } from '../../../hooks/use-namespace/index.mjs';
import { useFormDisabled } from '../../form/src/hooks/use-form-common-props.mjs';
import { isPlainObject, isFunction } from '@vue/shared';

const __default__ = defineComponent({
  name: "ElUploadContent",
  inheritAttrs: false
});
const _sfc_main = /* @__PURE__ */ defineComponent({
  ...__default__,
  props: uploadContentProps,
  setup(__props, { expose }) {
    const props = __props;
    const ns = useNamespace("upload");
    const disabled = useFormDisabled();
    const requests = shallowRef({});
    const inputRef = shallowRef();
    const uploadFiles = (files) => {
      if (files.length === 0)
        return;
      const { autoUpload, limit, fileList, multiple, onStart, onExceed } = props;
      if (limit && fileList.length + files.length > limit) {
        onExceed(files, fileList);
        return;
      }
      if (!multiple) {
        files = files.slice(0, 1);
      }
      for (const file of files) {
        const rawFile = file;
        rawFile.uid = genFileId();
        onStart(rawFile);
        if (autoUpload)
          upload(rawFile);
      }
    };
    const upload = async (rawFile) => {
      inputRef.value.value = "";
      if (!props.beforeUpload) {
        return doUpload(rawFile);
      }
      let hookResult;
      let beforeData = {};
      try {
        const originData = props.data;
        const beforeUploadPromise = props.beforeUpload(rawFile);
        beforeData = isPlainObject(props.data) ? cloneDeep(props.data) : props.data;
        hookResult = await beforeUploadPromise;
        if (isPlainObject(props.data) && isEqual(originData, beforeData)) {
          beforeData = cloneDeep(props.data);
        }
      } catch (e) {
        hookResult = false;
      }
      if (hookResult === false) {
        props.onRemove(rawFile);
        return;
      }
      let file = rawFile;
      if (hookResult instanceof Blob) {
        if (hookResult instanceof File) {
          file = hookResult;
        } else {
          file = new File([hookResult], rawFile.name, {
            type: rawFile.type
          });
        }
      }
      doUpload(Object.assign(file, {
        uid: rawFile.uid
      }), beforeData);
    };
    const resolveData = async (data, rawFile) => {
      if (isFunction(data)) {
        return data(rawFile);
      }
      return data;
    };
    const doUpload = async (rawFile, beforeData) => {
      const {
        headers,
        data,
        method,
        withCredentials,
        name: filename,
        action,
        onProgress,
        onSuccess,
        onError,
        httpRequest
      } = props;
      try {
        beforeData = await resolveData(beforeData != null ? beforeData : data, rawFile);
      } catch (e) {
        props.onRemove(rawFile);
        return;
      }
      const { uid } = rawFile;
      const options = {
        headers: headers || {},
        withCredentials,
        file: rawFile,
        data: beforeData,
        method,
        filename,
        action,
        onProgress: (evt) => {
          onProgress(evt, rawFile);
        },
        onSuccess: (res) => {
          onSuccess(res, rawFile);
          delete requests.value[uid];
        },
        onError: (err) => {
          onError(err, rawFile);
          delete requests.value[uid];
        }
      };
      const request = httpRequest(options);
      requests.value[uid] = request;
      if (request instanceof Promise) {
        request.then(options.onSuccess, options.onError);
      }
    };
    const handleChange = (e) => {
      const files = e.target.files;
      if (!files)
        return;
      uploadFiles(Array.from(files));
    };
    const handleClick = () => {
      if (!disabled.value) {
        inputRef.value.value = "";
        inputRef.value.click();
      }
    };
    const handleKeydown = () => {
      handleClick();
    };
    const abort = (file) => {
      const _reqs = entriesOf(requests.value).filter(file ? ([uid]) => String(file.uid) === uid : () => true);
      _reqs.forEach(([uid, req]) => {
        if (req instanceof XMLHttpRequest)
          req.abort();
        delete requests.value[uid];
      });
    };
    expose({
      abort,
      upload
    });
    return (_ctx, _cache) => {
      return openBlock(), createElementBlock("div", {
        class: normalizeClass([
          unref(ns).b(),
          unref(ns).m(_ctx.listType),
          unref(ns).is("drag", _ctx.drag),
          unref(ns).is("disabled", unref(disabled))
        ]),
        tabindex: unref(disabled) ? "-1" : "0",
        onClick: handleClick,
        onKeydown: withKeys(withModifiers(handleKeydown, ["self"]), ["enter", "space"])
      }, [
        _ctx.drag ? (openBlock(), createBlock(UploadDragger, {
          key: 0,
          disabled: unref(disabled),
          onFile: uploadFiles
        }, {
          default: withCtx(() => [
            renderSlot(_ctx.$slots, "default")
          ]),
          _: 3
        }, 8, ["disabled"])) : renderSlot(_ctx.$slots, "default", { key: 1 }),
        createElementVNode("input", {
          ref_key: "inputRef",
          ref: inputRef,
          class: normalizeClass(unref(ns).e("input")),
          name: _ctx.name,
          disabled: unref(disabled),
          multiple: _ctx.multiple,
          accept: _ctx.accept,
          type: "file",
          onChange: handleChange,
          onClick: withModifiers(() => {
          }, ["stop"])
        }, null, 42, ["name", "disabled", "multiple", "accept", "onClick"])
      ], 42, ["tabindex", "onKeydown"]);
    };
  }
});
var UploadContent = /* @__PURE__ */ _export_sfc(_sfc_main, [["__file", "upload-content.vue"]]);

export { UploadContent as default };
//# sourceMappingURL=upload-content2.mjs.map
