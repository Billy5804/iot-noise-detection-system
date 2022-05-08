<script>
import { ref, shallowRef } from "vue";
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
    locationId: { type: String, required: true },
    locations: { type: Object, required: true },
  },

  setup: function (props, context) {
    const { getIdToken } = useUserStore();

    const formChecked = ref(false);
    const syncing = ref(false);

    const updateError = shallowRef(null);

    const newDisplayName = ref("");
    const newDisplayNameValidity = shallowRef({});

    async function submitUpdateForm() {
      updateError.value = null;
      if (!newDisplayNameValidity.value.valid) {
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
          }/api/v1/locations`,
          {
            id: props.locationId,
            displayName: newDisplayName.value,
          },
          {
            timeout: 5000,
            headers: { authorization: await getIdToken() },
          }
        )
        .then(({ data }) => {
          const { id: locationId, ...location } = data;
          Object.assign(props.locations, { [locationId]: location });
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
      submitUpdateForm,
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
    @submit.prevent="submitUpdateForm"
  >
    <FormInput
      type="text"
      size="lg"
      v-model.trim="newDisplayName"
      label="New Location Name"
      :placeholder="locations[locationId].displayName"
      invalidFeedback="Please provide a new location name"
      @update:validity="newDisplayNameValidity = $event"
      required
      counter
      :maxlength="32"
      class="mb-3"
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
        >Update Location</AjaxButton
      >
    </MDBCol>
  </MDBRow>
</template>
