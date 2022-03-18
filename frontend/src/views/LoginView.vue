<script>
import { ref, shallowRef } from "vue";
import { useUserStore } from "@/stores/UserStore";
import { useRouter, RouterLink } from "vue-router";
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
    const router = useRouter();

    const formChecked = ref(false);
    const syncing = ref(false);

    const emailAddress = ref("");
    const emailAddressValidity = shallowRef({});

    const password = ref("");
    const passwordValidity = shallowRef({});

    const loginError = shallowRef(null);

    function submitLoginForm() {
      loginError.value = null;
      if (!(emailAddressValidity.value.valid && passwordValidity.value.valid)) {
        formChecked.value = true;
        return;
      }
      syncing.value = true;
      user
        .login(emailAddress.value, password.value)
        .then(() => router.push("/"))
        .catch((error) => (loginError.value = error.message || error))
        .finally(() => (syncing.value = false));
    }

    return {
      formChecked,
      emailAddress,
      emailAddressValidity,
      password,
      passwordValidity,
      loginError,
      submitLoginForm,
      syncing,
    };
  },
};
</script>

<template>
  <main id="login-view">
    <h1 class="text-center">Sign in to your account</h1>
    <MDBRow
      tag="form"
      class="g-3 mb-3 needs-validation"
      :class="formChecked && 'was-validated'"
      novalidate
      @submit.prevent="submitLoginForm"
    >
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
      <FormInput
        type="password"
        size="lg"
        v-model="password"
        label="Password"
        invalidFeedback="Please provide your password"
        @update:validity="passwordValidity = $event"
        required
        :formChecked="formChecked"
      />
      <MDBCol md="12" v-if="loginError">
        <div class="alert alert-danger text-center p-2 mb-0" role="alert">
          {{ loginError }}
        </div>
      </MDBCol>
      <MDBCol md="12">
        <AjaxButton
          color="secondary"
          type="submit"
          size="lg"
          class="w-100"
          :syncing="syncing"
          >Sign in</AjaxButton
        >
      </MDBCol>
    </MDBRow>
    <p class="text-center">
      <RouterLink to="/login/forgot">Forgot your password?</RouterLink>
    </p>
    <p class="text-center">
      Don't have an account?
      <RouterLink to="/register">Sign up</RouterLink>
    </p>
  </main>
</template>

<style>
#login-view form {
  width: 100%;
  max-width: 450px;
  margin-left: auto;
  margin-right: auto;
}
</style>
