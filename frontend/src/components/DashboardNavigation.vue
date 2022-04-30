<script setup>
import {
  MDBCol,
  MDBBtnGroup,
  MDBBtn,
  MDBDropdown,
  MDBDropdownMenu,
} from "mdb-vue-ui-kit";
import { ref } from "vue";
import { RouterLink } from "vue-router";
import AjaxButton from "./AjaxButton.vue";

defineProps({
  siteId: { type: String, required: true },
  locations: Object,
});

const locationsDropdown = ref(false);
</script>

<template>
  <MDBCol
    col="12"
    class="d-md-flex flex-row-reverse justify-content-between align-items-end"
  >
    <MDBBtnGroup
      aria-label="Dashboard site device overview and locations navigation"
      class="mb-3 mb-md-0 text-nowrap"
    >
      <RouterLink
        :to="{
          name: 'dashboard-device-overview',
          params: { siteId },
        }"
        class="btn btn-dark"
        >Site Devices</RouterLink
      >
      <AjaxButton
        v-if="!locations"
        color="dark"
        :syncing="true"
        class="dropdown-toggle"
        >Locations</AjaxButton
      >
      <RouterLink
        v-else-if="!Object.keys(locations).length"
        :to="{
          name: 'dashboard-location-add',
          params: { siteId },
        }"
        class="btn btn-dark"
        >Locations</RouterLink
      >
      <MDBBtnGroup v-else id="locations-navigation-dropdown">
        <MDBBtn
          color="dark"
          @click="locationsDropdown = !locationsDropdown"
          class="dropdown-toggle"
          >Locations
        </MDBBtn>
        <MDBDropdown
          v-model="locationsDropdown"
          target="#locations-navigation-dropdown"
          align="end"
        >
          <MDBDropdownMenu aria-labelledby="dropdownMenuButton">
            <template
              v-for="(location, locationId) in locations"
              :key="locationId"
            >
              <RouterLink
                :to="{
                  name: 'dashboard-location-overview',
                  params: { siteId, locationId },
                }"
                class="dropdown-item"
                >{{ location.displayName }}</RouterLink
              >
              <hr class="m-0" />
            </template>
          </MDBDropdownMenu>
        </MDBDropdown>
      </MDBBtnGroup>
    </MDBBtnGroup>
    <slot />
  </MDBCol>
</template>

<style scoped>
hr:last-of-type {
  display: none;
}

.dropdown-item:first-of-type {
  border-top-left-radius: 0.5rem;
  border-top-right-radius: 0.5rem;
  border-bottom-left-radius: 0;
  border-bottom-right-radius: 0;
}

.dropdown-item:last-of-type {
  border-top-left-radius: 0;
  border-top-right-radius: 0;
  border-bottom-left-radius: 0.5rem;
  border-bottom-right-radius: 0.5rem;
}
</style>
