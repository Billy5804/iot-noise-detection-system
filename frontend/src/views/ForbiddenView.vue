<script setup>
import ErrorRedirect from "@/components/ErrorRedirect.vue";

defineProps({
  redirectRoute: {
    type: [Object, String],
  },
});
</script>

<template>
  <ErrorRedirect
    :code="403"
    message="Forbidden"
    :redirectRoute="redirectRoute"
  />
</template>
