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
import axios from "axios";
import { useUserStore } from "@/stores/UserStore";
import { onBeforeMount, ref, computed } from "vue";
import { RouterLink, RouterView, useRouter } from "vue-router";
import SiteOptionsView from "./SiteOptionsView.vue";
import VerticalRule from "@/components/VerticalRule.vue";
import ForbiddenView from "../ForbiddenView.vue";

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
    SiteOptionsView,
    VerticalRule,
    ForbiddenView,
  },

  props: {
    siteId: String,
  },

  setup: function (props) {
    const user = useUserStore();
    const router = useRouter();

    const showModal = computed({
      get: () => !!props.siteId && !loading.value && !loadingError.value,
      set: (value) => {
        if (value === false) {
          router.push({ name: "site-management" });
        }
      },
    });

    const allowedModal = computed(() => {
      const allowedRoles = router.currentRoute.value.meta.allowedRoles;
      return (
        showModal.value &&
        (allowedRoles
          ? allowedRoles.includes(sites.value[props.siteId]?.role)
          : true)
      );
    });

    const loading = ref(true);
    const loadingError = ref(null);
    const sitesAPIPath = "http://localhost:443/api/v1/site-users";
    const sites = ref(null);

    onBeforeMount(async () => {
      const sitesResponse = await axios
        .get(sitesAPIPath, {
          timeout: 5000,
          headers: { authorization: await user.getIdToken() },
        })
        .catch((error) => (loadingError.value = error.message || error));

      sites.value = sitesResponse?.data?.reduce((result, { site, role }) => {
        const siteId = site.id;
        delete site.id;
        site.role = role;
        result[siteId] = site;
        return result;
      }, {});

      loading.value = false;
    });

    return { loading, loadingError, sites, showModal, allowedModal };
  },
};
</script>

<template>
  <main id="site-management-view">
    <h1 class="text-center">Site Management</h1>
    <hr />
    <MDBRow :cols="['1', 'md-2', 'lg-3', 'xl-4']" class="g-4 mb-3">
      <MDBCol v-if="loading">
        <MDBCard aria-hidden="true">
          <MDBCardHeader class="placeholder-glow">
            <h5 class="placeholder col-8" />
          </MDBCardHeader>
          <MDBCardBody class="placeholder-glow">
            <span class="placeholder col-5"></span>
            <span class="col-1 d-inline-block"></span>
            <VerticalRule class="placeholder" />
            <span class="col-1 d-inline-block"></span>
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
        <MDBCol v-for="(site, siteId) in sites" :key="siteId">
          <MDBCard>
            <MDBCardHeader
              class="h5 m-0 text-truncate"
              v-text="site.displayName"
            />
            <MDBCardBody>
              <SiteOptionsView :siteId="siteId" :sites="sites" />
            </MDBCardBody>
          </MDBCard>
        </MDBCol>
        <RouterLink
          :to="{ name: 'site-create' }"
          title="Add Site"
          class="btn btn-success btn-lg btn-floating ripple-surface position-fixed bottom-0 end-0 me-3 mb-3"
        >
          <MDBIcon iconStyle="fas" size="2x" icon="plus" />
        </RouterLink>
      </template>
    </MDBRow>
    <MDBModal
      id="siteModal"
      tabindex="-1"
      labelledby="siteModalTitle"
      v-model="showModal"
      staticBackdrop
    >
      <template
        v-if="sites[siteId] || ['create', 'invitation'].includes(siteId)"
      >
        <MDBModalHeader>
          <MDBModalTitle id="siteModalTitle" class="text-truncate">
            {{
              sites[siteId]
                ? sites[siteId].displayName
                : (siteId === "create" && "New Site") ||
                  (siteId === "invitation" && "Join Site")
            }}
          </MDBModalTitle>
        </MDBModalHeader>
        <MDBModalBody>
          <RouterView
            v-if="allowedModal"
            :sites="sites"
            @done="showModal = false"
          />
          <ForbiddenView v-else redirectRoute="/sites" />
        </MDBModalBody>
      </template>
      <template v-else>
        <MDBModalHeader>
          <MDBModalTitle id="siteModalTitle">Unknown Site</MDBModalTitle>
        </MDBModalHeader>
        <MDBModalBody>Please choose a different site.</MDBModalBody>
      </template>
      <MDBModalFooter>
        <MDBBtn color="secondary" @click="showModal = false">Close</MDBBtn>
      </MDBModalFooter>
    </MDBModal>
  </main>
</template>
