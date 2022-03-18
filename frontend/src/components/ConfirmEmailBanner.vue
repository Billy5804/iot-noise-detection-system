<script>
import { ref, onUnmounted } from "vue";
import { useUserStore } from "@/stores/UserStore";
import toastr from "toastr";
import AjaxButton from "./AjaxButton.vue";

export default {
  components: { AjaxButton },

  setup: function () {
    const user = useUserStore();

    const syncing = ref(false);

    const nextSendTime = ref(null);

    let reduceCountdownTimeout;

    function reduceCountdown() {
      if (nextSendTime.value > 0) {
        reduceCountdownTimeout = setTimeout(reduceCountdown, 1000);
      }
      nextSendTime.value--;
    }

    function sendVerificationEmail() {
      syncing.value = true;
      user
        .sendEmailVerification()
        .then(() => (syncing.value = false))
        .catch((error) => {
          syncing.value = false;
          toastr.error(error.message || error);
        });
      nextSendTime.value = 60;
      reduceCountdown();
    }

    onUnmounted(() => {
      clearTimeout(reduceCountdownTimeout);
    });

    return { syncing, nextSendTime, sendVerificationEmail };
  },
};
</script>
<template>
  <div class="alert alert-warning text-center" role="alert">
    <span class="d-inline-block"
      >Your account will be limited until you have verified your
      email.&nbsp;</span
    >
    <span class="d-inline-block">
      Can't find your verification email?
      <AjaxButton
        class="pe-auto"
        size="sm"
        color="dark"
        @click="sendVerificationEmail"
        :syncing="syncing"
        :disabled="nextSendTime > 0"
        :title="
          nextSendTime > 0
            ? `You can send another email in ${nextSendTime} seconds`
            : ''
        "
      >
        Resend email
      </AjaxButton>
    </span>
  </div>
</template>
