import { defineComponent, computed, getCurrentInstance, ref, watch, nextTick, provide, createVNode, renderSlot } from 'vue';
import { ElIcon } from '../../icon/index.mjs';
import { Plus } from '@element-plus/icons-vue';
import { tabsRootContextKey } from './constants.mjs';
import TabNav from './tab-nav.mjs';
import { buildProps, definePropType } from '../../../utils/vue/props/runtime.mjs';
import { UPDATE_MODEL_EVENT } from '../../../constants/event.mjs';
import { useNamespace } from '../../../hooks/use-namespace/index.mjs';
import { useOrderedChildren } from '../../../hooks/use-ordered-children/index.mjs';
import { EVENT_CODE } from '../../../constants/aria.mjs';
import { isString } from '@vue/shared';
import { isNumber, isUndefined } from '../../../utils/types.mjs';

const tabsProps = buildProps({
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
    type: definePropType(Function),
    default: () => true
  },
  stretch: Boolean
});
const isPaneName = (value) => isString(value) || isNumber(value);
const tabsEmits = {
  [UPDATE_MODEL_EVENT]: (name) => isPaneName(name),
  tabClick: (pane, ev) => ev instanceof Event,
  tabChange: (name) => isPaneName(name),
  edit: (paneName, action) => ["remove", "add"].includes(action),
  tabRemove: (name) => isPaneName(name),
  tabAdd: () => true
};
const Tabs = defineComponent({
  name: "ElTabs",
  props: tabsProps,
  emits: tabsEmits,
  setup(props, {
    emit,
    slots,
    expose
  }) {
    var _a;
    const ns = useNamespace("tabs");
    const isVertical = computed(() => ["left", "right"].includes(props.tabPosition));
    const {
      children: panes,
      addChild: sortPane,
      removeChild: unregisterPane
    } = useOrderedChildren(getCurrentInstance(), "ElTabPane");
    const nav$ = ref();
    const currentName = ref((_a = props.modelValue) != null ? _a : "0");
    const setCurrentName = async (value, trigger = false) => {
      var _a2, _b;
      if (currentName.value === value || isUndefined(value))
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
            emit(UPDATE_MODEL_EVENT, value);
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
      if (pane.props.disabled || isUndefined(pane.props.name))
        return;
      ev.stopPropagation();
      emit("edit", pane.props.name, "remove");
      emit("tabRemove", pane.props.name);
    };
    const handleTabAdd = () => {
      emit("edit", void 0, "add");
      emit("tabAdd");
    };
    watch(() => props.modelValue, (modelValue) => setCurrentName(modelValue));
    watch(currentName, async () => {
      var _a2;
      await nextTick();
      (_a2 = nav$.value) == null ? void 0 : _a2.scrollToActiveTab();
    });
    provide(tabsRootContextKey, {
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
      const newButton = props.editable || props.addable ? createVNode("div", {
        "class": [ns.e("new-tab"), isVertical.value && ns.e("new-tab-vertical")],
        "tabindex": "0",
        "onClick": handleTabAdd,
        "onKeydown": (ev) => {
          if ([EVENT_CODE.enter, EVENT_CODE.numpadEnter].includes(ev.code))
            handleTabAdd();
        }
      }, [addSlot ? renderSlot(slots, "add-icon") : createVNode(ElIcon, {
        "class": ns.is("icon-plus")
      }, {
        default: () => [createVNode(Plus, null, null)]
      })]) : null;
      const header = createVNode("div", {
        "class": [ns.e("header"), isVertical.value && ns.e("header-vertical"), ns.is(props.tabPosition)]
      }, [createVNode(TabNavRenderer, {
        "render": () => {
          const hasLabelSlot = panes.value.some((pane) => pane.slots.label);
          return createVNode(TabNav, {
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
      const panels = createVNode("div", {
        "class": ns.e("content")
      }, [renderSlot(slots, "default")]);
      return createVNode("div", {
        "class": [ns.b(), ns.m(props.tabPosition), {
          [ns.m("card")]: props.type === "card",
          [ns.m("border-card")]: props.type === "border-card"
        }]
      }, [panels, header]);
    };
  }
});
var Tabs$1 = Tabs;

export { Tabs$1 as default, tabsEmits, tabsProps };
//# sourceMappingURL=tabs.mjs.map
