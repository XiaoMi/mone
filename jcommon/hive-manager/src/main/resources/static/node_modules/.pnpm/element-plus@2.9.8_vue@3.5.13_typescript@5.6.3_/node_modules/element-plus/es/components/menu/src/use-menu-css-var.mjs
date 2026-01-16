import { computed } from 'vue';
import useMenuColor from './use-menu-color.mjs';
import { useNamespace } from '../../../hooks/use-namespace/index.mjs';

const useMenuCssVar = (props, level) => {
  const ns = useNamespace("menu");
  return computed(() => ns.cssVarBlock({
    "text-color": props.textColor || "",
    "hover-text-color": props.textColor || "",
    "bg-color": props.backgroundColor || "",
    "hover-bg-color": useMenuColor(props).value || "",
    "active-color": props.activeTextColor || "",
    level: `${level}`
  }));
};

export { useMenuCssVar };
//# sourceMappingURL=use-menu-css-var.mjs.map
