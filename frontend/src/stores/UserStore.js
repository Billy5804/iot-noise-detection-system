import { defineStore, acceptHMRUpdate } from "pinia";
import { ref } from "vue";
import { firebaseAuthentication } from "@/firebase/database";
import { useUserSitesStore } from "./UserSitesStore";
import {
  onAuthStateChanged,
  signInWithEmailAndPassword,
  signOut,
  createUserWithEmailAndPassword,
  sendEmailVerification,
  EmailAuthProvider,
  reauthenticateWithCredential,
  sendPasswordResetEmail,
  updateEmail,
  getIdToken,
  updatePassword,
  updateProfile,
} from "firebase/auth";
import { info as toastrInfo } from "toastr";

export const useUserStore = defineStore("UserStore", {
  state: () => ({}),
  getters: {
    finishedLoading: () => finishedLoading.value,
    loggedIn: () => !!authUser.value,
    emailVerified: () => authUser.value?.emailVerified,
    getEmailAddress: () => authUser.value?.email,
    getDisplayName: () => authUser.value?.displayName,
  },
  actions: {
    setEmailAddress: async function (newEmailAddress, currentPassword) {
      const credential = EmailAuthProvider.credential(
        this.getEmailAddress,
        currentPassword
      );
      await reauthenticateWithCredential(authUser.value, credential);
      await updateEmail(authUser.value, newEmailAddress);
      await this.sendEmailVerification();
    },
    setDisplayName: async function (displayName) {
      await updateProfile(authUser.value, { displayName });
    },
    setPassword: async function (newPassword, currentPassword) {
      const credential = EmailAuthProvider.credential(
        this.getEmailAddress,
        currentPassword
      );
      await reauthenticateWithCredential(authUser.value, credential);
      await updatePassword(authUser.value, newPassword);
    },
    logout: async function () {
      return await signOut(firebaseAuthentication);
    },
    login: async function (emailAddress, password) {
      await signInWithEmailAndPassword(
        firebaseAuthentication,
        emailAddress,
        password
      );
      toastrInfo("Welcome back!");
    },
    resetPassword: async function (emailAddress) {
      return await sendPasswordResetEmail(firebaseAuthentication, emailAddress);
    },
    register: async function ({ emailAddress, password, displayName }) {
      const { user } = await createUserWithEmailAndPassword(
        firebaseAuthentication,
        emailAddress,
        password
      );
      await updateProfile(user, { displayName });
      await sendEmailVerification(user);
      toastrInfo(`Welcome ${displayName}!`);
    },
    sendEmailVerification: async () =>
      await sendEmailVerification(authUser.value),
    getIdToken: async () => await getIdToken(authUser.value),
  },
});

const finishedLoading = ref(false);

const authUser = ref(null);

onAuthStateChanged(firebaseAuthentication, async (user) => {
  const { refreshSites, clearSites } = useUserSitesStore();
  if (user && user.emailVerified) {
    await refreshSites(await getIdToken(user));
  } else {
    clearSites();
  }
  authUser.value = user;
  finishedLoading.value = true;
});

if (import.meta.hot) {
  import.meta.hot.accept(acceptHMRUpdate(useUserStore, import.meta.hot));
}
