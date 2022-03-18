<script>
import { ref, reactive, shallowRef } from "vue";
import { useUserStore } from "@/stores/UserStore";
import { useRouter, RouterLink } from "vue-router";
import { MDBRow, MDBCol } from "mdb-vue-ui-kit";
import FormInput from "../components/FormInput.vue";
import AjaxButton from "../components/AjaxButton.vue";

export default {
  components: {
    MDBRow,
    MDBCol,
    FormInput,
    RouterLink,
    AjaxButton,
  },

  setup: function () {
    const user = useUserStore();
    const router = useRouter();

    const formChecked = ref(false);
    const syncing = ref(false);

    const formData = reactive({
      emailAddress: null,
      displayName: null,
      password: null,
      confirmPassword: null,
    });

    const formValidity = reactive({
      emailAddress: {},
      displayName: {},
      password: {},
      confirmPassword: {},
    });

    const activeItem = ref(null);

    const registerError = shallowRef(null);

    function isValid(values) {
      return values.every((value) => {
        if (value?.valid != null) {
          return value.valid === true;
        }
        return value && isValid(Object.values(value));
      });
    }

    function submitRegisterForm() {
      if (!isValid(Object.values(formValidity))) {
        formChecked.value = true;
        registerError.value = null;
        return;
      }
      syncing.value = true;
      user
        .register(formData)
        .then(() => router.push("/"))
        .catch((error) => (registerError.value = error.message || error))
        .finally(() => (syncing.value = false));
    }

    return {
      formChecked,
      formData,
      formValidity,
      activeItem,
      submitRegisterForm,
      registerError,
      syncing,
    };
  },
};
</script>

<template>
  <main id="register-view">
    <h1 class="text-center">Create an account</h1>
    <MDBRow
      tag="form"
      class="g-3 mb-3 needs-validation"
      :class="formChecked && 'was-validated'"
      novalidate
      @submit.prevent="submitRegisterForm"
    >
      <p class="text-center p-0 mb-0 mt-2">
        Let's get started! Registering only takes a minute.
      </p>
      <hr class="mb-0" />
      <FormInput
        type="text"
        size="lg"
        v-model.trim="formData.displayName"
        label="Display Name"
        placeholder="John Smith"
        invalidFeedback="Please provide a display name"
        @update:validity="formValidity.displayName = $event"
        required
        :formChecked="formChecked"
      />
      <FormInput
        type="email"
        size="lg"
        v-model.trim="formData.emailAddress"
        label="Email Address"
        placeholder="username@example.com"
        invalidFeedback="Please provide a valid email address"
        @update:validity="formValidity.emailAddress = $event"
        required
        :formChecked="formChecked"
      />
      <FormInput
        type="password"
        size="lg"
        v-model="formData.password"
        label="Password"
        title="Must be at least 6 characters"
        :invalidFeedback="
          !formData.password
            ? 'Please provide a password'
            : 'Your password must be at least 6 characters'
        "
        @update:validity="formValidity.password = $event"
        :minLength="6"
        required
        :formChecked="formChecked"
      />
      <FormInput
        type="password"
        size="lg"
        v-model="formData.confirmPassword"
        label="Confirm Password"
        :invalidFeedback="
          formData.confirmPassword
            ? 'Your password\'s do not match'
            : 'Please confirm your password'
        "
        @update:validity="formValidity.confirmPassword = $event"
        :pattern="RegExp.escape(formData.password)"
        required
        :formChecked="formChecked"
      />
      <MDBCol md="12" v-if="registerError">
        <div class="alert alert-danger text-center p-2 mb-0" role="alert">
          {{ registerError }}
        </div>
      </MDBCol>
      <MDBCol md="12">
        <AjaxButton
          color="primary"
          type="submit"
          size="lg"
          class="w-100"
          :syncing="syncing"
          >Sign up</AjaxButton
        >
      </MDBCol>
    </MDBRow>
    <p class="text-center">
      Have an account?
      <RouterLink to="/login">Sign in</RouterLink>
    </p>
  </main>
</template>

<style>
#register-view form {
  width: 100%;
  max-width: 450px;
  margin-left: auto;
  margin-right: auto;
}
</style>
