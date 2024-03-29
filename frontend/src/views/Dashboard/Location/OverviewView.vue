<script>
import {
  MDBRow,
  MDBCol,
  MDBCard,
  MDBCardHeader,
  MDBCardBody,
  MDBIcon,
  MDBBtn,
  MDBModal,
  MDBModalHeader,
  MDBModalTitle,
  MDBModalBody,
  MDBModalFooter,
  MDBFile,
} from "mdb-vue-ui-kit";
import { firebaseStorage } from "@/firebase/database";
import { ref as firebaseRef, getDownloadURL } from "firebase/storage";
import axios from "axios";
import { ref, computed, onMounted, watch } from "vue";
import { RouterLink, RouterView, useRouter } from "vue-router";
import DashboardNavigation from "@/components/DashboardNavigation.vue";
import DeviceCard from "@/components/DeviceCard.vue";
import ForbiddenView from "../../ForbiddenView.vue";
import SiteUserRoles from "@/utilitys/SiteUserRoles";
import { svgOptimiseAndStore } from "@/utilitys/SVGOptimiseAndStore";
import { useUserStore } from "@/stores/UserStore";
import { error as toastrError } from "toastr";

import LocationMapping from "@/components/LocationMapping.vue";
import LoadingView from "../../LoadingView.vue";
import VerticalRule from "../../../components/VerticalRule.vue";

