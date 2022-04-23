<script>
import { ref, shallowRef, computed } from "vue";
import { useUserStore } from "@/stores/UserStore";
import axios from "axios";
import { MDBRow, MDBCol, MDBIcon } from "mdb-vue-ui-kit";
import AjaxButton from "@/components/AjaxButton.vue";
import { RouterLink } from "vue-router";
import SiteUserRoles from "@/utilitys/SiteUserRoles";

export default {
  components: {
    MDBRow,
    MDBCol,
    MDBIcon,
    AjaxButton,
    RouterLink,
  },

  emits: ["done"],

  props: {
    users: {
      type: Object,
      required: true,
    },
    userId: {
      type: String,
      required: true,
    },
    siteId: {
      type: String,
      required: true,
    },
  },

  setup: function (props, context) {
    const { getIdToken } = useUserStore();

    const selectedUser = computed(() => {
      const userIndex = props.users.findIndex(({ id }) => id === props.userId);
      if (userIndex < 0) {
        return;
      }
      return props.users[userIndex];
    });

    const formChecked = ref(false);
    const syncing = ref(false);

    const updateError = shallowRef(null);

    const newRoleSelect = ref(null);
    const newRole = ref("");
    const newRoleValidity = computed(() => newRoleSelect.value?.validity);

    async function submitUpdateForm() {
      updateError.value = null;
      if (!newRoleValidity.value.valid) {
        formChecked.value = true;
        return;
      }
      syncing.value = true;
      axios
        .put(
          "http://localhost:443/api/v1/site-users",
          {
            siteId: props.siteId,
            userId: props.userId,
            role: newRole.value,
          },
          {
            timeout: 5000,
            headers: { authorization: await getIdToken() },
          }
        )
        .then(({ data }) => {
          const { user, site, ...siteUser } = data; // eslint-disable-line no-unused-vars
          Object.assign(selectedUser.value, { ...user, ...siteUser });
          context.emit("done");
        })
        .catch((error) => (updateError.value = error.message || error))
        .finally(() => (syncing.value = false));
    }

    return {
      formChecked,
      syncing,
      updateError,
      newRole,
      newRoleSelect,
      submitUpdateForm,
      SiteUserRoles,
      selectedUser,
    };
  },
};
</script>

<template>
  <span class="d-flex justify-content-between flex-wrap">
    <RouterLink :to="{ name: 'site-users' }" class="text-dark" title="Back">
      <MDBIcon iconStyle="fas" icon="arrow-left" size="2xl" />
    </RouterLink>
    <h2 v-if="selectedUser" class="h3">
      Update {{ selectedUser?.displayName }}
    </h2>
    <template v-else>
      <h2 class="h3">Unknown User</h2>
      <p class="w-100 text-center m-0">Please choose a different user.</p>
    </template>
  </span>
  <MDBRow
    v-if="selectedUser"
    tag="form"
    class="g-3 mb-3 needs-validation"
    :class="formChecked && 'was-validated'"
    novalidate
    @submit.prevent="submitUpdateForm"
  >
    <MDBCol md="12">
      <label class="form-label">New Role*</label>
      <select
        ref="newRoleSelect"
        class="form-select"
        v-model="newRole"
        aria-label="Site user role select field"
        required
      >
        <option value disabled>Choose New Role</option>
        <template v-for="role in SiteUserRoles">
          <option
            v-if="![SiteUserRoles.OWNER, selectedUser.role].includes(role)"
            :key="role"
            :value="role"
          >
            {{ `${role[0]}${role.slice(1).toLowerCase()}` }}
          </option>
        </template>
      </select>
    </MDBCol>
    <MDBCol md="12" v-if="updateError">
      <div class="alert alert-danger text-center p-2 mb-0" role="alert">
        {{ updateError }}
      </div>
    </MDBCol>
    <MDBCol md="12">
      <AjaxButton
        color="warning"
        type="submit"
        size="lg"
        class="w-100"
        :syncing="syncing"
        >Update User</AjaxButton
      >
    </MDBCol>
  </MDBRow>
</template>
