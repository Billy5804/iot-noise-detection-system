<script>
import {
  MDBCard,
  MDBCardHeader,
  MDBCardBody,
  MDBCardText,
  MDBIcon,
} from "mdb-vue-ui-kit";
import { ref, onUnmounted, computed } from "vue";

export default {
  components: {
    MDBCard,
    MDBCardHeader,
    MDBCardBody,
    MDBCardText,
    MDBIcon,
  },

  props: {
    device: { type: Object, required: true },
    deviceId: { type: String, required: true },
    borderOverride: String,
  },

  setup: function (props) {
    const currentTime = ref(+new Date());

    const currentTimeUpdate = setInterval(
      () => (currentTime.value = +new Date()),
      5000
    );

    onUnmounted(() => clearInterval(currentTimeUpdate));

    const warningOrDangerBorder = computed(() => {
      const mainSensor = props.device.sensors[0];
      if (mainSensor.latestValue >= mainSensor.unit.getDangerThreshold()) {
        return "border-danger";
      }
      if (mainSensor.latestValue >= mainSensor.unit.getWarningThreshold()) {
        return "border-warning";
      }
      return "border-white";
    });

    const signalDetails = computed(() => {
      const { rssi, lastBeatTime } = props.device;
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
    });

    return { signalDetails, warningOrDangerBorder };
  },
};
</script>

<template>
  <MDBCard
    class="border border-3"
    :class="borderOverride ? borderOverride : warningOrDangerBorder"
  >
    <MDBCardHeader class="d-flex">
      <h5
        class="text-truncate m-0"
        v-text="device.displayName"
        :title="device.displayName"
      />
      <span
        class="position-relative ms-auto me-1"
        :title="`Signal: ${signalDetails.text}\nRSSI: ${device.rssi}dBm`"
      >
        <MDBIcon
          v-for="({ icon, color }, index) in signalDetails.icons"
          :key="index"
          :icon="icon"
          :class="`${
            index > 0
              ? 'position-absolute top-50 start-50 translate-middle '
              : ''
          }text-${color}`"
        />
      </span>
      <slot />
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
</template>
