<script>
import { ref, shallowRef, onBeforeMount } from "vue";
import { useUserStore } from "@/stores/UserStore";
import axios from "axios";
import { MDBRow, MDBCol } from "mdb-vue-ui-kit";
import AjaxButton from "@/components/AjaxButton.vue";
import LoadingView from "@/views/LoadingView.vue";

export default {
  components: {
    MDBRow,
    MDBCol,
    AjaxButton,
    LoadingView,
  },

  emits: ["done"],

  props: {
    invitationId: {
      type: String,
      required: true,
    },
    sites: {
      type: Object,
      required: true,
    },
  },

  setup: function (props, context) {
    const { getIdToken } = useUserStore();

    const loading = ref(true);
    const loadingError = ref(null);
    const siteInvitationsAPIPath =
      "http://localhost:443/api/v1/site-invitations";
    const siteInvitation = ref(null);

    onBeforeMount(async () => {
      const siteInvitationsResponse = await axios
        .get(siteInvitationsAPIPath, {
          timeout: 5000,
          headers: { authorization: `Bearer ${await getIdToken()}` },
          params: { id: props.invitationId },
        })
        .catch((error) => {
          loadingError.value =
            (error?.response?.status === 403 &&
              "You are already a part of this site.") ||
            ((error?.response?.status === 404 || !props.invitationId) &&
              "Sorry, that link is expired, it has already been used or it was invalid.") ||
            error.message ||
            error;
        });

      siteInvitation.value = siteInvitationsResponse?.data;

      loading.value = false;
    });

    const syncing = ref(false);

    const joinError = shallowRef(null);

    async function submitJoinForm() {
      joinError.value = null;
      syncing.value = true;
      axios
        .post(
          "http://localhost:443/api/v1/site-users",
          { id: props.invitationId },
          {
            timeout: 5000,
            headers: { authorization: `Bearer ${await getIdToken()}` },
          }
        )
        .then(({ data }) => {
          const { site, role } = data;
          const siteId = site.id;
          delete site.id;
          site.role = role;
          Object.assign(props.sites, { [siteId]: site });
          context.emit("done");
        })
        .catch((error) => (joinError.value = error.message || error))
        .finally(() => (syncing.value = false));
    }

    return {
      siteInvitation,
      loading,
      loadingError,
      syncing,
      joinError,
      submitJoinForm,
    };
  },
};
</script>

<template>
  <div>
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
    <template v-else>
      <h2>
        Are you sure you want to join the site "{{
          siteInvitation.site.displayName
        }}"?
      </h2>
      <p>If you want to join this site press the accept button.</p>
      <MDBRow
        tag="form"
        class="g-3 mb-3"
        novalidate
        @submit.prevent="submitJoinForm"
      >
        <MDBCol md="12" v-if="joinError">
          <div class="alert alert-danger text-center p-2 mb-0" role="alert">
            {{ joinError }}
          </div>
        </MDBCol>
        <MDBCol md="12">
          <AjaxButton
            color="success"
            type="submit"
            size="lg"
            class="w-100"
            :syncing="syncing"
            >Accept Site Invitation</AjaxButton
          >
        </MDBCol>
      </MDBRow>
    </template>
  </div>
</template>
