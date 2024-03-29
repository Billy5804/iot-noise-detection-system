<script>
import { ref, onMounted, computed, shallowRef } from "vue";
import { useRouter, RouterView } from "vue-router";
import { useUserStore } from "@/stores/UserStore";
import axios from "axios";
import SiteUserRoles from "@/utilitys/SiteUserRoles";
import BootstrapTable from "@/components/BootstrapTable.vue";
import { MDBRow, MDBCol } from "mdb-vue-ui-kit";
import AjaxButton from "@/components/AjaxButton.vue";
import LoadingView from "@/views/LoadingView.vue";

export default {
  components: {
    BootstrapTable,
    MDBRow,
    MDBCol,
    AjaxButton,
    LoadingView,
    RouterView,
  },

  props: {
    siteId: {
      type: String,
      required: true,
    },
    userId: String,
  },

  setup: function (props) {
    const { getIdToken } = useUserStore();
    const router = useRouter();

    const loading = ref(true);
    const loadingError = ref(null);
    const siteUsersAPIPath = API_V1_URL + "site-users";
    const siteUsers = ref([]);

    const siteHasUnauthorisedUsers = computed(() =>
      siteUsers.value.some(({ role }) => role === SiteUserRoles.UNAUTHORISED)
    );

    onMounted(async () => {
      const siteUsersResponse = await axios
        .get(siteUsersAPIPath, {
          timeout: 5000,
          headers: { authorization: await getIdToken() },
          params: { siteId: props.siteId },
        })
        .catch((error) => (loadingError.value = error.message || error));

      siteUsers.value = siteUsersResponse?.data?.reduce(
        (result, { user, ...siteUser }) => {
          result.push({ ...user, ...siteUser });
          return result;
        },
        []
      );

      loading.value = false;
    });

    const tableColumns = [
      {
        field: "id",
        visible: false,
      },
      {
        align: "center",
        formatter: (index, { removing }) =>
          `<button type="button" id="table-remove-user" class="btn text-danger shadow-none p-0 m-0" 
          title="Remove users from site"${removing ? " disabled" : ""}>
            ${
              removing
                ? `<div class="spinner-grow spinner-grow-sm" role="status">
              <span class="visually-hidden">Removing...</span>
            </div>`
                : '<i class="fas fa-user-minus fa-xl"></i>'
            }
          </button>`,
        events: {
          "click #table-remove-user": (event, field, { id }) => removeUser(id),
        },
      },
      {
        field: "displayName",
        title: "Users",
        sortable: true,
        class: "w-50",
      },
      {
        field: "role",
        title: "Role",
        sortable: true,
        class: "w-50",
        formatter: (index, { role }) =>
          `${role[0]}${role.slice(1).toLowerCase()}`,
      },
      {
        align: "center",
        formatter: (index, { removing }) =>
          `<button type="button" id="table-edit-user" class="btn text-warning shadow-none p-0 m-0" 
            title="Edit user"${removing ? " disabled" : ""}>
            <i class="fas fa-edit fa-xl"></i>
          </button>`,
        events: {
          "click #table-edit-user": (event, field, { id }) =>
            router.push({ name: "site-user-edit", params: { userId: id } }),
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
    };

    async function removeUser(userId) {
      const userIndex = siteUsers.value.findIndex(({ id }) => id === userId);
      siteUsers.value[userIndex].removing = true;

      axios
        .delete(siteUsersAPIPath, {
          timeout: 5000,
          headers: { authorization: await getIdToken() },
          params: { siteId: props.siteId, userId },
        })
        .then(() => siteUsers.value.splice(userIndex, 1))
        .catch((error) => {
          loadingError.value = error.message || error;
          siteUsers.value[userIndex].removing = false;
        });
    }

    const syncing = ref(false);

    const removeError = shallowRef(null);

    async function submitRemoveForm() {
      removeError.value = null;
      syncing.value = true;
      axios
        .delete(`${siteUsersAPIPath}?unauthorised`, {
          timeout: 5000,
          headers: { authorization: await getIdToken() },
          params: { siteId: props.siteId },
        })
        .then(({ data }) => {
          const deletedUserIds = data.reduce((result, { userId }) => {
            result.push(userId);
            return result;
          }, []);
          siteUsers.value = siteUsers.value.filter(
            ({ id }) => !deletedUserIds.includes(id)
          );
        })
        .catch((error) => (removeError.value = error.message || error))
        .finally(() => (syncing.value = false));
    }

    return {
      loading,
      loadingError,
      tableColumns,
      tableOptions,
      siteUsers,
      siteHasUnauthorisedUsers,
      submitRemoveForm,
      removeError,
      syncing,
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
      v-else-if="userId"
      :users="siteUsers"
      @done="$router.push({ name: 'site-users' })"
    />
    <template v-else>
      <BootstrapTable
        :columns="tableColumns"
        :data="siteUsers"
        :options="tableOptions"
      />
      <MDBRow
        v-if="siteHasUnauthorisedUsers"
        tag="form"
        class="g-3 mb-3"
        novalidate
        @submit.prevent="submitRemoveForm"
      >
        <MDBCol md="12" v-if="removeError">
          <div class="alert alert-danger text-center p-2 mb-0" role="alert">
            {{ removeError }}
          </div>
        </MDBCol>
        <MDBCol md="12">
          <AjaxButton
            color="danger"
            type="submit"
            size="lg"
            class="w-100"
            :syncing="syncing"
            >Remove All Unauthorised Users</AjaxButton
          >
        </MDBCol>
      </MDBRow>
    </template>
  </div>
</template>

<style></style>
