<script>
import { ref, shallowRef } from "vue";
import { useRouter } from "vue-router";
import { useUserStore } from "@/stores/UserStore";
import axios from "axios";
import AjaxButton from "@/components/AjaxButton.vue";
import LocationMapping from "@/components/LocationMapping.vue";

export default {
  components: { AjaxButton, LocationMapping },

  emits: ["done"],

  props: {
    siteId: { type: String, required: true },
    locationId: { type: String, required: true },
    locationDevices: { type: Object, required: true },
    floorPlanURL: String,
    siteDevices: { type: Object, required: true },
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

    const deviceMappings = {};

    function setDeviceMapping({ deviceId, ...positions }) {
      deviceMappings[deviceId] = positions;
    }

    async function submitMapForm() {
      mapError.value = null;
      syncing.value = true;
      if (!Object.keys(deviceMappings).length) {
        emit("done");
      }

      const authorization = await getIdToken();

      const responses = await Promise.allSettled(
        Object.entries(deviceMappings).map(
          ([deviceId, { positionX, positionY }]) =>
            axios.put(
              "http://localhost:443/api/v1/location-devices",
              { locationId: props.locationId, deviceId, positionX, positionY },
              { timeout: 5000, headers: { authorization } }
            )
        )
      );

      const failedResponses = responses.reduce(
        (result, { status, reason, value }) => {
          if (reason?.config?.data) {
            const deviceId = JSON.parse(reason.config.data || "{}").deviceId;
            result.push(props.siteDevices[deviceId]?.displayName);
            return result;
          }
          if (value || status === "fulfilled") {
            const { deviceId, ...locationDevice } = value.data;
            Object.assign(props.locationDevices, {
              [deviceId]: { ...props.siteDevices[deviceId], ...locationDevice },
            });
            return result;
          }
          if (reason || status === "rejected") {
            mapError.value === reason.message || reason || "An error occurred";
          }
          return result;
        },
        []
      );

      mapError.value = failedResponses.length
        ? `An error occurred mapping the devices: [${failedResponses.join(
            ",\n"
          )}]`
        : "";

      syncing.value = false;

      if (!mapError.value) {
        emit("done");
      }
    }

    return {
      syncing,
      mapError,
      submitMapForm,
      setDeviceMapping,
    };
  },
};
</script>

<template>
  <form class="needs-validation" novalidate @submit.prevent="submitMapForm">
    <h2 class="h3">Position this locations devices on the floor plan.</h2>
    <LocationMapping
      v-if="floorPlanURL"
      :floorPlanURL="floorPlanURL"
      :locationDevices="locationDevices"
      :editable="true"
      @deviceMoved="setDeviceMapping"
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
