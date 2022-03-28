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
import SiteUsersView from "../views/SiteManagement/SiteUsersView.vue";

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
      path: "/sites",
      name: "site-management",
      component: SiteManagementView,
      props: ({ params, name }) => ({
        siteId: name === "site-create" ? "create" : params.siteId,
      }),
      children: [
        {
          path: "create",
          name: "site-create",
          component: SiteCreateView,
        },
        {
          path: ":siteId",
          name: "site-options",
          component: SiteOptionsView,
          props: ({ params }) => ({
            siteId: params.siteId,
            iconSize: "2x",
            roleSize: 2,
          }),
        },
        {
          path: ":siteId/edit",
          name: "site-edit",
          component: SiteEditView,
          props: true,
        },
        {
          path: ":siteId/delete",
          name: "site-delete",
          component: SiteDeleteView,
          props: true,
        },
        {
          path: ":siteId/leave",
          name: "site-leave",
          component: SiteLeaveView,
          props: true,
        },
        {
          path: ":siteId/users",
          name: "site-users",
          component: SiteUsersView,
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
