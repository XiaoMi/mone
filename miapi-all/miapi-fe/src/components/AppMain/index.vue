<template>
  <section class="app-main">
    <!-- <transition
      name="custom-classes-transition"
      enter-active-class="animate__animated animate__slideInLeft"
      leave-active-class="animate__animated animate__lightSpeedOutRight"
      mode="out-in"
    > -->
      <keep-alive :include="cachedViews">
        <router-view />
      </keep-alive>
    <!-- </transition> -->
  </section>
</template>

<script lang="ts">
import { defineComponent, computed } from "vue"
import { useStore } from "vuex"
import { useRoute } from "vue-router"
export default defineComponent({
  setup(){
    const route = useRoute()
    const store = useStore()
    const cachedViews = computed(() => store.state.tagsView.cachedViews)
    const key = computed(() => route.path)
    return{
      cachedViews,
      key
    }
  },
})
</script>

<style lang="scss" scoped>
@import '@/styles/variables.scss';
.app-main {
  height: calc(100vh - #{$headerHeight});
  width: 100%;
  overflow: hidden;
  position: relative;
}

.animate__animated {
  -webkit-animation-duration: 0.3s;
  animation-duration: 0.3s;
  -webkit-animation-duration: 0.3s;
  animation-duration: 0.3s;
  -webkit-animation-fill-mode: both;
  animation-fill-mode: both;
}

@-webkit-keyframes slideInLeft {
  from {
    -webkit-transform: translate3d(-100%, 0, 0);
    transform: translate3d(-100%, 0, 0);
    visibility: visible;
  }

  to {
    -webkit-transform: translate3d(0, 0, 0);
    transform: translate3d(0, 0, 0);
  }
}
@keyframes slideInLeft {
  from {
    -webkit-transform: translate3d(-100%, 0, 0);
    transform: translate3d(-100%, 0, 0);
    visibility: visible;
  }

  to {
    -webkit-transform: translate3d(0, 0, 0);
    transform: translate3d(0, 0, 0);
  }
}
.animate__slideInLeft {
  -webkit-animation-name: slideInLeft;
  animation-name: slideInLeft;
}
@-webkit-keyframes lightSpeedOutRight {
  from {
    opacity: 1;
  }

  to {
    -webkit-transform: translate3d(100%, 0, 0);
    transform: translate3d(100%, 0, 0);
    opacity: 0;
  }
}
@keyframes lightSpeedOutRight {
  from {
    opacity: 1;
  }

  to {
    -webkit-transform: translate3d(100%, 0, 0);
    transform: translate3d(100%, 0, 0);
    opacity: 0;
  }
}
.animate__lightSpeedOutRight {
  -webkit-animation-name: lightSpeedOutRight;
  animation-name: lightSpeedOutRight;
  -webkit-animation-timing-function: ease-in;
  animation-timing-function: ease-in;
}

</style>
