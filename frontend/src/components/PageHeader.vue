<script setup>
import { useUserStore } from "@/stores/UserStore";
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
} from "mdb-vue-ui-kit";

const user = useUserStore();

const loggedIn = computed(() => user.loggedIn);
const emailVerified = computed(() => user.emailVerified);

const collapse = ref(false);
const accountDropdown = ref(false);
const accountCollapse = ref(false);
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
          <template v-if="loggedIn">
            <template v-if="emailVerified"> </template>
          </template>
          <MDBNavbarItem linkClass="px-2" v-else to="/">Home</MDBNavbarItem>
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
            <MDBNavbarItem class="dropdown d-inline d-md-none">
              <MDBDropdownToggle
                tag="a"
                tabindex="0"
                class="nav-link px-2"
                @keydown.enter="accountCollapse = !accountCollapse"
                @click="accountCollapse = !accountCollapse"
                >Account</MDBDropdownToggle
              >
              <MDBCollapse v-model="accountCollapse" class="ps-3">
                <MDBNavbarItem linkClass="py-1 mt-n1" to="/account">
                  <MDBIcon icon="user-cog" class="text-primary" /> My Account
                </MDBNavbarItem>
                <MDBNavbarItem linkClass="py-1" to="/logout">
                  <MDBIcon icon="sign-out-alt" class="text-warning" /> Sign Out
                </MDBNavbarItem>
              </MDBCollapse>
            </MDBNavbarItem>
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
</style>
