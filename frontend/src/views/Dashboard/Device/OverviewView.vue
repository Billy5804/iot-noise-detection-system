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
import { ref, computed, onUnmounted } from "vue";
import { RouterLink, RouterView, useRouter } from "vue-router";
import DeviceOptionsView from "./OptionsView.vue";
import ForbiddenView from "../../ForbiddenView.vue";
import SiteUserRoles from "@/utilitys/SiteUserRoles";

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
    currentSite: { type: Object },
    siteDevices: { type: Object },
    siteId: { type: String, required: true },
    deviceId: String,
    loading: Boolean,
    modalSize: String,
  },

  setup: function (props) {
    const router = useRouter();

    const sortedSiteDevices = computed(() =>
      Object.entries(props.siteDevices || {}).sort(
        ([, { sensors: sensorsA }], [, { sensors: sensorsB }]) => {
          return sensorsB[0].latestValue - sensorsA[0].latestValue;
        }
      )
    );

    const showModal = computed({
      get: () => !props.loading && !!props.deviceId && !!props.siteDevices,
      set: (value) => {
        if (value === false) {
          router.push({
            name: "dashboard-device-overview",
            params: { siteId: props.siteId },
          });
        }
      },
    });

    const allowedModal = computed(() => {
      const allowedRoles = router.currentRoute.value.meta.allowedRoles;
      return (
        showModal.value &&
        (allowedRoles ? allowedRoles.includes(props.currentSite?.role) : true)
      );
    });

    const currentTime = ref(+new Date());

    const currentTimeUpdate = setInterval(
      () => (currentTime.value = +new Date()),
      5000
    );

    onUnmounted(() => clearInterval(currentTimeUpdate));

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
      sortedSiteDevices,
      showModal,
      allowedModal,
      getSignalDetails,
      SiteUserRoles,
    };
  },
};
</script>

<template>
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
          [SiteUserRoles.OWNER, SiteUserRoles.EDITOR].includes(currentSite.role)
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
    id="deviceModal"
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
</template>
