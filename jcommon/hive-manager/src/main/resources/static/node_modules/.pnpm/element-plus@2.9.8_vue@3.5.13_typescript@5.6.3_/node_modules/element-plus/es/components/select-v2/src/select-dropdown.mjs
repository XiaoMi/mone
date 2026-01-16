import { defineComponent, inject, ref, computed, watch, unref, createVNode, mergeProps, toRaw } from 'vue';
import { get } from 'lodash-unified';
import GroupItem from './group-item.mjs';
import OptionItem from './option-item.mjs';
import { useProps } from './useProps.mjs';
import { selectV2InjectionKey } from './token.mjs';
import FixedSizeList from '../../virtual-list/src/components/fixed-size-list.mjs';
import DynamicSizeList from '../../virtual-list/src/components/dynamic-size-list.mjs';
import { useNamespace } from '../../../hooks/use-namespace/index.mjs';
import { isUndefined } from '../../../utils/types.mjs';
import { isIOS } from '@vueuse/core';
import { EVENT_CODE } from '../../../constants/aria.mjs';
import { isObject } from '@vue/shared';

const props = {
  loading: Boolean,
  data: {
    type: Array,
    required: true
  },
  hoveringIndex: Number,
  width: Number
};
var ElSelectMenu = defineComponent({
  name: "ElSelectDropdown",
  props,
  setup(props2, {
    slots,
    expose
  }) {
    const select = inject(selectV2InjectionKey);
    const ns = useNamespace("select");
    const {
      getLabel,
      getValue,
      getDisabled
    } = useProps(select.props);
    const cachedHeights = ref([]);
    const listRef = ref();
    const size = computed(() => props2.data.length);
    watch(() => size.value, () => {
      var _a, _b;
      (_b = (_a = select.tooltipRef.value).updatePopper) == null ? void 0 : _b.call(_a);
    });
    const isSized = computed(() => isUndefined(select.props.estimatedOptionHeight));
    const listProps = computed(() => {
      if (isSized.value) {
        return {
          itemSize: select.props.itemHeight
        };
      }
      return {
        estimatedSize: select.props.estimatedOptionHeight,
        itemSize: (idx) => cachedHeights.value[idx]
      };
    });
    const contains = (arr = [], target) => {
      const {
        props: {
          valueKey
        }
      } = select;
      if (!isObject(target)) {
        return arr.includes(target);
      }
      return arr && arr.some((item) => {
        return toRaw(get(item, valueKey)) === get(target, valueKey);
      });
    };
    const isEqual = (selected, target) => {
      if (!isObject(target)) {
        return selected === target;
      } else {
        const {
          valueKey
        } = select.props;
        return get(selected, valueKey) === get(target, valueKey);
      }
    };
    const isItemSelected = (modelValue, target) => {
      if (select.props.multiple) {
        return contains(modelValue, getValue(target));
      }
      return isEqual(modelValue, getValue(target));
    };
    const isItemDisabled = (modelValue, selected) => {
      const {
        disabled,
        multiple,
        multipleLimit
      } = select.props;
      return disabled || !selected && (multiple ? multipleLimit > 0 && modelValue.length >= multipleLimit : false);
    };
    const isItemHovering = (target) => props2.hoveringIndex === target;
    const scrollToItem = (index) => {
      const list = listRef.value;
      if (list) {
        list.scrollToItem(index);
      }
    };
    const resetScrollTop = () => {
      const list = listRef.value;
      if (list) {
        list.resetScrollTop();
      }
    };
    const exposed = {
      listRef,
      isSized,
      isItemDisabled,
      isItemHovering,
      isItemSelected,
      scrollToItem,
      resetScrollTop
    };
    expose(exposed);
    const Item = (itemProps) => {
      const {
        index,
        data,
        style
      } = itemProps;
      const sized = unref(isSized);
      const {
        itemSize,
        estimatedSize
      } = unref(listProps);
      const {
        modelValue
      } = select.props;
      const {
        onSelect,
        onHover
      } = select;
      const item = data[index];
      if (item.type === "Group") {
        return createVNode(GroupItem, {
          "item": item,
          "style": style,
          "height": sized ? itemSize : estimatedSize
        }, null);
      }
      const isSelected = isItemSelected(modelValue, item);
      const isDisabled = isItemDisabled(modelValue, isSelected);
      const isHovering = isItemHovering(index);
      return createVNode(OptionItem, mergeProps(itemProps, {
        "selected": isSelected,
        "disabled": getDisabled(item) || isDisabled,
        "created": !!item.created,
        "hovering": isHovering,
        "item": item,
        "onSelect": onSelect,
        "onHover": onHover
      }), {
        default: (props3) => {
          var _a;
          return ((_a = slots.default) == null ? void 0 : _a.call(slots, props3)) || createVNode("span", null, [getLabel(item)]);
        }
      });
    };
    const {
      onKeyboardNavigate,
      onKeyboardSelect
    } = select;
    const onForward = () => {
      onKeyboardNavigate("forward");
    };
    const onBackward = () => {
      onKeyboardNavigate("backward");
    };
    const onKeydown = (e) => {
      const {
        code
      } = e;
      const {
        tab,
        esc,
        down,
        up,
        enter,
        numpadEnter
      } = EVENT_CODE;
      if ([esc, down, up, enter, numpadEnter].includes(code)) {
        e.preventDefault();
        e.stopPropagation();
      }
      switch (code) {
        case tab:
        case esc:
          break;
        case down:
          onForward();
          break;
        case up:
          onBackward();
          break;
        case enter:
        case numpadEnter:
          onKeyboardSelect();
          break;
      }
    };
    return () => {
      var _a, _b, _c, _d;
      const {
        data,
        width
      } = props2;
      const {
        height,
        multiple,
        scrollbarAlwaysOn
      } = select.props;
      const isScrollbarAlwaysOn = computed(() => {
        return isIOS ? true : scrollbarAlwaysOn;
      });
      const List = unref(isSized) ? FixedSizeList : DynamicSizeList;
      return createVNode("div", {
        "class": [ns.b("dropdown"), ns.is("multiple", multiple)],
        "style": {
          width: `${width}px`
        }
      }, [(_a = slots.header) == null ? void 0 : _a.call(slots), ((_b = slots.loading) == null ? void 0 : _b.call(slots)) || ((_c = slots.empty) == null ? void 0 : _c.call(slots)) || createVNode(List, mergeProps({
        "ref": listRef
      }, unref(listProps), {
        "className": ns.be("dropdown", "list"),
        "scrollbarAlwaysOn": isScrollbarAlwaysOn.value,
        "data": data,
        "height": height,
        "width": width,
        "total": data.length,
        "onKeydown": onKeydown
      }), {
        default: (props3) => createVNode(Item, props3, null)
      }), (_d = slots.footer) == null ? void 0 : _d.call(slots)]);
    };
  }
});

export { ElSelectMenu as default };
//# sourceMappingURL=select-dropdown.mjs.map
