import { createApp } from 'vue';
import App from './App.vue';
import router from './router';
import { createPinia } from 'pinia';
import VueAwesomePaginate from 'vue-awesome-paginate';
import 'vue-awesome-paginate/dist/style.css';
import 'bootstrap/dist/css/bootstrap.min.css';
import 'bootstrap/dist/js/bootstrap.bundle.min.js';
import "@mdi/font/css/materialdesignicons.css";
import "@fortawesome/fontawesome-free/css/all.css";
// Vuetify
import "vuetify/styles";
import { createVuetify} from "vuetify";
import * as components from "vuetify/components";
import * as directives from "vuetify/directives";

import { fa } from "vuetify/iconsets/fa";
import { aliases, mdi } from "vuetify/lib/iconsets/mdi";
// make sure to also import the coresponding css
import "@mdi/font/css/materialdesignicons.css"; // Ensure you are using css-loader
import "@fortawesome/fontawesome-free/css/all.css"; //
import colors from 'vuetify/lib/util/colors'
//axois intercepter
import { useAuthStore } from './stores/auth.store.js'
import { setupAxiosInterceptors } from './axiosHandler.js';
import VueClipboard from "vue-clipboard3";

const app = createApp(App);
const vuetify = createVuetify({
    components,
    directives,
    theme: {
        defaultTheme: 'light',
        themes: {
            light: {
                primary: colors.purple,
                secondary: colors.grey.darken1,
                accent: colors.shades.black,
                error: colors.red.accent3,
                background: colors.indigo.lighten5 // Not automatically applied

            },
            dark: {
                primary: colors.blue.lighten3,
                background: colors.indigo.base, // If not using lighten/darken, use base to return hex
            },
        },
    },

    icons: {
        defaultSet: "mdi",
        aliases,
        sets: {
            mdi,
            fa,
        },
    },
})
app.use(VueClipboard, {
    autoSetContainer: true,
    appendToBody: true,
})

app.use(router);
app.use(createPinia());
app.use(VueAwesomePaginate);
app.use(vuetify);
app.mount('#app');

const authStore = useAuthStore();
setupAxiosInterceptors(authStore);
