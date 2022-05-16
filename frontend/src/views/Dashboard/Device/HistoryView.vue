<script>
import { ref, computed, onMounted } from "vue";
import { useUserStore } from "@/stores/UserStore";
import axios from "axios";
import ApexChart from "vue3-apexcharts";
import LoadingView from "@/views/LoadingView.vue";

export default {
  components: { ApexChart, LoadingView },

  props: {
    deviceId: { type: String, required: true },
    siteId: { type: String, required: true },
    siteDevices: { type: Object, required: true },
  },

  setup: function (props) {
    const { getIdToken } = useUserStore();

    const loading = ref(true);
    const loadingError = ref(null);

    const currentDeviceSensors = computed(
      () => props.siteDevices[props.deviceId].sensors
    );

    const sensorsHistory = ref([]);

    onMounted(async () => {
      const historyResponse = await axios
        .get(API_V1_URL + "site-device-sensor-history", {
          timeout: 5000,
          headers: { authorization: await getIdToken() },
          params: { deviceId: props.deviceId, siteId: props.siteId },
        })
        .catch((error) => (loadingError.value = error.message || error));

      sensorsHistory.value =
        historyResponse?.data?.reduce(
          (result, { timestamp, value, sensorId }) => {
            result[sensorId].data.push([timestamp, value]);
            return result;
          },
          currentDeviceSensors.value
            .sort(({ id: id1 }, { id: id2 }) => id1 - id2)
            .map(({ unit }) => ({ name: unit, data: [] }))
        ) || [];

      loading.value = false;
    });

    const chartOptions = {
      chart: {
        type: "line",
        height: 350,
        zoom: {
          enabled: true,
        },
      },
      markers: {
        size: 3,
      },
      stroke: {
        curve: "smooth",
      },
      xaxis: {
        type: "datetime",
      },
      tooltip: {
        x: { formatter: (timestamp) => new Date(timestamp).toLocaleString() },
      },
    };

    return {
      loading,
      loadingError,
      sensorsHistory,
      chartOptions,
    };
  },
};
</script>

<template>
  <div id="device-history-line-chart">
    <div v-if="loading" class="p-5">
      <LoadingView />
    </div>
    <div
      v-else-if="loadingError"
      class="alert alert-danger text-center p-2 mb-0 w-100"
      role="alert"
    >
      {{ loadingError }}
    </div>
    <ApexChart
      v-else
      type="line"
      height="350"
      :options="chartOptions"
      :series="sensorsHistory"
    />
  </div>
</template>

<style scoped></style>
