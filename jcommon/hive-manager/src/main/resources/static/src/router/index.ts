import { createRouter, createWebHistory } from "vue-router";
import { useUserStore } from "@/stores/user";

const router = createRouter({
  history: createWebHistory('/page/'),
  routes: [
    {
      path: "/login",
      name: "Login",
      component: () => import("@/views/Login.vue")
    },
    {
      path: "/",
      name: "Home",
      component: () => import("@/views/Home.vue"),
      meta: { requiresAuth: true }
    },
    {
      path: "/about",
      name: "About",
      component: () => import("@/views/About.vue"),
      meta: { requiresAuth: true }
    },
    {
      path: "/recommend",
      name: "Recommend",
      component: () => import("@/views/Recommend.vue"),
      meta: { requiresAuth: true }
    },
    {
      path: "/person",
      name: "Person",
      component: () => import("@/views/Person.vue"),
      meta: { requiresAuth: true }
    },
    {
      path: "/agents",
      name: "AgentList",
      component: () => import("@/views/AgentList.vue"),
      meta: { requiresAuth: true }
    }
  ]
});

router.beforeEach((to, from, next) => {
  const userStore = useUserStore();
  userStore.initUser();

  if (to.meta.requiresAuth && !userStore.token) {
    next("/login");
  } else {
    next();
  }
});

export default router;
