import { createRouter, createWebHistory } from "vue-router";
import LoginView from "../views/LoginView.vue";
import RegisterView from "../views/RegisterView.vue";
import LogoutView from "../views/LogoutView.vue";
import NotFoundView from "../views/NotFoundView.vue";
import AccountView from "../views/AccountView.vue";
import ForgotPasswordView from "../views/ForgotPasswordView.vue";
import SiteManagementView from "../views/SiteManagement/SiteManagementView.vue";
import SiteUserRoles from "../utilitys/SiteUserRoles";

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
          component: () => import("../views/SiteManagement/SiteCreateView.vue"),
        },
        {
          path: ":siteId",
          name: "site-options",
          component: () =>
            import("../views/SiteManagement/SiteOptionsView.vue"),
          props: ({ params }) => ({
            siteId: params.siteId,
            iconSize: "2x",
            roleSize: 2,
          }),
        },
        {
          path: ":siteId/edit",
          name: "site-edit",
          component: () => import("../views/SiteManagement/SiteEditView.vue"),
          props: true,
          meta: { allowedRoles: [SiteUserRoles.OWNER, SiteUserRoles.EDITOR] },
        },
        {
          path: ":siteId/delete",
          name: "site-delete",
          component: () => import("../views/SiteManagement/SiteDeleteView.vue"),
          props: true,
          meta: { allowedRoles: [SiteUserRoles.OWNER] },
        },
        {
          path: ":siteId/leave",
          name: "site-leave",
          component: () => import("../views/SiteManagement/SiteLeaveView.vue"),
          props: true,
          meta: {
            allowedRoles: Object.values(SiteUserRoles).filter(
              (value) => value !== SiteUserRoles.OWNER
            ),
          },
        },
        {
          path: ":siteId/users",
          name: "site-users",
          component: () =>
            import("../views/SiteManagement/SiteUsers/SiteUsersView.vue"),
          props: true,
          meta: { allowedRoles: [SiteUserRoles.OWNER] },
          children: [
            {
              path: ":userId",
              name: "site-user-edit",
              component: () =>
                import(
                  "../views/SiteManagement/SiteUsers/SiteUserEditView.vue"
                ),
              props: true,
            },
          ],
        },
        {
          path: ":siteId/invites",
          name: "site-invitations",
          component: () =>
            import(
              "../views/SiteManagement/SiteInvitations/SiteInvitationsView.vue"
            ),
          props: ({ params, name }) => ({
            siteId: params.siteId,
            invitationId:
              name === "site-invitation-create"
                ? "create"
                : params.invitationId,
          }),
          meta: { allowedRoles: [SiteUserRoles.OWNER] },
          children: [
            {
              path: "create",
              name: "site-invitation-create",
              component: () =>
                import(
                  "../views/SiteManagement/SiteInvitations/SiteInvitationCreateView.vue"
                ),
              props: ({ params }) => ({
                siteId: params.siteId,
              }),
            },
          ],
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
