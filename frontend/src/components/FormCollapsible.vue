<script setup>
import { ref, watch, computed } from "vue";
import { MDBAccordion, MDBAccordionItem } from "mdb-vue-ui-kit";

const props = defineProps({
  size: {
    type: String,
    default: "md",
  },
  ...MDBAccordionItem.props,
  tag2: MDBAccordionItem.props.tag,
  ...MDBAccordion.props,
});

defineEmits(["update:modelValue"]);

const item = ref(null);

const unwatchItem = watch(item, (value) => {
  const accordionButton = value.$el.getElementsByTagName("BUTTON")[0];
  if (!accordionButton) {
    return;
  }
  accordionButton.type = "button";

  // Override button classes
  accordionButton.classList.add("form-control", `form-control-${props.size}`);
  value.buttonClassName = computed(() => {
    const isActive = props.modelValue == props.collapseId;
    return [
      "form-control",
      `form-control-${props.size}`,
      "accordion-button",
      !isActive && "collapsed",
    ];
  });
  unwatchItem();
});
</script>

<template>
  <MDBAccordion
    :modelValue="modelValue"
    @update:modelValue="$emit('update:modelValue', $event)"
    class="form-collapsible px-2"
    :tag="tag"
    :stayOpen="stayOpen"
    :flush="flush"
    :classes="classes"
  >
    <MDBAccordionItem
      :tag="tag2"
      :collapseId="collapseId"
      :headerTitle="headerTitle"
      :headerClasses="headerClasses"
      :bodyClasses="`row g-3 ${bodyClasses || ''}`"
      :itemClasses="`rounded ${itemClasses || ''}`"
      ref="item"
    >
      <slot />
    </MDBAccordionItem>
  </MDBAccordion>
</template>

<style>
.form-collapsible {
  padding: 0;
}

.form-collapsible .accordion-item {
  border: 1px solid;
  border-color: #bdbdbd;
  box-sizing: border-box;
}

.form-collapsible .accordion-button {
  box-sizing: border-box;
  padding: calc(0.375rem - 1.75px) calc(0.75rem - 1px);
}

.form-collapsible .accordion-button:not(.collapsed) {
  border-bottom-left-radius: 0;
  border-bottom-right-radius: 0;
}
</style>
