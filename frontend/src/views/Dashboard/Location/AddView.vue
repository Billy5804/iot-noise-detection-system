<script>
import { ref, shallowRef } from "vue";
import { useRouter } from "vue-router";
import { useUserStore } from "@/stores/UserStore";
import axios from "axios";
import { MDBRow, MDBCol } from "mdb-vue-ui-kit";
import AjaxButton from "@/components/AjaxButton.vue";
import FormInput from "@/components/FormInput.vue";

export default {
  components: {
    MDBRow,
    MDBCol,
    AjaxButton,
    FormInput,
  },

  emits: ["done"],

  props: {
    siteId: { type: String, required: true },
    locations: { type: Object, required: true },
  },

  setup: function (props, context) {
    const { replace: routerReplace } = useRouter();
    const { getIdToken } = useUserStore();

    const formChecked = ref(false);
    const syncing = ref(false);

    const addError = shallowRef(null);

    const displayName = ref("");
    const displayNameValidity = shallowRef({});

    async function submitAddForm() {
      addError.value = null;
      if (!displayNameValidity.value.valid) {
        formChecked.value = true;
        return;
      }
      syncing.value = true;
      axios
        .post(
          `${
            import.meta.env.BASE_URL
              ? import.meta.env.BASE_URL
              : "http://localhost:443"
          }/api/v1/locations`,
          {
            siteId: props.siteId,
            displayName: displayName.value,
          },
          {
            timeout: 5000,
            headers: { authorization: await getIdToken() },
          }
        )
        .then(async ({ data }) => {
          const { id: locationId, ...location } = data;
          Object.assign(props.locations, { [locationId]: location });
          await routerReplace({
            name: "dashboard-location-add",
            params: { siteId: props.siteId, locationId },
          });
          context.emit("done");
        })
        .catch((error) => (addError.value = error.message || error))
        .finally(() => (syncing.value = false));
    }

    return {
      formChecked,
      syncing,
      addError,
      displayName,
      displayNameValidity,
      submitAddForm,
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
    @submit.prevent="submitAddForm"
  >
    <FormInput
      type="text"
      size="lg"
      v-model.trim="displayName"
      label="Location Name"
      invalidFeedback="Please provide a location name"
      @update:validity="displayNameValidity = $event"
      required
      :formChecked="formChecked"
      counter
      :maxlength="32"
      class="mb-3"
    />
    <MDBCol md="12" v-if="addError">
      <div class="alert alert-danger text-center p-2 mb-0" role="alert">
        {{ addError }}
      </div>
    </MDBCol>
    <MDBCol md="12">
      <AjaxButton
        color="success"
        type="submit"
        size="lg"
        class="w-100"
        :syncing="syncing"
        >Add Location</AjaxButton
      >
    </MDBCol>
  </MDBRow>
</template>
