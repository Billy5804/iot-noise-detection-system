import { defineStore, acceptHMRUpdate } from "pinia";
import { ref } from "vue";
import router from "@/router";
import { firebaseAuthentication } from "@/firebase/database";
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
  reload,
  updatePassword,
  updateProfile,
} from "firebase/auth";
import { info } from "toastr";

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
      info("Welcome back!");
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
      info(`Welcome ${displayName}!`);
    },
    sendEmailVerification: async () =>
      await sendEmailVerification(authUser.value),
    getIdToken: async () => await getIdToken(authUser.value),
  },
});

const finishedLoading = ref(false);

const authUser = ref(null);

onAuthStateChanged(firebaseAuthentication, async (user) => {
  if (
    user &&
    user.emailVerified &&
    router.currentRoute.value.query?.emailNotVerified
  ) {
    await getIdToken(user, true);
    await reload(user);
  }
  authUser.value = user;
  finishedLoading.value = true;
});

if (import.meta.hot) {
  import.meta.hot.accept(acceptHMRUpdate(useUserStore, import.meta.hot));
}
