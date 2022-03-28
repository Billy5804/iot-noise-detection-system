<script setup>
import { computed } from "vue";
import { RouterLink } from "vue-router";
import { MDBRow, MDBCol, MDBIcon } from "mdb-vue-ui-kit";
import siteRoles from "@/utilitys/SiteRoles";
import VerticalRule from "../../components/VerticalRule.vue";

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
    default: "lg",
  },
  roleSize: {
    type: Number,
    validator: (size) => size >= 1 && size <= 6,
    default: 6,
  },
});

const siteRole = computed(() => props.sites[props.siteId].role);
</script>

<template>
  <MDBRow class="text-center d-flex align-items-center">
    <MDBCol class="site-buttons">
      <RouterLink
        v-if="[siteRoles.OWNER, siteRoles.EDITOR].includes(siteRole)"
        :to="{ name: 'site-edit', params: { siteId } }"
        class="text-warning"
        type="button"
        title="Edit site"
      >
        <MDBIcon iconStyle="fas" icon="edit" :size="iconSize" />
      </RouterLink>
      <template v-if="siteRole === siteRoles.OWNER">
        <RouterLink
          :to="{ name: 'site-users', params: { siteId } }"
          class="text-info"
          type="button"
          title="Manage site users"
        >
          <MDBIcon iconStyle="fas" icon="users" :size="iconSize" />
        </RouterLink>
        <RouterLink
          :to="{ name: 'site-delete', params: { siteId } }"
          class="text-danger"
          type="button"
          title="Delete site"
        >
          <MDBIcon iconStyle="fas" icon="trash-can" :size="iconSize" />
        </RouterLink>
      </template>
      <RouterLink
        v-else
        :to="{ name: 'site-leave', params: { siteId } }"
        class="text-danger"
        type="button"
        title="Leave site"
      >
        <MDBIcon
          iconStyle="fas"
          icon="arrow-right-from-bracket"
          :size="iconSize"
        />
      </RouterLink>
    </MDBCol>
    <VerticalRule />
    <MDBCol>
      <span :class="`h${roleSize}`"
        >Role: {{ `${siteRole[0]}${siteRole.slice(1).toLowerCase()}` }}</span
      >
    </MDBCol>
  </MDBRow>
</template>

<style scoped>
.site-buttons a {
  margin: 0.09rem;
}
</style>
