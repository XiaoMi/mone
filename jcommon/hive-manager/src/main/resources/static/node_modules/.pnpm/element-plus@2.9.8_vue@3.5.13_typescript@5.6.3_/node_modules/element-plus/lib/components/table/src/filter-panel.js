'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var vue = require('vue');
var index = require('../../checkbox/index.js');
var index$3 = require('../../icon/index.js');
var iconsVue = require('@element-plus/icons-vue');
var index$2 = require('../../tooltip/index.js');
var index$1 = require('../../scrollbar/index.js');
var pluginVue_exportHelper = require('../../../_virtual/plugin-vue_export-helper.js');
var index$4 = require('../../../directives/click-outside/index.js');
var index$5 = require('../../../hooks/use-locale/index.js');
var index$6 = require('../../../hooks/use-namespace/index.js');
var types = require('../../../utils/types.js');

const { CheckboxGroup: ElCheckboxGroup } = index.ElCheckbox;
const _sfc_main = vue.defineComponent({
  name: "ElTableFilterPanel",
  components: {
    ElCheckbox: index.ElCheckbox,
    ElCheckboxGroup,
    ElScrollbar: index$1.ElScrollbar,
    ElTooltip: index$2.ElTooltip,
    ElIcon: index$3.ElIcon,
    ArrowDown: iconsVue.ArrowDown,
    ArrowUp: iconsVue.ArrowUp
  },
  directives: { ClickOutside: index$4["default"] },
  props: {
    placement: {
      type: String,
      default: "bottom-start"
    },
    store: {
      type: Object
    },
    column: {
      type: Object
    },
    upDataColumn: {
      type: Function
    },
    appendTo: {
      type: String
    }
  },
  setup(props) {
    const instance = vue.getCurrentInstance();
    const { t } = index$5.useLocale();
    const ns = index$6.useNamespace("table-filter");
    const parent = instance == null ? void 0 : instance.parent;
    if (!parent.filterPanels.value[props.column.id]) {
      parent.filterPanels.value[props.column.id] = instance;
    }
    const tooltipVisible = vue.ref(false);
    const tooltip = vue.ref(null);
    const filters = vue.computed(() => {
      return props.column && props.column.filters;
    });
    const filterClassName = vue.computed(() => {
      if (props.column.filterClassName) {
        return `${ns.b()} ${props.column.filterClassName}`;
      }
      return ns.b();
    });
    const filterValue = vue.computed({
      get: () => {
        var _a;
        return (((_a = props.column) == null ? void 0 : _a.filteredValue) || [])[0];
      },
      set: (value) => {
        if (filteredValue.value) {
          if (!types.isPropAbsent(value)) {
            filteredValue.value.splice(0, 1, value);
          } else {
            filteredValue.value.splice(0, 1);
          }
        }
      }
    });
    const filteredValue = vue.computed({
      get() {
        if (props.column) {
          return props.column.filteredValue || [];
        }
        return [];
      },
      set(value) {
        if (props.column) {
          props.upDataColumn("filteredValue", value);
        }
      }
    });
    const multiple = vue.computed(() => {
      if (props.column) {
        return props.column.filterMultiple;
      }
      return true;
    });
    const isActive = (filter) => {
      return filter.value === filterValue.value;
    };
    const hidden = () => {
      tooltipVisible.value = false;
    };
    const showFilterPanel = (e) => {
      e.stopPropagation();
      tooltipVisible.value = !tooltipVisible.value;
    };
    const hideFilterPanel = () => {
      tooltipVisible.value = false;
    };
    const handleConfirm = () => {
      confirmFilter(filteredValue.value);
      hidden();
    };
    const handleReset = () => {
      filteredValue.value = [];
      confirmFilter(filteredValue.value);
      hidden();
    };
    const handleSelect = (_filterValue) => {
      filterValue.value = _filterValue;
      if (!types.isPropAbsent(_filterValue)) {
        confirmFilter(filteredValue.value);
      } else {
        confirmFilter([]);
      }
      hidden();
    };
    const confirmFilter = (filteredValue2) => {
      props.store.commit("filterChange", {
        column: props.column,
        values: filteredValue2
      });
      props.store.updateAllSelected();
    };
    vue.watch(tooltipVisible, (value) => {
      if (props.column) {
        props.upDataColumn("filterOpened", value);
      }
    }, {
      immediate: true
    });
    const popperPaneRef = vue.computed(() => {
      var _a, _b;
      return (_b = (_a = tooltip.value) == null ? void 0 : _a.popperRef) == null ? void 0 : _b.contentRef;
    });
    return {
      tooltipVisible,
      multiple,
      filterClassName,
      filteredValue,
      filterValue,
      filters,
      handleConfirm,
      handleReset,
      handleSelect,
      isPropAbsent: types.isPropAbsent,
      isActive,
      t,
      ns,
      showFilterPanel,
      hideFilterPanel,
      popperPaneRef,
      tooltip
    };
  }
});
function _sfc_render(_ctx, _cache, $props, $setup, $data, $options) {
  const _component_el_checkbox = vue.resolveComponent("el-checkbox");
  const _component_el_checkbox_group = vue.resolveComponent("el-checkbox-group");
  const _component_el_scrollbar = vue.resolveComponent("el-scrollbar");
  const _component_arrow_up = vue.resolveComponent("arrow-up");
  const _component_arrow_down = vue.resolveComponent("arrow-down");
  const _component_el_icon = vue.resolveComponent("el-icon");
  const _component_el_tooltip = vue.resolveComponent("el-tooltip");
  const _directive_click_outside = vue.resolveDirective("click-outside");
  return vue.openBlock(), vue.createBlock(_component_el_tooltip, {
    ref: "tooltip",
    visible: _ctx.tooltipVisible,
    offset: 0,
    placement: _ctx.placement,
    "show-arrow": false,
    "stop-popper-mouse-event": false,
    teleported: "",
    effect: "light",
    pure: "",
    "popper-class": _ctx.filterClassName,
    persistent: "",
    "append-to": _ctx.appendTo
  }, {
    content: vue.withCtx(() => [
      _ctx.multiple ? (vue.openBlock(), vue.createElementBlock("div", { key: 0 }, [
        vue.createElementVNode("div", {
          class: vue.normalizeClass(_ctx.ns.e("content"))
        }, [
          vue.createVNode(_component_el_scrollbar, {
            "wrap-class": _ctx.ns.e("wrap")
          }, {
            default: vue.withCtx(() => [
              vue.createVNode(_component_el_checkbox_group, {
                modelValue: _ctx.filteredValue,
                "onUpdate:modelValue": ($event) => _ctx.filteredValue = $event,
                class: vue.normalizeClass(_ctx.ns.e("checkbox-group"))
              }, {
                default: vue.withCtx(() => [
                  (vue.openBlock(true), vue.createElementBlock(vue.Fragment, null, vue.renderList(_ctx.filters, (filter) => {
                    return vue.openBlock(), vue.createBlock(_component_el_checkbox, {
                      key: filter.value,
                      value: filter.value
                    }, {
                      default: vue.withCtx(() => [
                        vue.createTextVNode(vue.toDisplayString(filter.text), 1)
                      ]),
                      _: 2
                    }, 1032, ["value"]);
                  }), 128))
                ]),
                _: 1
              }, 8, ["modelValue", "onUpdate:modelValue", "class"])
            ]),
            _: 1
          }, 8, ["wrap-class"])
        ], 2),
        vue.createElementVNode("div", {
          class: vue.normalizeClass(_ctx.ns.e("bottom"))
        }, [
          vue.createElementVNode("button", {
            class: vue.normalizeClass({ [_ctx.ns.is("disabled")]: _ctx.filteredValue.length === 0 }),
            disabled: _ctx.filteredValue.length === 0,
            type: "button",
            onClick: _ctx.handleConfirm
          }, vue.toDisplayString(_ctx.t("el.table.confirmFilter")), 11, ["disabled", "onClick"]),
          vue.createElementVNode("button", {
            type: "button",
            onClick: _ctx.handleReset
          }, vue.toDisplayString(_ctx.t("el.table.resetFilter")), 9, ["onClick"])
        ], 2)
      ])) : (vue.openBlock(), vue.createElementBlock("ul", {
        key: 1,
        class: vue.normalizeClass(_ctx.ns.e("list"))
      }, [
        vue.createElementVNode("li", {
          class: vue.normalizeClass([
            _ctx.ns.e("list-item"),
            {
              [_ctx.ns.is("active")]: _ctx.isPropAbsent(_ctx.filterValue)
            }
          ]),
          onClick: ($event) => _ctx.handleSelect(null)
        }, vue.toDisplayString(_ctx.t("el.table.clearFilter")), 11, ["onClick"]),
        (vue.openBlock(true), vue.createElementBlock(vue.Fragment, null, vue.renderList(_ctx.filters, (filter) => {
          return vue.openBlock(), vue.createElementBlock("li", {
            key: filter.value,
            class: vue.normalizeClass([_ctx.ns.e("list-item"), _ctx.ns.is("active", _ctx.isActive(filter))]),
            label: filter.value,
            onClick: ($event) => _ctx.handleSelect(filter.value)
          }, vue.toDisplayString(filter.text), 11, ["label", "onClick"]);
        }), 128))
      ], 2))
    ]),
    default: vue.withCtx(() => [
      vue.withDirectives((vue.openBlock(), vue.createElementBlock("span", {
        class: vue.normalizeClass([
          `${_ctx.ns.namespace.value}-table__column-filter-trigger`,
          `${_ctx.ns.namespace.value}-none-outline`
        ]),
        onClick: _ctx.showFilterPanel
      }, [
        vue.createVNode(_component_el_icon, null, {
          default: vue.withCtx(() => [
            vue.renderSlot(_ctx.$slots, "filter-icon", {}, () => [
              _ctx.column.filterOpened ? (vue.openBlock(), vue.createBlock(_component_arrow_up, { key: 0 })) : (vue.openBlock(), vue.createBlock(_component_arrow_down, { key: 1 }))
            ])
          ]),
          _: 3
        })
      ], 10, ["onClick"])), [
        [_directive_click_outside, _ctx.hideFilterPanel, _ctx.popperPaneRef]
      ])
    ]),
    _: 3
  }, 8, ["visible", "placement", "popper-class", "append-to"]);
}
var FilterPanel = /* @__PURE__ */ pluginVue_exportHelper["default"](_sfc_main, [["render", _sfc_render], ["__file", "filter-panel.vue"]]);

exports["default"] = FilterPanel;
//# sourceMappingURL=filter-panel.js.map
