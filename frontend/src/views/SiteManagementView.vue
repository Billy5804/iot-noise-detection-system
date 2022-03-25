<script>
import {
  MDBRow,
  MDBCol,
  MDBCard,
  MDBCardHeader,
  MDBCardBody,
  MDBIcon,
  MDBBtn,
} from "mdb-vue-ui-kit";
import axios from "axios";
import { useUserStore } from "../stores/UserStore";
import { onBeforeMount, ref } from "vue";

export default {
  components: {
    MDBRow,
    MDBCol,
    MDBCard,
    MDBCardHeader,
    MDBCardBody,
    MDBIcon,
    MDBBtn,
  },

  setup: function () {
    const user = useUserStore();

    const loading = ref(true);
    const loadingError = ref(null);
    const sitesAPIPath = "/api/v1/sites";
    const sites = ref(new Array(10));

    onBeforeMount(async () => {
      const sitesResponse = await axios
        .get(sitesAPIPath, {
          timeout: 1000,
          headers: { authorization: `Bearer ${await user.getIdToken()}` },
        })
        .catch((error) => (loadingError.value = error.message || error));

      sites.value = sitesResponse.data.reduce((result, {site, role}) => {
        site.role = role;
        result.push(site);
        return result;
      }, []);

      loading.value = false;
    });

    return { loading, loadingError, sites };
  },
};
</script>

<template>
  <main id="site-management-view">
    <h1 class="text-center">Site Management</h1>
    <hr />

    <MDBRow :cols="['1', 'md-2', 'lg-3', 'xl-4']" class="g-4 mb-3">
      <template v-if="loading">
        <MDBCol v-for="index in articles" :key="index">
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
        <MDBCol v-for="(site, index) in sites" :key="index">
          <MDBCard>
            <MDBCardHeader>{{ site.displayName }}</MDBCardHeader>
            <MDBCardBody>
              <MDBBtn class="m-1" type="button" style="color: rgb(59, 89, 152)">
                <MDBIcon iconStyle="fas" icon="edit" size="lg"></MDBIcon>
              </MDBBtn>
              <MDBBtn class="m-1" type="button" style="color: rgb(59, 89, 152)">
                <MDBIcon iconStyle="fas" icon="trash-can" size="lg"></MDBIcon>
              </MDBBtn>
            </MDBCardBody>
          </MDBCard>
        </MDBCol>
      </template>
    </MDBRow>
  </main>
</template>
