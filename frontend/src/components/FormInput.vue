<script setup>
import { ref, watch } from "vue";
import { MDBCol, MDBInput, MDBIcon } from "mdb-vue-ui-kit";

defineProps({
  modelValue: {},
  type: {
    type: String,
    default: "text",
  },
  size: {
    type: String,
    default: "md",
  },
  placeholder: String,
  required: {
    type: Boolean,
    default: false,
  },
  label: String,
  invalidFeedback: String,
  pattern: String,
  formChecked: {
    type: Boolean,
    default: false,
  },
  minlength: Number,
  maxlength: Number,
  counter: Boolean,
  min: Number,
  md: {
    type: String,
    default: "12",
  },
  ariaLabel: String,
  inputClass: String,
});

const emit = defineEmits(["update:modelValue", "update:validity"]);

const input = ref(null);

const unwatchInput = watch(input, (value) => {
  const inputRef = value.$refs.inputRef;
  if (!inputRef) {
    return;
  }
  emit("update:validity", inputRef.validity);
  unwatchInput();
});

const handleValidation = ({ target }) => {
  emit("update:validity", target.validity);
};
</script>

<template>
  <MDBCol :md="md">
    <slot />
    <MDBInput
      ref="input"
      :type="type"
      :size="size"
      :modelValue="modelValue"
      @update:modelValue="$emit('update:modelValue', $event)"
      :label="required && modelValue ? `${label} *` : label"
      :aria-label="ariaLabel || `${label.toLowerCase()} field`"
      :autocomplete="label.toLowerCase().replace(' ', '-')"
      :placeholder="placeholder"
      :class="`${inputClass} ${
        required && !modelValue && 'form-icon-trailing'
      }`"
      :invalidFeedback="invalidFeedback"
      :required="required"
      :minlength="minlength"
      :maxlength="maxlength"
      :counter="!!counter"
      :min="min"
      :pattern="pattern || RegExp.escape(modelValue)"
      @input="handleValidation"
      :inputGroup="!!$slots.append"
    >
      <MDBIcon
        v-if="required && !modelValue"
        class="trailing"
        :class="formChecked && 'text-danger'"
        >🞰</MDBIcon
      >
      <slot name="append" />
    </MDBInput>
    <slot name="bottom" />
  </MDBCol>
</template>

<style>
input.form-control:valid {
  margin-bottom: 0 !important;
}

input.form-control:focus ~ i.trailing {
  color: var(--mdb-primary);
}
</style>
