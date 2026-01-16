'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var vue = require('vue');
var index$2 = require('../../button/index.js');
var dateTable = require('./date-table2.js');
var useCalendar = require('./use-calendar.js');
var calendar = require('./calendar.js');
var pluginVue_exportHelper = require('../../../_virtual/plugin-vue_export-helper.js');
var index = require('../../../hooks/use-namespace/index.js');
var index$1 = require('../../../hooks/use-locale/index.js');

const COMPONENT_NAME = "ElCalendar";
const __default__ = vue.defineComponent({
  name: COMPONENT_NAME
});
const _sfc_main = /* @__PURE__ */ vue.defineComponent({
  ...__default__,
  props: calendar.calendarProps,
  emits: calendar.calendarEmits,
  setup(__props, { expose, emit }) {
    const props = __props;
    const ns = index.useNamespace("calendar");
    const {
      calculateValidatedDateRange,
      date,
      pickDay,
      realSelectedDay,
      selectDate,
      validatedRange
    } = useCalendar.useCalendar(props, emit, COMPONENT_NAME);
    const { t } = index$1.useLocale();
    const i18nDate = vue.computed(() => {
      const pickedMonth = `el.datepicker.month${date.value.format("M")}`;
      return `${date.value.year()} ${t("el.datepicker.year")} ${t(pickedMonth)}`;
    });
    expose({
      selectedDay: realSelectedDay,
      pickDay,
      selectDate,
      calculateValidatedDateRange
    });
    return (_ctx, _cache) => {
      return vue.openBlock(), vue.createElementBlock("div", {
        class: vue.normalizeClass(vue.unref(ns).b())
      }, [
        vue.createElementVNode("div", {
          class: vue.normalizeClass(vue.unref(ns).e("header"))
        }, [
          vue.renderSlot(_ctx.$slots, "header", { date: vue.unref(i18nDate) }, () => [
            vue.createElementVNode("div", {
              class: vue.normalizeClass(vue.unref(ns).e("title"))
            }, vue.toDisplayString(vue.unref(i18nDate)), 3),
            vue.unref(validatedRange).length === 0 ? (vue.openBlock(), vue.createElementBlock("div", {
              key: 0,
              class: vue.normalizeClass(vue.unref(ns).e("button-group"))
            }, [
              vue.createVNode(vue.unref(index$2.ElButtonGroup), null, {
                default: vue.withCtx(() => [
                  vue.createVNode(vue.unref(index$2.ElButton), {
                    size: "small",
                    onClick: ($event) => vue.unref(selectDate)("prev-month")
                  }, {
                    default: vue.withCtx(() => [
                      vue.createTextVNode(vue.toDisplayString(vue.unref(t)("el.datepicker.prevMonth")), 1)
                    ]),
                    _: 1
                  }, 8, ["onClick"]),
                  vue.createVNode(vue.unref(index$2.ElButton), {
                    size: "small",
                    onClick: ($event) => vue.unref(selectDate)("today")
                  }, {
                    default: vue.withCtx(() => [
                      vue.createTextVNode(vue.toDisplayString(vue.unref(t)("el.datepicker.today")), 1)
                    ]),
                    _: 1
                  }, 8, ["onClick"]),
                  vue.createVNode(vue.unref(index$2.ElButton), {
                    size: "small",
                    onClick: ($event) => vue.unref(selectDate)("next-month")
                  }, {
                    default: vue.withCtx(() => [
                      vue.createTextVNode(vue.toDisplayString(vue.unref(t)("el.datepicker.nextMonth")), 1)
                    ]),
                    _: 1
                  }, 8, ["onClick"])
                ]),
                _: 1
              })
            ], 2)) : vue.createCommentVNode("v-if", true)
          ])
        ], 2),
        vue.unref(validatedRange).length === 0 ? (vue.openBlock(), vue.createElementBlock("div", {
          key: 0,
          class: vue.normalizeClass(vue.unref(ns).e("body"))
        }, [
          vue.createVNode(dateTable["default"], {
            date: vue.unref(date),
            "selected-day": vue.unref(realSelectedDay),
            onPick: vue.unref(pickDay)
          }, vue.createSlots({
            _: 2
          }, [
            _ctx.$slots["date-cell"] ? {
              name: "date-cell",
              fn: vue.withCtx((data) => [
                vue.renderSlot(_ctx.$slots, "date-cell", vue.normalizeProps(vue.guardReactiveProps(data)))
              ])
            } : void 0
          ]), 1032, ["date", "selected-day", "onPick"])
        ], 2)) : (vue.openBlock(), vue.createElementBlock("div", {
          key: 1,
          class: vue.normalizeClass(vue.unref(ns).e("body"))
        }, [
          (vue.openBlock(true), vue.createElementBlock(vue.Fragment, null, vue.renderList(vue.unref(validatedRange), (range_, index) => {
            return vue.openBlock(), vue.createBlock(dateTable["default"], {
              key: index,
              date: range_[0],
              "selected-day": vue.unref(realSelectedDay),
              range: range_,
              "hide-header": index !== 0,
              onPick: vue.unref(pickDay)
            }, vue.createSlots({
              _: 2
            }, [
              _ctx.$slots["date-cell"] ? {
                name: "date-cell",
                fn: vue.withCtx((data) => [
                  vue.renderSlot(_ctx.$slots, "date-cell", vue.normalizeProps(vue.guardReactiveProps(data)))
                ])
              } : void 0
            ]), 1032, ["date", "selected-day", "range", "hide-header", "onPick"]);
          }), 128))
        ], 2))
      ], 2);
    };
  }
});
var Calendar = /* @__PURE__ */ pluginVue_exportHelper["default"](_sfc_main, [["__file", "calendar.vue"]]);

exports["default"] = Calendar;
//# sourceMappingURL=calendar2.js.map
