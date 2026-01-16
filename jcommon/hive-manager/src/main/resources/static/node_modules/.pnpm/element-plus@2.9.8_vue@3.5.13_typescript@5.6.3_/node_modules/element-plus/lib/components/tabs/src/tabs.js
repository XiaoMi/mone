'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var vue = require('vue');
var index$2 = require('../../icon/index.js');
var iconsVue = require('@element-plus/icons-vue');
var constants = require('./constants.js');
var tabNav = require('./tab-nav.js');
var runtime = require('../../../utils/vue/props/runtime.js');
var event = require('../../../constants/event.js');
var index = require('../../../hooks/use-namespace/index.js');
var index$1 = require('../../../hooks/use-ordered-children/index.js');
var aria = require('../../../constants/aria.js');
var shared = require('@vue/shared');
var types = require('../../../utils/types.js');

const tabsProps = runtime.buildProps({
  type: {
    type: String,
    values: ["card", "border-card", ""],
    default: ""
  },
  closable: Boolean,
  addable: Boolean,
  modelValue: {
    type: [String, Number]
  },
  editable: Boolean,
  tabPosition: {
    type: String,
    values: ["top", "right", "bottom", "left"],
    default: "top"
  },
  beforeLeave: {
    type: runtime.definePropType(Function),
    default: () => true
  },
  stretch: Boolean
});
const isPaneName = (value) => shared.isString(value) || types.isNumber(value);
const tabsEmits = {
  [event.UPDATE_MODEL_EVENT]: (name) => isPaneName(name),
  tabClick: (pane, ev) => ev instanceof Event,
  tabChange: (name) => isPaneName(name),
  edit: (paneName, action) => ["remove", "add"].includes(action),
  tabRemove: (name) => isPaneName(name),
  tabAdd: () => true
};
const Tabs = vue.defineComponent({
  name: "ElTabs",
  props: tabsProps,
  emits: tabsEmits,
  setup(props, {
    emit,
    slots,
    expose
  }) {
    var _a;
    const ns = index.useNamespace("tabs");
    const isVertical = vue.computed(() => ["left", "right"].includes(props.tabPosition));
    const {
      children: panes,
      addChild: sortPane,
      removeChild: unregisterPane
    } = index$1.useOrderedChildren(vue.getCurrentInstance(), "ElTabPane");
    const nav$ = vue.ref();
    const currentName = vue.ref((_a = props.modelValue) != null ? _a : "0");
    const setCurrentName = async (value, trigger = false) => {
      var _a2, _b;
      if (currentName.value === value || types.isUndefined(value))
        return;
      try {
        let canLeave;
        if (props.beforeLeave) {
          const result = props.beforeLeave(value, currentName.value);
          canLeave = result instanceof Promise ? await result : result;
        } else {
          canLeave = true;
        }
        if (canLeave !== false) {
          currentName.value = value;
          if (trigger) {
            emit(event.UPDATE_MODEL_EVENT, value);
            emit("tabChange", value);
          }
          (_b = (_a2 = nav$.value) == null ? void 0 : _a2.removeFocus) == null ? void 0 : _b.call(_a2);
        }
      } catch (e) {
      }
    };
    const handleTabClick = (tab, tabName, event) => {
      if (tab.props.disabled)
        return;
      emit("tabClick", tab, event);
      setCurrentName(tabName, true);
    };
    const handleTabRemove = (pane, ev) => {
      if (pane.props.disabled || types.isUndefined(pane.props.name))
        return;
      ev.stopPropagation();
      emit("edit", pane.props.name, "remove");
      emit("tabRemove", pane.props.name);
    };
    const handleTabAdd = () => {
      emit("edit", void 0, "add");
      emit("tabAdd");
    };
    vue.watch(() => props.modelValue, (modelValue) => setCurrentName(modelValue));
    vue.watch(currentName, async () => {
      var _a2;
      await vue.nextTick();
      (_a2 = nav$.value) == null ? void 0 : _a2.scrollToActiveTab();
    });
    vue.provide(constants.tabsRootContextKey, {
      props,
      currentName,
      registerPane: (pane) => {
        panes.value.push(pane);
      },
      sortPane,
      unregisterPane
    });
    expose({
      currentName
    });
    const TabNavRenderer = ({
      render
    }) => {
      return render();
    };
    return () => {
      const addSlot = slots["add-icon"];
      const newButton = props.editable || props.addable ? vue.createVNode("div", {
        "class": [ns.e("new-tab"), isVertical.value && ns.e("new-tab-vertical")],
        "tabindex": "0",
        "onClick": handleTabAdd,
        "onKeydown": (ev) => {
          if ([aria.EVENT_CODE.enter, aria.EVENT_CODE.numpadEnter].includes(ev.code))
            handleTabAdd();
        }
      }, [addSlot ? vue.renderSlot(slots, "add-icon") : vue.createVNode(index$2.ElIcon, {
        "class": ns.is("icon-plus")
      }, {
        default: () => [vue.createVNode(iconsVue.Plus, null, null)]
      })]) : null;
      const header = vue.createVNode("div", {
        "class": [ns.e("header"), isVertical.value && ns.e("header-vertical"), ns.is(props.tabPosition)]
      }, [vue.createVNode(TabNavRenderer, {
        "render": () => {
          const hasLabelSlot = panes.value.some((pane) => pane.slots.label);
          return vue.createVNode(tabNav["default"], {
            ref: nav$,
            currentName: currentName.value,
            editable: props.editable,
            type: props.type,
            panes: panes.value,
            stretch: props.stretch,
            onTabClick: handleTabClick,
            onTabRemove: handleTabRemove
          }, {
            $stable: !hasLabelSlot
          });
        }
      }, null), newButton]);
      const panels = vue.createVNode("div", {
        "class": ns.e("content")
      }, [vue.renderSlot(slots, "default")]);
      return vue.createVNode("div", {
        "class": [ns.b(), ns.m(props.tabPosition), {
          [ns.m("card")]: props.type === "card",
          [ns.m("border-card")]: props.type === "border-card"
        }]
      }, [panels, header]);
    };
  }
});
var Tabs$1 = Tabs;

exports["default"] = Tabs$1;
exports.tabsEmits = tabsEmits;
exports.tabsProps = tabsProps;
//# sourceMappingURL=tabs.js.map
