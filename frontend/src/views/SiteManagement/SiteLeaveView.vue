<script>
import { ref, shallowRef } from "vue";
import { useUserStore } from "@/stores/UserStore";
import axios from "axios";
import { MDBRow, MDBCol } from "mdb-vue-ui-kit";
import AjaxButton from "@/components/AjaxButton.vue";

export default {
  components: {
    MDBRow,
    MDBCol,
    AjaxButton,
  },

  emits: ["done"],

  props: {
    sites: {
      type: Object,
      required: true,
    },
    siteId: {
      type: String,
      required: true,
    },
  },

  setup: function (props, context) {
    const { getIdToken } = useUserStore();

    const syncing = ref(false);

    const leaveError = shallowRef(null);

    async function submitLeaveForm() {
      leaveError.value = null;
      syncing.value = true;
      axios
        .delete(
          `${
            import.meta.env.BASE_URL
              ? import.meta.env.BASE_URL
              : "http://localhost:443"
          }/api/v1/site-users`,
          {
            timeout: 5000,
            headers: { authorization: await getIdToken() },
            params: { siteId: props.siteId },
          }
        )
        .then(() => {
          delete props.sites[props.siteId];
          context.emit("done");
        })
        .catch((error) => (leaveError.value = error.message || error))
        .finally(() => (syncing.value = false));
    }

    return {
      syncing,
      leaveError,
      submitLeaveForm,
    };
  },
};
</script>

<template>
  <h2>Are you sure you want to leave this site?</h2>
  <p>
    This action cannot be undone.<br />If you want to rejoin this site in the
    future you will need an invite from the owner.
  </p>
  <MDBRow
    tag="form"
    class="g-3 mb-3"
    novalidate
    @submit.prevent="submitLeaveForm"
  >
    <MDBCol md="12" v-if="leaveError">
      <div class="alert alert-danger text-center p-2 mb-0" role="alert">
        {{ leaveError }}
      </div>
    </MDBCol>
    <MDBCol md="12">
      <AjaxButton
        color="danger"
        type="submit"
        size="lg"
        class="w-100"
        :syncing="syncing"
        >Leave Site</AjaxButton
      >
    </MDBCol>
  </MDBRow>
</template>
