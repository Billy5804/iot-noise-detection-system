<script>
import { reactive, ref, shallowRef, computed } from "vue";
import { useRouter } from "vue-router";
import { useUserStore } from "@/stores/UserStore";
import axios from "axios";
import AjaxButton from "@/components/AjaxButton.vue";
import FabricCanvas from "@/components/FabricCanvas.vue";

export default {
  components: { AjaxButton, FabricCanvas },

  emits: ["done"],

  props: {
    siteId: { type: String, required: true },
    locationId: { type: String, required: true },
    // locations: { type: Object, required: true },
    locationDevices: { type: Object, required: true },
    floorPlanURL: String,
  },

  setup: function (props, { emit }) {
    const { replace: routerReplace } = useRouter();
    if (!props.floorPlanURL) {
      routerReplace({
        name: "dashboard-location-floor-plan",
        params: { siteId: props.siteId, locationId: props.locationId },
      });
    }

    const { getIdToken } = useUserStore();
    const syncing = ref(false);

    const mapError = shallowRef(null);

    async function submitMapForm() {
      mapError.value = null;
      syncing.value = true;
    }

    return {
      syncing,
      mapError,
      submitMapForm,
    };
  },
};
</script>

<template>
  <form class="needs-validation" novalidate @submit.prevent="submitMapForm">
    <h2 class="h3">Position this locations devices on the floor plan.</h2>
    <FabricCanvas
      v-if="floorPlanURL"
      :floorPlanURL="floorPlanURL"
      :locationDevices="locationDevices"
      :editable="true"
    />
    <div
      v-if="mapError"
      class="alert alert-danger text-center p-2 mb-0 mt-3"
      role="alert"
    >
      {{ mapError }}
    </div>
    <AjaxButton
      color="primary"
      type="submit"
      size="lg"
      class="my-3 w-100"
      :syncing="syncing"
      >Submit</AjaxButton
    >
  </form>
</template>

<style scoped>
label {
  cursor: pointer;
}

label:first-of-type {
  border-top-left-radius: 0.5rem;
  border-top-right-radius: 0.5rem;
  border-bottom-left-radius: 0;
  border-bottom-right-radius: 0;
}

label:hover,
label:focus {
  color: #222222;
  background-color: #eeeeee;
}

label:last-of-type {
  border-top-left-radius: 0;
  border-top-right-radius: 0;
  border-bottom-left-radius: 0.5rem;
  border-bottom-right-radius: 0.5rem;
}

hr:last-of-type {
  display: none;
}
</style>