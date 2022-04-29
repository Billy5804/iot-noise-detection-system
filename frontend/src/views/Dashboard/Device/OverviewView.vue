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
} from "mdb-vue-ui-kit";
import { computed } from "vue";
import { RouterLink, RouterView, useRouter } from "vue-router";
import DeviceOptionsView from "./OptionsView.vue";
import DeviceCard from "@/components/DeviceCard.vue";
import ForbiddenView from "../../ForbiddenView.vue";
import SiteUserRoles from "@/utilitys/SiteUserRoles";

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
    RouterLink,
    RouterView,
    DeviceOptionsView,
    ForbiddenView,
    DeviceCard,
  },

  props: {
    loading: { type: Boolean, required: true },
    siteId: { type: String, required: true },
    currentSiteRole: { type: String, required: true },
    siteDevices: Object,
    deviceId: String,
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
        (allowedRoles ? allowedRoles.includes(props.currentSiteRole) : true)
      );
    });

    return {
      sortedSiteDevices,
      showModal,
      allowedModal,
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
        <DeviceCard :device="device" :deviceId="deviceId">
          <DeviceOptionsView
            :siteId="siteId"
            :deviceId="deviceId"
            :role="currentSiteRole"
            class="d-flex"
          />
        </DeviceCard>
      </MDBCol>
      <RouterLink
        v-if="
          [SiteUserRoles.OWNER, SiteUserRoles.EDITOR].includes(currentSiteRole)
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
          :role="currentSiteRole"
          @done="showModal = false"
        />
        <ForbiddenView
          v-else
          :redirectRoute="{
            name: 'dashboard-device-overview',
            params: { siteId: props.siteId },
          }"
        />
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
