<script>
import { ref, shallowRef, computed } from "vue";
import { MDBRow, MDBCol, MDBFile } from "mdb-vue-ui-kit";
import AjaxButton from "@/components/AjaxButton.vue";
import { svgOptimiseAndStore } from "@/utilitys/SVGOptimiseAndStore";

export default {
  components: {
    MDBRow,
    MDBCol,
    AjaxButton,
    MDBFile,
  },

  emits: ["done"],

  props: {
    locationId: { type: String, required: true },
    locations: { type: Object, required: true },
  },

  setup: function (props, { emit }) {
    const currentLocation = computed(() => props.locations[props.locationId]);

    const formChecked = ref(false);
    const syncing = computed({
      get: () => currentLocation.value.floorPlan === "loading",
      set: (loading) =>
        (currentLocation.value.floorPlan = loading ? "loading" : null),
    });

    const updateError = shallowRef(null);

    const newFloorPlan = ref(null);

    async function submitUpdateForm() {
      updateError.value = null;
      if (!newFloorPlan.value && newFloorPlan.value[0]) {
        formChecked.value = true;
        return;
      }
      syncing.value = true;

      svgOptimiseAndStore(
        newFloorPlan.value[0],
        `/floorPlans/${props.locationId}`
      )
        .then((blobURL) => {
          currentLocation.value.floorPlan = blobURL;
          emit("done");
        })
        .catch((error) => (updateError.value = error.message || error))
        .finally(() => (syncing.value = false));
    }

    return {
      formChecked,
      syncing,
      updateError,
      newFloorPlan,
      submitUpdateForm,
    };
  },
};
</script>

<template>
  <MDBRow
    tag="form"
    class="g-3 mb-3 needs-validation"
    :class="formChecked && 'was-validated'"
    novalidate
    @submit.prevent="submitUpdateForm"
  >
    <MDBCol>
      <MDBFile
        v-model="newFloorPlan"
        label="Choose SVG floor plan"
        accept="image/svg+xml"
        invalidFeedback="Please provide a new floor plan"
        class="mb-3"
        required
      />
    </MDBCol>
    <MDBCol md="12" v-if="updateError">
      <div class="alert alert-danger text-center p-2 mb-0" role="alert">
        {{ updateError }}
      </div>
    </MDBCol>
    <MDBCol md="12">
      <AjaxButton
        color="warning"
        type="submit"
        size="lg"
        class="w-100"
        :syncing="syncing"
        >Update Floor Plan</AjaxButton
      >
    </MDBCol>
  </MDBRow>
</template>
