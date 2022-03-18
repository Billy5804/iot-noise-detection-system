<script setup>
import { computed, reactive } from "vue";
import { useUserStore } from "@/stores/UserStore";
import toastr from "toastr";
import {
  MDBRow,
  MDBCol,
  MDBModal,
  MDBModalHeader,
  MDBModalTitle,
  MDBModalBody,
  MDBModalFooter,
  MDBBtn,
} from "mdb-vue-ui-kit";
import UpdateEmailAddress from "../components/UpdateEmailAddress.vue";
import UpdatePassword from "../components/UpdatePassword.vue";
import EditButton from "../components/EditButton.vue";
import UpdateDisplayName from "../components/UpdateDisplayName.vue";
const user = useUserStore();
const displayName = computed(() => user.getDisplayName);
const emailAddress = computed(() => user.getEmailAddress);

const updateModals = reactive({
  email: { shown: false, title: "Email Address" },
  password: { shown: false, title: "Password" },
  displayName: { shown: false, title: "Display Name" },
});

const showModal = computed({
  get: () => Object.values(updateModals).some(({ shown }) => shown),
  set: (value) =>
    value === false &&
    Object.keys(updateModals).forEach(
      (key) => (updateModals[key].shown = false)
    ),
});

const currentModalKey = computed(() =>
  Object.entries(updateModals).reduce(
    (result, [key, { shown }]) => (shown ? key : result),
    null
  )
);

function updated() {
  toastr.success(
    `Successfully updated your ${updateModals[currentModalKey.value].title}`
  );
  showModal.value = false;
}
</script>

<template>
  <main id="account-view">
    <h1>My account</h1>
    <hr />
    <h4>Account information</h4>
    <MDBRow class="mb-3 align-items-end">
      <MDBCol sm="4">Display Name:</MDBCol>
      <MDBCol sm="8" class="d-flex align-items-center">
        <strong>{{ displayName }}</strong>
        <EditButton
          @click="updateModals.displayName.shown = true"
          title="Edit display name"
        />
      </MDBCol>
      <MDBCol sm="4">Email Address:</MDBCol>
      <MDBCol sm="8" class="d-flex align-items-center">
        <strong>{{ emailAddress }}</strong>
        <EditButton
          @click="updateModals.email.shown = true"
          title="Edit email address"
        />
      </MDBCol>
    </MDBRow>
    <hr />
    <MDBBtn
      class="float-end"
      size="sm"
      color="secondary"
      @click="updateModals.password.shown = true"
      >Change Password</MDBBtn
    >
    <h4>Password</h4>
    <hr />

    <MDBModal
      id="editModal"
      tabindex="-1"
      labelledby="editModalLabel"
      v-model="showModal"
      staticBackdrop
    >
      <MDBModalHeader>
        <MDBModalTitle id="editModalLabel"
          >Update {{ updateModals[currentModalKey].title }}</MDBModalTitle
        >
      </MDBModalHeader>
      <MDBModalBody>
        <UpdateEmailAddress
          v-if="currentModalKey === 'email'"
          @updated="updated"
        />
        <UpdatePassword
          v-if="currentModalKey === 'password'"
          @updated="updated"
        />
        <UpdateDisplayName
          v-if="currentModalKey === 'displayName'"
          @updated="updated"
        />
      </MDBModalBody>
      <MDBModalFooter>
        <MDBBtn color="secondary" @click="showModal = false">Close</MDBBtn>
      </MDBModalFooter>
    </MDBModal>
  </main>
</template>
