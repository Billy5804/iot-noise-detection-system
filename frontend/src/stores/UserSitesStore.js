import { defineStore, acceptHMRUpdate } from "pinia";
import { ref } from "vue";
import axios from "axios";
import SiteUserRoles from "@/utilitys/SiteUserRoles";

const siteUsersAPIPath = "http://localhost:443/api/v1/site-users";

const sites = ref(null);

export const useUserSitesStore = defineStore("UserSitesStore", {
  state: () => ({}),
  getters: {
    getSites: () => sites.value,
    getAuthorisedSites: () =>
      sites.value &&
      Object.fromEntries(
        Object.entries(sites.value).filter(
          ([, { role }]) => role !== SiteUserRoles.UNAUTHORISED
        )
      ),
    finishedFirstLoad: () => !!sites.value,
  },
  actions: {
    refreshSites: async (authorization) => {
      const sitesResponse = await axios.get(siteUsersAPIPath, {
        timeout: 5000,
        headers: { authorization },
      });

      sites.value =
        sitesResponse?.data?.reduce((result, { site, role, ...siteUser }) => {
          console.log(siteUser);
          const siteId = site.id;
          delete site.id;
          site.role = role;
          result[siteId] = site;
          return result;
        }, {}) || {};
    },
    clearSites: () => (sites.value = null),
  },
});

if (import.meta.hot) {
  import.meta.hot.accept(acceptHMRUpdate(useUserSitesStore, import.meta.hot));
}
