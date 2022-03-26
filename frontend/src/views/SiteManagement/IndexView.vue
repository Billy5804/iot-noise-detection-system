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
import siteRoles from "@/utilitys/SiteRoles";

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
  },

  props: {
    siteId: {
      type: String,
    },
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

    const loading = ref(true);
    const loadingError = ref(null);
    const sitesAPIPath = "https://noise.alexroyle.com/api/v1/site-users";
    const sites = ref(null);

    onBeforeMount(async () => {
      const sitesResponse = await axios
        .get(sitesAPIPath, {
          timeout: 5000,
          headers: { authorization: `Bearer ${await user.getIdToken()}` },
        })
        .catch((error) => (loadingError.value = error.message || error));

      sites.value = sitesResponse.data.reduce((result, { site, role }) => {
        const siteId = site.id;
        delete site.id;
        site.role = role;
        result[siteId] = site;
        return result;
      }, {});

      loading.value = false;
    });

    return { loading, loadingError, sites, siteRoles, showModal };
  },
};
</script>

<template>
  <main id="site-management-view">
    <h1 class="text-center">Site Management</h1>
    <hr />
    <MDBRow :cols="['1', 'md-2', 'lg-3', 'xl-4']" class="g-4 mb-3">
      <template v-if="loading">
        <MDBCol>
          <MDBCard aria-hidden="true">
            <MDBCardHeader class="placeholder-glow">
              <span class="placeholder col-8"></span>
            </MDBCardHeader>
            <MDBCardBody></MDBCardBody>
          </MDBCard>
        </MDBCol>
      </template>
      <div
        v-else-if="loadingError"
        class="alert alert-danger text-center p-2 mb-0"
        role="alert"
      >
        {{ loadingError }}
      </div>
      <template v-else>
        <MDBCol v-for="(site, siteId) in sites" :key="siteId">
          <MDBCard>
            <MDBCardHeader>{{ site.displayName }}</MDBCardHeader>
            <MDBCardBody>
              <RouterLink
                v-if="[siteRoles.OWNER, siteRoles.EDITOR].includes(site.role)"
                :to="{ name: 'site-edit', params: { siteId } }"
                class="m-1 text-warning"
                type="button"
              >
                <MDBIcon iconStyle="fas" icon="edit" size="lg" />
              </RouterLink>
              <RouterLink
                v-if="site.role === siteRoles.OWNER"
                :to="{ name: 'site-delete', params: { siteId } }"
                class="m-1 text-danger"
                type="button"
              >
                <MDBIcon iconStyle="fas" icon="trash-can" size="lg" />
              </RouterLink>
              <RouterLink
                v-else
                :to="{ name: 'site-leave', params: { siteId } }"
                class="m-1 text-danger"
                type="button"
              >
                <MDBIcon
                  iconStyle="fas"
                  icon="arrow-right-from-bracket"
                  size="lg"
                />
              </RouterLink>
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
      <template v-if="sites[siteId] || siteId === 'new'">
        <MDBModalHeader>
          <MDBModalTitle id="siteModalTitle">{{
            siteId === 'new' ? "New Site" : sites[siteId]?.displayName
          }}</MDBModalTitle>
        </MDBModalHeader>
        <MDBModalBody>
          <RouterView :sites="sites" @done="showModal = false" />
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
