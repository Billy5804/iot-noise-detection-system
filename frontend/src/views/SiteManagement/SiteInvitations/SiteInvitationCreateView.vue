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
    siteId: {
      type: String,
      required: true,
    },
    siteInvitations: {
      type: Object,
      required: true,
    },
  },

  setup: function (props, context) {
    const { getIdToken } = useUserStore();

    const formChecked = ref(false);
    const syncing = ref(false);

    const createError = shallowRef(null);

    const displayName = ref("");
    const displayNameValidity = shallowRef({});

    const availableUses = ref(null);
    const availableUsesValidity = shallowRef({});

    const expiresAt = ref(new Date());
    expiresAt.value.setUTCDate(expiresAt.value.getUTCDate() + 1);
    const expiresAtInput = computed({
      get: () =>
        expiresAt.value?.toLocaleString("sv").slice(0, -3).replace(" ", "T"),
      set: (value) => (expiresAt.value = new Date(value)),
    });
    const expiresAtValidity = shallowRef({});
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

    async function submitCreateForm() {
      createError.value = null;
      if (
        !displayNameValidity.value.valid ||
        !availableUsesValidity.value.valid ||
        !expiresAtValidity.value.valid
      ) {
        formChecked.value = true;
        return;
      }
      syncing.value = true;
      axios
        .post(
          API_V1_URL + "site-invitations",
          {
            siteId: props.siteId,
            displayName: displayName.value,
            availableUses: availableUses.value ? availableUses.value : null,
            expiresAt: +expiresAt.value,
          },
          {
            timeout: 5000,
            headers: { authorization: await getIdToken() },
          }
        )
        .then(({ data }) => {
          delete data.site;
          props.siteInvitations.push(data); // eslint-disable-line vue/no-mutating-props
          context.emit("done");
        })
        .catch((error) => (createError.value = error.message || error))
        .finally(() => (syncing.value = false));
    }

    return {
      formChecked,
      syncing,
      createError,
      displayName,
      displayNameValidity,
      availableUses,
      availableUsesValidity,
      expiresAtInput,
      expiresAtValidity,
      expiresAtMin,
      submitCreateForm,
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
    <h2 class="h3">Create Invitation</h2>
  </span>
  <MDBRow
    tag="form"
    class="g-3 mb-3 needs-validation"
    :class="formChecked && 'was-validated'"
    novalidate
    @submit.prevent="submitCreateForm"
  >
    <FormInput
      type="text"
      size="lg"
      v-model.trim="displayName"
      label="Display Name"
      invalidFeedback="Please provide a display name"
      @update:validity="displayNameValidity = $event"
      required
      :formChecked="formChecked"
    />
    <FormInput
      type="number"
      size="lg"
      v-model.number="availableUses"
      label="Total Uses"
      invalidFeedback="Please provide a valid positive number"
      :min="1"
      @update:validity="availableUsesValidity = $event"
      :formChecked="formChecked"
    />
    <FormInput
      type="datetime-local"
      size="lg"
      v-model.trim="expiresAtInput"
      label="Expiry Datetime"
      invalidFeedback="Please provide a valid future datetime"
      :min="expiresAtMin"
      required
      @update:validity="expiresAtValidity = $event"
      :formChecked="formChecked"
    />
    <MDBCol md="12" v-if="createError">
      <div class="alert alert-danger text-center p-2 mb-0" role="alert">
        {{ createError }}
      </div>
    </MDBCol>
    <MDBCol md="12">
      <AjaxButton
        color="success"
        type="submit"
        size="lg"
        class="w-100"
        :syncing="syncing"
        >Create Invitation</AjaxButton
      >
    </MDBCol>
  </MDBRow>
</template>
