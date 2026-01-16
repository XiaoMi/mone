'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var vue = require('vue');
var lodashUnified = require('lodash-unified');
var groupItem = require('./group-item.js');
var optionItem = require('./option-item.js');
var useProps = require('./useProps.js');
var token = require('./token.js');
var fixedSizeList = require('../../virtual-list/src/components/fixed-size-list.js');
var dynamicSizeList = require('../../virtual-list/src/components/dynamic-size-list.js');
var index = require('../../../hooks/use-namespace/index.js');
var types = require('../../../utils/types.js');
var core = require('@vueuse/core');
var aria = require('../../../constants/aria.js');
var shared = require('@vue/shared');

const props = {
  loading: Boolean,
  data: {
    type: Array,
    required: true
  },
  hoveringIndex: Number,
  width: Number
};
var ElSelectMenu = vue.defineComponent({
  name: "ElSelectDropdown",
  props,
  setup(props2, {
    slots,
    expose
  }) {
    const select = vue.inject(token.selectV2InjectionKey);
    const ns = index.useNamespace("select");
    const {
      getLabel,
      getValue,
      getDisabled
    } = useProps.useProps(select.props);
    const cachedHeights = vue.ref([]);
    const listRef = vue.ref();
    const size = vue.computed(() => props2.data.length);
    vue.watch(() => size.value, () => {
      var _a, _b;
      (_b = (_a = select.tooltipRef.value).updatePopper) == null ? void 0 : _b.call(_a);
    });
    const isSized = vue.computed(() => types.isUndefined(select.props.estimatedOptionHeight));
    const listProps = vue.computed(() => {
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
      if (!shared.isObject(target)) {
        return arr.includes(target);
      }
      return arr && arr.some((item) => {
        return vue.toRaw(lodashUnified.get(item, valueKey)) === lodashUnified.get(target, valueKey);
      });
    };
    const isEqual = (selected, target) => {
      if (!shared.isObject(target)) {
        return selected === target;
      } else {
        const {
          valueKey
        } = select.props;
        return lodashUnified.get(selected, valueKey) === lodashUnified.get(target, valueKey);
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
      const sized = vue.unref(isSized);
      const {
        itemSize,
        estimatedSize
      } = vue.unref(listProps);
      const {
        modelValue
      } = select.props;
      const {
        onSelect,
        onHover
      } = select;
      const item = data[index];
      if (item.type === "Group") {
        return vue.createVNode(groupItem["default"], {
          "item": item,
          "style": style,
          "height": sized ? itemSize : estimatedSize
        }, null);
      }
      const isSelected = isItemSelected(modelValue, item);
      const isDisabled = isItemDisabled(modelValue, isSelected);
      const isHovering = isItemHovering(index);
      return vue.createVNode(optionItem["default"], vue.mergeProps(itemProps, {
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
          return ((_a = slots.default) == null ? void 0 : _a.call(slots, props3)) || vue.createVNode("span", null, [getLabel(item)]);
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
      } = aria.EVENT_CODE;
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
      const isScrollbarAlwaysOn = vue.computed(() => {
        return core.isIOS ? true : scrollbarAlwaysOn;
      });
      const List = vue.unref(isSized) ? fixedSizeList["default"] : dynamicSizeList["default"];
      return vue.createVNode("div", {
        "class": [ns.b("dropdown"), ns.is("multiple", multiple)],
        "style": {
          width: `${width}px`
        }
      }, [(_a = slots.header) == null ? void 0 : _a.call(slots), ((_b = slots.loading) == null ? void 0 : _b.call(slots)) || ((_c = slots.empty) == null ? void 0 : _c.call(slots)) || vue.createVNode(List, vue.mergeProps({
        "ref": listRef
      }, vue.unref(listProps), {
        "className": ns.be("dropdown", "list"),
        "scrollbarAlwaysOn": isScrollbarAlwaysOn.value,
        "data": data,
        "height": height,
        "width": width,
        "total": data.length,
        "onKeydown": onKeydown
      }), {
        default: (props3) => vue.createVNode(Item, props3, null)
      }), (_d = slots.footer) == null ? void 0 : _d.call(slots)]);
    };
  }
});

exports["default"] = ElSelectMenu;
//# sourceMappingURL=select-dropdown.js.map
