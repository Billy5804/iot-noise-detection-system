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
import { ref, computed, onUnmounted, onBeforeMount, watch } from "vue";
import { RouterLink, RouterView, useRouter } from "vue-router";
import ForbiddenView from "../../ForbiddenView.vue";
import SiteUserRoles from "@/utilitys/SiteUserRoles";
import { useUserStore } from "@/stores/UserStore";

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
    ForbiddenView,
  },

  props: {
    loading: { type: Boolean, required: true },
    siteId: { type: String, required: true },
    currentSiteRole: { type: String, required: true },
    siteDevices: Object,
    locationId: String,
    deviceId: String,
    modalSize: String,
    modalName: String,
  },

  setup: function (props) {
    const router = useRouter();
    const { getIdToken } = useUserStore();

    const loading = ref(true);
    const loadingError = ref(null);

    const computedLoading = computed(() => loading.value || props.loading);

    const currentLocationDevices = computed(() => ({}));

    const sortedFilteredSiteDevices = computed(() => {
      const deviceIds = Object.entries(currentLocationDevices.value).map(
        ([deviceId]) => deviceId
      );
      return Object.entries(props.siteDevices || {})
        .filter(([deviceId]) => deviceIds.includes(deviceId))
        .sort(([, { sensors: sensorsA }], [, { sensors: sensorsB }]) => {
          return sensorsB[0].latestValue - sensorsA[0].latestValue;
        });
    });

    const showModal = computed({
      get: () => !computedLoading.value && !!props.modalName,
      set: (value) => {
        if (value === false) {
          router.push({
            name: "dashboard-location-overview",
            params: { siteId: props.siteId, locationId: props.locationId },
          });
        }
      },
    });

    const allowedModal = computed(() => {
      const allowedRoles = router.currentRoute.value.meta.allowedRoles;
      return (
        showModal.value &&
        (allowedRoles ? allowedRoles.includes(props.currentSiteRole) : true)
      );
    });

    const locationsAPIPath = "http://localhost:443/api/v1/locations";
    const locations = ref(null);

    const currentLocation = computed(
      () => locations.value && locations.value[props.locationId]
    );

    const locationDevicesAPIPath =
      "http://localhost:443/api/v1/location-devices";
    const locationDevices = ref(null);

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

    onBeforeMount(async () => {
      if (props.loading) {
        await new Promise((resolve) => {
          const loadingWatcher = watch(
            () => props.loading,
            (loadingValue) => {
              if (!loadingValue) {
                loadingWatcher();
                resolve();
              }
            }
          );
        });
      }

      await setupLocations();

      const locationKeys = Object.keys(locations.value);
      console.log(locationKeys);
      if (!currentLocation.value && locationKeys.length) {
        await router.replace({
          name: "dashboard-location-overview",
          params: {
            siteId: props.siteId,
            locationId: locationKeys[0],
          },
        });
      }

      loading.value = false;
    });

    return {
      computedLoading,
      sortedFilteredSiteDevices,
      showModal,
      allowedModal,
      SiteUserRoles,
      currentLocation,
      locations,
    };
  },
};
</script>

<template>
  <MDBRow :cols="['1', 'md-2', 'lg-3', 'xl-4']" class="g-4 mb-3">
    <MDBCol v-if="computedLoading">
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
        v-if="!currentLocation"
        sm="12"
        md="12"
        lg="12"
        xl="12"
        col="12"
        class="d-flex"
      >
        <RouterLink
          :to="{
            name: 'dashboard-location-add',
            params: { siteId, locationId },
          }"
          class="btn btn-success btn-lg mx-auto"
        >
          Add first location
        </RouterLink>
      </MDBCol>
      {{ currentLocation }}
      {{ locationId }}
      {{ locations }}
      <MDBCol
        v-for="[deviceId, device] in sortedFilteredSiteDevices"
        :key="deviceId"
      >
        <MDBCard>
          <MDBCardHeader class="d-flex">
            <h5 class="text-truncate m-0" v-text="device.displayName" />
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
          [SiteUserRoles.OWNER, SiteUserRoles.EDITOR].includes(currentSiteRole)
        "
        :to="{ name: 'dashboard-location-add', params: { siteId, locationId } }"
        title="Add Device"
        class="btn btn-success btn-lg btn-floating ripple-surface position-fixed bottom-0 end-0 me-3 mb-3"
      >
        <MDBIcon iconStyle="fas" size="2x" icon="plus" />
      </RouterLink>
    </template>
  </MDBRow>
  <MDBModal
    id="locationModal"
    tabindex="-1"
    labelledby="locationModalTitle"
    v-model="showModal"
    staticBackdrop
    :size="modalSize"
  >
    <template v-if="locations[locationId] || modalName === 'add'">
      <MDBModalHeader>
        <MDBModalTitle id="locationModalTitle">
          {{
            modalName === "add"
              ? "Add Location"
              : locations[locationId]
              ? locations[locationId].displayName
              : modalName
          }}
        </MDBModalTitle>
      </MDBModalHeader>
      <MDBModalBody>
        <RouterView
          v-if="allowedModal"
          :locations="locations"
          :role="currentSiteRole"
          @done="showModal = false"
        />
        <ForbiddenView
          v-else
          :redirectRoute="{
            name: 'dashboard-location-overview',
            params: { siteId: props.siteId },
          }"
        />
      </MDBModalBody>
    </template>
    <template v-else>
      <MDBModalHeader>
        <MDBModalTitle id="locationModalTitle">Unknown Location</MDBModalTitle>
      </MDBModalHeader>
      <MDBModalBody>Please choose a different location.</MDBModalBody>
    </template>
    <MDBModalFooter>
      <MDBBtn color="secondary" @click="showModal = false">Close</MDBBtn>
    </MDBModalFooter>
  </MDBModal>
</template>
