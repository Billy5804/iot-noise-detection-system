<script setup>
import { useUserStore } from "@/stores/UserStore";
import { useUserSitesStore } from "@/stores/UserSitesStore";
import { ref, computed } from "vue";
import {
  MDBNavbar,
  MDBNavbarToggler,
  MDBNavbarNav,
  MDBNavbarItem,
  MDBCollapse,
  MDBDropdown,
  MDBDropdownToggle,
  MDBIcon,
  MDBDropdownMenu,
  MDBBtn,
  MDBModal,
  MDBModalHeader,
  MDBModalTitle,
  MDBModalBody,
  MDBModalFooter,
} from "mdb-vue-ui-kit";

const user = useUserStore();

const userSites = useUserSitesStore();

const sites = computed(() => userSites.authorisedSites);

const loggedIn = computed(() => user.loggedIn);
const emailVerified = computed(() => user.emailVerified);

const collapse = ref(false);

const accountDropdown = ref(false);

const dashboardDropdown = ref(false);
const dashboardModal = ref(false);
</script>

<template>
  <header>
    <MDBNavbar expand="md" light bg="white" container="md">
      <MDBNavbarToggler
        @click="collapse = !collapse"
        target="#navigation"
      ></MDBNavbarToggler>
      <MDBCollapse v-model="collapse" id="navigation">
        <MDBNavbarNav collapse class="mb-2 mb-md-0 w-100">
          <template v-if="loggedIn && emailVerified">
            <MDBNavbarItem linkClass="px-2" to="/sites"
              >Site Management</MDBNavbarItem
            >
            <template v-if="sites && Object.keys(sites).length">
              <template v-if="Object.keys(sites).length > 1">
                <MDBNavbarItem class="d-inline d-md-none"
                  ><a
                    tabindex="0"
                    type="button"
                    aria-controls="dashboardSitesNavigationModal"
                    class="nav-link px-2"
                    @keydown.enter="dashboardModal = true"
                    @click="dashboardModal = true"
                    >Dashboard</a
                  ></MDBNavbarItem
                >
                <MDBModal
                  id="dashboardSitesNavigationModal"
                  tabindex="-1"
                  labelledby="dashboardSitesNavigationLabel"
                  v-model="dashboardModal"
                >
                  <MDBModalHeader>
                    <MDBModalTitle id="dashboardSitesNavigationLabel"
                      >Dashboard Sites</MDBModalTitle
                    >
                  </MDBModalHeader>
                  <MDBModalBody class="dashboard-sites-navigation">
                    <template v-for="(site, siteId) in sites" :key="siteId">
                      <MDBNavbarItem
                        :to="{ name: 'dashboard', params: { siteId } }"
                        class="dropdown-item"
                      >
                        <span class="text-truncate">{{ site.displayName }}</span
                        ><br />
                        <small class="text-truncate"
                          >Role:
                          {{
                            `${site.role[0]}${site.role.slice(1).toLowerCase()}`
                          }}</small
                        >
                      </MDBNavbarItem>
                      <hr class="m-0" />
                    </template>
                  </MDBModalBody>
                  <MDBModalFooter>
                    <MDBBtn color="secondary" @click="dashboardModal = false"
                      >Close</MDBBtn
                    >
                  </MDBModalFooter>
                </MDBModal>
                <MDBNavbarItem class="dropdown d-none d-md-inline">
                  <MDBDropdown v-model="dashboardDropdown" align="end">
                    <MDBDropdownToggle
                      tag="a"
                      tabindex="0"
                      class="nav-link"
                      @keydown.enter="dashboardDropdown = !dashboardDropdown"
                      @click="dashboardDropdown = !dashboardDropdown"
                    >
                      Dashboard
                    </MDBDropdownToggle>
                    <MDBDropdownMenu class="dashboard-sites-navigation">
                      <template v-for="(site, siteId) in sites" :key="siteId">
                        <MDBNavbarItem
                          :to="{ name: 'dashboard', params: { siteId } }"
                          class="dropdown-item"
                        >
                          <span class="text-truncate">{{
                            site.displayName
                          }}</span
                          ><br />
                          <small class="text-truncate"
                            >Role:
                            {{
                              `${site.role[0]}${site.role
                                .slice(1)
                                .toLowerCase()}`
                            }}</small
                          >
                        </MDBNavbarItem>
                        <hr class="m-0" />
                      </template>
                    </MDBDropdownMenu>
                  </MDBDropdown>
                </MDBNavbarItem>
              </template>
              <MDBNavbarItem
                v-else
                linkClass="px-2"
                :to="{
                  name: 'dashboard',
                  params: { siteId: Object.keys(sites)[0] },
                }"
                >Dashboard</MDBNavbarItem
              >
            </template>
          </template>
          <MDBNavbarItem linkClass="px-2" v-else-if="!loggedIn" to="/"
            >Home</MDBNavbarItem
          >
          <template v-if="loggedIn">
            <MDBNavbarItem class="ms-md-auto dropdown d-none d-md-inline">
              <MDBDropdown v-model="accountDropdown" align="end">
                <MDBDropdownToggle
                  tag="a"
                  tabindex="0"
                  class="nav-link"
                  @keydown.enter="accountDropdown = !accountDropdown"
                  @click="accountDropdown = !accountDropdown"
                >
                  <MDBIcon icon="user" size="lg" />
                </MDBDropdownToggle>
                <MDBDropdownMenu>
                  <MDBNavbarItem to="/account" class="dropdown-item">
                    <MDBIcon icon="user-cog" class="text-primary" /> My Account
                  </MDBNavbarItem>
                  <MDBNavbarItem to="/logout" class="dropdown-item">
                    <MDBIcon icon="sign-out-alt" class="text-warning" /> Sign
                    Out
                  </MDBNavbarItem>
                </MDBDropdownMenu>
              </MDBDropdown>
            </MDBNavbarItem>
            <MDBNavbarItem
              class="d-inline d-md-none"
              linkClass="px-2"
              to="/account"
              >My Account</MDBNavbarItem
            >
            <MDBNavbarItem
              class="d-inline d-md-none"
              linkClass="px-2"
              to="/logout"
              >Sign out</MDBNavbarItem
            >
          </template>
          <template v-else>
            <MDBNavbarItem class="ms-md-auto" linkClass="px-2" to="/login"
              >Login</MDBNavbarItem
            >
            <MDBNavbarItem linkClass="px-2" to="/register"
              >Register</MDBNavbarItem
            >
          </template>
        </MDBNavbarNav>
      </MDBCollapse>
    </MDBNavbar>
  </header>
</template>

<style>
.nav-item.dropdown-item a {
  padding: 0 !important;
}

.dashboard-sites-navigation hr:last-of-type {
  display: none;
}
</style>
