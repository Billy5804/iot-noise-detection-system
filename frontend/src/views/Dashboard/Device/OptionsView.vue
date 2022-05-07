<script setup>
import { RouterLink } from "vue-router";
import { MDBIcon } from "mdb-vue-ui-kit";
import SiteUserRoles from "@/utilitys/SiteUserRoles";

defineProps({
  deviceId: {
    type: String,
    required: true,
  },
  siteId: {
    type: String,
    required: true,
  },
  iconSize: {
    type: String,
    default: "lg",
  },
  role: {
    type: String,
    required: true,
  },
});
</script>

<template>
  <div class="device-buttons">
    <RouterLink
      :to="{ name: 'dashboard-device-history', params: { siteId, deviceId } }"
      class="text-info"
      type="button"
      title="Device sensors history"
    >
      <MDBIcon iconStyle="fas" icon="chart-line" :size="iconSize" />
    </RouterLink>
    <template v-if="[SiteUserRoles.OWNER, SiteUserRoles.EDITOR].includes(role)">
      <RouterLink
        :to="{ name: 'dashboard-device-edit', params: { siteId, deviceId } }"
        class="text-warning"
        type="button"
        title="Edit device"
      >
        <MDBIcon iconStyle="fas" icon="edit" :size="iconSize" />
      </RouterLink>
      <RouterLink
        :to="{ name: 'dashboard-device-delete', params: { siteId, deviceId } }"
        class="text-danger"
        type="button"
        title="Delete device"
      >
        <MDBIcon iconStyle="fas" icon="trash-can" :size="iconSize" />
      </RouterLink>
    </template>
  </div>
</template>

<style scoped>
.device-buttons a {
  margin-left: 0.125rem;
  margin-right: 0.125rem;
}
</style>
