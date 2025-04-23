// https://nuxt.com/docs/api/configuration/nuxt-config
export default defineNuxtConfig({
  modules: ["@nuxtjs/tailwindcss"],
  tailwindcss: {
    cssPath: "~/assets/css/tailwind.css",
    configPath: "tailwind.config.ts",
    exposeConfig: {
      level: 2
    },
    viewer: true,
    config: {}
  },
  compatibilityDate: '2024-11-01',
  devtools: { enabled: true }
})
