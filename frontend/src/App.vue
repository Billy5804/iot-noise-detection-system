<script>
import { useUserStore } from "@/stores/UserStore";
import { computed, watch } from "vue";
import { useRouter, RouterView } from "vue-router";
import PageHeader from "./components/PageHeader.vue";
import LoadingView from "./views/LoadingView.vue";
import ConfirmEmailBanner from "./components/ConfirmEmailBanner.vue";
import ForbiddenView from "./views/ForbiddenView.vue";
import UnauthorisedView from "./views/UnauthorisedView.vue";

export default {
  components: {
    RouterView,
    PageHeader,
    LoadingView,
    ConfirmEmailBanner,
    ForbiddenView,
    UnauthorisedView,
  },

  setup: function () {
    const user = useUserStore();
    const router = useRouter();

    const userLoading = computed(() => !user.finishedLoading);

    const loadingWatcher = watch(userLoading, (loading) => {
      if (!loading) {
        const replacePath =
          routerRedirect(router.currentRoute.value) ||
          router.currentRoute.value.path;
        router.replace(replacePath);
        loadingWatcher();
      }
    });

    const userEmailVerified = computed(() => user.emailVerified);

    const onEntranceView = computed(
      () => router.currentRoute.value.meta.isEntrance
    );
    const onHomeView = computed(() => router.currentRoute.value.name == "home");
    const bypassAuth = computed(
      () => router.currentRoute.value.meta.bypassAuth
    );
    const authorised = computed(() => user.loggedIn);

    const viewRequiresEmailVerification = computed(
      () => router.currentRoute.value.meta.requireEmailVerification
    );

    );

    function routerRedirect(to) {
      if (to.meta.bypassAuth) {
        return;
      }
      if (user.loggedIn && to.meta.isEntrance) {
        return "/account";
      }
      return;
    }

    router.beforeEach((to) => {
      return routerRedirect(to);
    });

    return {
      authorised,
      bypassAuth,
      onEntranceView,
      onHomeView,
      userLoading,
      userEmailVerified,
      viewRequiresEmailVerification,
    };
  },
};
</script>

<template>
  <main v-if="userLoading"><LoadingView /></main>
  <template v-else>
    <PageHeader />
    <RouterView v-if="onEntranceView" class="container m-auto mt-5" />
    <template v-else-if="authorised || bypassAuth">
      <ConfirmEmailBanner
        v-if="!userEmailVerified && authorised"
        class="mt-3 mb-n4"
      />
      <ForbiddenView
        v-if="!userEmailVerified && viewRequiresEmailVerification"
        class="container m-auto mt-5"
      />
      <RouterView v-else class="container m-auto mt-5" />
    </template>
    <UnauthorisedView v-else class="container m-auto mt-5" />
  </template>
</template>

<style>
#app {
  font-family: Roboto, Helvetica, Arial, sans-serif;
}
</style>
