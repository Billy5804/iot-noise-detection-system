<script>
import { ref, onUnmounted } from "vue";
import { useRouter } from "vue-router";

export default {
  props: {
    code: {
      type: Number,
      required: true,
    },
    message: {
      type: String,
      required: true,
    },
    countdownStart: {
      type: Number,
      default: 10,
    },
    redirectRoute: {
      type: [Object, String],
      default: "/",
    },
  },

  setup: function (props) {
    const router = useRouter();

    const counter = ref(props.countdownStart);
    let countdownTimeout;

    function countdown() {
      if (counter.value > 0) {
        countdownTimeout = setTimeout(() => {
          counter.value--;
          countdown();
        }, 1000);
      } else if (counter.value == 0) {
        router.replace(props.redirectRoute);
      }
    }
    countdown();

    onUnmounted(() => clearTimeout(countdownTimeout));

    return { counter };
  },
};
</script>

<template>
  <main class="error-redirect-component container text-center">
    <h1 class="display-1 mb-3">
      <b>{{ code }}</b>
    </h1>
    <hr />
    <h2 class="display-3">{{ message }}</h2>
    <div
      v-if="counter >= 0"
      class="alert alert-dismissible px-5 alert-warning"
      role="alert"
    >
      Redirecting{{ counter ? ` in ${counter}` : "" }}...
      <button
        type="button"
        class="btn-close"
        aria-label="Close"
        @click="counter = -1"
      ></button>
    </div>
  </main>
</template>

<style>
.error-redirect-component {
  max-width: 650px;
}
</style>
