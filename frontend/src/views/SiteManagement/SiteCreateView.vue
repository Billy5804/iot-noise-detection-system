<script>
import { ref, shallowRef } from "vue";
import { useUserStore } from "@/stores/UserStore";
import axios from "axios";
import { MDBRow, MDBCol } from "mdb-vue-ui-kit";
import AjaxButton from "@/components/AjaxButton.vue";
import FormInput from "@/components/FormInput.vue";
import siteRoles from "@/utilitys/SiteRoles";

export default {
  components: {
    MDBRow,
    MDBCol,
    AjaxButton,
    FormInput,
  },

  emits: ["done"],

  props: {
    sites: {
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

    async function submitCreateForm() {
      createError.value = null;
      if (!displayNameValidity.value.valid) {
        formChecked.value = true;
        return;
      }
      syncing.value = true;
      axios
        .post(
          "https://noise.alexroyle.com/api/v1/sites",
          { displayName: displayName.value },
          {
            timeout: 5000,
            headers: { authorization: `Bearer ${await getIdToken()}` },
          }
        )
        .then(({ data }) => {
          const siteId = data.id;
          delete data.id;
          data.role = siteRoles.OWNER;
          Object.assign(props.sites, { [siteId]: data });
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
      submitCreateForm,
    };
  },
};
</script>

<template>
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
        >Create Site</AjaxButton
      >
    </MDBCol>
  </MDBRow>
</template>
