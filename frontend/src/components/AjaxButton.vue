<script setup>
import { MDBBtn, MDBSpinner } from "mdb-vue-ui-kit";

defineProps({
  syncing: {
    type: Boolean,
    default: false,
  },
  color: {
    type: String,
    default: "primary",
  },
});
</script>

<template>
  <MDBBtn class="ajax-button" :color="color">
    <span :class="syncing && `text-${color}`"><slot /></span>
    <span v-if="syncing" class="syncing">
      <MDBSpinner grow />
      <MDBSpinner grow />
      <MDBSpinner grow />
    </span>
  </MDBBtn>
</template>

<style>
.ajax-button {
  min-width: 100px;
}

.ajax-button .syncing {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 50px;
  display: flex;
  justify-content: space-between;
}

.ajax-button .spinner-grow {
  animation-duration: 1.5s;
  width: 0.75rem;
  height: 0.75rem;
}

.ajax-button .spinner-grow:nth-child(2) {
  animation-delay: 0.25s;
}

.ajax-button .spinner-grow:last-child {
  animation-delay: 0.5s;
}
</style>
