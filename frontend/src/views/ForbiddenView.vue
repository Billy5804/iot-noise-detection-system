<script setup>
import ErrorRedirect from "@/components/ErrorRedirect.vue";
</script>

<template>
  <ErrorRedirect :code="403" message="Forbidden" />
</template>
