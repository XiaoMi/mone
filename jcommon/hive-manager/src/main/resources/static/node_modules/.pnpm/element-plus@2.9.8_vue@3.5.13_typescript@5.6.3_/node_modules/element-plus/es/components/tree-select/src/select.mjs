import { watch, nextTick, toRefs, computed } from 'vue';
import { pick } from 'lodash-unified';
import { ElSelect } from '../../select/index.mjs';
import { useNamespace } from '../../../hooks/use-namespace/index.mjs';
import { UPDATE_MODEL_EVENT } from '../../../constants/event.mjs';

const useSelect = (props, { attrs, emit }, {
  select,
  tree,
  key
}) => {
  const ns = useNamespace("tree-select");
  watch(() => props.data, () => {
    if (props.filterable) {
      nextTick(() => {
        var _a, _b;
        (_b = tree.value) == null ? void 0 : _b.filter((_a = select.value) == null ? void 0 : _a.states.inputValue);
      });
    }
  }, { flush: "post" });
  const result = {
    ...pick(toRefs(props), Object.keys(ElSelect.props)),
    ...attrs,
    class: computed(() => attrs.class),
    style: computed(() => attrs.style),
    "onUpdate:modelValue": (value) => emit(UPDATE_MODEL_EVENT, value),
    valueKey: key,
    popperClass: computed(() => {
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

export { useSelect };
//# sourceMappingURL=select.mjs.map
