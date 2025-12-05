import { createRouter, createWebHistory } from "vue-router";
import { useUserStore } from "@/stores/user";
import { isTokenExpired } from "@/utils/parseToken";

const router = createRouter({
  history: createWebHistory('/agent-manager/'),
  routes: [
    {
      path: "/login",
      name: "Login",
      component: () => import("@/views/Login.vue")
    },
    {
      path: "/",
      name: "Home",
      redirect: "/login"
    },
    {
      path: "/bindInner",
      name: "BindInner",
      component: () => import("@/views/BindInner.vue")
    },
    {
      path: "/",
      component: () => import("@/components/Header.vue"),
      children: [
        {
          path: "agents",
          name: "AgentList",
          component: () => import("@/views/AgentList.vue"),
          meta: { requiresAuth: true }
        },
        {
          path: "tasks",
          name: "TaskList",
          component: () => import("@/views/TaskList.vue"),
          meta: { requiresAuth: true }
        },
        {
          path: "reportList",
          name: "ReportList",
          component: () => import("@/views/ReportList.vue"),
          meta: { requiresAuth: true }
        }
      ]
    },
    {
      path: "/about",
      name: "About",
      component: () => import("@/views/About.vue"),
      meta: { requiresAuth: true }
    },
    {
      path: "/chat",
      name: "Chat",
      component: () => import("@/views/Chat.vue"),
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
      path: "/:pathMatch(.*)*",
      name: "NotFound",
      component: () => import("@/views/Login.vue")
    }
  ]
});

router.beforeEach((to, from, next) => {
  const userStore = useUserStore();
  const isExpired = isTokenExpired();
  if (isExpired && to.path !== "/login") {
    userStore.clearUser();
    next("/login");
    return
  }
  if (!userStore.initUser()) {
    if (to.path === "/login") {
      userStore.clearUser();
      next();
    } else if (to.path === "/bindInner") {
      next();
    } else {
      next("/login");
    }
  } else {
    if (to.meta.requiresAuth && !userStore.token) {
      next("/login");
    } else if (to.path === "/login") {
      next("/agents");
    } else {
      next();
    }
  }
});

export default router;
