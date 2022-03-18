<script setup>
import { MDBBtn, MDBIcon } from "mdb-vue-ui-kit";
</script>

<template>
  <MDBBtn class="ml-1 p-0 shadow-0 btn-edit text-warning" type="button">
    <MDBIcon iconStyle="fas" icon="pen" size="lg" />
  </MDBBtn>
</template>

<style scoped>
.btn-edit:is(:hover, :focus) {
  filter: brightness(0.75);
}
</style>
