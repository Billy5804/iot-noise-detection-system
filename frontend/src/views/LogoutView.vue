<script>
import { useUserStore } from "@/stores/UserStore";
import { useRouter } from "vue-router";

export default {
  setup: function () {
    const user = useUserStore();
    const router = useRouter();

    async function logout() {
      await user.logout();
      router.replace("/");
    }

    logout();
  },
};
</script>

<template>
  <main id="logout-view" class="container text-center">
    <h1 class="display-1 mb-3">
      <b>Signing out...</b>
    </h1>
    <hr />
  </main>
</template>

<style>
#logout-view {
  max-width: 650px;
}
</style>
