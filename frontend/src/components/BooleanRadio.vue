<script setup>
import { computed } from "vue";
import { MDBRadio } from "mdb-vue-ui-kit";

const props = defineProps({
  modelValue: {},
});

const emits = defineEmits(["update:modelValue"]);

const value = computed({
  get: () => (props.modelValue == null ? null : String(props.modelValue)),
  set: (value) => {
    if (value === "true") {
      emits("update:modelValue", true);
      return;
    }
    if (value === "false") {
      emits("update:modelValue", false);
      return;
    }
    emits("update:modelValue", null);
  },
});
</script>

<template>
  <MDBRadio v-model="value" />
</template>
