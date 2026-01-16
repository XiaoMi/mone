import { renderSlot, createVNode } from 'vue';

const HeaderCell = (props, {
  slots
}) => renderSlot(slots, "default", props, () => {
  var _a, _b;
  return [createVNode("div", {
    "class": props.class,
    "title": (_a = props.column) == null ? void 0 : _a.title
  }, [(_b = props.column) == null ? void 0 : _b.title])];
});
HeaderCell.displayName = "ElTableV2HeaderCell";
HeaderCell.inheritAttrs = false;
var HeaderCell$1 = HeaderCell;

export { HeaderCell$1 as default };
//# sourceMappingURL=header-cell.mjs.map
