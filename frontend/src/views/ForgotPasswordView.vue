<script>
import { ref, shallowRef } from "vue";
import { useUserStore } from "@/stores/UserStore";
import { RouterLink } from "vue-router";
import { MDBRow, MDBCol } from "mdb-vue-ui-kit";
import FormInput from "@/components/FormInput.vue";
import AjaxButton from "../components/AjaxButton.vue";

export default {
  components: {
    RouterLink,
    MDBRow,
    FormInput,
    MDBCol,
    AjaxButton,
  },

  setup: function () {
    const user = useUserStore();

    const formChecked = ref(false);
    const syncing = ref(false);

    const emailAddress = ref("");
    const emailAddressValidity = shallowRef({});

    const resetError = shallowRef(null);

    const passwordReset = ref(false);

    function submitResetForm() {
      resetError.value = null;
      if (!emailAddressValidity.value.valid) {
        formChecked.value = true;
        return;
      }
      syncing.value = true;
      user
        .resetPassword(emailAddress.value)
        .then(() => (passwordReset.value = true))
        .catch((error) => (resetError.value = error.message || error))
        .finally(() => (syncing.value = false));
    }
    return {
      formChecked,
      emailAddress,
      emailAddressValidity,
      resetError,
      submitResetForm,
      syncing,
      passwordReset,
    };
  },
};
</script>

<template>
  <main id="forgot-view" class="text-center">
    <h1 class="text-center">Recover password</h1>
    <template v-if="!passwordReset">
      <MDBRow
        tag="form"
        class="g-3 mb-3 needs-validation"
        :class="formChecked && 'was-validated'"
        novalidate
        @submit.prevent="submitResetForm"
      >
        <p class="text-center p-0 mb-0 mt-2">
          Enter your email address below to reset the password for your account.
        </p>
        <hr class="mb-0" />
        <FormInput
          type="email"
          size="lg"
          v-model.trim="emailAddress"
          label="Email Address"
          placeholder="username@example.com"
          invalidFeedback="Please provide your email address"
          @update:validity="emailAddressValidity = $event"
          required
          :formChecked="formChecked"
        />
        <MDBCol md="12" v-if="resetError">
          <div class="alert alert-danger text-center p-2 mb-0" role="alert">
            {{ resetError }}
          </div>
        </MDBCol>
        <MDBCol md="12">
          <AjaxButton
            color="secondary"
            type="submit"
            size="lg"
            class="w-100"
            :syncing="syncing"
            >Send Reset Link</AjaxButton
          >
        </MDBCol>
      </MDBRow>
      <p class="text-center">
        <RouterLink to="/login">Back to login</RouterLink>
      </p>
    </template>
    <template v-else>
      <p class="text-center p-0 mb-0 mt-2">
        We've sent you a link to update your password.
      </p>
      <hr class="mb-0" />
      <p class="text-center p-0">
        If the email doesn’t arrive after a few minutes, try checking your spam
        folder. If you still can’t find it, please try again.
      </p>
      <RouterLink to="/login" class="btn ripple-surface btn-outline-secondary"
        >Back to login</RouterLink
      >
    </template>
  </main>
</template>

<style>
#forgot-view form {
  width: 100%;
  max-width: 450px;
  margin-left: auto;
  margin-right: auto;
}
</style>
