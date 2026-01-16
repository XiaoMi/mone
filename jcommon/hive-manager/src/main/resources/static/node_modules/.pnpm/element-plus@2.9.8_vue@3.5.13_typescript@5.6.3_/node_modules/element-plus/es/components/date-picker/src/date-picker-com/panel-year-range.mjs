import { defineComponent, ref, inject, computed, useSlots, toRef, watch, openBlock, createElementBlock, normalizeClass, unref, createElementVNode, renderSlot, Fragment, renderList, toDisplayString, createCommentVNode, createVNode, withCtx } from 'vue';
import dayjs from 'dayjs';
import { DArrowLeft, DArrowRight } from '@element-plus/icons-vue';
import { ElIcon } from '../../../icon/index.mjs';
import { panelYearRangeProps, panelYearRangeEmits } from '../props/panel-year-range.mjs';
import { useShortcut } from '../composables/use-shortcut.mjs';
import { useYearRangeHeader } from '../composables/use-year-range-header.mjs';
import { isValidRange, correctlyParseUserInput } from '../utils.mjs';
import { ROOT_PICKER_INJECTION_KEY } from '../constants.mjs';
import YearTable from './basic-year-table.mjs';
import _export_sfc from '../../../../_virtual/plugin-vue_export-helper.mjs';
import { useLocale } from '../../../../hooks/use-locale/index.mjs';
import { useNamespace } from '../../../../hooks/use-namespace/index.mjs';
import { isArray } from '@vue/shared';

