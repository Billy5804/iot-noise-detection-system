<script>
import { reactive, ref, shallowRef, computed } from "vue";
import { useUserStore } from "@/stores/UserStore";
import axios from "axios";
import { MDBCard } from "mdb-vue-ui-kit";
import AjaxButton from "@/components/AjaxButton.vue";

export default {
  components: { AjaxButton, MDBCard },

  emits: ["done"],

  props: {
    locationId: { type: String, required: true },
    siteId: { type: String, required: true },
    locations: { type: Object, required: true },
    siteDevices: { type: Object, required: true },
    locationDevices: { type: Object, required: true },
  },

  setup: function (props, { emit }) {
    const { getIdToken } = useUserStore();
    const syncing = ref(false);

    const manageError = shallowRef(null);

    const locationDeviceIds = computed(() =>
      Object.keys(props.locationDevices)
    );

    const chosenDeviceIds = reactive(
      locationDeviceIds.value.reduce((result, deviceId) => {
        result[deviceId] = true;
        return result;
      }, {})
    );

    async function submitManagementForm() {
      manageError.value = null;
      syncing.value = true;

      const { newDeviceIds, removeDeviceIds } = Object.entries(
        chosenDeviceIds
      ).reduce(
        (result, [deviceId, selected]) => {
          if (selected && locationDeviceIds.value.includes(deviceId)) {
            return result;
          }
          if (selected) {
            result.newDeviceIds.push(deviceId);
            return result;
          }
          if (locationDeviceIds.value.includes(deviceId)) {
            result.removeDeviceIds.push(deviceId);
          }
          return result;
        },
        { newDeviceIds: [], removeDeviceIds: [] }
      );

      const authorization = await getIdToken();

      const responses = await Promise.allSettled([
        ...newDeviceIds.map((deviceId) =>
          axios.post(
            API_V1_URL + "location-devices",
            { locationId: props.locationId, deviceId: deviceId },
            { timeout: 5000, headers: { authorization } }
          )
        ),
        ...removeDeviceIds.map((deviceId) =>
          axios.delete(API_V1_URL + "location-devices", {
            timeout: 5000,
            headers: { authorization },
            params: { locationId: props.locationId, deviceId: deviceId },
          })
        ),
      ]);

      const { failedAdditions, failedRemovals } = responses.reduce(
        (result, { status, reason, value }) => {
          if (reason?.config?.method === "post") {
            const deviceId = JSON.parse(reason.config.data || "{}").deviceId;
            result.failedAdditions.push(
              props.siteDevices[deviceId]?.displayName
            );
            return result;
          }
          if (reason?.config?.method === "delete") {
            const deviceId = reason.config.params.deviceId;
            result.failedRemovals.push(
              props.siteDevices[deviceId]?.displayName
            );
            return result;
          }
          if (value?.config?.method === "post") {
            const { deviceId, ...locationDevice } = value.data;
            Object.assign(props.locationDevices, {
              [deviceId]: {
                ...props.siteDevices[deviceId],
                ...locationDevice,
              },
            });
            return result;
          }
          if (value?.config?.method === "delete") {
            const deviceId = value.config.params.deviceId;
            delete props.locationDevices[deviceId];
            return result;
          }
          if (status === "rejected") {
            manageError.value === reason.message ||
              reason ||
              "An error occurred";
          }
          return result;
        },
        { failedAdditions: [], failedRemovals: [] }
      );

      manageError.value =
        (failedAdditions.length
          ? `An error occurred adding the devices: [${failedAdditions.join(
              ",\n"
            )}] `
          : "") +
        (failedRemovals.length
          ? `An error occurred removing the devices: [${failedRemovals}]`
          : "");

      syncing.value = false;

      if (!manageError.value) {
        emit("done");
      }
    }

    return {
      syncing,
      manageError,
      chosenDeviceIds,
      submitManagementForm,
    };
  },
};
</script>

<template>
  <form
    class="needs-validation"
    novalidate
    @submit.prevent="submitManagementForm"
  >
    <h2 class="h3">Manage inclusion of devices for this location.</h2>
    <MDBCard>
      <template v-for="(device, deviceId) in siteDevices" :key="deviceId">
        <label class="form-check ps-5 pe-4 py-2 text-truncate m-0">
          <input
            class="form-check-input"
            type="checkbox"
            :id="deviceId"
            v-model="chosenDeviceIds[deviceId]"
          />
          {{ device.displayName }}
        </label>
        <hr class="m-0" />
      </template>
    </MDBCard>
    <div
      v-if="manageError"
      class="alert alert-danger text-center p-2 mb-0 mt-3"
      role="alert"
    >
      {{ manageError }}
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
