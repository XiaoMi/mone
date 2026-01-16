'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var vue = require('vue');
var dayjs = require('dayjs');
var customParseFormat = require('dayjs/plugin/customParseFormat.js');
var index$2 = require('../../select/index.js');
var index$3 = require('../../icon/index.js');
var timeSelect = require('./time-select.js');
var utils = require('./utils.js');
var pluginVue_exportHelper = require('../../../_virtual/plugin-vue_export-helper.js');
var event = require('../../../constants/event.js');
var index = require('../../../hooks/use-namespace/index.js');
var useFormCommonProps = require('../../form/src/hooks/use-form-common-props.js');
var index$1 = require('../../../hooks/use-locale/index.js');

function _interopDefaultLegacy (e) { return e && typeof e === 'object' && 'default' in e ? e : { 'default': e }; }

var dayjs__default = /*#__PURE__*/_interopDefaultLegacy(dayjs);
var customParseFormat__default = /*#__PURE__*/_interopDefaultLegacy(customParseFormat);

const __default__ = vue.defineComponent({
  name: "ElTimeSelect"
});
const _sfc_main = /* @__PURE__ */ vue.defineComponent({
  ...__default__,
  props: timeSelect.timeSelectProps,
  emits: [event.CHANGE_EVENT, "blur", "focus", "clear", event.UPDATE_MODEL_EVENT],
  setup(__props, { expose }) {
    const props = __props;
    dayjs__default["default"].extend(customParseFormat__default["default"]);
    const { Option: ElOption } = index$2.ElSelect;
    const nsInput = index.useNamespace("input");
    const select = vue.ref();
    const _disabled = useFormCommonProps.useFormDisabled();
    const { lang } = index$1.useLocale();
    const value = vue.computed(() => props.modelValue);
    const start = vue.computed(() => {
      const time = utils.parseTime(props.start);
      return time ? utils.formatTime(time) : null;
    });
    const end = vue.computed(() => {
      const time = utils.parseTime(props.end);
      return time ? utils.formatTime(time) : null;
    });
    const step = vue.computed(() => {
      const time = utils.parseTime(props.step);
      return time ? utils.formatTime(time) : null;
    });
    const minTime = vue.computed(() => {
      const time = utils.parseTime(props.minTime || "");
      return time ? utils.formatTime(time) : null;
    });
    const maxTime = vue.computed(() => {
      const time = utils.parseTime(props.maxTime || "");
      return time ? utils.formatTime(time) : null;
    });
    const items = vue.computed(() => {
      var _a;
      const result = [];
      const push = (formattedValue, rawValue) => {
        result.push({
          value: formattedValue,
          disabled: utils.compareTime(rawValue, minTime.value || "-1:-1") <= 0 || utils.compareTime(rawValue, maxTime.value || "100:100") >= 0
        });
      };
      if (props.start && props.end && props.step) {
        let current = start.value;
        let currentTime;
        while (current && end.value && utils.compareTime(current, end.value) <= 0) {
          currentTime = dayjs__default["default"](current, "HH:mm").locale(lang.value).format(props.format);
          push(currentTime, current);
          current = utils.nextTime(current, step.value);
        }
        if (props.includeEndTime && end.value && ((_a = result[result.length - 1]) == null ? void 0 : _a.value) !== end.value) {
          const formattedValue = dayjs__default["default"](end.value, "HH:mm").locale(lang.value).format(props.format);
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
      return vue.openBlock(), vue.createBlock(vue.unref(index$2.ElSelect), {
        ref_key: "select",
        ref: select,
        "model-value": vue.unref(value),
        disabled: vue.unref(_disabled),
        clearable: _ctx.clearable,
        "clear-icon": _ctx.clearIcon,
        size: _ctx.size,
        effect: _ctx.effect,
        placeholder: _ctx.placeholder,
        "default-first-option": "",
        filterable: _ctx.editable,
        "empty-values": _ctx.emptyValues,
        "value-on-clear": _ctx.valueOnClear,
        "onUpdate:modelValue": (event$1) => _ctx.$emit(vue.unref(event.UPDATE_MODEL_EVENT), event$1),
        onChange: (event$1) => _ctx.$emit(vue.unref(event.CHANGE_EVENT), event$1),
        onBlur: (event) => _ctx.$emit("blur", event),
        onFocus: (event) => _ctx.$emit("focus", event),
        onClear: () => _ctx.$emit("clear")
      }, {
        prefix: vue.withCtx(() => [
          _ctx.prefixIcon ? (vue.openBlock(), vue.createBlock(vue.unref(index$3.ElIcon), {
            key: 0,
            class: vue.normalizeClass(vue.unref(nsInput).e("prefix-icon"))
          }, {
            default: vue.withCtx(() => [
              (vue.openBlock(), vue.createBlock(vue.resolveDynamicComponent(_ctx.prefixIcon)))
            ]),
            _: 1
          }, 8, ["class"])) : vue.createCommentVNode("v-if", true)
        ]),
        default: vue.withCtx(() => [
          (vue.openBlock(true), vue.createElementBlock(vue.Fragment, null, vue.renderList(vue.unref(items), (item) => {
            return vue.openBlock(), vue.createBlock(vue.unref(ElOption), {
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
var TimeSelect = /* @__PURE__ */ pluginVue_exportHelper["default"](_sfc_main, [["__file", "time-select.vue"]]);

exports["default"] = TimeSelect;
//# sourceMappingURL=time-select2.js.map
