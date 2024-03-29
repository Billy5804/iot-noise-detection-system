<script>
import { ref, onMounted } from "vue";
import { useRouter, RouterView } from "vue-router";
import { useUserStore } from "@/stores/UserStore";
import axios from "axios";
import { MDBBtn } from "mdb-vue-ui-kit";
import BootstrapTable from "@/components/BootstrapTable.vue";
import LoadingView from "@/views/LoadingView.vue";
import { success as toastrSuccess, error as toastrError } from "toastr";

export default {
  components: { MDBBtn, BootstrapTable, LoadingView, RouterView },

  props: {
    siteId: {
      type: String,
      required: true,
    },
    sites: {
      type: Object,
      required: true,
    },
    invitationId: String,
  },

  setup: function (props) {
    const { getIdToken, getDisplayName } = useUserStore();
    const router = useRouter();

    const loading = ref(true);
    const loadingError = ref(null);
    const siteInvitationsAPIPath = API_V1_URL + "site-invitations";
    const siteInvitations = ref([]);

    onMounted(async () => {
      const siteInvitationsResponse = await axios
        .get(siteInvitationsAPIPath, {
          timeout: 5000,
          headers: { authorization: await getIdToken() },
          params: { siteId: props.siteId },
        })
        .catch((error) => (loadingError.value = error.message || error));

      siteInvitations.value = siteInvitationsResponse?.data?.reduce(
        (result, invitation) => {
          result.push(invitation);
          return result;
        },
        []
      );

      loading.value = false;
    });

    function dateFormatter(date) {
      return date
        .toLocaleString([navigator.language, "en-GB"], {
          weekday: "short",
          year: "numeric",
          month: "short",
          day: "2-digit",
          hour: "2-digit",
          minute: "2-digit",
          timeZoneName: "short",
        })
        .replace(",", "");
    }

    const tableColumns = [
      {
        field: "id",
        visible: false,
      },
      {
        field: "displayName",
        title: "Name",
        sortable: true,
        class: "w-50",
      },
      {
        field: "availableUses",
        title: "Uses",
        sortable: true,
        formatter: (index, { availableUses }) =>
          availableUses || `<i class="fas fa-infinity"></i>`,
      },
      {
        field: "expiresAt",
        title: "Expires at",
        sortable: true,
        class: "w-50",
        formatter: (index, { expiresAt }) => dateFormatter(new Date(expiresAt)),
      },
      {
        align: "center",
        formatter: (index, { removing }) =>
          `<div class="btn-group shadow-none" role="group" aria-label="Invitation options">
            <button type="button" id="table-edit-invitation" title="Edit invitation"
              class="btn text-warning p-0 me-1 shadow-none"${
                removing ? " disabled" : ""
              }>
              <i class="fas fa-edit fa-xl"></i>
            </button>
            <button type="button" id="table-delete-invitation" title="Delete invitation"
              class="btn text-danger p-0 ms-1 shadow-none"${
                removing ? " disabled" : ""
              }>
              ${
                removing
                  ? `<div class="spinner-grow spinner-grow-sm" role="status">
                <span class="visually-hidden">Removing...</span>
              </div>`
                  : '<i class="fas fa-trash-can fa-xl"></i>'
              }
            </button>
          </div>`,
        events: {
          "click #table-edit-invitation": (event, field, { id }) =>
            router.push({
              name: "site-invitation-edit",
              params: { invitationId: id },
            }),
          "click #table-delete-invitation": (event, field, { id }) =>
            removeInvitation(id),
        },
      },
    ];

    const tableOptions = {
      uniqueId: "id",
      pagination: true,
      search: true,
      toggle: "table",
      paginationSuccessivelySize: 1,
      paginationPagesBySide: 1,
      paginationParts: ["pageSize", "pageList"],
      pageList: [10, 25, 50, 100, 200, "All"],
      pageSize: 5,
      icons: {
        detailOpen: "fas fa-share fa-lg",
        detailClose: "fas fa-minus fa-lg",
      },
      detailView: true,
      detailViewIcon: true,
      detailFormatter: (index, { id }) => {
        const link = `${location.origin}/sites/invitation/${id}`;
        return `<div class="d-flex">
        <a onclick="event.preventDefault()" class="float-start" href="${link}">${link}</a>
        <button class="btn btn-outline-primary p-3 float-end" title="Copy link" onclick="
          function getTableComponent(node) {
            return node.classList.contains('bootstrap-table')
              ? node.parentNode
              : getTableComponent(node.parentNode);
          }
          getTableComponent(this).vueEmit('copyLink', '${link}');
        "><i class="fa-solid fa-copy fa-xl"></i></button></div>`;
      },
    };

    async function removeInvitation(invitationId) {
      const invitationIndex = siteInvitations.value.findIndex(
        ({ id }) => id === invitationId
      );
      siteInvitations.value[invitationIndex].removing = true;

      axios
        .delete(siteInvitationsAPIPath, {
          timeout: 5000,
          headers: { authorization: await getIdToken() },
          params: { id: invitationId },
        })
        .then(() => siteInvitations.value.splice(invitationIndex, 1))
        .catch((error) => {
          loadingError.value = error.message || error;
          siteInvitations.value[invitationIndex].removing = false;
        });
    }

    function copyLink(link) {
      navigator.clipboard
        .writeText(link)
        .then(() => toastrSuccess("Invitation link copied"))
        .catch(() => toastrError("Unable to copy invitation link"));
    }

    function share(index, { id }) {
      navigator.share &&
        navigator
          .share({
            title: "You have been invited to join a site",
            text: `Hello,
              ${getDisplayName} has invited you to join the site "${
              props.sites[props.siteId].displayName
            }"
              If you wish to join the site you can do so by following the link.`,
            url: `${location.origin}/sites/invitation/${id}`,
          })
          .catch(console.error);
    }

    return {
      loading,
      loadingError,
      tableColumns,
      tableOptions,
      siteInvitations,
      copyLink,
      share,
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
    <RouterView
      v-else-if="invitationId"
      :siteInvitations="siteInvitations"
      @done="$router.push({ name: 'site-invitations' })"
    />
    <template v-else>
      <BootstrapTable
        :columns="tableColumns"
        :data="siteInvitations"
        :options="tableOptions"
        @copyLink="copyLink"
        @onExpandRow="share"
      />
      <MDBBtn
        color="success"
        type="button"
        size="lg"
        class="w-100"
        @click="$router.push({ name: 'site-invitation-create' })"
        >Add New Invitation</MDBBtn
      >
    </template>
  </div>
</template>

<style></style>
