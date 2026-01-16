import { buildProps } from '../../../utils/vue/props/runtime.mjs';

const Effect = {
  LIGHT: "light",
  DARK: "dark"
};
const roleTypes = [
  "dialog",
  "grid",
  "group",
  "listbox",
  "menu",
  "navigation",
  "tooltip",
  "tree"
];
const popperProps = buildProps({
  role: {
    type: String,
    values: roleTypes,
    default: "tooltip"
  }
});
const usePopperProps = popperProps;

export { Effect, popperProps, roleTypes, usePopperProps };
//# sourceMappingURL=popper.mjs.map
