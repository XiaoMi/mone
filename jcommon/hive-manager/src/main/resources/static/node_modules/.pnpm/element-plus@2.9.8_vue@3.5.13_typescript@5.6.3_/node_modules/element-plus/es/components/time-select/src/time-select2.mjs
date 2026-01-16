import { defineComponent, ref, computed, openBlock, createBlock, unref, withCtx, normalizeClass, resolveDynamicComponent, createCommentVNode, createElementBlock, Fragment, renderList } from 'vue';
import dayjs from 'dayjs';
import customParseFormat from 'dayjs/plugin/customParseFormat.js';
import { ElSelect } from '../../select/index.mjs';
import { ElIcon } from '../../icon/index.mjs';
import { timeSelectProps } from './time-select.mjs';
import { parseTime, formatTime, compareTime, nextTime } from './utils.mjs';
import _export_sfc from '../../../_virtual/plugin-vue_export-helper.mjs';
import { CHANGE_EVENT, UPDATE_MODEL_EVENT } from '../../../constants/event.mjs';
import { useNamespace } from '../../../hooks/use-namespace/index.mjs';
import { useFormDisabled } from '../../form/src/hooks/use-form-common-props.mjs';
import { useLocale } from '../../../hooks/use-locale/index.mjs';

const __default__ = defineComponent({
  name: "ElTimeSelect"
});
const _sfc_main = /* @__PURE__ */ defineComponent({
  ...__default__,
  props: timeSelectProps,
  emits: [CHANGE_EVENT, "blur", "focus", "clear", UPDATE_MODEL_EVENT],
  setup(__props, { expose }) {
    const props = __props;
    dayjs.extend(customParseFormat);
    const { Option: ElOption } = ElSelect;
    const nsInput = useNamespace("input");
    const select = ref();
    const _disabled = useFormDisabled();
    const { lang } = useLocale();
    const value = computed(() => props.modelValue);
    const start = computed(() => {
      const time = parseTime(props.start);
      return time ? formatTime(time) : null;
    });
    const end = computed(() => {
      const time = parseTime(props.end);
      return time ? formatTime(time) : null;
    });
    const step = computed(() => {
      const time = parseTime(props.step);
      return time ? formatTime(time) : null;
    });
    const minTime = computed(() => {
      const time = parseTime(props.minTime || "");
      return time ? formatTime(time) : null;
    });
    const maxTime = computed(() => {
      const time = parseTime(props.maxTime || "");
      return time ? formatTime(time) : null;
    });
    const items = computed(() => {
      var _a;
      const result = [];
      const push = (formattedValue, rawValue) => {
        result.push({
          value: formattedValue,
          disabled: compareTime(rawValue, minTime.value || "-1:-1") <= 0 || compareTime(rawValue, maxTime.value || "100:100") >= 0
        });
      };
      if (props.start && props.end && props.step) {
        let current = start.value;
        let currentTime;
        while (current && end.value && compareTime(current, end.value) <= 0) {
          currentTime = dayjs(current, "HH:mm").locale(lang.value).format(props.format);
          push(currentTime, current);
          current = nextTime(current, step.value);
        }
        if (props.includeEndTime && end.value && ((_a = result[result.length - 1]) == null ? void 0 : _a.value) !== end.value) {
          const formattedValue = dayjs(end.value, "HH:mm").locale(lang.value).format(props.format);
          push(formattedValue, end.value);
        }
      }
      return result;
    });
    const blur = () => {
      var _a, _b;
      (_b = (_a = select.value) == null ? void 0 : _a.blur) == null ? void 0 : _b.call(_a);
    };
    const focus = () => {
      var _a, _b;
      (_b = (_a = select.value) == null ? void 0 : _a.focus) == null ? void 0 : _b.call(_a);
    };
    expose({
      blur,
      focus
    });
    return (_ctx, _cache) => {
      return openBlock(), createBlock(unref(ElSelect), {
        ref_key: "select",
        ref: select,
        "model-value": unref(value),
        disabled: unref(_disabled),
        clearable: _ctx.clearable,
        "clear-icon": _ctx.clearIcon,
        size: _ctx.size,
        effect: _ctx.effect,
        placeholder: _ctx.placeholder,
        "default-first-option": "",
        filterable: _ctx.editable,
        "empty-values": _ctx.emptyValues,
        "value-on-clear": _ctx.valueOnClear,
        "onUpdate:modelValue": (event) => _ctx.$emit(unref(UPDATE_MODEL_EVENT), event),
        onChange: (event) => _ctx.$emit(unref(CHANGE_EVENT), event),
        onBlur: (event) => _ctx.$emit("blur", event),
        onFocus: (event) => _ctx.$emit("focus", event),
        onClear: () => _ctx.$emit("clear")
      }, {
        prefix: withCtx(() => [
          _ctx.prefixIcon ? (openBlock(), createBlock(unref(ElIcon), {
            key: 0,
            class: normalizeClass(unref(nsInput).e("prefix-icon"))
          }, {
            default: withCtx(() => [
              (openBlock(), createBlock(resolveDynamicComponent(_ctx.prefixIcon)))
            ]),
            _: 1
          }, 8, ["class"])) : createCommentVNode("v-if", true)
        ]),
        default: withCtx(() => [
          (openBlock(true), createElementBlock(Fragment, null, renderList(unref(items), (item) => {
            return openBlock(), createBlock(unref(ElOption), {
              key: item.value,
              label: item.value,
              value: item.value,
              disabled: item.disabled
            }, null, 8, ["label", "value", "disabled"]);
          }), 128))
        ]),
        _: 1
      }, 8, ["model-value", "disabled", "clearable", "clear-icon", "size", "effect", "placeholder", "filterable", "empty-values", "value-on-clear", "onUpdate:modelValue", "onChange", "onBlur", "onFocus", "onClear"]);
    };
  }
});
var TimeSelect = /* @__PURE__ */ _export_sfc(_sfc_main, [["__file", "time-select.vue"]]);

export { TimeSelect as default };
//# sourceMappingURL=time-select2.mjs.map
