<script>
import { ref, shallowRef, watch } from "vue";
import { useUserStore } from "@/stores/UserStore";
import { firebaseStorage } from "@/firebase/database";
import { ref as firebaseRef, deleteObject } from "firebase/storage";
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

    const deleteError = shallowRef(null);

    const deleteConfirm = ref("");
    const deleteConfirmValidity = shallowRef({});

    watch(
      deleteConfirm,
      (value) => (deleteConfirm.value = value.toUpperCase())
    );

    async function submitDeleteForm() {
      deleteError.value = null;
      if (!deleteConfirmValidity.value.valid) {
        formChecked.value = true;
        return;
      }
      syncing.value = true;

      const floorPlanRef = firebaseRef(
        firebaseStorage,
        `/floorPlans/${props.locationId}`
      );

      await deleteObject(floorPlanRef).catch(
        (error) =>
          (deleteError.value =
            error.code !== "storage/object-not-found"
              ? error.message || error
              : null)
      );

      if (deleteError.value) {
        syncing.value = false;
        return;
      }

      axios
        .delete(API_V1_URL + "locations", {
          timeout: 5000,
          headers: { authorization: await getIdToken() },
          params: { locationId: props.locationId },
        })
        .then(() => {
          delete props.locations[props.locationId];
          context.emit("done");
        })
        .catch((error) => (deleteError.value = error.message || error))
        .finally(() => (syncing.value = false));
    }

    return {
      formChecked,
      syncing,
      deleteError,
      deleteConfirm,
      deleteConfirmValidity,
      submitDeleteForm,
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
    @submit.prevent="submitDeleteForm"
  >
    <MDBCol col="12">
      <h2>Are you sure you want to delete this location?</h2>
      <p class="mb-0">
        This action cannot be undone.<br />All data associated data with this
        location will be lost.
      </p>
    </MDBCol>
    <FormInput
      type="text"
      size="lg"
      v-model.trim="deleteConfirm"
      label="Confirm by typing 'DELETE'"
      invalidFeedback="Please confirm you want to delete this location"
      @update:validity="deleteConfirmValidity = $event"
      required
      pattern="DELETE"
      :formChecked="formChecked"
    />
    <MDBCol md="12" v-if="deleteError">
      <div class="alert alert-danger text-center p-2 mb-0" role="alert">
        {{ deleteError }}
      </div>
    </MDBCol>
    <MDBCol md="12">
      <AjaxButton
        color="danger"
        type="submit"
        size="lg"
        class="w-100"
        :syncing="syncing"
        >Delete Location</AjaxButton
      >
    </MDBCol>
  </MDBRow>
</template>
