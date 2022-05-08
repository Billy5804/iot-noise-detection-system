<script>
import axios from "axios";
import { useUserStore } from "@/stores/UserStore";
import { useUserSitesStore } from "@/stores/UserSitesStore";
import { onBeforeMount, ref, computed, onUnmounted, watch } from "vue";
import { RouterView, useRouter } from "vue-router";
import SensorUnits from "@/utilitys/SensorUnits";
import WebSocket from "@/utilitys/WebSocket";
import DeviceTypes from "@/utilitys/DeviceTypes";

export default {
  components: { RouterView },

  props: { siteId: { type: String, required: true } },

  setup: function (props) {
    const { getIdToken } = useUserStore();
    const sitesStore = useUserSitesStore();
    const router = useRouter();

    const loading = ref(true);
    const loadingError = ref(null);

    const sites = computed(() => sitesStore.authorisedSites);

    const siteDevicesAPIPath = `${
      import.meta.env.BASE_URL
        ? import.meta.env.BASE_URL
        : "http://localhost:443"
    }/api/v1/site-devices`;
    const siteDevices = ref(null);

    const currentSite = computed(
      () => sites.value && sites.value[props.siteId]
    );

    const locationsAPIPath = `${
      import.meta.env.BASE_URL
        ? import.meta.env.BASE_URL
        : "http://localhost:443"
    }/api/v1/locations`;
    const locations = ref(null);

    async function setupLocations() {
      const locationsResponse = await axios
        .get(locationsAPIPath, {
          timeout: 5000,
          headers: { authorization: await getIdToken() },
          params: { siteId: props.siteId },
        })
        .catch((error) => (loadingError.value = error.message || error));

      locations.value =
        locationsResponse?.data?.reduce((result, { id, ...location }) => {
          result[id] = location;
          return result;
        }, {}) || {};
    }

    async function setupDevices() {
      const siteDevicesResponse = await axios
        .get(siteDevicesAPIPath, {
          timeout: 5000,
          headers: { authorization: await getIdToken() },
          params: { siteId: props.siteId },
        })
        .catch((error) => (loadingError.value = error.message || error));

      siteDevices.value =
        siteDevicesResponse?.data?.reduce(
          (result, { id, sensors, type, ...device }) => {
            device.type = DeviceTypes[type];
            device.sensors = sensors.map(({ unit, ...sensor }) => ({
              ...sensor,
              unit: SensorUnits[unit],
            }));
            result[id] = device;
            return result;
          },
          {}
        ) || {};
    }

    function onDeviceUpdateReceived({ body }) {
      const {
        id: deviceId,
        lastBeatTime,
        sensors,
        rssi,
      } = Object.fromEntries(
        Object.entries(JSON.parse(body)).filter(([, value]) => value != null)
      );
      siteDevices.value[deviceId].lastBeatTime = +new Date(lastBeatTime);
      siteDevices.value[deviceId].rssi = rssi;
      siteDevices.value[deviceId].sensors.forEach(
        (sensor, index) => (sensor.latestValue = sensors[index].latestValue)
      );
    }

    const canSubscribe = ref(false);

    watch(
      [currentSite, canSubscribe],
      async ([currentSite, canSubscribe], [previousSite]) => {
        if (currentSite !== previousSite) {
          loading.value = true;
          await setupDevices();
          loading.value = false;
        }
        if (canSubscribe && currentSite) {
          WebSocket.subscribe(
            `/message/site-device/${props.siteId}`,
            onDeviceUpdateReceived
          );
        }
      }
    );

    onBeforeMount(async () => {
      if (+new Date() > sitesStore.lastRefreshTime + 1000) {
        await sitesStore
          .refreshSites(await getIdToken())
          .catch((error) => (loadingError.value = error.message || error));
      }

      const siteKeys = Object.keys(sites.value);
      if (!siteKeys.length) {
        router.replace("/sites");
        return;
      }

      if (!currentSite.value) {
        await router.replace({
          name: "dashboard-device-overview",
          params: { siteId: siteKeys[0] },
        });
      }

      await Promise.all([setupDevices(), setupLocations()]);

      WebSocket.connect(
        await getIdToken(),
        () => (canSubscribe.value = true),
        console.error
      );

      loading.value = false;
    });

    onUnmounted(() => WebSocket.disconnect());

    return {
      loading,
      loadingError,
      currentSite,
      siteDevices,
      locations,
    };
  },
};
</script>

<template>
  <main id="dashboard-view">
    <h1
      class="text-center text-truncate"
      v-text="currentSite?.displayName || 'Dashboard'"
    ></h1>
    <hr />
    <div
      v-if="loadingError"
      class="alert alert-danger text-center p-2 mb-0 mx-auto"
      role="alert"
    >
      {{ loadingError }}
    </div>
    <RouterView
      v-else
      :loading="loading"
      :currentSiteRole="currentSite.role"
      :siteDevices="siteDevices"
      :locations="locations"
    />
  </main>
</template>
