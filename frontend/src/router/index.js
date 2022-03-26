import { createRouter, createWebHistory } from "vue-router";
import LoginView from "../views/LoginView.vue";
import RegisterView from "../views/RegisterView.vue";
import LogoutView from "../views/LogoutView.vue";
import NotFoundView from "../views/NotFoundView.vue";
import AccountView from "../views/AccountView.vue";
import ForgotPasswordView from "../views/ForgotPasswordView.vue";
import SiteManagementView from "../views/SiteManagement/IndexView.vue";
import SiteEditView from "../views/SiteManagement/SiteEditView.vue";
import SiteDeleteView from "../views/SiteManagement/SiteDeleteView.vue";
import SiteCreateView from "../views/SiteManagement/SiteCreateView.vue";
import SiteLeaveView from "../views/SiteManagement/SiteLeaveView.vue";
import SiteOptionsView from "../views/SiteManagement/SiteOptionsView.vue";

function siteManagementProps({ params }, staticProps) {
  return {
    siteId: params.siteId !== "new" ? params.siteId : "unknown",
    ...staticProps,
  };
}

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
      path: "/sites/create",
      name: "site-create",
      component: SiteManagementView,
      props: { siteId: "new" },
      children: [
        {
          path: "",
          component: SiteCreateView,
        },
      ],
    },
    {
      path: "/sites/:siteId?",
      name: "site-management",
      component: SiteManagementView,
      props: siteManagementProps,
      children: [
        {
          path: "edit",
          name: "site-edit",
          component: SiteEditView,
          props: siteManagementProps,
        },
        {
          path: "delete",
          name: "site-delete",
          component: SiteDeleteView,
          props: siteManagementProps,
        },
        {
          path: "leave",
          name: "site-leave",
          component: SiteLeaveView,
          props: siteManagementProps,
        },
        {
          path: ":pathMatch?",
          component: SiteOptionsView,
          props: (route) => siteManagementProps(route, { iconSize: "2x" }),
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
