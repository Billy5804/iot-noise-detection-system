<script setup>
import ErrorRedirect from "@/components/ErrorRedirect.vue";
</script>

<template>
  <ErrorRedirect :code="401" message="Unauthorised" />
</template>
