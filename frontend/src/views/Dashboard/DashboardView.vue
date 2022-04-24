<script>
import {
  MDBRow,
  MDBCol,
  MDBCard,
  MDBCardHeader,
  MDBCardBody,
  MDBCardText,
  MDBIcon,
  MDBBtn,
  MDBModal,
  MDBModalHeader,
  MDBModalTitle,
  MDBModalBody,
  MDBModalFooter,
} from "mdb-vue-ui-kit";
import axios from "axios";
import { useUserStore } from "@/stores/UserStore";
import { useUserSitesStore } from "@/stores/UserSitesStore";
import { onBeforeMount, ref, computed, onUnmounted, watch } from "vue";
import { RouterLink, RouterView, useRouter } from "vue-router";
import DeviceOptionsView from "./DeviceOptionsView.vue";
import ForbiddenView from "../ForbiddenView.vue";
import SensorUnits from "@/utilitys/SensorUnits";
import SiteUserRoles from "@/utilitys/SiteUserRoles";
import WebSocket from "@/utilitys/WebSocket";

export default {
  components: {
    MDBRow,
    MDBCol,
    MDBCard,
    MDBCardHeader,
    MDBCardBody,
    MDBCardText,
    MDBIcon,
    MDBBtn,
    MDBModal,
    MDBModalHeader,
    MDBModalTitle,
    MDBModalBody,
    MDBModalFooter,
    RouterLink,
    RouterView,
    DeviceOptionsView,
    ForbiddenView,
  },

  props: {
    siteId: String,
    deviceId: String,
    modalSize: String,
  },

  setup: function (props) {
    const user = useUserStore();
    const sitesStore = useUserSitesStore();
    const router = useRouter();

    const loading = ref(true);
    const loadingError = ref(null);

    const sites = computed(() => sitesStore.authorisedSites);

    const siteDevicesAPIPath = "http://localhost:443/api/v1/site-devices";
    const siteDevices = ref(null);

    const sortedSiteDevices = computed(() =>
      Object.entries(siteDevices.value || {}).sort(
        ([, { sensors: sensorsA }], [, { sensors: sensorsB }]) => {
          return sensorsB[0].latestValue - sensorsA[0].latestValue;
        }
      )
    );

    const currentSite = computed(
      () => sites.value && sites.value[props.siteId]
    );

    const showModal = computed({
      get: () => !!props.deviceId && !loading.value && !loadingError.value,
      set: (value) => {
        if (value === false) {
          router.push({ name: "dashboard", params: { siteId: props.siteId } });
        }
      },
    });

    const allowedModal = computed(() => {
      const allowedRoles = router.currentRoute.value.meta.allowedRoles;
      return (
        showModal.value &&
        (allowedRoles ? allowedRoles.includes(currentSite.value?.role) : true)
      );
    });

    async function setupDevices() {
      const siteDevicesResponse = await axios
        .get(siteDevicesAPIPath, {
          timeout: 5000,
          headers: { authorization: await user.getIdToken() },
          params: { siteId: props.siteId },
        })
        .catch((error) => (loadingError.value = error.message || error));

      siteDevices.value =
        siteDevicesResponse?.data?.reduce(
          (result, { id, sensors, ...device }) => {
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
          .refreshSites(await user.getIdToken())
          .catch((error) => (loadingError.value = error.message || error));
      }

      const siteKeys = Object.keys(sites.value);
      if (!siteKeys.length) {
        router.replace("/sites");
        return;
      }

      if (!currentSite.value) {
        await router.replace({
          name: "dashboard",
          params: { siteId: siteKeys[0] },
        });
      }

      await setupDevices();

      WebSocket.connect(
        await user.getIdToken(),
        () => (canSubscribe.value = true),
        console.error
      );

      loading.value = false;
    });

    const currentTime = ref(+new Date());

    const currentTimeUpdate = setInterval(
      () => (currentTime.value = +new Date()),
      5000
    );

    onUnmounted(() => {
      clearInterval(currentTimeUpdate);
      WebSocket.disconnect();
    });

    function getSignalDetails(rssi, lastBeatTime) {
      if (lastBeatTime + 900000 < currentTime.value) {
        return {
          text: "Offline",
          icons: [
            { color: "dark", icon: "signal" },
            { color: "danger", icon: "slash" },
          ],
        };
      }
      if (rssi <= 0 && rssi >= -30) {
        return {
          text: "Adjacent to access point",
          icons: [{ color: "success", icon: "signal" }],
        };
      }
      if (rssi <= -31 && rssi >= -60) {
        return {
          text: "Excellent",
          icons: [{ color: "success", icon: "signal" }],
        };
      }
      if (rssi <= -61 && rssi >= -72) {
        return { text: "OK", icons: [{ color: "warning", icon: "signal" }] };
      }
      if (rssi <= -73 && rssi >= -80) {
        return { text: "Poor", icons: [{ color: "warning", icon: "signal" }] };
      }
      if (rssi <= -78 && rssi >= -94) {
        return {
          text: "Very poor",
          icons: [{ color: "danger", icon: "signal" }],
        };
      }
      if (rssi <= -95 && rssi >= -120) {
        return {
          text: "Unusable",
          icons: [
            { color: "danger", icon: "signal" },
            { color: "dark", icon: "slash" },
          ],
        };
      }
      return {
        text: "unknown",
        icons: [
          { color: "dark", icon: "signal" },
          { color: "info", icon: "question" },
        ],
      };
    }

    return {
      loading,
      loadingError,
      sites,
      currentSite,
      siteDevices,
      sortedSiteDevices,
      showModal,
      allowedModal,
      getSignalDetails,
      currentTime,
      SiteUserRoles,
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
    <MDBRow :cols="['1', 'md-2', 'lg-3', 'xl-4']" class="g-4 mb-3">
      <MDBCol v-if="loading">
        <MDBCard aria-hidden="true">
          <MDBCardHeader class="placeholder-glow">
            <span class="placeholder col-8"></span>
          </MDBCardHeader>
          <MDBCardBody class="placeholder-glow">
            <span class="col-7 d-inline-block"></span>
            <span class="placeholder col-5"></span>
          </MDBCardBody>
        </MDBCard>
      </MDBCol>
      <div
        v-else-if="loadingError"
        class="alert alert-danger text-center p-2 mb-0 mx-auto"
        role="alert"
      >
        {{ loadingError }}
      </div>
      <template v-else>
        <MDBCol
          v-if="!sortedSiteDevices.length"
          sm="12"
          md="12"
          lg="12"
          xl="12"
          col="12"
          class="d-flex"
        >
          <RouterLink
            :to="{ name: 'dashboard-device-add', params: { siteId } }"
            class="btn btn-success btn-lg mx-auto"
          >
            Add first device
          </RouterLink>
        </MDBCol>
        <MDBCol v-for="[deviceId, device] in sortedSiteDevices" :key="deviceId">
          <MDBCard>
            <MDBCardHeader class="d-flex">
              <h5 class="text-truncate m-0" v-text="device.displayName" />
              <span
                class="position-relative ms-auto me-1"
                :title="`Signal: ${
                  getSignalDetails(device.rssi, device.lastBeatTime).text
                }\nRSSI: ${device.rssi}dBm`"
              >
                <MDBIcon
                  v-for="({ icon, color }, index) in getSignalDetails(
                    device.rssi,
                    device.lastBeatTime
                  ).icons"
                  :key="index"
                  :icon="icon"
                  :class="`${
                    index > 0
                      ? 'position-absolute top-50 start-50 translate-middle '
                      : ''
                  }text-${color}`"
                />
              </span>
              <DeviceOptionsView
                :siteId="siteId"
                :deviceId="deviceId"
                :role="currentSite.role"
                class="d-flex"
              />
            </MDBCardHeader>
            <MDBCardBody>
              <MDBCardText
                v-for="(sensor, sensorId) in device.sensors"
                :key="sensorId"
                class="text-end"
              >
                {{ sensor.latestValue + sensor.unit.getSymbol() }}
                <MDBIcon :icon="sensor.unit.getSensorType().getIcon()" />
              </MDBCardText>
            </MDBCardBody>
          </MDBCard>
        </MDBCol>
        <RouterLink
          v-if="
            [SiteUserRoles.OWNER, SiteUserRoles.EDITOR].includes(
              currentSite.role
            )
          "
          :to="{ name: 'dashboard-device-add', params: { siteId } }"
          title="Add Device"
          class="btn btn-success btn-lg btn-floating ripple-surface position-fixed bottom-0 end-0 me-3 mb-3"
        >
          <MDBIcon iconStyle="fas" size="2x" icon="plus" />
        </RouterLink>
      </template>
    </MDBRow>
    <MDBModal
      id="siteModal"
      tabindex="-1"
      labelledby="deviceModalTitle"
      v-model="showModal"
      staticBackdrop
      :size="modalSize"
    >
      <template v-if="siteDevices[deviceId] || deviceId === 'add'">
        <MDBModalHeader>
          <MDBModalTitle id="deviceModalTitle">
            {{
              siteDevices[deviceId]
                ? siteDevices[deviceId].displayName
                : deviceId === "add" && "Add Device"
            }}
          </MDBModalTitle>
        </MDBModalHeader>
        <MDBModalBody>
          <RouterView
            v-if="allowedModal"
            :siteDevices="siteDevices"
            :role="currentSite.role"
            @done="showModal = false"
          />
          <ForbiddenView v-else redirectRoute="/dashboard" />
        </MDBModalBody>
      </template>
      <template v-else>
        <MDBModalHeader>
          <MDBModalTitle id="deviceModalTitle">Unknown Device</MDBModalTitle>
        </MDBModalHeader>
        <MDBModalBody>Please choose a different device.</MDBModalBody>
      </template>
      <MDBModalFooter>
        <MDBBtn color="secondary" @click="showModal = false">Close</MDBBtn>
      </MDBModalFooter>
    </MDBModal>
  </main>
</template>
