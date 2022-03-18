<script setup>
import { ref, shallowRef } from "vue";
import { useUserStore } from "@/stores/UserStore";
import { MDBRow, MDBCol } from "mdb-vue-ui-kit";
import AjaxButton from "../components/AjaxButton.vue";
import FormInput from "../components/FormInput.vue";
const { getEmailAddress, setEmailAddress } = useUserStore();

const emit = defineEmits(["updated"]);

const formChecked = ref(false);
const syncing = ref(false);

const updateError = shallowRef(null);

const newEmailAddress = ref("");
const newEmailAddressValidity = shallowRef({});

const currentPassword = ref("");
const currentPasswordValidity = shallowRef({});

function submitEmailForm() {
  updateError.value = null;
  if (
    !(
      newEmailAddressValidity.value.valid && currentPasswordValidity.value.valid
    )
  ) {
    formChecked.value = true;
    return;
  }
  syncing.value = true;
  setEmailAddress(newEmailAddress.value, currentPassword.value)
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
    @submit.prevent="submitEmailForm"
  >
    <FormInput
      type="email"
      size="lg"
      v-model.trim="newEmailAddress"
      label="New Email Address"
      :placeholder="getEmailAddress"
      invalidFeedback="Please provide your new email address"
      @update:validity="newEmailAddressValidity = $event"
      required
      :formChecked="formChecked"
    />
    <FormInput
      type="password"
      size="lg"
      v-model="currentPassword"
      label="Account Password"
      invalidFeedback="Please provide your current password"
      @update:validity="currentPasswordValidity = $event"
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
        >Update Email</AjaxButton
      >
    </MDBCol>
  </MDBRow>
</template>
