<script setup>
import { computed } from "vue";
import { RouterLink } from "vue-router";
import { MDBIcon } from "mdb-vue-ui-kit";
import siteRoles from "@/utilitys/SiteRoles";

const props = defineProps({
  sites: {
    type: Object,
    required: true,
  },
  siteId: {
    type: String,
    required: true,
  },
  iconSize: {
    type: String,
    default: "lg"
  }
});

const siteRole = computed(() => props.sites[props.siteId].role);
</script>

<template>
  <RouterLink
    v-if="[siteRoles.OWNER, siteRoles.EDITOR].includes(siteRole)"
    :to="{ name: 'site-edit', params: { siteId } }"
    class="m-1 text-warning"
    type="button"
  >
    <MDBIcon iconStyle="fas" icon="edit" :size="iconSize" />
  </RouterLink>
  <RouterLink
    v-if="siteRole === siteRoles.OWNER"
    :to="{ name: 'site-delete', params: { siteId } }"
    class="m-1 text-danger"
    type="button"
  >
    <MDBIcon iconStyle="fas" icon="trash-can" :size="iconSize" />
  </RouterLink>
  <RouterLink
    v-else
    :to="{ name: 'site-leave', params: { siteId } }"
    class="m-1 text-danger"
    type="button"
  >
    <MDBIcon iconStyle="fas" icon="arrow-right-from-bracket" :size="iconSize" />
  </RouterLink>
</template>