export default {
  components: {
    MDBRow,
    MDBCol,
    MDBCard,
    MDBCardHeader,
    MDBCardBody,
    MDBIcon,
    MDBBtn,
    MDBModal,
    MDBModalHeader,
    MDBModalTitle,
    MDBModalBody,
    MDBModalFooter,
    MDBFile,
    RouterLink,
    RouterView,
    ForbiddenView,
    LoadingView,
    LocationMapping,
    DeviceCard,
    DashboardNavigation,
    VerticalRule,
  },

  props: {
    loading: { type: Boolean, required: true },
    siteId: { type: String, required: true },
    currentSiteRole: { type: String, required: true },
    locations: Object,
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

    const currentLocation = computed(
      () => props.locations && props.locations[props.locationId]
    );

    const locationDevicesAPIPath = API_V1_URL + "location-devices";
    const locationDevices = ref(null);

    const mergedLocationDevices = computed(() => {
      return (
        locationDevices.value &&
        Object.entries(locationDevices.value).reduce(
          (result, [deviceId, locationDevice]) => {
            result[deviceId] = Object.assign(
              { ...props.siteDevices[deviceId] },
              locationDevice
            );
            return result;
          },
          {}
        )
      );
    });

    const loadingDevices = ref(true);

    const locationFloorPlanURL = ref(null);
    const loadingFloorPlan = ref(true);

    const selectedDeviceId = ref(null);

    const showModal = computed({
      get: () =>
        !computedLoading.value &&
        !!props.modalName &&
        (props.modalName !== "manage-devices" || !loadingDevices.value) &&
        (props.modalName !== "map-devices" ||
          (!loadingDevices.value && !loadingFloorPlan.value)),
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

    const sortedLocationDevices = computed(() => {
      return Object.entries(mergedLocationDevices.value || {}).sort(
        (
          [deviceIdA, { sensors: sensorsA }],
          [deviceIdB, { sensors: sensorsB }]
        ) => {
          if (selectedDeviceId.value === deviceIdA) {
            return -1;
          }
          if (selectedDeviceId.value === deviceIdB) {
            return 1;
          }
          return sensorsB[0].latestValue - sensorsA[0].latestValue;
        }
      );
    });

    const floorPlanUpload = ref(null);

    function setupCurrentLocation() {
      loadingFloorPlan.value = true;
      loadingDevices.value = true;
      locationFloorPlanURL.value = null;

      getDownloadURL(
        firebaseRef(firebaseStorage, `floorPlans/${props.locationId}`)
      )
        .then((floorPlanURL) => (locationFloorPlanURL.value = floorPlanURL))
        .catch()
        .finally(() => (loadingFloorPlan.value = false));

      setupLocationDevices().then(() => (loadingDevices.value = false));
    }

    async function checkLocationAndRedirect() {
      const locationKeys = Object.keys(props.locations);

      if (!locationKeys.length) {
        router.replace({
          name: "dashboard-location-add",
          params: { siteId: props.siteId },
        });
        loading.value = false;
        return;
      }

      if (!currentLocation.value && locationKeys.length) {
        await router.replace({
          name: "dashboard-location-overview",
          params: {
            siteId: props.siteId,
            locationId: locationKeys[0],
          },
        });
      }
    }

    watch(currentLocation, (currentLocation) => {
      if (computedLoading.value) {
        return;
      }
      if (!currentLocation) {
        checkLocationAndRedirect();
        return;
      }
      setupCurrentLocation();
    });

    watch(floorPlanUpload, (file) => {
      if (!file || !file[0]) {
        return;
      }
      loadingFloorPlan.value = true;
      locationFloorPlanURL.value = null;
      floorPlanUpload.value = null;

      svgOptimiseAndStore(file[0], `/floorPlans/${props.locationId}`)
        .then((blobURL) => (locationFloorPlanURL.value = blobURL))
        .catch((error) => toastrError(error.message || error))
        .finally(() => (loadingFloorPlan.value = false));
    });

    async function setupLocationDevices() {
      const locationDevicesResponse = await axios
        .get(locationDevicesAPIPath, {
          timeout: 5000,
          headers: { authorization: await getIdToken() },
          params: { locationId: props.locationId },
        })
        .catch((error) => (loadingError.value = error.message || error));

      locationDevices.value =
        locationDevicesResponse?.data?.reduce(
          (result, { deviceId, ...locationDevice }) => {
            result[deviceId] = locationDevice;
            return result;
          },
          {}
        ) || {};
    }

    onMounted(async () => {
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

      await checkLocationAndRedirect();

      if (!loading.value) {
        return;
      }

      setupCurrentLocation();

      loading.value = false;
    });

    return {
      computedLoading,
      sortedLocationDevices,
      showModal,
      allowedModal,
      SiteUserRoles,
      currentLocation,
      floorPlanUpload,
      locationDevices,
      mergedLocationDevices,
      loadingDevices,
      locationFloorPlanURL,
      loadingFloorPlan,
      selectedDeviceId,
    };
  },
};
</script>

<template>
  <MDBRow class="g-3 mb-3">
    <DashboardNavigation :locations="locations" :siteId="siteId">
      <div
        v-if="!computedLoading && currentLocation"
        class="d-flex align-items-center text-truncate"
      >
        <h2 class="h3 m-0 text-truncate">{{ currentLocation?.displayName }}</h2>
        <template
          v-if="
            [SiteUserRoles.OWNER, SiteUserRoles.EDITOR].includes(
              currentSiteRole
            )
          "
        >
          <span class="h3 m-0">:</span>
          <span class="h4 mb-0">
            <RouterLink
              :to="{
                name: 'dashboard-location-edit',
                params: { siteId, locationId },
              }"
              class="mx-1 text-warning"
              type="button"
              title="Edit location"
            >
              <MDBIcon iconStyle="fas" icon="edit" />
            </RouterLink>
            <RouterLink
              :to="{
                name: 'dashboard-location-delete',
                params: { siteId, locationId },
              }"
              class="mx-1 text-danger"
              type="button"
              title="Delete location"
            >
              <MDBIcon iconStyle="fas" icon="trash-can" />
            </RouterLink>
            <RouterLink
              :to="{
                name: 'dashboard-location-floor-plan',
                params: { siteId, locationId },
              }"
              class="mx-1 text-info"
              type="button"
              title="Change floor plan"
            >
              <MDBIcon iconStyle="fas" icon="arrow-right-arrow-left" />
            </RouterLink>
            <RouterLink
              :to="{
                name: 'dashboard-location-manage-devices',
                params: { siteId, locationId },
              }"
              class="mx-1 text-secondary"
              type="button"
              title="Manage devices"
            >
              <MDBIcon iconStyle="fas" icon="plus-minus" />
            </RouterLink>
            <RouterLink
              v-if="locationFloorPlanURL"
              :to="{
                name: 'dashboard-location-map-devices',
                params: { siteId, locationId },
              }"
              class="mx-1 text-primary"
              type="button"
              title="Map devices"
            >
              <MDBIcon iconStyle="fas" icon="arrows-up-down-left-right" />
            </RouterLink>
          </span>
        </template>
      </div>
    </DashboardNavigation>
    <MDBCol v-if="!currentLocation && !computedLoading" col="12" class="d-flex">
      <RouterLink
        v-if="
          [SiteUserRoles.OWNER, SiteUserRoles.EDITOR].includes(currentSiteRole)
        "
        :to="{
          name: 'dashboard-location-add',
          params: { siteId, locationId },
        }"
        class="btn btn-success btn-lg mx-auto"
      >
        Add first location
      </RouterLink>
    </MDBCol>
    <template v-else>
      <MDBCol sm="12" md="7" lg="8" xl="9" col="12">
        <div
          v-if="computedLoading || loadingFloorPlan"
          class="position-relative h-100"
        >
          <LoadingView />
        </div>
        <LocationMapping
          v-else-if="locationFloorPlanURL"
          :key="locationFloorPlanURL"
          :floorPlanURL="locationFloorPlanURL"
          :locationDevices="mergedLocationDevices"
          v-model:selectedDeviceId="selectedDeviceId"
        />
        <MDBFile
          v-else
          v-model="floorPlanUpload"
          label="Add SVG floor plan"
          accept="image/svg+xml"
        />
      </MDBCol>
      <VerticalRule class="d-md-block d-none" />
      <MDBCol sm="12" md="5" lg="4" xl="3" col="12">
        <MDBCard v-if="computedLoading || loadingDevices" aria-hidden="true">
          <MDBCardHeader class="placeholder-glow">
            <span class="placeholder col-8"></span>
          </MDBCardHeader>
          <MDBCardBody class="placeholder-glow">
            <span class="col-7 d-inline-block"></span>
            <span class="placeholder col-5"></span>
          </MDBCardBody>
        </MDBCard>
        <RouterLink
          v-else-if="
            !Object.keys(locationDevices).length &&
            [SiteUserRoles.OWNER, SiteUserRoles.EDITOR].includes(
              currentSiteRole
            )
          "
          :to="{
            name: 'dashboard-location-manage-devices',
            params: { siteId, locationId },
          }"
          class="btn btn-success btn-lg w-100"
        >
          Add first location device
        </RouterLink>
        <MDBRow v-else class="g-3 mb-3">
          <MDBCol
            v-for="[deviceId, device] in sortedLocationDevices"
            :key="deviceId"
            tag="button"
            type="button"
            title="Select device"
            @click="
              selectedDeviceId = selectedDeviceId === deviceId ? null : deviceId
            "
          >
            <DeviceCard
              :device="device"
              :deviceId="deviceId"
              :borderOverride="
                selectedDeviceId === deviceId ? 'border-primary' : ''
              "
            />
          </MDBCol>
        </MDBRow>
      </MDBCol>
    </template>
    <RouterLink
      v-if="
        [SiteUserRoles.OWNER, SiteUserRoles.EDITOR].includes(currentSiteRole)
      "
      :to="{ name: 'dashboard-location-add', params: { siteId, locationId } }"
      title="Add Location"
      class="btn btn-success btn-lg btn-floating ripple-surface position-fixed bottom-0 end-0 me-3 mb-3"
    >
      <MDBIcon iconStyle="fas" size="2x" icon="plus" />
    </RouterLink>
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
            :siteDevices="siteDevices"
            :locationDevices="locationDevices"
            v-model:floorPlanURL="locationFloorPlanURL"
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
          <MDBModalTitle id="locationModalTitle"
            >Unknown Location</MDBModalTitle
          >
        </MDBModalHeader>
        <MDBModalBody>Please choose a different location.</MDBModalBody>
      </template>
      <MDBModalFooter>
        <MDBBtn color="secondary" @click="showModal = false">Close</MDBBtn>
      </MDBModalFooter>
    </MDBModal>
  </MDBRow>
</template>

<style scoped>
button.col {
  background-color: transparent;
  border: 0.125rem solid transparent;
  color: #4f4f4f;
}

button.col .card:hover {
  box-shadow: 0 4px 10px 0 rgb(0 0 0 / 20%), 0 4px 20px 0 rgb(0 0 0 / 10%);
}
</style>
