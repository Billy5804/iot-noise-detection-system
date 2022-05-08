<script>
import { ref, shallowRef, computed, onUnmounted } from "vue";
import { useUserStore } from "@/stores/UserStore";
import axios from "axios";
import { MDBRow, MDBCol, MDBIcon } from "mdb-vue-ui-kit";
import AjaxButton from "@/components/AjaxButton.vue";
import FormInput from "@/components/FormInput.vue";
import { RouterLink } from "vue-router";

export default {
  components: {
    MDBRow,
    MDBCol,
    MDBIcon,
    AjaxButton,
    FormInput,
    RouterLink,
  },

  emits: ["done"],

  props: {
    siteInvitations: {
      type: Object,
      required: true,
    },
    invitationId: {
      type: String,
      required: true,
    },
  },

  setup: function (props, context) {
    const { getIdToken } = useUserStore();

    const selectedInvitation = computed(() => {
      const invitationIndex = props.siteInvitations.findIndex(
        ({ id }) => id === props.invitationId
      );
      if (invitationIndex < 0) {
        return;
      }
      return props.siteInvitations[invitationIndex];
    });

    const formChecked = ref(false);
    const syncing = ref(false);

    const updateError = shallowRef(null);

    const newDisplayName = ref(selectedInvitation.value.displayName);
    const newDisplayNameValidity = shallowRef({});

    const newAvailableUses = ref(selectedInvitation.value.availableUses);
    const newAvailableUsesValidity = shallowRef({});

    const newExpiresAt = ref(new Date(selectedInvitation.value.expiresAt));
    const newExpiresAtInput = computed({
      get: () =>
        newExpiresAt.value?.toLocaleString("sv").slice(0, -3).replace(" ", "T"),
      set: (value) => (newExpiresAt.value = new Date(value)),
    });
    const newExpiresAtValidity = shallowRef({});
    const expiresAtMin = ref(
      new Date().toLocaleString("sv").slice(0, -3).replace(" ", "T")
    );

    const expiresAtMinInterval = setInterval(
      () =>
        (expiresAtMin.value = new Date()
          .toLocaleString("sv")
          .slice(0, -3)
          .replace(" ", "T")),
      1000
    );

    onUnmounted(() => clearInterval(expiresAtMinInterval));

    async function submitUpdateForm() {
      updateError.value = null;
      if (
        !newDisplayNameValidity.value.valid ||
        !newAvailableUsesValidity.value.valid ||
        !newExpiresAtValidity.value.valid
      ) {
        formChecked.value = true;
        return;
      }
      syncing.value = true;
      axios
        .put(
          `${
            import.meta.env.BASE_URL
              ? import.meta.env.BASE_URL
              : "http://localhost:443"
          }/api/v1/site-invitations`,
          {
            id: props.invitationId,
            displayName: newDisplayName.value,
            availableUses: newAvailableUses.value
              ? newAvailableUses.value
              : null,
            expiresAt: +newExpiresAt.value,
          },
          {
            timeout: 5000,
            headers: { authorization: await getIdToken() },
          }
        )
        .then(({ data }) => {
          delete data.site;
          Object.assign(selectedInvitation.value, data);
          context.emit("done");
        })
        .catch((error) => (updateError.value = error.message || error))
        .finally(() => (syncing.value = false));
    }

    return {
      formChecked,
      syncing,
      updateError,
      newDisplayName,
      newDisplayNameValidity,
      newAvailableUses,
      newAvailableUsesValidity,
      newExpiresAtInput,
      newExpiresAtValidity,
      expiresAtMin,
      submitUpdateForm,
      selectedInvitation,
    };
  },
};
</script>

<template>
  <span class="d-flex justify-content-between flex-wrap">
    <RouterLink
      :to="{ name: 'site-invitations' }"
      class="text-dark"
      title="Back"
    >
      <MDBIcon iconStyle="fas" icon="arrow-left" size="2xl" />
    </RouterLink>
    <h2 v-if="selectedInvitation" class="h3">
      Update {{ selectedInvitation?.displayName }}
    </h2>
    <template v-else>
      <h2 class="h3">Unknown Invitation</h2>
      <p class="w-100 text-center m-0">Please choose a different invitation.</p>
    </template>
  </span>
  <MDBRow
    v-if="selectedInvitation"
    tag="form"
    class="g-3 mb-3 needs-validation"
    :class="formChecked && 'was-validated'"
    novalidate
    @submit.prevent="submitUpdateForm"
  >
    <FormInput
      type="text"
      size="lg"
      v-model.trim="newDisplayName"
      label="New Display Name"
      invalidFeedback="Please provide a display name"
      @update:validity="newDisplayNameValidity = $event"
      required
      :formChecked="formChecked"
    />
    <FormInput
      type="number"
      size="lg"
      v-model.number="newAvailableUses"
      label="New Total Uses"
      invalidFeedback="Please provide a valid positive number"
      :min="1"
      @update:validity="newAvailableUsesValidity = $event"
      :formChecked="formChecked"
    />
    <FormInput
      type="datetime-local"
      size="lg"
      v-model.trim="newExpiresAtInput"
      label="Expiry Datetime"
      invalidFeedback="Please provide a valid future datetime"
      :min="expiresAtMin"
      required
      @update:validity="newExpiresAtValidity = $event"
      :formChecked="formChecked"
    />
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
        >Update Invitation</AjaxButton
      >
    </MDBCol>
  </MDBRow>
</template>
