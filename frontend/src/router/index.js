import { createRouter, createWebHistory } from "vue-router";
import LoginView from "../views/LoginView.vue";
import RegisterView from "../views/RegisterView.vue";
import LogoutView from "../views/LogoutView.vue";
import NotFoundView from "../views/NotFoundView.vue";
import AccountView from "../views/AccountView.vue";
import ForgotPasswordView from "../views/ForgotPasswordView.vue";
import SiteManagementView from "../views/SiteManagement/SiteManagementView.vue";
import DashboardView from "../views/Dashboard/DashboardView.vue";
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
      meta: { bypassEmailVerification: true },
    },
    {
      path: "/sites",
      name: "site-management",
      component: SiteManagementView,
      props: ({ params, name }) => ({
        siteId: ["site-create", "site-invitation"].includes(name)
          ? name.replace("site-", "")
          : params.siteId,
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
            {
              path: ":invitationId",
              name: "site-invitation-edit",
              component: () =>
                import(
                  "../views/SiteManagement/SiteInvitations/SiteInvitationEditView.vue"
                ),
              props: true,
            },
          ],
        },
        {
          path: "invitation/:invitationId?",
          name: "site-invitation",
          component: () =>
            import("../views/SiteManagement/SiteInvitationView.vue"),
          props: true,
        },
      ],
    },
    {
      path: "/dashboard/:siteId",
      // name: "dashboard",
      component: DashboardView,
      props: true,
      children: [
        {
          path: "location",
          // name: "dashboard-location-overview",
          component: () =>
            import("../views/Dashboard/Location/OverviewView.vue"),
          props: ({ params, name }) => ({
            locationId: params.locationId,
            siteId: params.siteId,
            modalName:
              name !== "dashboard-location-overview"
                ? name?.replace("dashboard-location-", "")
                : null,
            ...(name === "dashboard-location-map-devices" && {
              modalSize: "xl",
            }),
          }),
          children: [
            {
              path: ":locationId?/add",
              name: "dashboard-location-add",
              component: () =>
                import("../views/Dashboard/Location/AddView.vue"),
              props: true,
              meta: {
                allowedRoles: [SiteUserRoles.OWNER, SiteUserRoles.EDITOR],
              },
            },
            {
              path: ":locationId?",
              name: "dashboard-location-overview",
              component: {},
            },
            {
              path: ":locationId/edit",
              name: "dashboard-location-edit",
              component: () =>
                import("../views/Dashboard/Location/EditView.vue"),
              props: true,
              meta: {
                allowedRoles: [SiteUserRoles.OWNER, SiteUserRoles.EDITOR],
              },
            },
            {
              path: ":locationId/delete",
              name: "dashboard-location-delete",
              component: () =>
                import("../views/Dashboard/Location/DeleteView.vue"),
              props: true,
              meta: {
                allowedRoles: [SiteUserRoles.OWNER, SiteUserRoles.EDITOR],
              },
            },
            {
              path: ":locationId/floor-plan",
              name: "dashboard-location-floor-plan",
              component: () =>
                import("../views/Dashboard/Location/ChangeFloorPlanView.vue"),
              props: true,
              meta: {
                allowedRoles: [SiteUserRoles.OWNER, SiteUserRoles.EDITOR],
              },
            },
            {
              path: ":locationId/manage-devices",
              name: "dashboard-location-manage-devices",
              component: () =>
                import("../views/Dashboard/Location/ManageDevicesView.vue"),
              props: true,
              meta: {
                allowedRoles: [SiteUserRoles.OWNER, SiteUserRoles.EDITOR],
              },
            },
            {
              path: ":locationId/map-devices",
              name: "dashboard-location-map-devices",
              component: () =>
                import("../views/Dashboard/Location/MapDevicesView.vue"),
              props: true,
              meta: {
                allowedRoles: [SiteUserRoles.OWNER, SiteUserRoles.EDITOR],
              },
            },
          ],
        },
        {
          path: "",
          name: "dashboard-device-overview",
          component: () => import("../views/Dashboard/Device/OverviewView.vue"),
          props: ({ params, name }) => ({
            deviceId: ["dashboard-device-add"].includes(name)
              ? name.replace("dashboard-device-", "")
              : params.deviceId,
            siteId: params.siteId,
            ...(name === "dashboard-device-history" && { modalSize: "xl" }),
          }),
          children: [
            {
              path: "add",
              name: "dashboard-device-add",
              component: () => import("../views/Dashboard/Device/AddView.vue"),
              props: true,
              meta: {
                allowedRoles: [SiteUserRoles.OWNER, SiteUserRoles.EDITOR],
              },
            },
            {
              path: ":deviceId",
              name: "dashboard-device-options",
              component: () =>
                import("../views/Dashboard/Device/OptionsView.vue"),
              props: ({ params }) => ({ ...params, iconSize: "2x" }),
            },
            {
              path: ":deviceId/edit",
              name: "dashboard-device-edit",
              component: () => import("../views/Dashboard/Device/EditView.vue"),
              props: true,
              meta: {
                allowedRoles: [SiteUserRoles.OWNER, SiteUserRoles.EDITOR],
              },
            },
            {
              path: ":deviceId/delete",
              name: "dashboard-device-delete",
              component: () =>
                import("../views/Dashboard/Device/DeleteView.vue"),
              props: ({ params }) => ({ deviceId: params.deviceId }),
              meta: {
                allowedRoles: [SiteUserRoles.OWNER, SiteUserRoles.EDITOR],
              },
            },
            {
              path: ":deviceId/history",
              name: "dashboard-device-history",
              component: () =>
                import("../views/Dashboard/Device/HistoryView.vue"),
              props: true,
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
