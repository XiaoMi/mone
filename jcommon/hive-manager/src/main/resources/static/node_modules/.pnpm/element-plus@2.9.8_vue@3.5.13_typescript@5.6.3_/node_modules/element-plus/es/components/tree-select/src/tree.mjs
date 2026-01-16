import { toRefs, computed, nextTick, watch } from 'vue';
import { pick, isEqual, isNil } from 'lodash-unified';
import { ElTree } from '../../tree/index.mjs';
import component from './tree-select-option.mjs';
import { treeEach, toValidArray, treeFind, isValidValue, isValidArray } from './utils.mjs';
import { escapeStringRegexp } from '../../../utils/strings.mjs';
import { UPDATE_MODEL_EVENT } from '../../../constants/event.mjs';
import { isFunction } from '@vue/shared';
import { isEmpty } from '../../../utils/types.mjs';

const useTree = (props, { attrs, slots, emit }, {
  select,
  tree,
  key
}) => {
  watch(() => props.modelValue, () => {
    if (props.showCheckbox) {
      nextTick(() => {
        const treeInstance = tree.value;
        if (treeInstance && !isEqual(treeInstance.getCheckedKeys(), toValidArray(props.modelValue))) {
          treeInstance.setCheckedKeys(toValidArray(props.modelValue));
        }
      });
    }
  }, {
    immediate: true,
    deep: true
  });
  const propsMap = computed(() => ({
    value: key.value,
    label: "label",
    children: "children",
    disabled: "disabled",
    isLeaf: "isLeaf",
    ...props.props
  }));
  const getNodeValByProp = (prop, data) => {
    var _a;
    const propVal = propsMap.value[prop];
    if (isFunction(propVal)) {
      return propVal(data, (_a = tree.value) == null ? void 0 : _a.getNode(getNodeValByProp("value", data)));
    } else {
      return data[propVal];
    }
  };
  const defaultExpandedParentKeys = toValidArray(props.modelValue).map((value) => {
    return treeFind(props.data || [], (data) => getNodeValByProp("value", data) === value, (data) => getNodeValByProp("children", data), (data, index, array, parent) => parent && getNodeValByProp("value", parent));
  }).filter((item) => isValidValue(item));
  const cacheOptions = computed(() => {
    if (!props.renderAfterExpand && !props.lazy)
      return [];
    const options = [];
    treeEach(props.data.concat(props.cacheData), (node) => {
      const value = getNodeValByProp("value", node);
      options.push({
        value,
        currentLabel: getNodeValByProp("label", node),
        isDisabled: getNodeValByProp("disabled", node)
      });
    }, (data) => getNodeValByProp("children", data));
    return options;
  });
  const getChildCheckedKeys = () => {
    var _a;
    return (_a = tree.value) == null ? void 0 : _a.getCheckedKeys().filter((checkedKey) => {
      var _a2;
      const node = (_a2 = tree.value) == null ? void 0 : _a2.getNode(checkedKey);
      return !isNil(node) && isEmpty(node.childNodes);
    });
  };
  return {
    ...pick(toRefs(props), Object.keys(ElTree.props)),
    ...attrs,
    nodeKey: key,
    expandOnClickNode: computed(() => {
      return !props.checkStrictly && props.expandOnClickNode;
    }),
    defaultExpandedKeys: computed(() => {
      return props.defaultExpandedKeys ? props.defaultExpandedKeys.concat(defaultExpandedParentKeys) : defaultExpandedParentKeys;
    }),
    renderContent: (h, { node, data, store }) => {
      return h(component, {
        value: getNodeValByProp("value", data),
        label: getNodeValByProp("label", data),
        disabled: getNodeValByProp("disabled", data),
        visible: node.visible
      }, props.renderContent ? () => props.renderContent(h, { node, data, store }) : slots.default ? () => slots.default({ node, data, store }) : void 0);
    },
    filterNodeMethod: (value, data, node) => {
      if (props.filterNodeMethod)
        return props.filterNodeMethod(value, data, node);
      if (!value)
        return true;
      const regexp = new RegExp(escapeStringRegexp(value), "i");
      return regexp.test(getNodeValByProp("label", data) || "");
    },
    onNodeClick: (data, node, e) => {
      var _a, _b, _c, _d;
      (_a = attrs.onNodeClick) == null ? void 0 : _a.call(attrs, data, node, e);
      if (props.showCheckbox && props.checkOnClickNode)
        return;
      if (!props.showCheckbox && (props.checkStrictly || node.isLeaf)) {
        if (!getNodeValByProp("disabled", data)) {
          const option = (_b = select.value) == null ? void 0 : _b.states.options.get(getNodeValByProp("value", data));
          (_c = select.value) == null ? void 0 : _c.handleOptionSelect(option);
        }
      } else if (props.expandOnClickNode) {
        e.proxy.handleExpandIconClick();
      }
      (_d = select.value) == null ? void 0 : _d.focus();
    },
    onCheck: (data, params) => {
      var _a;
      if (!props.showCheckbox)
        return;
      const dataValue = getNodeValByProp("value", data);
      const dataMap = {};
      treeEach([tree.value.store.root], (node) => dataMap[node.key] = node, (node) => node.childNodes);
      const uncachedCheckedKeys = params.checkedKeys;
      const cachedKeys = props.multiple ? toValidArray(props.modelValue).filter((item) => !(item in dataMap) && !uncachedCheckedKeys.includes(item)) : [];
      const checkedKeys = cachedKeys.concat(uncachedCheckedKeys);
      if (props.checkStrictly) {
        emit(UPDATE_MODEL_EVENT, props.multiple ? checkedKeys : checkedKeys.includes(dataValue) ? dataValue : void 0);
      } else {
        if (props.multiple) {
          const childKeys = getChildCheckedKeys();
          emit(UPDATE_MODEL_EVENT, cachedKeys.concat(childKeys));
        } else {
          const firstLeaf = treeFind([data], (data2) => !isValidArray(getNodeValByProp("children", data2)) && !getNodeValByProp("disabled", data2), (data2) => getNodeValByProp("children", data2));
          const firstLeafKey = firstLeaf ? getNodeValByProp("value", firstLeaf) : void 0;
          const hasCheckedChild = isValidValue(props.modelValue) && !!treeFind([data], (data2) => getNodeValByProp("value", data2) === props.modelValue, (data2) => getNodeValByProp("children", data2));
          emit(UPDATE_MODEL_EVENT, firstLeafKey === props.modelValue || hasCheckedChild ? void 0 : firstLeafKey);
        }
      }
      nextTick(() => {
        var _a2;
        const checkedKeys2 = toValidArray(props.modelValue);
        tree.value.setCheckedKeys(checkedKeys2);
        (_a2 = attrs.onCheck) == null ? void 0 : _a2.call(attrs, data, {
          checkedKeys: tree.value.getCheckedKeys(),
          checkedNodes: tree.value.getCheckedNodes(),
          halfCheckedKeys: tree.value.getHalfCheckedKeys(),
          halfCheckedNodes: tree.value.getHalfCheckedNodes()
        });
      });
      (_a = select.value) == null ? void 0 : _a.focus();
    },
    onNodeExpand: (data, node, e) => {
      var _a;
      (_a = attrs.onNodeExpand) == null ? void 0 : _a.call(attrs, data, node, e);
      nextTick(() => {
        if (!props.checkStrictly && props.lazy && props.multiple && node.checked) {
          const dataMap = {};
          const uncachedCheckedKeys = tree.value.getCheckedKeys();
          treeEach([tree.value.store.root], (node2) => dataMap[node2.key] = node2, (node2) => node2.childNodes);
          const cachedKeys = toValidArray(props.modelValue).filter((item) => !(item in dataMap) && !uncachedCheckedKeys.includes(item));
          const childKeys = getChildCheckedKeys();
          emit(UPDATE_MODEL_EVENT, cachedKeys.concat(childKeys));
        }
      });
    },
    cacheOptions
  };
};

export { useTree };
//# sourceMappingURL=tree.mjs.map
