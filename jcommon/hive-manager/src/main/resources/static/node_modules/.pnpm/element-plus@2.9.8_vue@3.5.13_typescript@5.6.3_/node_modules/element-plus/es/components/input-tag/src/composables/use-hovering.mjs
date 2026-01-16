import { ref } from 'vue';

function useHovering() {
  const hovering = ref(false);
  const handleMouseEnter = () => {
    hovering.value = true;
  };
  const handleMouseLeave = () => {
    hovering.value = false;
  };
  return {
    hovering,
    handleMouseEnter,
    handleMouseLeave
  };
}

export { useHovering };
//# sourceMappingURL=use-hovering.mjs.map
