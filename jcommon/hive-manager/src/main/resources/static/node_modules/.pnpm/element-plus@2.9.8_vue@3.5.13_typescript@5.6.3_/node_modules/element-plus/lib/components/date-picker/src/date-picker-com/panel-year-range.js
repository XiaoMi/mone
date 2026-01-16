'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var vue = require('vue');
var dayjs = require('dayjs');
var iconsVue = require('@element-plus/icons-vue');
var index$2 = require('../../../icon/index.js');
var panelYearRange = require('../props/panel-year-range.js');
var useShortcut = require('../composables/use-shortcut.js');
var useYearRangeHeader = require('../composables/use-year-range-header.js');
var utils = require('../utils.js');
var constants = require('../constants.js');
var basicYearTable = require('./basic-year-table.js');
var pluginVue_exportHelper = require('../../../../_virtual/plugin-vue_export-helper.js');
var index = require('../../../../hooks/use-locale/index.js');
var index$1 = require('../../../../hooks/use-namespace/index.js');
var shared = require('@vue/shared');

function _interopDefaultLegacy (e) { return e && typeof e === 'object' && 'default' in e ? e : { 'default': e }; }

var dayjs__default = /*#__PURE__*/_interopDefaultLegacy(dayjs);

const unit = "year";
const __default__ = vue.defineComponent({
  name: "DatePickerYearRange"
});
const _sfc_main = /* @__PURE__ */ vue.defineComponent({
  ...__default__,
  props: panelYearRange.panelYearRangeProps,
  emits: panelYearRange.panelYearRangeEmits,
  setup(__props, { emit }) {
    const props = __props;
    const { lang } = index.useLocale();
    const leftDate = vue.ref(dayjs__default["default"]().locale(lang.value));
    const rightDate = vue.ref(leftDate.value.add(10, "year"));
    const { pickerNs: ppNs } = vue.inject(constants.ROOT_PICKER_INJECTION_KEY);
    const drpNs = index$1.useNamespace("date-range-picker");
    const isDefaultFormat = vue.inject("isDefaultFormat");
    const hasShortcuts = vue.computed(() => !!shortcuts.length);
    const panelKls = vue.computed(() => [
      ppNs.b(),
      drpNs.b(),
      {
        "has-sidebar": Boolean(vue.useSlots().sidebar) || hasShortcuts.value
      }
    ]);
    const leftPanelKls = vue.computed(() => {
      return {
        content: [ppNs.e("content"), drpNs.e("content"), "is-left"],
        arrowLeftBtn: [ppNs.e("icon-btn"), "d-arrow-left"],
        arrowRightBtn: [
          ppNs.e("icon-btn"),
          { [ppNs.is("disabled")]: !enableYearArrow.value },
          "d-arrow-right"
        ]
      };
    });
    const rightPanelKls = vue.computed(() => {
      return {
        content: [ppNs.e("content"), drpNs.e("content"), "is-right"],
        arrowLeftBtn: [
          ppNs.e("icon-btn"),
          { "is-disabled": !enableYearArrow.value },
          "d-arrow-left"
        ],
        arrowRightBtn: [ppNs.e("icon-btn"), "d-arrow-right"]
      };
    });
    const handleShortcutClick = useShortcut.useShortcut(lang);
    const {
      leftPrevYear,
      rightNextYear,
      leftNextYear,
      rightPrevYear,
      leftLabel,
      rightLabel,
      leftYear,
      rightYear
    } = useYearRangeHeader.useYearRangeHeader({
      unlinkPanels: vue.toRef(props, "unlinkPanels"),
      leftDate,
      rightDate
    });
    const enableYearArrow = vue.computed(() => {
      return props.unlinkPanels && rightYear.value > leftYear.value + 1;
    });
    const minDate = vue.ref();
    const maxDate = vue.ref();
    const rangeState = vue.ref({
      endDate: null,
      selecting: false
    });
    const handleChangeRange = (val) => {
      rangeState.value = val;
    };
    const handleRangePick = (val, close = true) => {
      const minDate_ = val.minDate;
      const maxDate_ = val.maxDate;
      if (maxDate.value === maxDate_ && minDate.value === minDate_) {
        return;
      }
      emit("calendar-change", [minDate_.toDate(), maxDate_ && maxDate_.toDate()]);
      maxDate.value = maxDate_;
      minDate.value = minDate_;
      if (!close)
        return;
      handleConfirm();
    };
    const handleConfirm = (visible = false) => {
      if (utils.isValidRange([minDate.value, maxDate.value])) {
        emit("pick", [minDate.value, maxDate.value], visible);
      }
    };
    const onSelect = (selecting) => {
      rangeState.value.selecting = selecting;
      if (!selecting) {
        rangeState.value.endDate = null;
      }
    };
    const pickerBase = vue.inject("EP_PICKER_BASE");
    const { shortcuts, disabledDate } = pickerBase.props;
    const format = vue.toRef(pickerBase.props, "format");
    const defaultValue = vue.toRef(pickerBase.props, "defaultValue");
    const getDefaultValue = () => {
      let start;
      if (shared.isArray(defaultValue.value)) {
        const left = dayjs__default["default"](defaultValue.value[0]);
        let right = dayjs__default["default"](defaultValue.value[1]);
        if (!props.unlinkPanels) {
          right = left.add(10, unit);
        }
        return [left, right];
      } else if (defaultValue.value) {
        start = dayjs__default["default"](defaultValue.value);
      } else {
        start = dayjs__default["default"]();
      }
      start = start.locale(lang.value);
      return [start, start.add(10, unit)];
    };
    vue.watch(() => defaultValue.value, (val) => {
      if (val) {
        const defaultArr = getDefaultValue();
        leftDate.value = defaultArr[0];
        rightDate.value = defaultArr[1];
      }
    }, { immediate: true });
    vue.watch(() => props.parsedValue, (newVal) => {
      if (newVal && newVal.length === 2) {
        minDate.value = newVal[0];
        maxDate.value = newVal[1];
        leftDate.value = minDate.value;
        if (props.unlinkPanels && maxDate.value) {
          const minDateYear = minDate.value.year();
          const maxDateYear = maxDate.value.year();
          rightDate.value = minDateYear === maxDateYear ? maxDate.value.add(10, "year") : maxDate.value;
        } else {
          rightDate.value = leftDate.value.add(10, "year");
        }
      } else {
        const defaultArr = getDefaultValue();
        minDate.value = void 0;
        maxDate.value = void 0;
        leftDate.value = defaultArr[0];
        rightDate.value = defaultArr[1];
      }
    }, { immediate: true });
    const parseUserInput = (value) => {
      return utils.correctlyParseUserInput(value, format.value, lang.value, isDefaultFormat);
    };
    const formatToString = (value) => {
      return shared.isArray(value) ? value.map((day) => day.format(format.value)) : value.format(format.value);
    };
    const isValidValue = (date) => {
      return utils.isValidRange(date) && (disabledDate ? !disabledDate(date[0].toDate()) && !disabledDate(date[1].toDate()) : true);
    };
    const handleClear = () => {
      const defaultArr = getDefaultValue();
      leftDate.value = defaultArr[0];
      rightDate.value = defaultArr[1];
      maxDate.value = void 0;
      minDate.value = void 0;
      emit("pick", null);
    };
    emit("set-picker-option", ["isValidValue", isValidValue]);
    emit("set-picker-option", ["parseUserInput", parseUserInput]);
    emit("set-picker-option", ["formatToString", formatToString]);
    emit("set-picker-option", ["handleClear", handleClear]);
    return (_ctx, _cache) => {
      return vue.openBlock(), vue.createElementBlock("div", {
        class: vue.normalizeClass(vue.unref(panelKls))
      }, [
        vue.createElementVNode("div", {
          class: vue.normalizeClass(vue.unref(ppNs).e("body-wrapper"))
        }, [
          vue.renderSlot(_ctx.$slots, "sidebar", {
            class: vue.normalizeClass(vue.unref(ppNs).e("sidebar"))
          }),
          vue.unref(hasShortcuts) ? (vue.openBlock(), vue.createElementBlock("div", {
            key: 0,
            class: vue.normalizeClass(vue.unref(ppNs).e("sidebar"))
          }, [
            (vue.openBlock(true), vue.createElementBlock(vue.Fragment, null, vue.renderList(vue.unref(shortcuts), (shortcut, key) => {
              return vue.openBlock(), vue.createElementBlock("button", {
                key,
                type: "button",
                class: vue.normalizeClass(vue.unref(ppNs).e("shortcut")),
                onClick: ($event) => vue.unref(handleShortcutClick)(shortcut)
              }, vue.toDisplayString(shortcut.text), 11, ["onClick"]);
            }), 128))
          ], 2)) : vue.createCommentVNode("v-if", true),
          vue.createElementVNode("div", {
            class: vue.normalizeClass(vue.unref(ppNs).e("body"))
          }, [
            vue.createElementVNode("div", {
              class: vue.normalizeClass(vue.unref(leftPanelKls).content)
            }, [
              vue.createElementVNode("div", {
                class: vue.normalizeClass(vue.unref(drpNs).e("header"))
              }, [
                vue.createElementVNode("button", {
                  type: "button",
                  class: vue.normalizeClass(vue.unref(leftPanelKls).arrowLeftBtn),
                  onClick: vue.unref(leftPrevYear)
                }, [
                  vue.renderSlot(_ctx.$slots, "prev-year", {}, () => [
                    vue.createVNode(vue.unref(index$2.ElIcon), null, {
                      default: vue.withCtx(() => [
                        vue.createVNode(vue.unref(iconsVue.DArrowLeft))
                      ]),
                      _: 1
                    })
                  ])
                ], 10, ["onClick"]),
                _ctx.unlinkPanels ? (vue.openBlock(), vue.createElementBlock("button", {
                  key: 0,
                  type: "button",
                  disabled: !vue.unref(enableYearArrow),
                  class: vue.normalizeClass(vue.unref(leftPanelKls).arrowRightBtn),
                  onClick: vue.unref(leftNextYear)
                }, [
                  vue.renderSlot(_ctx.$slots, "next-year", {}, () => [
                    vue.createVNode(vue.unref(index$2.ElIcon), null, {
                      default: vue.withCtx(() => [
                        vue.createVNode(vue.unref(iconsVue.DArrowRight))
                      ]),
                      _: 1
                    })
                  ])
                ], 10, ["disabled", "onClick"])) : vue.createCommentVNode("v-if", true),
                vue.createElementVNode("div", null, vue.toDisplayString(vue.unref(leftLabel)), 1)
              ], 2),
              vue.createVNode(basicYearTable["default"], {
                "selection-mode": "range",
                date: leftDate.value,
                "min-date": minDate.value,
                "max-date": maxDate.value,
                "range-state": rangeState.value,
                "disabled-date": vue.unref(disabledDate),
                onChangerange: handleChangeRange,
                onPick: handleRangePick,
                onSelect
              }, null, 8, ["date", "min-date", "max-date", "range-state", "disabled-date"])
            ], 2),
            vue.createElementVNode("div", {
              class: vue.normalizeClass(vue.unref(rightPanelKls).content)
            }, [
              vue.createElementVNode("div", {
                class: vue.normalizeClass(vue.unref(drpNs).e("header"))
              }, [
                _ctx.unlinkPanels ? (vue.openBlock(), vue.createElementBlock("button", {
                  key: 0,
                  type: "button",
                  disabled: !vue.unref(enableYearArrow),
                  class: vue.normalizeClass(vue.unref(rightPanelKls).arrowLeftBtn),
                  onClick: vue.unref(rightPrevYear)
                }, [
                  vue.renderSlot(_ctx.$slots, "prev-year", {}, () => [
                    vue.createVNode(vue.unref(index$2.ElIcon), null, {
                      default: vue.withCtx(() => [
                        vue.createVNode(vue.unref(iconsVue.DArrowLeft))
                      ]),
                      _: 1
                    })
                  ])
                ], 10, ["disabled", "onClick"])) : vue.createCommentVNode("v-if", true),
                vue.createElementVNode("button", {
                  type: "button",
                  class: vue.normalizeClass(vue.unref(rightPanelKls).arrowRightBtn),
                  onClick: vue.unref(rightNextYear)
                }, [
                  vue.renderSlot(_ctx.$slots, "next-year", {}, () => [
                    vue.createVNode(vue.unref(index$2.ElIcon), null, {
                      default: vue.withCtx(() => [
                        vue.createVNode(vue.unref(iconsVue.DArrowRight))
                      ]),
                      _: 1
                    })
                  ])
                ], 10, ["onClick"]),
                vue.createElementVNode("div", null, vue.toDisplayString(vue.unref(rightLabel)), 1)
              ], 2),
              vue.createVNode(basicYearTable["default"], {
                "selection-mode": "range",
                date: rightDate.value,
                "min-date": minDate.value,
                "max-date": maxDate.value,
                "range-state": rangeState.value,
                "disabled-date": vue.unref(disabledDate),
                onChangerange: handleChangeRange,
                onPick: handleRangePick,
                onSelect
              }, null, 8, ["date", "min-date", "max-date", "range-state", "disabled-date"])
            ], 2)
          ], 2)
        ], 2)
      ], 2);
    };
  }
});
var YearRangePickPanel = /* @__PURE__ */ pluginVue_exportHelper["default"](_sfc_main, [["__file", "panel-year-range.vue"]]);

exports["default"] = YearRangePickPanel;
//# sourceMappingURL=panel-year-range.js.map
