import { createApp } from "vue";
import { createPinia } from "pinia";
import "@/styles/importer.scss";

import App from "./App.vue";
import router from "./router";

RegExp.escape = (string) => {
  if (string) {
    return String(string).replace(/[.*+?^${}()|[\]\\]/g, "\\$&");
  }
};

const app = createApp(App);

app.use(createPinia());
app.use(router);

app.mount("#app");
