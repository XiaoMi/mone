<template>
  <div>
    {{ $dateFormat(time, "yyyy/mm/dd HH:MM:ss") }}
  </div>
</template>
<script>
export default {
  props: {
    timeStamp: {
      required: true,
    },
  },
  computed: {
    time() {
      return this.timeStamp;
    },
  },
};
</script>
