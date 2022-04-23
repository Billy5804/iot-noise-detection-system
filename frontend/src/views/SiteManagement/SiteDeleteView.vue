<script>
import { ref, shallowRef, watch } from "vue";
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
    sites: {
      type: Object,
      required: true,
    },
    siteId: {
      type: String,
      required: true,
    },
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
      axios
        .delete("http://localhost:443/api/v1/sites", {
          timeout: 5000,
          headers: { authorization: await getIdToken() },
          params: { siteId: props.siteId },
        })
        .then(() => {
          delete props.sites[props.siteId];
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
  <h2>Are you sure you want to delete this site?</h2>
  <p>
    This action cannot be undone.<br />All data associated with this site will
    be lost.
  </p>
  <MDBRow
    tag="form"
    class="g-3 mb-3 needs-validation"
    :class="formChecked && 'was-validated'"
    novalidate
    @submit.prevent="submitDeleteForm"
  >
    <FormInput
      type="text"
      size="lg"
      v-model.trim="deleteConfirm"
      label="Confirm by typing 'DELETE'"
      invalidFeedback="Please confirm you want to delete this site"
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
        >Delete Site</AjaxButton
      >
    </MDBCol>
  </MDBRow>
</template>
