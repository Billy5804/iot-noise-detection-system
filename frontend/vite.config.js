import { fileURLToPath, URL } from "url";
import { defineConfig } from "vite";
import vue from "@vitejs/plugin-vue";

// https://vitejs.dev/config/
export default defineConfig(({ mode }) => ({
  plugins: [vue()],
  resolve: {
    alias: {
      "@": fileURLToPath(new URL("./src", import.meta.url)),
    },
  },
  define: {
    API_V1_URL: JSON.stringify(
      mode === "production" ? "/api/v1/" : "http://localhost/api/v1/"
    ),
    WS_URL: JSON.stringify(
      mode === "production" ? "/ws" : "http://localhost/ws"
    ),
  },
}));