const unit = "year";
const __default__ = defineComponent({
  name: "DatePickerYearRange"
});
const _sfc_main = /* @__PURE__ */ defineComponent({
  ...__default__,
  props: panelYearRangeProps,
  emits: panelYearRangeEmits,
  setup(__props, { emit }) {
    const props = __props;
    const { lang } = useLocale();
    const leftDate = ref(dayjs().locale(lang.value));
    const rightDate = ref(leftDate.value.add(10, "year"));
    const { pickerNs: ppNs } = inject(ROOT_PICKER_INJECTION_KEY);
    const drpNs = useNamespace("date-range-picker");
    const isDefaultFormat = inject("isDefaultFormat");
    const hasShortcuts = computed(() => !!shortcuts.length);
    const panelKls = computed(() => [
      ppNs.b(),
      drpNs.b(),
      {
        "has-sidebar": Boolean(useSlots().sidebar) || hasShortcuts.value
      }
    ]);
    const leftPanelKls = computed(() => {
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
    const rightPanelKls = computed(() => {
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
    const handleShortcutClick = useShortcut(lang);
    const {
      leftPrevYear,
      rightNextYear,
      leftNextYear,
      rightPrevYear,
      leftLabel,
      rightLabel,
      leftYear,
      rightYear
    } = useYearRangeHeader({
      unlinkPanels: toRef(props, "unlinkPanels"),
      leftDate,
      rightDate
    });
    const enableYearArrow = computed(() => {
      return props.unlinkPanels && rightYear.value > leftYear.value + 1;
    });
    const minDate = ref();
    const maxDate = ref();
    const rangeState = ref({
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
      if (isValidRange([minDate.value, maxDate.value])) {
        emit("pick", [minDate.value, maxDate.value], visible);
      }
    };
    const onSelect = (selecting) => {
      rangeState.value.selecting = selecting;
      if (!selecting) {
        rangeState.value.endDate = null;
      }
    };
    const pickerBase = inject("EP_PICKER_BASE");
    const { shortcuts, disabledDate } = pickerBase.props;
    const format = toRef(pickerBase.props, "format");
    const defaultValue = toRef(pickerBase.props, "defaultValue");
    const getDefaultValue = () => {
      let start;
      if (isArray(defaultValue.value)) {
        const left = dayjs(defaultValue.value[0]);
        let right = dayjs(defaultValue.value[1]);
        if (!props.unlinkPanels) {
          right = left.add(10, unit);
        }
        return [left, right];
      } else if (defaultValue.value) {
        start = dayjs(defaultValue.value);
      } else {
        start = dayjs();
      }
      start = start.locale(lang.value);
      return [start, start.add(10, unit)];
    };
    watch(() => defaultValue.value, (val) => {
      if (val) {
        const defaultArr = getDefaultValue();
        leftDate.value = defaultArr[0];
        rightDate.value = defaultArr[1];
      }
    }, { immediate: true });
    watch(() => props.parsedValue, (newVal) => {
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
      return correctlyParseUserInput(value, format.value, lang.value, isDefaultFormat);
    };
    const formatToString = (value) => {
      return isArray(value) ? value.map((day) => day.format(format.value)) : value.format(format.value);
    };
    const isValidValue = (date) => {
      return isValidRange(date) && (disabledDate ? !disabledDate(date[0].toDate()) && !disabledDate(date[1].toDate()) : true);
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
      return openBlock(), createElementBlock("div", {
        class: normalizeClass(unref(panelKls))
      }, [
        createElementVNode("div", {
          class: normalizeClass(unref(ppNs).e("body-wrapper"))
        }, [
          renderSlot(_ctx.$slots, "sidebar", {
            class: normalizeClass(unref(ppNs).e("sidebar"))
          }),
          unref(hasShortcuts) ? (openBlock(), createElementBlock("div", {
            key: 0,
            class: normalizeClass(unref(ppNs).e("sidebar"))
          }, [
            (openBlock(true), createElementBlock(Fragment, null, renderList(unref(shortcuts), (shortcut, key) => {
              return openBlock(), createElementBlock("button", {
                key,
                type: "button",
                class: normalizeClass(unref(ppNs).e("shortcut")),
                onClick: ($event) => unref(handleShortcutClick)(shortcut)
              }, toDisplayString(shortcut.text), 11, ["onClick"]);
            }), 128))
          ], 2)) : createCommentVNode("v-if", true),
          createElementVNode("div", {
            class: normalizeClass(unref(ppNs).e("body"))
          }, [
            createElementVNode("div", {
              class: normalizeClass(unref(leftPanelKls).content)
            }, [
              createElementVNode("div", {
                class: normalizeClass(unref(drpNs).e("header"))
              }, [
                createElementVNode("button", {
                  type: "button",
                  class: normalizeClass(unref(leftPanelKls).arrowLeftBtn),
                  onClick: unref(leftPrevYear)
                }, [
                  renderSlot(_ctx.$slots, "prev-year", {}, () => [
                    createVNode(unref(ElIcon), null, {
                      default: withCtx(() => [
                        createVNode(unref(DArrowLeft))
                      ]),
                      _: 1
                    })
                  ])
                ], 10, ["onClick"]),
                _ctx.unlinkPanels ? (openBlock(), createElementBlock("button", {
                  key: 0,
                  type: "button",
                  disabled: !unref(enableYearArrow),
                  class: normalizeClass(unref(leftPanelKls).arrowRightBtn),
                  onClick: unref(leftNextYear)
                }, [
                  renderSlot(_ctx.$slots, "next-year", {}, () => [
                    createVNode(unref(ElIcon), null, {
                      default: withCtx(() => [
                        createVNode(unref(DArrowRight))
                      ]),
                      _: 1
                    })
                  ])
                ], 10, ["disabled", "onClick"])) : createCommentVNode("v-if", true),
                createElementVNode("div", null, toDisplayString(unref(leftLabel)), 1)
              ], 2),
              createVNode(YearTable, {
                "selection-mode": "range",
                date: leftDate.value,
                "min-date": minDate.value,
                "max-date": maxDate.value,
                "range-state": rangeState.value,
                "disabled-date": unref(disabledDate),
                onChangerange: handleChangeRange,
                onPick: handleRangePick,
                onSelect
              }, null, 8, ["date", "min-date", "max-date", "range-state", "disabled-date"])
            ], 2),
            createElementVNode("div", {
              class: normalizeClass(unref(rightPanelKls).content)
            }, [
              createElementVNode("div", {
                class: normalizeClass(unref(drpNs).e("header"))
              }, [
                _ctx.unlinkPanels ? (openBlock(), createElementBlock("button", {
                  key: 0,
                  type: "button",
                  disabled: !unref(enableYearArrow),
                  class: normalizeClass(unref(rightPanelKls).arrowLeftBtn),
                  onClick: unref(rightPrevYear)
                }, [
                  renderSlot(_ctx.$slots, "prev-year", {}, () => [
                    createVNode(unref(ElIcon), null, {
                      default: withCtx(() => [
                        createVNode(unref(DArrowLeft))
                      ]),
                      _: 1
                    })
                  ])
                ], 10, ["disabled", "onClick"])) : createCommentVNode("v-if", true),
                createElementVNode("button", {
                  type: "button",
                  class: normalizeClass(unref(rightPanelKls).arrowRightBtn),
                  onClick: unref(rightNextYear)
                }, [
                  renderSlot(_ctx.$slots, "next-year", {}, () => [
                    createVNode(unref(ElIcon), null, {
                      default: withCtx(() => [
                        createVNode(unref(DArrowRight))
                      ]),
                      _: 1
                    })
                  ])
                ], 10, ["onClick"]),
                createElementVNode("div", null, toDisplayString(unref(rightLabel)), 1)
              ], 2),
              createVNode(YearTable, {
                "selection-mode": "range",
                date: rightDate.value,
                "min-date": minDate.value,
                "max-date": maxDate.value,
                "range-state": rangeState.value,
                "disabled-date": unref(disabledDate),
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
var YearRangePickPanel = /* @__PURE__ */ _export_sfc(_sfc_main, [["__file", "panel-year-range.vue"]]);

export { YearRangePickPanel as default };
//# sourceMappingURL=panel-year-range.mjs.map
