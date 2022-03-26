import { createRouter, createWebHistory } from "vue-router";
import LoginView from "../views/LoginView.vue";
import RegisterView from "../views/RegisterView.vue";
import LogoutView from "../views/LogoutView.vue";
import NotFoundView from "../views/NotFoundView.vue";
import AccountView from "../views/AccountView.vue";
import ForgotPasswordView from "../views/ForgotPasswordView.vue";
import SiteManagementView from "../views/SiteManagementView/IndexView.vue";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: "/register",
      alias: "/",
      name: "register",
      component: RegisterView,
      meta: { isEntrance: true },
    },
    {
      path: "/login",
      name: "login",
      component: LoginView,
      meta: { isEntrance: true },
    },
    {
      path: "/login/forgot",
      name: "forgot-password",
      component: ForgotPasswordView,
      meta: { isEntrance: true },
    },
    {
      path: "/account",
      name: "account",
      component: AccountView,
    },
    {
      path: "/sites/:siteId?",
      name: "site-management",
      component: SiteManagementView,
      props: true,
      children: [
        {
          path: "edit",
          name: "site-edit",
          component: SiteEditView,
          props: true,
        },
        {
          path: "delete",
          name: "site-delete",
          component: SiteDeleteView,
          props: true,
        },
      ],
    },
    {
      path: "/logout",
      name: "logout",
      component: LogoutView,
      meta: { bypassAuth: true },
    },
    {
      path: "/:pathMatch(.*)*",
      name: "not-found",
      component: NotFoundView,
      meta: { bypassAuth: true },
    },
  ],
});

export default router;
