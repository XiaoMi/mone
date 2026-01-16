import { defineComponent } from 'vue';
import { flattedChildren } from '../../../utils/vue/vnode.mjs';
import { isArray } from '@vue/shared';

var ElTourSteps = defineComponent({
  name: "ElTourSteps",
  props: {
    current: {
      type: Number,
      default: 0
    }
  },
  emits: ["update-total"],
  setup(props, { slots, emit }) {
    let cacheTotal = 0;
    return () => {
      var _a, _b;
      const children = (_a = slots.default) == null ? void 0 : _a.call(slots);
      const result = [];
      let total = 0;
      function filterSteps(children2) {
        if (!isArray(children2))
          return;
        children2.forEach((item) => {
          var _a2;
          const name = (_a2 = (item == null ? void 0 : item.type) || {}) == null ? void 0 : _a2.name;
          if (name === "ElTourStep") {
            result.push(item);
            total += 1;
          }
        });
      }
      if (children.length) {
        filterSteps(flattedChildren((_b = children[0]) == null ? void 0 : _b.children));
      }
      if (cacheTotal !== total) {
        cacheTotal = total;
        emit("update-total", total);
      }
      if (result.length) {
        return result[props.current];
      }
      return null;
    };
  }
});

export { ElTourSteps as default };
//# sourceMappingURL=steps.mjs.map
