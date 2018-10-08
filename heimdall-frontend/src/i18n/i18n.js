import i18next from 'i18next'
import LanguageDetector from 'i18next-browser-languagedetector'
import { reactI18nextModule } from 'react-i18next'

import en from './en'
import ptBR from './pt-br'

i18next
    .use(LanguageDetector)
    .use(reactI18nextModule)
    .init({
    // we init with resources
    resources: {
        "en": en,
        "pt-BR": ptBR
    },
    fallbackLng: ['en', 'pt-BR'],
    debug: true,

    // have a common namespace used around the full app
    ns: ["translations"],
    defaultNS: "translations",

    keySeparator: false, // we use content as keys

    interpolation: {
        escapeValue: false, // not needed for react!!
        formatSeparator: ","
    },

    react: {
        wait: false,
    }
})

export default i18next
