<script setup>
import { MDBSpinner } from "mdb-vue-ui-kit";
</script>

<template>
  <main
    id="loading-view"
    class="position-absolute top-50 start-50 translate-middle text-center"
  >
    <MDBSpinner size="lg" color="primary" />
    <h6 class="text-primary">Loading...</h6>
  </main>
</template>
