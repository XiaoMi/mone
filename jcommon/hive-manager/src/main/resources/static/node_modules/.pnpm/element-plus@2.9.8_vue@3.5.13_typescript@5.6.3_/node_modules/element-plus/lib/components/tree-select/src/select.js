'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var vue = require('vue');
var lodashUnified = require('lodash-unified');
var index$1 = require('../../select/index.js');
var index = require('../../../hooks/use-namespace/index.js');
var event = require('../../../constants/event.js');

const useSelect = (props, { attrs, emit }, {
  select,
  tree,
  key
}) => {
  const ns = index.useNamespace("tree-select");
  vue.watch(() => props.data, () => {
    if (props.filterable) {
      vue.nextTick(() => {
        var _a, _b;
        (_b = tree.value) == null ? void 0 : _b.filter((_a = select.value) == null ? void 0 : _a.states.inputValue);
      });
    }
  }, { flush: "post" });
  const result = {
    ...lodashUnified.pick(vue.toRefs(props), Object.keys(index$1.ElSelect.props)),
    ...attrs,
    class: vue.computed(() => attrs.class),
    style: vue.computed(() => attrs.style),
    "onUpdate:modelValue": (value) => emit(event.UPDATE_MODEL_EVENT, value),
    valueKey: key,
    popperClass: vue.computed(() => {
      const classes = [ns.e("popper")];
      if (props.popperClass)
        classes.push(props.popperClass);
      return classes.join(" ");
    }),
    filterMethod: (keyword = "") => {
      var _a;
      if (props.filterMethod) {
        props.filterMethod(keyword);
      } else if (props.remoteMethod) {
        props.remoteMethod(keyword);
      } else {
        (_a = tree.value) == null ? void 0 : _a.filter(keyword);
      }
    }
  };
  return result;
};

exports.useSelect = useSelect;
//# sourceMappingURL=select.js.map
