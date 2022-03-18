<script setup>
import { ref, shallowRef } from "vue";
import { useUserStore } from "@/stores/UserStore";
import { MDBRow, MDBCol } from "mdb-vue-ui-kit";
import AjaxButton from "../components/AjaxButton.vue";
import FormInput from "../components/FormInput.vue";
const { getDisplayName, setDisplayName } = useUserStore();

const emit = defineEmits(["updated"]);

const formChecked = ref(false);
const syncing = ref(false);

const updateError = shallowRef(null);

const newDisplayName = ref("");
const newDisplayNameValidity = shallowRef({});

function submitDisplayNameForm() {
  updateError.value = null;
  if (!newDisplayNameValidity.value.valid) {
    formChecked.value = true;
    return;
  }
  syncing.value = true;
  setDisplayName(newDisplayName.value)
    .then(() => emit("updated"))
    .catch((error) => (updateError.value = error.message || error))
    .finally(() => (syncing.value = false));
}
</script>

<template>
  <MDBRow
    tag="form"
    class="g-3 mb-3 needs-validation"
    :class="formChecked && 'was-validated'"
    novalidate
    @submit.prevent="submitDisplayNameForm"
  >
    <FormInput
      type="text"
      size="lg"
      v-model.trim="newDisplayName"
      label="New Display Name"
      :placeholder="getDisplayName"
      invalidFeedback="Please provide your new display name"
      @update:validity="newDisplayNameValidity = $event"
      required
      :formChecked="formChecked"
    />
    <MDBCol md="12" v-if="updateError">
      <div class="alert alert-danger text-center p-2 mb-0" role="alert">
        {{ updateError }}
      </div>
    </MDBCol>
    <MDBCol md="12">
      <AjaxButton
        color="primary"
        type="submit"
        size="lg"
        class="w-100"
        :syncing="syncing"
        >Update Display Name</AjaxButton
      >
    </MDBCol>
  </MDBRow>
</template>
