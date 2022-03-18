<script setup>
import { ref, shallowRef } from "vue";
import { useUserStore } from "@/stores/UserStore";
import { MDBRow, MDBCol } from "mdb-vue-ui-kit";
import AjaxButton from "../components/AjaxButton.vue";
import FormInput from "../components/FormInput.vue";
const { setPassword } = useUserStore();

const emit = defineEmits(["updated"]);

const formChecked = ref(false);
const syncing = ref(false);

const updateError = shallowRef(null);

const newPassword = ref("");
const newPasswordValidity = shallowRef({});

const newPasswordConfirm = ref("");
const newPasswordConfirmValidity = ref("");

const currentPassword = ref("");
const currentPasswordValidity = shallowRef({});

function submitPasswordForm() {
  updateError.value = null;
  if (
    !(
      newPasswordValidity.value.valid &&
      newPasswordConfirmValidity.value.valid &&
      currentPasswordValidity.value.valid
    )
  ) {
    formChecked.value = true;
    return;
  }
  syncing.value = true;
  setPassword(newPassword.value, currentPassword.value)
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
    @submit.prevent="submitPasswordForm"
  >
    <FormInput
      type="password"
      size="lg"
      v-model="currentPassword"
      label="Current Password"
      invalidFeedback="Please provide your current password"
      @update:validity="currentPasswordValidity = $event"
      required
      :formChecked="formChecked"
    />
    <FormInput
      type="password"
      size="lg"
      v-model="newPassword"
      label="New Password"
      title="Must be at least 6 characters"
      :invalidFeedback="
        !newPassword
          ? 'Please provide a new password'
          : 'Your password must be at least 6 characters'
      "
      @update:validity="newPasswordValidity = $event"
      :minLength="6"
      required
      :formChecked="formChecked"
    />
    <FormInput
      type="password"
      size="lg"
      v-model="newPasswordConfirm"
      label="Confirm New Password"
      :invalidFeedback="
        newPasswordConfirm
          ? 'Your password\'s do not match'
          : 'Please confirm your password'
      "
      @update:validity="newPasswordConfirmValidity = $event"
      :pattern="RegExp.escape(newPassword)"
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
        >Update Password</AjaxButton
      >
    </MDBCol>
  </MDBRow>
</template>
