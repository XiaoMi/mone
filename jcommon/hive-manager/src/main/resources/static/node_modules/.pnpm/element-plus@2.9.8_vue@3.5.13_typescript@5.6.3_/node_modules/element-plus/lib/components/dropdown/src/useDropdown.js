'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var vue = require('vue');

const useDropdown = () => {
  const elDropdown = vue.inject("elDropdown", {});
  const _elDropdownSize = vue.computed(() => elDropdown == null ? void 0 : elDropdown.dropdownSize);
  return {
    elDropdown,
    _elDropdownSize
  };
};

exports.useDropdown = useDropdown;
//# sourceMappingURL=useDropdown.js.map
