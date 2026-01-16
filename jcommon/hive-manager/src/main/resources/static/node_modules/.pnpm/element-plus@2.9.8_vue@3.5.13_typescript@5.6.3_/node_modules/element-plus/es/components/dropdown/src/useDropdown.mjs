import { inject, computed } from 'vue';

const useDropdown = () => {
  const elDropdown = inject("elDropdown", {});
  const _elDropdownSize = computed(() => elDropdown == null ? void 0 : elDropdown.dropdownSize);
  return {
    elDropdown,
    _elDropdownSize
  };
};

export { useDropdown };
//# sourceMappingURL=useDropdown.mjs.map
