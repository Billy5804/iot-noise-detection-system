<script>
import { ref, onBeforeMount } from "vue";
import { useUserStore } from "@/stores/UserStore";
import axios from "axios";
import BootstrapTable from "../../components/BootstrapTable.vue";

export default {
  components: { BootstrapTable },

  props: {
    siteId: {
      type: String,
      required: true,
    },
  },

  setup: function (props) {
    const { getIdToken } = useUserStore();

    const loading = ref(true);
    const loadingError = ref(null);
    const siteUsersAPIPath = "https://noise.alexroyle.com/api/v1/site-users";
    const siteUsers = ref([]);

    onBeforeMount(async () => {
      const siteUsersResponse = await axios
        .get(siteUsersAPIPath, {
          timeout: 5000,
          headers: { authorization: `Bearer ${await getIdToken()}` },
          params: { siteId: props.siteId },
        })
        .catch((error) => (loadingError.value = error.message || error));

      siteUsers.value = siteUsersResponse?.data.reduce(
        (result, { user, role }) => {
          result.push({ ...user, role });
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
        field: "options",
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
      },
    ];

    const tableOptions = {
      uniqueId: "userId",
      pagination: true,
      search: true,
      toggle: "table",
      paginationSuccessivelySize: 1,
      paginationPagesBySide: 1,
      paginationParts: ["pageSize", "pageList"],
      pageList: [10, 25, 50, 100, 200, "All"],
    };

    function removeUnauthorisedUsers() {}

    async function removeUser(userId) {
      const userIndex = siteUsers.value.findIndex(({ id }) => id === userId);
      siteUsers.value[userIndex].removing = true;

      axios
        .delete(siteUsersAPIPath, {
          timeout: 5000,
          headers: { authorization: `Bearer ${await getIdToken()}` },
          params: { siteId: props.siteId, userId },
        })
        .then(() => siteUsers.value.splice(userIndex, 1))
        .catch((error) => {
          loadingError.value = error.message || error;
          siteUsers.value[userIndex].removing = false;
        });
    }

    return {
      tableColumns,
      tableOptions,
      siteUsers,
    };
  },
};
</script>

<template>
  <BootstrapTable
    :columns="tableColumns"
    :data="siteUsers"
    :options="tableOptions"
  />
</template>

<style></style>
