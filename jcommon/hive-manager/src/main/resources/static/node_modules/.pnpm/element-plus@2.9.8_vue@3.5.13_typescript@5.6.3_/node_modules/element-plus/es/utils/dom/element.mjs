import { isClient } from '@vueuse/core';
import { isString } from '@vue/shared';

const getElement = (target) => {
  if (!isClient || target === "")
    return null;
  if (isString(target)) {
    try {
      return document.querySelector(target);
    } catch (e) {
      return null;
    }
  }
  return target;
};

export { getElement };
//# sourceMappingURL=element.mjs.map
