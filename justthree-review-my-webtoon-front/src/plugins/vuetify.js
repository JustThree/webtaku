import { createVuetify } from 'vuetify'
import { aliases, mdi } from 'vuetify/iconsets/mdi-svg'
import { mdiAccount } from '@mdi/js'
import '@mdi/font/css/materialdesignicons.css' // Ensure you are using css-loader

const savedTheme = (typeof localStorage !== 'undefined' && localStorage.getItem('theme')) || 'dark';

export default createVuetify({
    theme: {
        defaultTheme: savedTheme,
    },
    icons: {
        defaultSet: 'mdi',
        aliases: {
            ...aliases,
            account: mdiAccount,
        },
        sets: {
            mdi,
        },
    },
})