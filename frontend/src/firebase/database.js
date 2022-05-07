// Import the functions you need from the SDKs you need
import { initializeApp } from "firebase/app";
import { getAnalytics } from "firebase/analytics";
// TODO: Add SDKs for Firebase products that you want to use
import { getAuth } from "firebase/auth";
import { getStorage } from "firebase/storage";
// https://firebase.google.com/docs/web/setup#available-libraries

// Your web app's Firebase configuration
// For Firebase JS SDK v7.20.0 and later, measurementId is optional
const firebaseConfig = {
  apiKey: "AIzaSyCEaaNZE0-9FgSJhjIuoxxfAnxdu6dgtRQ",
  authDomain: "iot-noise-detect-system-auth.firebaseapp.com",
  projectId: "iot-noise-detect-system-auth",
  storageBucket: "iot-noise-detect-system-auth.appspot.com",
  messagingSenderId: "949946266829",
  appId: "1:949946266829:web:acb08947de92b27deb2584",
  measurementId: "G-Z8R684S15Z",
};

// Initialize Firebase
const app = initializeApp(firebaseConfig);
const analytics = getAnalytics(app);
const firebaseAuthentication = getAuth(app);
const firebaseStorage = getStorage(app);

export { analytics, firebaseAuthentication, firebaseStorage };
