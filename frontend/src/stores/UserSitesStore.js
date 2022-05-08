import { defineStore, acceptHMRUpdate } from "pinia";
import { ref } from "vue";
import axios from "axios";
import SiteUserRoles from "@/utilitys/SiteUserRoles";

const siteUsersAPIPath = `${
  import.meta.env.BASE_URL ? import.meta.env.BASE_URL : "http://localhost:443"
}/api/v1/site-users`;

const sites = ref(null);

export const useUserSitesStore = defineStore("UserSitesStore", {
  state: () => ({
    lastRefreshTime: NaN,
  }),
  getters: {
    sites: () => sites.value,
    authorisedSites: () =>
      sites.value &&
      Object.fromEntries(
        Object.entries(sites.value).filter(
          ([, { role }]) => role !== SiteUserRoles.UNAUTHORISED
        )
      ),
  },
  actions: {
    refreshSites: async function (authorization) {
      const sitesResponse = await axios.get(siteUsersAPIPath, {
        timeout: 5000,
        headers: { authorization },
      });

      sites.value =
        sitesResponse?.data?.reduce((result, { site, ...siteUser }) => {
          const siteId = site.id;
          delete site.id;
          Object.assign(site, siteUser);
          result[siteId] = site;
          return result;
        }, {}) || {};

      this.lastRefreshTime = +new Date();
    },
    clearSites: () => (sites.value = null),
  },
});

if (import.meta.hot) {
  import.meta.hot.accept(acceptHMRUpdate(useUserSitesStore, import.meta.hot));
}
