import { defineComponent, inject, computed, openBlock, createElementBlock, normalizeClass, normalizeStyle, withModifiers, renderSlot, unref } from 'vue';
import { DROPDOWN_INJECTION_KEY } from './tokens.mjs';
import { dropdownMenuProps, DROPDOWN_COLLECTION_INJECTION_KEY as COLLECTION_INJECTION_KEY, FIRST_LAST_KEYS, LAST_KEYS } from './dropdown.mjs';
import { useDropdown } from './useDropdown.mjs';
import _export_sfc from '../../../_virtual/plugin-vue_export-helper.mjs';
import { ROVING_FOCUS_GROUP_INJECTION_KEY } from '../../roving-focus-group/src/tokens.mjs';
import { ROVING_FOCUS_COLLECTION_INJECTION_KEY as COLLECTION_INJECTION_KEY$1 } from '../../roving-focus-group/src/roving-focus-group.mjs';
import { focusFirst } from '../../roving-focus-group/src/utils.mjs';
import { useNamespace } from '../../../hooks/use-namespace/index.mjs';
import { FOCUS_TRAP_INJECTION_KEY } from '../../focus-trap/src/tokens.mjs';
import { composeRefs } from '../../../utils/vue/refs.mjs';
import { composeEventHandlers } from '../../../utils/dom/event.mjs';
import { EVENT_CODE } from '../../../constants/aria.mjs';

const _sfc_main = defineComponent({
  name: "ElDropdownMenu",
  props: dropdownMenuProps,
  setup(props) {
    const ns = useNamespace("dropdown");
    const { _elDropdownSize } = useDropdown();
    const size = _elDropdownSize.value;
    const { focusTrapRef, onKeydown } = inject(FOCUS_TRAP_INJECTION_KEY, void 0);
    const { contentRef, role, triggerId } = inject(DROPDOWN_INJECTION_KEY, void 0);
    const { collectionRef: dropdownCollectionRef, getItems } = inject(COLLECTION_INJECTION_KEY, void 0);
    const {
      rovingFocusGroupRef,
      rovingFocusGroupRootStyle,
      tabIndex,
      onBlur,
      onFocus,
      onMousedown
    } = inject(ROVING_FOCUS_GROUP_INJECTION_KEY, void 0);
    const { collectionRef: rovingFocusGroupCollectionRef } = inject(COLLECTION_INJECTION_KEY$1, void 0);
    const dropdownKls = computed(() => {
      return [ns.b("menu"), ns.bm("menu", size == null ? void 0 : size.value)];
    });
    const dropdownListWrapperRef = composeRefs(contentRef, dropdownCollectionRef, focusTrapRef, rovingFocusGroupRef, rovingFocusGroupCollectionRef);
    const composedKeydown = composeEventHandlers((e) => {
      var _a;
      (_a = props.onKeydown) == null ? void 0 : _a.call(props, e);
    }, (e) => {
      const { currentTarget, code, target } = e;
      currentTarget.contains(target);
      if (EVENT_CODE.tab === code) {
        e.stopImmediatePropagation();
      }
      e.preventDefault();
      if (target !== unref(contentRef) || !FIRST_LAST_KEYS.includes(code))
        return;
      const items = getItems().filter((item) => !item.disabled);
      const targets = items.map((item) => item.ref);
      if (LAST_KEYS.includes(code)) {
        targets.reverse();
      }
      focusFirst(targets);
    });
    const handleKeydown = (e) => {
      composedKeydown(e);
      onKeydown(e);
    };
    return {
      size,
      rovingFocusGroupRootStyle,
      tabIndex,
      dropdownKls,
      role,
      triggerId,
      dropdownListWrapperRef,
      handleKeydown,
      onBlur,
      onFocus,
      onMousedown
    };
  }
});
function _sfc_render(_ctx, _cache, $props, $setup, $data, $options) {
  return openBlock(), createElementBlock("ul", {
    ref: _ctx.dropdownListWrapperRef,
    class: normalizeClass(_ctx.dropdownKls),
    style: normalizeStyle(_ctx.rovingFocusGroupRootStyle),
    tabindex: -1,
    role: _ctx.role,
    "aria-labelledby": _ctx.triggerId,
    onBlur: _ctx.onBlur,
    onFocus: _ctx.onFocus,
    onKeydown: withModifiers(_ctx.handleKeydown, ["self"]),
    onMousedown: withModifiers(_ctx.onMousedown, ["self"])
  }, [
    renderSlot(_ctx.$slots, "default")
  ], 46, ["role", "aria-labelledby", "onBlur", "onFocus", "onKeydown", "onMousedown"]);
}
var DropdownMenu = /* @__PURE__ */ _export_sfc(_sfc_main, [["render", _sfc_render], ["__file", "dropdown-menu.vue"]]);

export { DropdownMenu as default };
//# sourceMappingURL=dropdown-menu.mjs.map
